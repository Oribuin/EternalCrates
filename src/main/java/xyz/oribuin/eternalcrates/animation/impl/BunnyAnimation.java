package xyz.oribuin.eternalcrates.animation.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BunnyAnimation extends Animation {

    private final List<LivingEntity> toRemove = new ArrayList<>();
    private int bunnyCount = 10;

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     */
    public BunnyAnimation() {
        super("bunny", Duration.ofSeconds(3));
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
        for (int i = 0; i <= this.bunnyCount; i++) {
            Rabbit rabbit = world.spawn(spawnLocation, Rabbit.class, x -> {
                x.setRabbitType(this.getRandomType());
                x.setCollidable(false);
                x.setInvulnerable(true);

                if (random.nextBoolean()) {
                    x.setBaby();
                }

                this.markEntity(x);
            });

            rabbit.setVelocity(rabbit.getVelocity().clone().multiply(0.5));
            this.toRemove.add(rabbit);
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

        this.toRemove.stream().filter(Entity::isValid).forEach(Entity::remove);
    }

    /**
     * Get a random colour of rabbit to spawn
     *
     * @return The rabbit type
     */
    private Rabbit.Type getRandomType() {
        return Rabbit.Type.values()[(int) (Math.random() * Rabbit.Type.values().length)];
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     */
    @Override
    public Map<String, Object> settings() {
        return Map.of("bunny-count", 10);
    }

    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        this.bunnyCount = (int) configValues.get("bunny-count");
    }

}
