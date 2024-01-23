package xyz.oribuin.eternalcrates.animation.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ChickenAnimation extends Animation {

    private final List<LivingEntity> toRemove = new ArrayList<>();
    private int chickenCount = 10;
    private boolean useBlood = true;

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     */
    public ChickenAnimation() {
        super("chicken");
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

        for (int i = 0; i <= this.chickenCount; i++) {
            double xOffset = random.nextDouble(-3, 3);
            double zOffset = random.nextDouble(-3, 3);
            double yOffset = random.nextDouble(0, 3);

            Chicken chimkin = world.spawn(spawnLocation.clone().add(xOffset, yOffset, zOffset), Chicken.class, x -> {
                x.setCollidable(false);
                x.setInvulnerable(true);

                if (random.nextBoolean()) {
                    x.setBaby();
                }

                this.markEntity(x);
            });

            this.toRemove.add(chimkin);
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

        World world = location.getWorld();
        if (world == null) return;

        BlockData blood = Material.REDSTONE_BLOCK.createBlockData();
        for (LivingEntity entity : this.toRemove) {
            if (!this.useBlood) {
                entity.remove();
                continue;
            }

            entity.setHealth(0);
            world.spawnParticle(
                    Particle.BLOCK_CRACK,
                    entity.getLocation().clone().add(0, 0.5, 0),
                    5,
                    0, 0, 0,
                    blood
            );
        }
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     */
    @Override
    public Map<String, Object> settings() {
        return Map.of(
                "chicken-count", 25,
                "use-blood", true
        );
    }


    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        this.chickenCount = (int) configValues.get("chicken-count");
        this.useBlood = (boolean) configValues.get("use-blood");
    }

}
