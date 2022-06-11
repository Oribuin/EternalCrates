package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.HexUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.action.PluginAction;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateType;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.eternalcrates.util.ItemBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class CrateManager extends Manager {

    private final List<UUID> activeUsers = new ArrayList<>();
    private final Map<String, Crate> cachedCrates = new HashMap<>();
    private final Map<String, CommentedFileConfiguration> unregisteredCrates = new HashMap<>();

    public CrateManager(RosePlugin plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        this.activeUsers.clear();

        final File folder = new File(this.rosePlugin.getDataFolder(), "crates");
        if (folder.exists()) {
            return;
        }

        folder.mkdir();

        final File file = new File(folder, "example.yml");
        try {
            if (!file.exists()) {
                file.createNewFile();

                // Add default crate config values.
                CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
                this.getDefaultCrateValues().forEach((path, object) -> {
                    if (path.startsWith("#")) {
                        config.addPathedComments(path, (String) object);
                    } else {
                        config.set(path, object);
                    }
                });

                config.save();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        this.cachedCrates.clear();
    }


    /**
     * Load all the plugin crates from the /EternalCrates/crates folder
     * and cache them
     */
    public void loadCrates() {
        this.cachedCrates.clear();
        this.rosePlugin.getLogger().info("Loading all crates from the /EternalCrates/crates folder");
        final File folder = new File(this.rosePlugin.getDataFolder(), "crates");
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        Arrays.stream(files).filter(file -> file.getName().toLowerCase().endsWith(".yml"))
                .forEach(file -> {
                    final CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
                    final Crate crate = this.createCreate(config);
                    if (crate == null)
                        return;

                    this.cachedCrates.put(crate.getId(), crate);
                });
    }

    /**
     * Get a crate from the id of the crate.
     *
     * @param id The ID of the crate.
     * @return An Optional Crate
     */
    public Optional<Crate> getCrate(String id) {
        return Optional.ofNullable(this.cachedCrates.get(id));
    }

    /**
     * Get a cached crate from the location of it.
     *
     * @param location The location of the crate
     * @return The optional crate.
     */
    public Optional<Crate> getCrate(Location location) {
        return this.getCachedCrates().values()
                .stream()
                .filter(crate -> crate.getLocations().contains(location))
                .findFirst();
    }

    /**
     * Create a creation from a file configuration, Seriously avert your eyes holy shit
     *
     * @param config The config the crate is being created from
     * @return The crate.
     */
    public Crate createCreate(final CommentedFileConfiguration config) {
        final AnimationManager animationManager = this.rosePlugin.getManager(AnimationManager.class);

        final Optional<String> name = Optional.ofNullable(config.getString("name"));
        if (name.isEmpty())
            return null;

        Optional<String> displayName = Optional.ofNullable(config.getString("display-name"));
        if (displayName.isEmpty())
            displayName = name;

        // this line isn't dumb
        final Optional<? extends Animation> animation = animationManager.getAnimationFromConfig(config);
        if (animation.isEmpty()) {
            this.rosePlugin.getLogger().warning("Couldn't load animation for crate: " + name.get());
            this.unregisteredCrates.put(name.get().toLowerCase(), config);
            return null;
        }

        final CrateType crateType = Arrays.stream(CrateType.values())
                .filter(x -> x.name().equalsIgnoreCase(PluginUtils.get(config, "crate-type", "PHYSICAL")))
                .findFirst()
                .orElse(CrateType.PHYSICAL);

        if (crateType == CrateType.VIRTUAL && !animation.get().canBeVirtual()) {
            this.rosePlugin.getLogger().warning("Cannot load crate " + name.get() + ", Animation does not support virtual crates.");
            return null;
        }

        final Map<Integer, Reward> rewards = new HashMap<>();
        final CommentedConfigurationSection section = config.getConfigurationSection("rewards");
        if (section == null)
            return null;

        final AtomicInteger id = new AtomicInteger(0);
        section.getKeys(false).forEach(s -> {

            final ItemStack item = PluginUtils.getItemStack(section, s);
            if (item == null)
                return;

            final Reward reward = new Reward(id.getAndIncrement(), item, section.getDouble(s + ".chance"));

            if (section.get(s + ".preview-item") != null) {
                ItemStack previewItem = PluginUtils.getItemStack(section, section.getCurrentPath() + "." + s + ".preview-item");
                if (previewItem != null)
                    reward.setPreviewItem(previewItem);
            }

            // section inception
            // holup that rhymes
            final List<String> actionSection = section.getStringList(s + ".actions");
            actionSection.stream().map(PluginAction::parse)
                    .filter(Optional::isPresent)
                    .forEach(action -> reward.getActions().add(action.get()));

            rewards.put(reward.getId(), reward);
        });

        // Get all the crate actions when it's opened by a player.
        List<Action> openActions = config.getStringList("open-actions")
                .stream()
                .map(PluginAction::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        final Crate crate = new Crate(name.get());
        final DataManager data = this.rosePlugin.getManager(DataManager.class);


        crate.setAnimation(animation.get());
        crate.setName(displayName.get());
        crate.setRewardMap(rewards);
        crate.setMaxRewards(Math.max(PluginUtils.get(config, "max-rewards", 1), 1));
        crate.setMinRewards(Math.min(PluginUtils.get(config, "min-rewards", 1), crate.getMaxRewards()));
        crate.setMinGuiSlots(Math.max(PluginUtils.get(config, "min-inv-slots", crate.getMaxRewards()), crate.getMaxRewards()));
        crate.setConfig(config);
        crate.setOpenActions(openActions);
        crate.setType(crateType);
        data.loadCrateLocation(crate);
        animation.get().setCrate(crate);

        // Add crate animation settings to plugin config.
        animation.get().getRequiredValues().forEach((path, object) -> {
            if (config.get(path) == null) {
                config.set(path, object);
            }
        });

        config.save();

        ItemStack item = new ItemBuilder(PluginUtils.getItemStack(config, "key"))
                .setNBT(rosePlugin, "crateKey", crate.getId().toLowerCase())
                .create();

        if (item == null) {
            item = new ItemBuilder(Material.TRIPWIRE_HOOK)
                    .setName(HexUtils.colorify("#00B4DB&lCrate Key &7» &f" + crate.getId()))
                    .glow(true)
                    .setLore(HexUtils.colorify("&7A key to open the " + crate.getId().toLowerCase() + " crate!"),
                            "",
                            HexUtils.colorify("&7Right-Click on the #00B4DB&l" + crate.getId() + " &7crate to open")
                    )
                    .setNBT(this.rosePlugin, "crateKey", crate.getId().toLowerCase())
                    .create();
        }

        crate.setKey(item);
        this.rosePlugin.getLogger().info("Registered Crate: " + crate.getId() + " with " + crate.getRewardMap().size() + " rewards!");
        return crate;
    }

    /**
     * Get crate from a block
     *
     * @param block The block to get the crate from
     */
    public Optional<Crate> getCrateFromBlock(final Block block) {
        return this.cachedCrates.values().stream()
                .filter(crate -> crate.getLocations().contains(block.getLocation()))
                .findFirst();
    }

    /**
     * Save crate values to a CommentedFileConfiguration from the crate object
     *
     * @param crate The crate to save
     */
    public void saveCrate(Crate crate) {
        this.cachedCrates.put(crate.getId().toLowerCase(), crate);
        final CommentedFileConfiguration config = crate.getConfig();
        if (config == null)
            return;

        config.set("name", crate.getId());
        config.set("display-name", crate.getName());
        config.set("max-rewards", crate.getMaxRewards());
        config.set("min-rewards", crate.getMinRewards());
        config.set("min-inv-slots", crate.getMinGuiSlots());
        config.save();

        this.rosePlugin.getManager(DataManager.class).saveCrateLocations(crate);

    }

    /**
     * Gets the crate default configuration options.
     *
     * @return The configuration options.
     */
    public Map<String, Object> getDefaultCrateValues() {
        return new LinkedHashMap<>() {{
            // General Crate Settings
            this.put("#0", "Technical name of the crate");
            this.put("name", "example");
            this.put("#1", "The display name of the crate");
            this.put("display-name", "Example Crate");
            this.put("#3", "The crate type, options are: [PHYSICAL, VIRTUAL]");
            this.put("crate-type", "PHYSICAL");

            // Configure crate reward settings
            this.put("#2", "Maximum Rewards to be given");
            this.put("max-rewards", 1);
            this.put("#4", "Minimum rewards to be given");
            this.put("min-rewards", 1);
            this.put("#5", "Minimum inventory slots required to open the crate");
            this.put("min-inv-slots", 1);

            // Configure crate animation settings
            this.put("#6", "The animation to use for the crate");
            this.put("#7", "Each crate animation has individual settings which will be automatically applied to the config file");
            this.put("animation.name", "rings");

            // Configure crate key settings
            this.put("#8", "The key to open the crate");
            this.put("key.material", "TRIPWIRE_HOOK");
            this.put("key.glow", true);
            this.put("key.name", "&lCrate Key &7» &fExample");
            this.put("key.lore", Arrays.asList(
                    "&7A key to open the &lExample &7crate!",
                    "",
                    "&7Right-Click on the &lExample &7crate to open"
            ));

            this.put("#9", "The crate rewards");
            this.put("rewards.1.material", "STONE");
            this.put("rewards.1.name", "<r:0.7>&lSpecial Stone");
            this.put("rewards.1.amount", 1);
            this.put("rewards.1.lore", List.of(
                    "&7This is a &lSpecial Stone"
            ));
            this.put("rewards.1.enchants.SILK_TOUCH", 1);
            this.put("rewards.1.chance", 50.0);
            this.put("rewards.1.actions", List.of(
                    "[GIVE]",
                    "[CONSOLE] eco give %player% 100",
                    "[MESSAGE] &lExample &7» &fYou have been given 100 coins & special stone!"
            ));
        }};
    }

    public Map<String, Crate> getCachedCrates() {
        return this.cachedCrates;
    }

    public Map<String, CommentedFileConfiguration> getUnregisteredCrates() {
        return this.unregisteredCrates;
    }

    public List<UUID> getActiveUsers() {
        return activeUsers;
    }
}
