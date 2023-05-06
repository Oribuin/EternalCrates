package xyz.oribuin.eternalcrates.crate;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.event.AnimationEndEvent;
import xyz.oribuin.eternalcrates.event.AnimationStartEvent;
import xyz.oribuin.eternalcrates.event.CrateOpenEvent;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Crate {

    private final String id; // The id of the crate.
    private String name; // The name of the crate.
    private Map<String, Reward> rewardMap; // The rewards for the crate.
    private Animation animation; // The animation to play when a crate is opened.
    private List<Location> locations; // The locations of the crate.
    private ItemStack key; // The key to open the crate.
    private int maxRewards; // The maximum amount of rewards to give.
    private int minRewards;  // The minimum amount of rewards to give.
    private int minGuiSlots; // The minimum amount of slots in the GUI.
    private int multiplier; // The multiplier for the rewards.
    private CommentedFileConfiguration config; // The config where the crate is stored.
    private List<Action> openActions; // The actions to run when a crate is opened.
    private CrateType type; // The type of crate.
    private File file; // The file where the crate is stored.

    public Crate(final String id) {
        this.id = id;
        this.name = id;
        this.rewardMap = new HashMap<>();
        this.animation = null;
        this.locations = new ArrayList<>();
        this.multiplier = 1;
        this.maxRewards = 1;
        this.minRewards = 1;
        this.minGuiSlots = this.maxRewards;
        this.file = null;
        this.config = null;
        this.openActions = new ArrayList<>();
        this.type = CrateType.PHYSICAL;
    }

    /**
     * Open a crate for the player.
     *
     * @param player The player who is opening the crate
     */
    public boolean open(Player player, Location location) {

        if (this.getAnimation().isBlockRequired(location))
            return false;

        final CrateOpenEvent event = new CrateOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;

        final StringPlaceholders plc = StringPlaceholders.builder()
                .add("player", player.getName())
                .add("add", this.getName())
                .build();

        this.openActions.forEach(action -> action.execute(player, plc));

        EternalCrates.getInstance().getManager(CrateManager.class).getActiveUsers().add(player.getUniqueId());

        // The crate location or the player location.
        final Location spawnLocation = location != null ? CrateUtils.centerLocation(location) : player.getLocation();

        Bukkit.getPluginManager().callEvent(new AnimationStartEvent(this, this.getAnimation()));
        animation.play(spawnLocation, player, this);
        return true;
    }

    /**
     * Finish the crate animation and give the player the rewards.
     *
     * @param player  The player who is getting the rewards
     * @param rewards The rewards the player is getting.
     */
    public void finish(Player player, List<Reward> rewards, Location location) {
        this.animation.finish(player, this, location);
        Bukkit.getPluginManager().callEvent(new AnimationEndEvent(this.getAnimation()));

        this.getAnimation().setActive(false);
        EternalCrates.getInstance().getManager(CrateManager.class).getActiveUsers().remove(player.getUniqueId());
        rewards.forEach(reward -> reward.execute(player, this));
    }

    /**
     * Finish the crate animation and give the player the rewards.
     *
     * @param player The player who is getting the rewards
     */
    public void finish(Player player, Location location) {
        this.finish(player, this.createRewards(), location);
    }

    /**
     * Select a list of rewards won from the max reward count
     *
     * @return The list of rewards.
     */
    public List<Reward> createRewards() {
        final List<Reward> rewards = new ArrayList<>();

        // Select a random amount of rewards from the min and max reward count and times by the multiplier.
        final int rewardCount = ThreadLocalRandom.current().nextInt(this.getMinRewards(), this.getMaxRewards() + 1) * this.getMultiplier();
        for (int i = 0; i < rewardCount; i++)
            rewards.add(this.selectReward());

        return rewards;
    }

    public Reward selectReward() {

        // Select a reward.
        // https://stackoverflow.com/a/28711505
        Map<Reward, Double> chanceMap = new HashMap<>();
        this.rewardMap.forEach((integer, reward) -> chanceMap.put(reward, reward.getChance()));
        double sumOfPercentages = chanceMap.values().stream().reduce(0.0, Double::sum);
        int current = 0;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public ItemStack getKey() {
        return key;
    }

    public void setKey(ItemStack key) {
        this.key = key;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMaxRewards() {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
    }

    public int getMinRewards() {
        return minRewards;
    }

    public void setMinRewards(int minRewards) {
        this.minRewards = minRewards;
    }

    public CommentedFileConfiguration getConfig() {
        return config;
    }

    public void setConfig(CommentedFileConfiguration config) {
        this.config = config;
    }

    public int getMinGuiSlots() {
        return minGuiSlots;
    }

    public void setMinGuiSlots(int minGuiSlots) {
        this.minGuiSlots = minGuiSlots;
    }

    public Map<String, Reward> getRewardMap() {
        return rewardMap;
    }

    public void setRewardMap(Map<String, Reward> rewardMap) {
        this.rewardMap = rewardMap;
    }

    public List<Action> getOpenActions() {
        return openActions;
    }

    public void setOpenActions(List<Action> openActions) {
        this.openActions = openActions;
    }

    public CrateType getType() {
        return type;
    }

    public void setType(CrateType type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

}
