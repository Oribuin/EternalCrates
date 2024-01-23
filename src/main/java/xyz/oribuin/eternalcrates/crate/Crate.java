package xyz.oribuin.eternalcrates.crate;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.action.ActionType;
import xyz.oribuin.eternalcrates.animation.Animation;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Crate {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Basic Crate Settings
    private final String id;
    private String name;
    private KeyType type;
    private Animation animation;
    private ItemStack key;
    private RewardSettings settings;
    private Map<String, Reward> rewards;
    private List<Location> locations;
    private List<String> openActions;

    // Config Loading
    private CommentedConfigurationSection config;
    private File file;

    // Animation Settings
    private boolean active;

    public Crate(final String id) {
        this.id = id;
        this.name = id;
        this.rewards = new HashMap<>();
        this.animation = null;
        this.locations = new ArrayList<>();
        this.file = null;
        this.config = null;
        this.openActions = new ArrayList<>();
        this.type = KeyType.PHYSICAL;
    }

    /**
     * Open the crate for the player
     *
     * @param player The player who opened the crate
     * @param block  The location of the crate
     */
    public void open(Player player, Location block) {
        if (this.animation == null) {
            Bukkit.getLogger().severe("The animation for the crate " + this.id + " is null. Please report this to the developer.");
            return;
        }

        if (this.active) return;

        this.active = true;
        this.animation.load(this.getAnimationSettings());
        this.animation.start(this, player, block);

        // Run the open commands for the crate
        ActionType.run(this, player, this.openActions);

        // Immediately stop the animation if the duration is 0
        if (this.animation.getDuration() == Duration.ZERO) {
            this.active = false;
            this.animation.stop(this, player, block);
            return;
        }

        // Tick the animation every 3 ticks
        BukkitTask tickTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.get(), () ->
                this.animation.tick(
                        this,
                        player,
                        block
                ), 0L, 3L);

        // Stop the animation after the duration
        Bukkit.getScheduler().runTaskLater(EternalCrates.get(), () -> {
            this.active = false;
            this.animation.stop(this, player, block);
            tickTask.cancel();
        }, this.animation.getDuration().toSeconds() * 20);
    }

    /**
     * Hand out the rewards to the player who opened the crate
     *
     * @param player  The player who opened the crate
     * @param rewards The rewards to give out
     */
    public void reward(Player player, List<Reward> rewards) {
        rewards.forEach(reward -> ActionType.run(
                this,
                player,
                reward.getActions()
        ));
    }

    /**
     * Hand out the rewards to the player who opened the crate
     *
     * @param player The player who opened the crate
     */
    public void reward(Player player) {
        this.reward(player, this.generate());
    }

    /**
     * Load all the animation settings from the crate.
     *
     * @return The map of settings
     */
    private Map<String, Object> getAnimationSettings() {
        Map<String, Object> settings = new HashMap<>();

        CommentedConfigurationSection animationSection = this.config.getConfigurationSection("animation");
        if (animationSection == null) return settings;

        animationSection.getKeys(false).forEach(key -> settings.put(key, animationSection.get(key)));
        return settings;
    }

    /**
     * Generate a list of rewards from the crate
     *
     * @param amount The amount of rewards to generate
     * @return The list of rewards
     */
    public List<Reward> generate(int amount) {
        // Ref: https://stackoverflow.com/a/28711505
        Map<Reward, Double> chanceMap = new HashMap<>();
        List<Reward> results = new ArrayList<>();
        this.rewards.values().forEach(reward -> chanceMap.put(reward, reward.getChance()));

        for (int i = 0; i < amount; i++) {
            double sumOfPercentages = chanceMap.values().stream().reduce(0.0, Double::sum);
            int current = 0;
            double randomNumber = RANDOM.nextDouble(sumOfPercentages);
            for (Map.Entry<Reward, Double> entry : chanceMap.entrySet()) {
                current += entry.getValue();
                if (randomNumber > current) continue;
                results.add(entry.getKey());
            }
        }

        return results;
    }

    /**
     * Generate the defined amount of rewards
     *
     * @return The list of rewards
     */
    public List<Reward> generate() {
        int minRewards = this.settings.minRewards();
        int maxRewards = this.settings.maxRewards() + 1;
        double multiplier = this.settings.multiplier();

        return this.generate(RANDOM.nextInt(
                (int) (minRewards * multiplier),
                (int) (maxRewards * multiplier)
        ));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KeyType getType() {
        return type;
    }

    public void setType(KeyType type) {
        this.type = type;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public ItemStack getKey() {
        return key;
    }

    public void setKey(ItemStack key) {
        this.key = key;
    }

    public RewardSettings getSettings() {
        return settings;
    }

    public void setSettings(RewardSettings settings) {
        this.settings = settings;
    }

    public Map<String, Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Map<String, Reward> rewards) {
        this.rewards = rewards;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<String> getOpenActions() {
        return openActions;
    }

    public void setOpenActions(List<String> openActions) {
        this.openActions = openActions;
    }

    public CommentedConfigurationSection getConfig() {
        return config;
    }

    public void setConfig(CommentedConfigurationSection config) {
        this.config = config;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
