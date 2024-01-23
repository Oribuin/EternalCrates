package xyz.oribuin.eternalcrates.animation.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FountainAnimation extends Animation {

    private final List<Item> toRemove = new ArrayList<>();
    private int itemCount = 115;

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     */
    public FountainAnimation() {
        super("fountain", Duration.ofSeconds(3));
    }

    /**
     * Start the animation for the player. This is called when a player opens a crate.
     * This method will be called when the crate is opened
     * It will be done synchronously, make sure to use async methods if needed.
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    @Override
    public void start(Crate crate, Player player, Location location) {
        super.start(crate, player, location);

        World world = location.getWorld();
        if (world == null) return;

        Location spawnLocation = location.clone().add(0, 1, 0);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Reward> rewards = crate.generate();
        for (int i = 0; i <= this.itemCount; i++) {
            Reward reward = rewards.get(random.nextInt(rewards.size()));

            Item item = world.dropItem(spawnLocation, reward.getPreviewItem(), x -> {
                x.setPickupDelay(Integer.MAX_VALUE);
                x.setInvulnerable(true);
                x.setCustomNameVisible(true);

                this.markEntity(x);
            });

            item.setVelocity(item.getVelocity().clone().add(new Vector(
                    random.nextDouble(-0.5, 0.5),
                    random.nextDouble(0.5, 1.5),
                    random.nextDouble(-0.5, 0.5)
            )));

            this.toRemove.add(item);
        }
    }

    /**
     * Stop the animation for the player. This is called when the animation is finished.
     * Use this method to clean up any blocks or entities created by the animation.
     * This method will be called when the crate is opened
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    @Override
    public void stop(Crate crate, Player player, Location location) {
        super.stop(crate, player, location);

        this.toRemove.stream().filter(Item::isValid).forEach(Item::remove);
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     */
    @Override
    public Map<String, Object> settings() {
        return Map.of("item-count", 15);
    }

    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        this.itemCount = (int) configValues.get("item-count");
    }

}
