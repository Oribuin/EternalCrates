package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import gs.mclo.java.Log;
import org.bukkit.Location;
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
                .forEach(this::create);
    }

    /**
     * Create a new crate from a file configuration
     *
     * @param file The file to load
     */
    public void create(File file) {
        CommentedConfigurationSection config = CommentedFileConfiguration.loadConfiguration(file);
        CommentedConfigurationSection crateSettings = config.getConfigurationSection("crate-settings");
        if (crateSettings == null) {
            LOGGER.severe("Failed to load crate because it does not have a crate-settings section.");
            return;
        }

        String id = crateSettings.getString("name");
        if (id == null) {
            LOGGER.warning("Failed to load crate because it does not have a name.");
            return;
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
            if (animationName != null) {
                this.unregisteredCrates.put(file, animationName);

                LOGGER.warning("Failed to load crate " + id + " because the animation " + animationName + " is not loaded, It will be loaded if the animation it requires is loaded.");
                return;
            }

            LOGGER.warning("Failed to load crate " + id + " because it does not have an animation set");
            return;
        }

        // Load the crate key
        if (crate.getType() == KeyType.PHYSICAL) {
            crate.setKey(CrateUtils.deserialize(crateSettings, "key"));
        }

        // Load the crate rewards
        CommentedConfigurationSection rewardsSection = crateSettings.getConfigurationSection("rewards");
        if (rewardsSection == null) {
            LOGGER.warning("Failed to load crate " + id + " because it does not have any rewards.");
            return;
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
        this.cachedCrates.put(id, crate);
    }

    /**
     * Get a crate from its id
     *
     * @param id The id of the crate
     * @return The crate
     */
    public Crate get(String id) {
        return this.cachedCrates.get(id);
    }

    /**
     * Get a crate from the location of it
     *
     * @param location The location of the crate
     * @return The crate
     */
    public Crate get(Location location) {
        return this.cachedCrates.values().stream()
                .filter(crate -> crate.getLocations().contains(location))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a crate from the type of it
     *
     * @param type The type of the crate
     * @return The crate
     */
    public List<Crate> get(KeyType type) {
        return this.cachedCrates.values().stream()
                .filter(crate -> crate.getType() == type)
                .toList();
    }

    /**
     * Update the crate into the current cache.
     *
     * @param crate The crate to update
     */
    public void update(Crate crate) {
        this.cachedCrates.put(crate.getId(), crate);
    }

    /**
     * Save the crate into the current cache and locations in the database.
     *
     * @param crate The crate to save
     */
    public void save(Crate crate) {
        this.cachedCrates.put(crate.getId(), crate);

        this.rosePlugin.getManager(DataManager.class).save(crate);
    }

    /**
     * Get a list of all the crates
     *
     * @return The list of crates
     */
    public List<Crate> all() {
        return new ArrayList<>(this.cachedCrates.values());
    }

    @Override
    public void disable() {
        this.cachedCrates.clear();
        this.activeUsers.clear();
        this.unregisteredCrates.clear();
    }

    public Map<String, Crate> getCachedCrates() {
        return cachedCrates;
    }

    public Map<File, String> getUnregisteredCrates() {
        return unregisteredCrates;
    }


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

}
