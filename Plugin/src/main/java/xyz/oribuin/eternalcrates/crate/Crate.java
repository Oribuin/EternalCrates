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
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.event.AnimationEndEvent;
import xyz.oribuin.eternalcrates.event.AnimationStartEvent;
import xyz.oribuin.eternalcrates.event.CrateOpenEvent;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Crate {

    private final String id;
    private String name;
    private Map<Integer, Reward> rewardMap;
    private Animation animation;
    private Location location;
    private ItemStack key;
    private int maxRewards;
    private int minRewards;
    private int minGuiSlots;
    private CommentedFileConfiguration config;
    private List<Action> openActions;
    private CrateType type;

    public Crate(final String id) {
        this.id = id;
        this.setName(id);
        this.setRewardMap(new HashMap<>());
        this.setAnimation(null);
        this.location = null;
        this.maxRewards = 1;
        this.minRewards = 1;
        this.minGuiSlots = this.maxRewards;
        this.config = null;
        this.openActions = new ArrayList<>();
        this.type = CrateType.PHYSICAL;
    }

    /**
     * Open a crate for the player.
     *
     * @param plugin The plugin instance.
     * @param player The player who is opening the crate
     */
    public void open(EternalCrates plugin, Player player) {

        if (this.getAnimation().isBlockRequired())
            return;

        final CrateOpenEvent event = new CrateOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        final StringPlaceholders plc = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .addPlaceholder("add", this.getName())
                .build();

        this.openActions.forEach(action -> action.execute(player, plc));

        plugin.getActiveUsers().add(player.getUniqueId());

        // The crate location or the player location.
        final Location spawnLocation = location != null ? PluginUtils.centerLocation(location) : player.getLocation();

        Bukkit.getPluginManager().callEvent(new AnimationStartEvent(this, this.getAnimation()));

        switch (animation.getAnimationType()) {
//            case GUI -> new SpinningGUI(plugin, this, player);
            case PARTICLES -> ((ParticleAnimation) animation).play(spawnLocation, 1, player);
            case FIREWORKS -> ((FireworkAnimation) animation).play(spawnLocation, player);
            case CUSTOM, SEASONAL -> ((CustomAnimation) animation).spawn(spawnLocation, player);
            case NONE -> this.finish(player);
        }
    }

    /**
     * Finish the crate animation and give the player the rewards.
     *
     * @param player  The player who is getting the rewards
     * @param rewards The rewards the player is getting.
     */
    public void finish(Player player, List<Reward> rewards) {
        this.animation.finishFunction(player, this);
        Bukkit.getPluginManager().callEvent(new AnimationEndEvent(this.getAnimation()));
        this.getAnimation().setActive(false);
        EternalCrates.getInstance().getActiveUsers().remove(player.getUniqueId());
        rewards.forEach(reward -> reward.execute(player, this));
    }

    /**
     * Finish the crate animation and give the player the rewards.
     *
     * @param player The player who is getting the rewards
     */
    public void finish(Player player) {
        this.finish(player, this.createRewards());
    }

    /**
     * Select a list of rewards won from the max reward count
     *
     * @return The list of rewards.
     */
    public List<Reward> createRewards() {
        final List<Reward> rewards = new ArrayList<>();
        for (int i = this.minRewards; i <= this.maxRewards; i++)
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

    public CrateType getType() {
        return type;
    }

    public void setType(CrateType type) {
        this.type = type;
    }
}
