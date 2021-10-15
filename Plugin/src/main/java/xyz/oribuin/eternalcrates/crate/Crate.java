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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Crate {

    private final String id;
    private String displayName;
    private Map<Reward, Double> rewardMap;
    private Animation animation;
    private Location location;

    public Crate(final String id) {
        this.id = id;
        this.setDisplayName(id);
        this.setRewardMap(new HashMap<>());
        this.setAnimation(null);
        this.location = null;
    }

    /**
     * Open a crate for the player.
     *
     * @param plugin The plugin instance.
     * @param player The player who is opening the crate
     */
    public void open(EternalCrates plugin, Player player) {

        if (plugin.getActiveUsers().contains(player.getUniqueId())) {
            System.out.println("wont run, in active users");
            return;
        }

        if (animation.isInAnimation()) {
            System.out.println("wont run, in animation");
            return;
        }

        final CrateOpenEvent event = new CrateOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        plugin.getActiveUsers().add(player.getUniqueId());

        // The crate location or the player location.
        final Location spawnLocation = location != null ? PluginUtils.centerLocation(location) : player.getLocation();

        switch (animation.getAnimationType()) {
            case GUI -> new AnimatedGUI(plugin, this, player);
            case PARTICLES -> ((ParticleAnimation) animation).play(this, spawnLocation, 1, player);
            case FIREWORKS -> ((FireworkAnimation) animation).play(this, spawnLocation, player);
            case NONE -> animation.finishFunction(this.selectReward(), player);
            case CUSTOM -> ((CustomAnimation) animation).spawn(this, spawnLocation, player);
            case HOLOGRAM -> {
            }
        }
    }

    public Reward selectReward() {

        // Select a reward.
        // https://stackoverflow.com/a/28711505
        Map<Reward, Double> chanceMap = new HashMap<>(this.rewardMap);
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

    public Map<Reward, Double> getRewardMap() {
        return rewardMap;
    }

    public void setRewardMap(Map<Reward, Double> rewardMap) {
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
