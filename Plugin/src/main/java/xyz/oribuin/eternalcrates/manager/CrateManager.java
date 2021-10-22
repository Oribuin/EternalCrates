package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.action.*;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class CrateManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final AnimationManager animationManager = this.plugin.getManager(AnimationManager.class);

    private final Map<String, Crate> cachedCrates = new HashMap<>();
    private final Map<String, FileConfiguration> unregisteredCrates = new HashMap<>();

    public CrateManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the crates from the plugin.");

        this.cachedCrates.clear();
        final File folder = new File(this.plugin.getDataFolder(), "crates");
        if (!folder.exists()) {
            folder.mkdir();
            PluginUtils.createDefaultFiles(plugin);
        }

        File[] files = folder.listFiles();

        if (files == null)
            return;

        Arrays.stream(files).filter(file -> file.getName().toLowerCase().endsWith(".yml"))
                .forEach(file -> {
                    final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    final Crate crate = this.createCreate(config);
                    if (crate == null)
                        return;

                    this.cachedCrates.put(crate.getId().toLowerCase(), crate);
                });
    }

    /**
     * Get a crate from the id of the crate.
     *
     * @param id The ID of the crate.
     * @return An Optional Crate
     */
    public Optional<Crate> getCrate(String id) {
        return this.cachedCrates.values()
                .stream()
                .filter(crate -> crate.getId().equalsIgnoreCase(id))
                .findFirst();
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
                .filter(crate -> crate.getLocation() != null)
                .filter(crate -> crate.getLocation().equals(location))
                .findFirst();
    }

    /**
     * Create a creation from a file configuration, Seriously avert your eyes holy shit
     *
     * @param config The config the crate is being created from
     * @return The crate.
     */
    public Crate createCreate(final FileConfiguration config) {

        final Optional<String> name = Optional.ofNullable(config.getString("name"));
        if (name.isEmpty())
            return null;

        final Optional<String> displayName = Optional.ofNullable(config.getString("display-name"));
        if (displayName.isEmpty())
            return null;

        // this line isn't dumb
        final Optional<? extends Animation> animation = animationManager.getAnimationFromConfig(config);
        if (animation.isEmpty()) {
//            this.plugin.getLogger().warning("Unable to register crate " + name.get());
            this.unregisteredCrates.put(name.get().toLowerCase(), config);
            return null;
        }

        final Map<Reward, Double> rewards = new HashMap<>();
        final ConfigurationSection section = config.getConfigurationSection("rewards");
        if (section == null)
            return null;

        final AtomicInteger id = new AtomicInteger(0);
        section.getKeys(false).forEach(s -> {
            final ItemStack item = this.animationManager.itemFromConfig(config, section.getCurrentPath() + "." + s);
            if (item == null)
                return;

            final Reward reward = new Reward(id.getAndIncrement());
            reward.setDisplayItem(item);
            reward.setChance(section.getInt(s + ".chance"));
            // section inception
            // holup that rhymes
            final List<String> actionSection = section.getStringList(s + ".actions");
            actionSection.forEach(i -> {

                final List<Action> actions = Arrays.asList(
                        new BroadcastAction(),
                        new CloseAction(),
                        new ConsoleAction(),
                        new MessageAction(),
                        new PlayerAction(),
                        new SoundAction()
                );

                Optional<Action> optional = actions.stream().filter(x -> i.toLowerCase().startsWith("[" + x.actionType().toLowerCase() + "]")).findFirst();

                if (optional.isEmpty())
                    return;

                Action action = optional.get();
                final String formattedAction = "[" + action.actionType().toLowerCase() + "]";
                String actionMessage = i.substring(formattedAction.length());

                // yes this is scuffed
                while (actionMessage.startsWith(" "))
                    actionMessage = actionMessage.substring(1);

                action.setMessage(actionMessage);
                reward.getActions().add(action);

            });

            rewards.put(reward, reward.getChance());
        });


        final Crate crate = new Crate(name.get());
        crate.setAnimation(animation.get());
        crate.setDisplayName(displayName.get());
        crate.setRewardMap(rewards);
        crate.setMaxRewards(Math.max(PluginUtils.get(config, "max-rewards", 1), 1));
        crate.setMinGuiSlots(Math.max(PluginUtils.get(config, "min-inv-slots", crate.getMaxRewards()), crate.getMaxRewards()));
        crate.setConfig(config);
        ItemStack item = new Item.Builder(animationManager.itemFromConfig(config, "key"))
                .setNBT(plugin, "crateKey", crate.getId().toLowerCase())
                .create();

        if (item == null) {
            item = new Item.Builder(Material.TRIPWIRE_HOOK)
                    .setName(HexUtils.colorify("#99ff99&lCrate Key &7» &f" + crate.getId()))
                    .glow(true)
                    .setLore(HexUtils.colorify("&7A key to open the " + crate.getId().toLowerCase() + " crate!"),
                            "",
                            HexUtils.colorify("&7Right-Click on the #99ff99&l" + crate.getId() + " &7crate to open")
                    )
                    .create();
        }
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;
        final PersistentDataContainer cont = meta.getPersistentDataContainer();
        cont.set(new NamespacedKey(plugin, "crateKey"), PersistentDataType.STRING, crate.getId().toLowerCase());
        item.setItemMeta(meta);
        crate.setKey(item);
        this.plugin.getLogger().info("Registered Crate: " + crate.getId() + " with " + crate.getRewardMap().size() + " rewards!");
        return crate;
    }


    @Override
    public void disable() {
        this.cachedCrates.clear();
    }

    /**
     * Get a value from a configuration file.
     *
     * @param config The file configuration
     * @param path   The path to the option.
     * @param def    The default value for the option.
     * @return The config option or the default.
     */
    public <T> T get(FileConfiguration config, String path, T def) {
        return config.get(path) != null ? (T) config.get(path) : def;
    }

    /**
     * Get a value from a configuration section.
     *
     * @param section The configuration section
     * @param path    The path to the option.
     * @param def     The default value for the option.
     * @return The config option or the default.
     */
    public <T> T get(ConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }

    public Map<String, Crate> getCachedCrates() {
        return cachedCrates;
    }

    public Map<String, FileConfiguration> getUnregisteredCrates() {
        return unregisteredCrates;
    }

}
