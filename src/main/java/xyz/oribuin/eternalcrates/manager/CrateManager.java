package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.action.PluginAction;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateKeys;
import xyz.oribuin.eternalcrates.crate.CrateType;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalcrates.util.CrateUtils;
import xyz.oribuin.eternalcrates.util.ItemBuilder;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
        final File folder = new File(this.rosePlugin.getDataFolder(), "crates");
        if (!folder.exists()) {
            folder.mkdir();
            CrateUtils.createFile(this.rosePlugin, "crates", "example.yml");
        }

        this.loadCrates();
    }

    @Override
    public void disable() {
        this.cachedCrates.clear();
        this.activeUsers.clear();
        this.unregisteredCrates.clear();
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
            CrateUtils.createFile(this.rosePlugin, "crates", "example.yml");
            files = folder.listFiles(); // Try again
        }

        if (files == null)
            return;

        Arrays.stream(files).filter(file -> file.getName().toLowerCase().endsWith(".yml"))
                .forEach(file -> {
                    final CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);

                    if (config.get("crate-settings") == null)
                        return;

                    this.rosePlugin.getLogger().info("Attempting to load crate " + file.getName());

                    final Crate crate = this.createCreate(file, config);
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
    @Nullable
    public Crate getCrate(String id) {
        return this.cachedCrates.get(id);
    }

    /**
     * Get a cached crate from the location of it.
     *
     * @param location The location of the crate
     * @return The optional crate.
     */
    @Nullable
    public Crate getCrate(Location location) {
        return this.getCachedCrates().values()
                .stream()
                .filter(crate -> crate.getLocations().contains(location))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a cached crate from the type of it.
     *
     * @param type The type of the crate.
     * @return The list of crates.
     */
    @NotNull
    public List<Crate> getCratesByType(CrateType type) {
        return this.getCachedCrates().values()
                .stream()
                .filter(crate -> crate.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Get all virtual or unclaimed crate keys.
     *
     * @param uuid The uuid of the player.
     * @return The list of crate keys.
     */
    @NotNull
    public Map<String, Integer> getUserKeys(UUID uuid) {
        return this.rosePlugin.getManager(DataManager.class)
                .getUser(uuid)
                .getKeys();
    }

    /**
     * Check if a crate is already at a location.
     *
     * @param location The location to check.
     * @return True if there is a crate at the location.
     */
    public boolean hasCrateAtLocation(Location location) {
        return this.getCachedCrates().values()
                .stream()
                .anyMatch(crate -> crate.getLocations().contains(location));
    }

    /**
     * Set a player's total crate keys. This will overwrite any existing keys.
     *
     * @param uuid The uuid of the player.
     * @param keys The map of crate keys.
     */
    public void saveUserKeys(UUID uuid, Map<String, Integer> keys) {
        this.rosePlugin.getManager(DataManager.class).saveUser(uuid, new CrateKeys(keys));
    }

    /**
     * Create a creation from a file configuration, Seriously avert your eyes holy shit
     *
     * @param config The config the crate is being created from
     * @return The crate.
     */
    public Crate createCreate(final File file, final CommentedFileConfiguration config) {
        final AnimationManager animationManager = this.rosePlugin.getManager(AnimationManager.class);

        // Get the crate id
        final String name = config.getString("crate-settings.name");
        if (name == null) {
            this.rosePlugin.getLogger().warning("Failed to load crate because it does not have a name.");
            return null;
        }

        // Get the crate display name
        String displayName = config.getString("crate-settings.display-name");
        if (displayName == null)
            displayName = name;

        // Get the crate animation data.
        Animation animation = animationManager.getAnimation(config);
        if (animation == null) {
            this.rosePlugin.getLogger().warning("Couldn't load animation for crate: " + name);
            this.unregisteredCrates.put(name.toLowerCase(), config);
            return null;
        }

        // Try to create a new instance of the animation class.
        try {
            animation = animation.getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }

        // Get the crate type
        String crateTypeName = config.getString("crate-settings.type");
        CrateType crateType = Arrays.stream(CrateType.values())
                .filter(x -> x.name().equalsIgnoreCase(crateTypeName))
                .findFirst()
                .orElse(null);

        if (crateType == null) {
            this.rosePlugin.getLogger().warning("Failed to load crate type for crate: " + name + " because [" + crateTypeName + "] is not a valid crate type, defaulting to PHYSICAL");
            crateType = CrateType.PHYSICAL;
        }


        // Check if the crate is a virtual crate and the animation is a physical crate animation
        if (crateType == CrateType.VIRTUAL && !animation.canBeVirtual()) {
            this.rosePlugin.getLogger().warning("Cannot load crate " + name + ", Animation does not support virtual crates.");
            return null;
        }

        // Load all the rewards for the crate
        final Map<String, Reward> rewards = new HashMap<>();
        final CommentedConfigurationSection section = config.getConfigurationSection("crate-settings.rewards");
        if (section == null) {
            this.rosePlugin.getLogger().warning("Failed to load crate " + name + " because it does not have any rewards.");
            return null;
        }

        // Load the reward data from the config
        section.getKeys(false).forEach(s -> {

            // Base itemstack
            final ItemStack item = CrateUtils.getItemStack(section, s);
            if (item == null)
                return;

            // Get the chance of the reward
            final Reward reward = new Reward(s, item, section.getDouble(s + ".chance"));

            // Get the preview item for the reward if it exists
            if (section.get(s + ".preview-item") != null) {
                ItemStack previewItem = CrateUtils.getItemStack(section, section.getCurrentPath() + "." + s + ".preview-item");
                if (previewItem != null)
                    reward.setPreviewItem(previewItem);
            }

            // Get the commands for the reward if it exists
            final List<String> actionSection = section.getStringList(s + ".actions");
            actionSection.stream().map(PluginAction::parse)
                    .filter(Objects::nonNull)
                    .forEach(action -> reward.getActions().add(action));

            // Add the reward to the map
            rewards.put(reward.getId(), reward);
        });

        // Get all the crate actions when it's opened by a player.
        List<Action> openActions = config.getStringList("crate-settings.open-actions")
                .stream()
                .map(PluginAction::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final Crate crate = new Crate(name);
        final DataManager data = this.rosePlugin.getManager(DataManager.class);

        // Get the physical crate key data
        if (crate.getType() == CrateType.PHYSICAL) {
            ItemStack item = new ItemBuilder(CrateUtils.getItemStack(config, "crate-settings.key"))
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
        }

        // Add crate animation settings to plugin config.

        Animation finalAnimation = animation;
        CommentedConfigurationSection animationSection = config.getConfigurationSection("crate-settings.animation");
        if (animationSection == null) return null;

        finalAnimation.getRequiredValues().forEach((path, object) -> {
            // All other animations have default values, so we need to add them to the config if they don't exist.
            if (animationSection.get(path) == null) {
                animationSection.set(path, object);
            }
        });

        finalAnimation.load(animationSection);
        config.save(file);

        // Set the crate data
        crate.setAnimation(finalAnimation);
        crate.setName(displayName);
        crate.setRewardMap(rewards);
        crate.setMaxRewards(Math.max(config.getInt("crate-settings.max-rewards", 1), 1));
        crate.setMinRewards(Math.min(config.getInt("crate-settings.min-rewards", 1), crate.getMaxRewards()));
        crate.setMultiplier(Math.min(config.getInt("crate-settings.multiplier", 1), 1));
        crate.setMinGuiSlots(Math.max(config.getInt("crate-settings.min-inv-slots", crate.getMaxRewards()), crate.getMaxRewards()));
        crate.setConfig(config);
        crate.setFile(file);
        crate.setOpenActions(openActions);
        crate.setType(crateType);
        data.loadCrateLocation(crate);

        this.rosePlugin.getLogger().info("Registered Crate: " + crate.getId() + " with " + crate.getRewardMap().size() + " rewards!");
        return crate;
    }

    /**
     * Get crate from a block
     *
     * @param block The block to get the crate from
     */
    public Crate getCrate(final Block block) {
        if (block == null)
            return null;

        return this.cachedCrates.values().stream()
                .filter(crate -> crate.getLocations().contains(block.getLocation()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Save crate values to a CommentedFileConfiguration from the crate object
     *
     * @param crate The crate to save
     */
    public void saveCrate(Crate crate, File file) {
        this.cachedCrates.put(crate.getId().toLowerCase(), crate);
        final CommentedFileConfiguration config = crate.getConfig();
        if (config == null)
            return;

        // Save all the general crate settings
        config.set("crate-settings.name", crate.getId());
        config.set("crate-settings.display-name", crate.getName());
        config.set("crate-settings.crate-type", crate.getType().name());
        config.set("crate-settings.max-rewards", crate.getMaxRewards());
        config.set("crate-settings.min-rewards", crate.getMinRewards());
        config.set("crate-settings.multiplier", crate.getMultiplier());
        config.set("crate-settings.min-inv-slots", crate.getMinGuiSlots());
        config.set("crate-settings.animation.name", crate.getAnimation().getName());
        crate.getAnimation().getRequiredValues().forEach((s, o) -> config.set("crate-settings.animation." + s, o));

        config.save(file);
        this.rosePlugin.getManager(DataManager.class).saveCrateLocations(crate);
    }

    /**
     * Give a player a virtual crate key
     *
     * @param player The player
     * @param crate  The crate
     * @param amount The amount of keys to give
     */
    public void giveVirtualKey(Player player, Crate crate, int amount) {
        final DataManager data = this.rosePlugin.getManager(DataManager.class);
        final int newAmount = Math.max(amount, 1);

        data.addKeys(player.getUniqueId(), crate.getId().toLowerCase(), newAmount);

        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
                .add("amount", newAmount)
                .build();

        this.rosePlugin.getManager(LocaleManager.class).sendMessage(player, "command-give-success-other", placeholders);
    }

    /**
     * Use a virtual crate key on a crate
     *
     * @param crate    The crate to use the key on
     * @param player   The player to use the key on
     * @param location The location to use the key on
     */
    public void useVirtualKey(Crate crate, Player player, Location location) {
        final DataManager data = this.rosePlugin.getManager(DataManager.class);
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        Map<String, Integer> usersKeys = data.getUser(player.getUniqueId()).getKeys();
        Integer totalKeys = usersKeys.getOrDefault(crate.getId().toLowerCase(), 0);

        if (totalKeys <= 0) {
            locale.sendMessage(player, "crate-open-no-keys");
            this.sendPlayerBackwards(player);
            return;
        }

        // Remove a key from the user
        if (crate.open(player, location)) {
            usersKeys.put(crate.getId().toLowerCase(), totalKeys - 1);
            data.saveUser(player.getUniqueId(), new CrateKeys(usersKeys));
        }

    }

    /**
     * Give a player the physical key for a crate
     *
     * @param player The player to give the key to
     * @param crate  The crate to give the key to
     */
    public void givePhysicalKey(Player player, Crate crate, int amount) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        final int newAmount = Math.max(amount, 1);
        final int maxAmount = crate.getKey().getMaxStackSize();

        // TODO: Rework this whole method, it's a mess.
        ItemStack key = crate.getKey().clone();

        // Player has no space in their inventory, send key to unclaimed.
        if (player.getInventory().firstEmpty() == -1) {
            this.giveVirtualKey(player, crate, newAmount);

            final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
                    .add("amount", newAmount)
                    .build();

            this.rosePlugin.getManager(DataManager.class).addKeys(player.getUniqueId(), crate.getId().toLowerCase(), newAmount);
            locale.sendMessage(player, "command-give-full-inventory", placeholders);
            return;
        }

        // Player has space in their inventory, give them the key.
        key.setAmount(Math.min(newAmount, maxAmount));
        player.getInventory().addItem(key);

        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
                .add("amount", newAmount)
                .build();

        locale.sendMessage(player, "command-give-success", placeholders);
    }

    /**
     * Use a physical crate key on a crate
     *
     * @param player   The player to use the key on
     * @param crate    The crate to use the key on
     * @param item     The item to use the key on
     * @param location The location to use the key on
     */
    public void usePhysicalKey(Player player, Crate crate, @Nullable ItemStack item, Location location) {
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        if (item == null) {
            locale.sendMessage(player, "crate-open-invalid-key");
            this.sendPlayerBackwards(player);
            return;
        }

        var meta = item.getItemMeta();
        if (meta == null) {
            locale.sendMessage(player, "crate-open-invalid-key");
            this.sendPlayerBackwards(player);
            return;
        }

        var key = new NamespacedKey(this.rosePlugin, "crateKey");
        var container = meta.getPersistentDataContainer();

        if (!container.has(key, PersistentDataType.STRING)) {
            locale.sendMessage(player, "crate-open-invalid-key");
            this.sendPlayerBackwards(player);
            return;
        }

        final var keyId = container.get(key, PersistentDataType.STRING);
        if (keyId == null || !keyId.equalsIgnoreCase(crate.getId())) {
            locale.sendMessage(player, "crate-open-invalid-key");
            this.sendPlayerBackwards(player);
            return;
        }

        if (crate.open(player, location)) {
            // remove 1 of the Item from the player inventory
            if (item.getAmount() == 1)
                player.getInventory().setItemInMainHand(null);
            else
                item.setAmount(item.getAmount() - 1);
        }

    }

    /**
     * Send the player backwards
     *
     * @param player The player to send backwards
     */
    public void sendPlayerBackwards(Player player) {
        if (!Setting.NO_KEY_VELOCITY.getBoolean())
            return;

        var vector = player.getLocation().getDirection().clone();
        vector = vector.clone().multiply(-1);
        vector.multiply(1);
        player.setVelocity(vector);
    }

    /**
     * Gets the crate default configuration options.
     *
     * @return The configuration options.
     */
    public Map<String, Object> getDefaultCrateValues() {
        return new LinkedHashMap<>() {{
            // General Crate Settings
            this.put("#0", "Change the general settings for the crate.");
            this.put("#1", "name - The name of the crate.");
            this.put("#2", "display-name - The display name of the crate.");
            this.put("#3", "type - The type of crate, one uses physical items, the other doesn't [VIRTUAL, PHYSICAL]");
            this.put("#4", "max-rewards - The maximum amount of rewards a player can get from the crate.");
            this.put("#5", "min-rewards - The minimum amount of rewards a player can get from the crate.");
            this.put("#6", "multiplier - The multiplier for the rewards.");
            this.put("#7", "min-inv-slots - The minimum amount of slots in the GUI.");
            this.put("#8", "open-actions - The actions to perform when the crate is opened.");
            this.put("#9", " ");

            // The options for the crate key if the crate type is physical
            this.put("#10", "The animation settings for the crate.");
            this.put("#11", "Each crate animation has individual settings which will be automatically applied to the config file");
            this.put("#12", "animation name - The name of the animation to use.");
            this.put("#13", " ");

            // Change the item for the crate key
            this.put("#14", "key - The item to use as a key for the crate.");
            this.put("#15", " ");

            // Alternative options for the crate rewards
            this.put("#16", "The change the rewards for the crate.");
            this.put("#17", "These are the alternative options for the rewards.");
            this.put("#18", "plugin - The item from a plugin to use as a reward. (e.g. slimefun:portable_crafter)");
            this.put("#19", "preview-item - The reward item to use in the GUI.");
            this.put("#20", " ");

            // General options to configure the rewards.
            this.put("#21", "The general options for the customising itemstacks.");
            this.put("#22", " ");
            this.put("#23", "material - The material of the reward.");
            this.put("#24", "amount - The amount of the reward.");
            this.put("#25", "chance - The chance of the reward.");
            this.put("#26", "name - The name of the reward.");
            this.put("#27", "lore - The lore of the reward.");
            this.put("#28", "glow - Whether the reward item should glow.");
            this.put("#29", "texture - The base64 texture of the reward item (Only for skulls)");
            this.put("#30", "potion-color - The color of the potion reward. (Only for potions)");
            this.put("#31", "model-data - The model data of the reward item. (Requires texture packs)");
            this.put("#32", "owner - The uuid of the player for the reward item (Only for skulls)");
            this.put("#33", "flags - The item flags for the reward item.");
            this.put("#34", "enchants - The enchantments for the reward item.");
            this.put("#35", "actions - The actions to perform when the reward is received.");
            this.put("#36", " ");

            // All actions for the crate rewards
            this.put("#37", "All available actions can be found here:");
            this.put("#38", "[BROADCAST] Text - Broadcasts a message to the server.");
            this.put("#39", "[CLOSE] - Closes the GUI.");
            this.put("#40", "[CONSOLE] Command - Executes a command in the console.");
            this.put("#41", "[GIVE] - Gives the player the reward item.");
            this.put("#42", "[MESSAGE] Text - Sends a message to the player.");
            this.put("#43", "[PLAYER] Command - Executes a command as the player.");
            this.put("#44", "[SOUND] ENTITY_ARROW_HIT_PLAYER - Plays a sound to the player.");
            this.put("#46", "");

            // General Crate Settings
            this.put("crate-settings.name", "example");
            this.put("crate-settings.display-name", "Example Crate");
            this.put("crate-settings.type", "PHYSICAL");

            // Global Crate Rewards Settings
            this.put("crate-settings.max-rewards", 1);
            this.put("crate-settings.min-rewards", 1);
            this.put("crate-settings.min-inv-slots", 1);
            this.put("crate-settings.multiplier", 1);

            // Configure crate animation settings
            this.put("crate-settings.animation.name", "rings");

            // Configure crate key settings
            this.put("crate-settings.key.material", "TRIPWIRE_HOOK");
            this.put("crate-settings.key.glow", true);
            this.put("crate-settings.key.name", "#00B4DB&l&lCrate Key &7» &fExample");
            this.put("crate-settings.key.lore", Arrays.asList(
                    "&7A key to open the #00B4DB&lExample &7crate!",
                    "",
                    "&7Right-Click on the #00B4DB&lExample &7crate to open"
            ));

            // Reward Settings
            this.put("crate-settings.rewards.1.material", "STONE");
            this.put("crate-settings.rewards.1.name", "<r:0.7>&lSpecial Stone");
            this.put("crate-settings.rewards.1.amount", 1);
            this.put("crate-settings.rewards.1.lore", List.of(
                    "&7This is a &lSpecial Stone"
            ));
            this.put("crate-settings.rewards.1.enchants.SILK_TOUCH", 1);
            this.put("crate-settings.rewards.1.chance", 50.0);
            this.put("crate-settings.rewards.1.actions", List.of(
                    "[GIVE]",
                    "[CONSOLE] eco give %player% 100",
                    "[MESSAGE] #00B4DB&l&lExample &7» &fYou have been given 100 coins & special stone!"
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
