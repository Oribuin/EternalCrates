package xyz.oribuin.eternalcrates.crate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.event.AnimationStartEvent;
import xyz.oribuin.eternalcrates.event.CrateOpenEvent;
import xyz.oribuin.eternalcrates.gui.AnimatedGUI;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Crate {

    private final String id;
    private String displayName;
    private Map<Integer, Reward> rewardMap;
    private Animation animation;
    private Location location;
    private ItemStack key;
    private int maxRewards;
    private int minGuiSlots;
    private FileConfiguration config;
    private List<Action> openActions;

    public Crate(final String id) {
        this.id = id;
        this.setDisplayName(id);
        this.setRewardMap(new HashMap<>());
        this.setAnimation(null);
        this.location = null;
        this.maxRewards = 1;
        this.minGuiSlots = this.maxRewards;
        this.config = null;
        this.openActions = new ArrayList<>();
    }

    /**
     * Open a crate for the player.
     *
     * @param plugin The plugin instance.
     * @param player The player who is opening the crate
     */
    public void open(EternalCrates plugin, Player player) {
        final CrateOpenEvent event = new CrateOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        this.openActions.forEach(action -> action.executeAction(plugin, player));

        plugin.getActiveUsers().add(player.getUniqueId());

        // The crate location or the player location.
        final Location spawnLocation = location != null ? PluginUtils.centerLocation(location) : player.getLocation();

        Bukkit.getPluginManager().callEvent(new AnimationStartEvent(this, this.getAnimation()));

        switch (animation.getAnimationType()) {
            case GUI -> new AnimatedGUI(plugin, this, player);
            case PARTICLES -> ((ParticleAnimation) animation).play(this, spawnLocation, 1, player);
            case FIREWORKS -> ((FireworkAnimation) animation).play(this, spawnLocation, player);
            case NONE -> animation.finishFunction(this.selectReward(), player);
            case CUSTOM, SEASONAL -> ((CustomAnimation) animation).spawn(this, spawnLocation, player);
        }
    }

    /**
     * Select a list of rewards won from the max reward count
     *
     * @return The list of rewards.
     */
    public List<Reward> createRewards() {
        final List<Reward> rewards = new ArrayList<>();
        for (int i = 0; i < this.maxRewards; i++)
            rewards.add(this.selectReward());

        return rewards;
    }

    public Reward selectReward() {

        // Select a reward.
        // https://stackoverflow.com/a/28711505
        Map<Reward, Double> chanceMap = new HashMap<>();
        this.rewardMap.forEach((integer, reward) -> chanceMap.put(reward, reward.getChance()));
        double sumOfPercentages = chanceMap.values().stream().reduce(0.0, Double::sum);
        double current = 0;
        double randomNumber = ThreadLocalRandom.current().nextDouble(sumOfPercentages);
        for (Map.Entry<Reward, Double> entry : chanceMap.entrySet()) {
            current += entry.getValue();
            if (randomNumber > current)
                continue;

            return entry.getKey();
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemStack getKey() {
        return key;
    }

    public void setKey(ItemStack key) {
        this.key = key;
    }

    public int getMaxRewards() {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    public int getMinGuiSlots() {
        return minGuiSlots;
    }

    public void setMinGuiSlots(int minGuiSlots) {
        this.minGuiSlots = minGuiSlots;
    }

    public Map<Integer, Reward> getRewardMap() {
        return rewardMap;
    }

    public void setRewardMap(Map<Integer, Reward> rewardMap) {
        this.rewardMap = rewardMap;
    }

    public List<Action> getOpenActions() {
        return openActions;
    }

    public void setOpenActions(List<Action> openActions) {
        this.openActions = openActions;
    }

}
