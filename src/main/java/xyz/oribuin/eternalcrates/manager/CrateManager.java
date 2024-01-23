package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.factory.AnimationFactory;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.KeyType;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.crate.RewardSettings;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class CrateManager extends Manager {

    private static final Logger LOGGER = Logger.getLogger("EternalCrates");

    private final List<UUID> activeUsers = new ArrayList<>();
    private final Map<String, Crate> cachedCrates = new HashMap<>();
    private final Map<File, String> unregisteredCrates = new HashMap<>();

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

        this.cachedCrates.clear();
        this.unregisteredCrates.clear();

        File[] files = folder.listFiles();
        if (files == null) {
            CrateUtils.createFile(this.rosePlugin, "crates", "example.yml");
            files = folder.listFiles(); // Try again
        }

        if (files == null) return;
        Arrays.stream(files)
                .filter(x -> x.getName().endsWith("yml"))
                .map(this::create)
                .filter(Objects::nonNull)
                .forEach(crate -> this.cachedCrates.put(crate.getId(), crate));
    }

    /**
     * Create a new crate from a file configuration
     *
     * @param file The file to load
     * @return The crate
     */
    private Crate create(File file) {
        CommentedConfigurationSection config = CommentedFileConfiguration.loadConfiguration(file);
        if (config.get("crate-settings") == null) return null;

        CommentedConfigurationSection crateSettings = config.getConfigurationSection("crate-settings");
        if (crateSettings == null) {
            LOGGER.severe("Failed to load crate because it does not have a crate-settings section.");
            return null;
        }

        String id = crateSettings.getString("name");
        if (id == null) {
            LOGGER.warning("Failed to load crate because it does not have a name.");
            return null;
        }

        Crate crate = new Crate(id);
        crate.setConfig(config);
        crate.setFile(file);
        crate.setName(crateSettings.getString("display-name", id));
        crate.setType(CrateUtils.getEnum(KeyType.class, crateSettings.getString("type"), KeyType.PHYSICAL));
        crate.setSettings(new RewardSettings(
                crateSettings.getInt("min-rewards", 1),
                crateSettings.getInt("max-rewards", 1),
                crateSettings.getInt("multiplier", 1),
                crateSettings.getInt("min-inv-slots", 1)
        ));

        // Load the crate animation
        String animationName = crateSettings.getString("animation.name");
        Animation animation = AnimationFactory.get().find(animationName);

        // Make sure the crate doesn't require blocks if it's a virtual crate
        if (crate.getType() == KeyType.VIRTUAL && animation != null && !animation.getRequiredBlocks().isEmpty()) {
            LOGGER.warning("Cannot load crate " + id + ", Animation does not support virtual crates, Switching animation to the default.");
            animation = AnimationFactory.get().find("default");
        }

        // Make sure the animation is actually loaded
        if (animation == null) {
            if (animationName != null)
                this.unregisteredCrates.put(file, animationName);
            return null;
        }

        // Load the crate key
        if (crate.getType() == KeyType.PHYSICAL) {
            crate.setKey(CrateUtils.deserialize(crateSettings, "key"));
        }

        // Load the crate rewards
        CommentedConfigurationSection rewardsSection = crateSettings.getConfigurationSection("rewards");
        if (rewardsSection == null) {
            LOGGER.warning("Failed to load crate " + id + " because it does not have any rewards.");
            return null;
        }

        Map<String, Reward> rewards = new HashMap<>();
        for (String key : rewardsSection.getKeys(false)) {
            double chance = rewardsSection.getDouble(key + ".chance");
            ItemStack item = CrateUtils.deserialize(rewardsSection, key);
            ItemStack previewItem = CrateUtils.deserialize(rewardsSection, key + ".preview-item");
            if (chance <= 0 || item == null) continue;

            Reward reward = new Reward(key, item, chance);
            reward.setPreviewItem(previewItem != null ? previewItem : item);
            reward.setActions(rewardsSection.getStringList(key + ".actions"));
            rewards.put(key, reward);
        }

        crate.setRewards(rewards);
        return crate;
    }

    @Override
    public void disable() {
        this.cachedCrates.clear();
        this.activeUsers.clear();
        this.unregisteredCrates.clear();
    }

//
//    /**
//     * Load all the plugin crates from the /EternalCrates/crates folder
//     * and cache them
//     */
//    public void loadCrates() {
//        this.cachedCrates.clear();
//        this.rosePlugin.getLogger().info("Loading all crates from the /EternalCrates/crates folder");
//        final File folder = new File(this.rosePlugin.getDataFolder(), "crates");
//        File[] files = folder.listFiles();
//        if (files == null) {
//            CrateUtils.createFile(this.rosePlugin, "crates", "example.yml");
//            files = folder.listFiles(); // Try again
//        }
//
//        if (files == null)
//            return;
//
//        Arrays.stream(files).filter(file -> file.getName().toLowerCase().endsWith(".yml"))
//                .forEach(file -> {
//                    final CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
//
//                    if (config.get("crate-settings") == null)
//                        return;
//
//                    this.rosePlugin.getLogger().info("Attempting to load crate " + file.getName());
//
//                    final Crate crate = this.createCreate(file, config);
//                    if (crate == null)
//                        return;
//
//                    this.cachedCrates.put(crate.getId(), crate);
//                });
//    }
//
//    /**
//     * Get a crate from the id of the crate.
//     *
//     * @param id The ID of the crate.
//     * @return An Optional Crate
//     */
//    @Nullable
//    public Crate getCrate(String id) {
//        return this.cachedCrates.get(id);
//    }
//
//    /**
//     * Get a cached crate from the location of it.
//     *
//     * @param location The location of the crate
//     * @return The optional crate.
//     */
//    @Nullable
//    public Crate getCrate(Location location) {
//        return this.getCachedCrates().values()
//                .stream()
//                .filter(crate -> crate.getLocations().contains(location))
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Get a cached crate from the type of it.
//     *
//     * @param type The type of the crate.
//     * @return The list of crates.
//     */
//    @NotNull
//    public List<Crate> getCratesByType(CrateType type) {
//        return this.getCachedCrates().values()
//                .stream()
//                .filter(crate -> crate.getType() == type)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Check if a crate is already at a location.
//     *
//     * @param location The location to check.
//     * @return True if there is a crate at the location.
//     */
//    public boolean hasCrateAtLocation(Location location) {
//        return this.getCachedCrates().values()
//                .stream()
//                .anyMatch(crate -> crate.getLocations().contains(location));
//    }
//
//    /**
//     * Create a creation from a file configuration, Seriously avert your eyes holy shit
//     *
//     * @param config The config the crate is being created from
//     * @return The crate.
//     */
//    public Crate createCreate(final File file, final CommentedFileConfiguration config) {
//        final AnimationManager animationManager = this.rosePlugin.getManager(AnimationManager.class);
//
//        // Get the crate id
//        final String name = config.getString("crate-settings.name");
//        if (name == null) {
//            this.rosePlugin.getLogger().warning("Failed to load crate because it does not have a name.");
//            return null;
//        }
//
//        // Get the crate display name
//        String displayName = config.getString("crate-settings.display-name");
//        if (displayName == null)
//            displayName = name;
//
//        // Get the crate animation data.
//        Animation animation = animationManager.getAnimation(config);
//        if (animation == null) {
//            this.rosePlugin.getLogger().warning("Couldn't load animation for crate: " + name);
//            this.unregisteredCrates.put(name.toLowerCase(), config);
//            return null;
//        }
//
//        // Try to create a new instance of the animation class.
//        try {
//            animation = animation.getClass().getConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
//        }
//
//        // Get the crate type
//        String crateTypeName = config.getString("crate-settings.type");
//        CrateType crateType = Arrays.stream(CrateType.values())
//                .filter(x -> x.name().equalsIgnoreCase(crateTypeName))
//                .findFirst()
//                .orElse(null);
//
//        if (crateType == null) {
//            this.rosePlugin.getLogger().warning("Failed to load crate type for crate: " + name + " because [" + crateTypeName + "] is not a valid crate type, defaulting to PHYSICAL");
//            crateType = CrateType.PHYSICAL;
//        }
//
//
//        // Check if the crate is a virtual crate and the animation is a physical crate animation
//        if (crateType == CrateType.VIRTUAL && !animation.canBeVirtual()) {
//            this.rosePlugin.getLogger().warning("Cannot load crate " + name + ", Animation does not support virtual crates.");
//            return null;
//        }
//
//        // Load all the rewards for the crate
//        final Map<String, Reward> rewards = new HashMap<>();
//        final CommentedConfigurationSection section = config.getConfigurationSection("crate-settings.rewards");
//        if (section == null) {
//            this.rosePlugin.getLogger().warning("Failed to load crate " + name + " because it does not have any rewards.");
//            return null;
//        }
//
//        // Load the reward data from the config
//        section.getKeys(false).forEach(s -> {
//
//            // Base itemstack
//            final ItemStack item = CrateUtils.getItemStack(section, s);
//            if (item == null)
//                return;
//
//            // Get the chance of the reward
//            final Reward reward = new Reward(s, item, section.getDouble(s + ".chance"));
//
//            // Get the preview item for the reward if it exists
//            if (section.get(s + ".preview-item") != null) {
//                ItemStack previewItem = CrateUtils.getItemStack(section, section.getCurrentPath() + "." + s + ".preview-item");
//                if (previewItem != null)
//                    reward.setPreviewItem(previewItem);
//            }
//
//            // Get the commands for the reward if it exists
//            final List<String> actionSection = section.getStringList(s + ".actions");
//            actionSection.stream().map(PluginAction::parse)
//                    .filter(Objects::nonNull)
//                    .forEach(action -> reward.getActions().add(action));
//
//            // Add the reward to the map
//            rewards.put(reward.getId(), reward);
//        });
//
//        // Get all the crate actions when it's opened by a player.
//        List<Action> openActions = config.getStringList("crate-settings.open-actions")
//                .stream()
//                .map(PluginAction::parse)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        final Crate crate = new Crate(name);
//        final DataManager data = this.rosePlugin.getManager(DataManager.class);
//
//        // Get the physical crate key data
//        if (crate.getType() == CrateType.PHYSICAL) {
//            ItemStack item = new ItemBuilder(CrateUtils.getItemStack(config, "crate-settings.key"))
//                    .setNBT(rosePlugin, "crateKey", crate.getId().toLowerCase())
//                    .create();
//
//            if (item == null) {
//                item = new ItemBuilder(Material.TRIPWIRE_HOOK)
//                        .setName(HexUtils.colorify("#00B4DB&lCrate Key &7» &f" + crate.getId()))
//                        .glow(true)
//                        .setLore(HexUtils.colorify("&7A key to open the " + crate.getId().toLowerCase() + " crate!"),
//                                "",
//                                HexUtils.colorify("&7Right-Click on the #00B4DB&l" + crate.getId() + " &7crate to open")
//                        )
//                        .setNBT(this.rosePlugin, "crateKey", crate.getId().toLowerCase())
//                        .create();
//            }
//
//            crate.setKey(item);
//        }
//
//        // Add crate animation settings to plugin config.
//
//        Animation finalAnimation = animation;
//        CommentedConfigurationSection animationSection = config.getConfigurationSection("crate-settings.animation");
//        if (animationSection == null) return null;
//
//        finalAnimation.getRequiredValues().forEach((path, object) -> {
//            // All other animations have default values, so we need to add them to the config if they don't exist.
//            if (animationSection.get(path) == null) {
//                animationSection.set(path, object);
//            }
//        });
//
//        finalAnimation.load(animationSection);
//        config.save(file);
//
//        // Set the crate data
//        crate.setAnimation(finalAnimation);
//        crate.setName(displayName);
//        crate.setRewardMap(rewards);
//        crate.setMaxRewards(Math.max(config.getInt("crate-settings.max-rewards", 1), 1));
//        crate.setMinRewards(Math.min(config.getInt("crate-settings.min-rewards", 1), crate.getMaxRewards()));
//        crate.setMultiplier(Math.min(config.getInt("crate-settings.multiplier", 1), 1));
//        crate.setMinGuiSlots(Math.max(config.getInt("crate-settings.min-inv-slots", crate.getMaxRewards()), crate.getMaxRewards()));
//        crate.setConfig(config);
//        crate.setFile(file);
//        crate.setOpenActions(openActions);
//        crate.setType(crateType);
//        data.getLocations(crate).thenAc
//    cept(crate::setLocations);
//
//        this.rosePlugin.getLogger().info("Registered Crate: " + crate.getId() + " with " + crate.getRewardMap().size() + " rewards!");
//        return crate;
//    }
//
//    /**
//     * Get crate from a block
//     *
//     * @param block The block to get the crate from
//     */
//    public Crate getCrate(final Block block) {
//        if (block == null)
//            return null;
//
//        return this.cachedCrates.values().stream()
//                .filter(crate -> crate.getLocations().contains(block.getLocation()))
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Save crate values to a CommentedFileConfiguration from the crate object
//     *
//     * @param crate The crate to save
//     */
//    public void saveCrate(Crate crate, File file) {
//        this.cachedCrates.put(crate.getId().toLowerCase(), crate);
//        final CommentedFileConfiguration config = crate.getConfig();
//        if (config == null)
//            return;
//
//        // Save all the general crate settings
//        config.set("crate-settings.name", crate.getId());
//        config.set("crate-settings.display-name", crate.getName());
//        config.set("crate-settings.crate-type", crate.getType().name());
//        config.set("crate-settings.max-rewards", crate.getMaxRewards());
//        config.set("crate-settings.min-rewards", crate.getMinRewards());
//        config.set("crate-settings.multiplier", crate.getMultiplier());
//        config.set("crate-settings.min-inv-slots", crate.getMinGuiSlots());
//        config.set("crate-settings.animation.name", crate.getAnimation().getName());
//        crate.getAnimation().getRequiredValues().forEach((s, o) -> config.set("crate-settings.animation." + s, o));
//
//        config.save(file);
//        this.rosePlugin.getManager(DataManager.class).saveCrateLocations(crate);
//    }
//
//    /**
//     * Give a player a virtual crate key
//     *
//     * @param player The player
//     * @param crate  The crate
//     * @param amount The amount of keys to give
//     */
//    public void giveVirtualKey(Player player, Crate crate, int amount) {
//        final DataManager data = this.rosePlugin.getManager(DataManager.class);
//        final int newAmount = Math.max(amount, 1);
//
//        data.addKeys(player.getUniqueId(), crate.getId().toLowerCase(), newAmount);
//
//        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
//                .add("amount", newAmount)
//                .build();
//
//        this.rosePlugin.getManager(LocaleManager.class).sendMessage(player, "command-give-success-other", placeholders);
//    }
//
//    /**
//     * Use a virtual crate key on a crate
//     *
//     * @param crate    The crate to use the key on
//     * @param player   The player to use the key on
//     * @param location The location to use the key on
//     */
//    public void useVirtualKey(Crate crate, Player player, Location location) {
//        final DataManager data = this.rosePlugin.getManager(DataManager.class);
//        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
//
//        Map<String, Integer> usersKeys = data.user(player.getUniqueId()).getKeys();
//        Integer totalKeys = usersKeys.getOrDefault(crate.getId().toLowerCase(), 0);
//
//        if (totalKeys <= 0) {
//            locale.sendMessage(player, "crate-open-no-keys");
//            this.sendPlayerBackwards(player);
//            return;
//        }
//
//        // Remove a key from the user
//        if (crate.open(player, location)) {
//            usersKeys.put(crate.getId().toLowerCase(), totalKeys - 1);
//            data.saveUser(player.getUniqueId(), new CrateKeys(usersKeys));
//        }
//
//    }
//
//    /**
//     * Give a player the physical key for a crate
//     *
//     * @param player The player to give the key to
//     * @param crate  The crate to give the key to
//     */
//    public void givePhysicalKey(Player player, Crate crate, int amount) {
//        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
//        final int newAmount = Math.max(amount, 1);
//        final int maxAmount = crate.getKey().getMaxStackSize();
//
//        // TODO: Rework this whole method, it's a mess.
//        ItemStack key = crate.getKey().clone();
//
//        // Player has no space in their inventory, send key to unclaimed.
//        if (player.getInventory().firstEmpty() == -1) {
//            this.giveVirtualKey(player, crate, newAmount);
//
//            final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
//                    .add("amount", newAmount)
//                    .build();
//
//            this.rosePlugin.getManager(DataManager.class).addKeys(player.getUniqueId(), crate.getId().toLowerCase(), newAmount);
//            locale.sendMessage(player, "command-give-full-inventory", placeholders);
//            return;
//        }
//
//        // Player has space in their inventory, give them the key.
//        key.setAmount(Math.min(newAmount, maxAmount));
//        player.getInventory().addItem(key);
//
//        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getId())
//                .add("amount", newAmount)
//                .build();
//
//        locale.sendMessage(player, "command-give-success", placeholders);
//    }
//
//    /**
//     * Use a physical crate key on a crate
//     *
//     * @param player   The player to use the key on
//     * @param crate    The crate to use the key on
//     * @param item     The item to use the key on
//     * @param location The location to use the key on
//     */
//    public void usePhysicalKey(Player player, Crate crate, @Nullable ItemStack item, Location location) {
//        final var locale = this.rosePlugin.getManager(LocaleManager.class);
//
//        if (item == null) {
//            locale.sendMessage(player, "crate-open-invalid-key");
//            this.sendPlayerBackwards(player);
//            return;
//        }
//
//        var meta = item.getItemMeta();
//        if (meta == null) {
//            locale.sendMessage(player, "crate-open-invalid-key");
//            this.sendPlayerBackwards(player);
//            return;
//        }
//
//        var key = new NamespacedKey(this.rosePlugin, "crateKey");
//        var container = meta.getPersistentDataContainer();
//
//        if (!container.has(key, PersistentDataType.STRING)) {
//            locale.sendMessage(player, "crate-open-invalid-key");
//            this.sendPlayerBackwards(player);
//            return;
//        }
//
//        final var keyId = container.get(key, PersistentDataType.STRING);
//        if (keyId == null || !keyId.equalsIgnoreCase(crate.getId())) {
//            locale.sendMessage(player, "crate-open-invalid-key");
//            this.sendPlayerBackwards(player);
//            return;
//        }
//
//        if (crate.open(player, location)) {
//            // remove 1 of the Item from the player inventory
//            if (item.getAmount() == 1)
//                player.getInventory().setItemInMainHand(null);
//            else
//                item.setAmount(item.getAmount() - 1);
//        }
//
//    }
//
//    /**
//     * Send the player backwards
//     *
//     * @param player The player to send backwards
//     */
//    public void sendPlayerBackwards(Player player) {
//        if (!Setting.NO_KEY_VELOCITY.getBoolean())
//            return;
//
//        var vector = player.getLocation().getDirection().clone();
//        vector = vector.clone().multiply(-1);
//        vector.multiply(1);
//        player.setVelocity(vector);
//    }
//
//    public Map<String, Crate> getCachedCrates() {
//        return this.cachedCrates;
//    }
//
//    public Map<String, CommentedFileConfiguration> getUnregisteredCrates() {
//        return this.unregisteredCrates;
//    }
//
//    public List<UUID> getActiveUsers() {
//        return activeUsers;
//    }
}
