package xyz.oribuin.eternalcrates.crate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.animation.FireworkAnimation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.event.CrateOpenEvent;
import xyz.oribuin.eternalcrates.gui.AnimatedGUI;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.*;

public class Crate {

    private final String id;
    private String displayName;
    private Map<Reward, Integer> rewardMap;
    private Animation animation;
    private Location location;
    private final List<UUID> activeUsers;

    public Crate(final String id) {
        this.id = id;
        this.setDisplayName(id);
        this.setRewardMap(new HashMap<>());
        this.setAnimation(null);
        this.location = null;
        this.activeUsers = new ArrayList<>();
    }

    /**
     * Open a crate for the player.
     *
     * @param plugin The plugin instance.
     * @param player The player who is opening the crate
     */
    public void open(EternalCrates plugin, Player player) {

        if (this.activeUsers.contains(player.getUniqueId()))
            return;

        final CrateOpenEvent event = new CrateOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        this.activeUsers.add(player.getUniqueId());

        // The crate location or the player location.
        final Location spawnLocation = location != null ? PluginUtils.centerLocation(location) : player.getLocation();

        switch (animation.getAnimationType()) {
            case GUI -> new AnimatedGUI(plugin, this, player);
            case PARTICLES -> ((ParticleAnimation) animation).play(this, spawnLocation, 1, player);
            case FIREWORKS -> ((FireworkAnimation) animation).play(this, spawnLocation, player);
            case NONE -> animation.finishFunction(this, this.selectReward(), player);
            case CUSTOM ->  ((CustomAnimation) animation).spawn(this, spawnLocation, player);
            case HOLOGRAM -> {
            }
        }
    }

    public Reward selectReward() {

        final List<Reward> rewards = new ArrayList<>(this.getRewardMap().keySet());
        Collections.shuffle(rewards);

        // Select a reward.
        Map<Reward, Integer> chanceMap = new HashMap<>();
        rewards.forEach(reward -> chanceMap.put(reward, reward.getChance()));
        int sum = chanceMap.values().stream().reduce(0, Integer::sum);
        int current = 0;
        Random random = new Random();

        // Select a random reward based on chance.
        int randomNumber = random.nextInt(sum);
        for (Map.Entry<Reward, Integer> entry : chanceMap.entrySet()) {
            current += entry.getValue();
            if (randomNumber > current)
                continue;

            return entry.getKey();
        }

        return null;
    }

    public List<UUID> getActiveUsers() {
        return activeUsers;
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

    public Map<Reward, Integer> getRewardMap() {
        return rewardMap;
    }

    public void setRewardMap(Map<Reward, Integer> rewardMap) {
        this.rewardMap = rewardMap;
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

}
