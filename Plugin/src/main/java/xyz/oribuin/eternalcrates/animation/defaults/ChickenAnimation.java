package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ChickenAnimation extends CustomAnimation {

    private final Random random = new Random();

    public ChickenAnimation() {
        super("Chicken", "Oribuin");
    }

    @Override
    public void spawn(Crate crate, Location location, Player player) {
        final World world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i < 7; i++) {
            double xOffset = ThreadLocalRandom.current().nextDouble(-3, 3);
            double zOffset = ThreadLocalRandom.current().nextDouble(-3, 3);

            Chicken chicken = world.spawn(location.clone().add(xOffset, 5, zOffset), Chicken.class, x -> {
                x.setInvulnerable(true);
                x.getPersistentDataContainer().set(new NamespacedKey(EternalCrates.getInstance(), "chicken"), PersistentDataType.INTEGER, 1);
                x.setCollidable(false);
            });

            Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
                chicken.setHealth(0);
                chicken.getWorld().spawnParticle(Particle.BLOCK_CRACK, chicken.getLocation().clone().add(0.0, 0.25, 0.0), 10, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
            }, 60);
        }

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> finishFunction(crate, crate.selectReward(), player), 60);

    }

}
