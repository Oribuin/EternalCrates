package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SnowmanAnimation extends Animation {

    private int snowballCount = 10;

    public SnowmanAnimation() {
        super("Snowman", "Oribuin", AnimationType.SEASONAL);
    }

    @Override
    public void play(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final World world = location.getWorld();
        if (world == null)
            return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.setActive(true);

        final Location entityLoc = location.clone();
        entityLoc.setDirection(player.getLocation().getDirection().clone().multiply(-1)); // Make the snowman face the player
        entityLoc.setPitch(0);

        final Snowman entity = world.spawn(entityLoc, Snowman.class, snowman -> {
            snowman.setAI(false);
            snowman.setGravity(false);
            snowman.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            snowman.setInvulnerable(true);
        });

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> entity.setDerp(true), 20);
        BukkitTask dustParticle = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> entity.getWorld().spawnParticle(Particle.FALLING_DUST, entity.getLocation().clone().add(0.0, 2.0, 0.0), 3, 0.5, 0.5, 0.5, Material.WHITE_CARPET.createBlockData()), 0, 3);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            entity.remove();
            dustParticle.cancel();

            crate.finish(player, location);

            for (int i = 0; i <= this.snowballCount; i++)
                world.spawn(entity.getLocation().clone().add(0.0, 0.5, 0.0), Snowball.class, snowball ->
                        snowball.setVelocity(new Vector(
                                random.nextDouble(-0.2, 0.2),
                                random.nextDouble(0.3, 0.5),
                                random.nextDouble(-0.2, 0.2)
                        ).clone().multiply(1)));

            this.setActive(false);
        }, 30);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("snowball-count", 10);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.snowballCount = config.getInt("crate-settings.animation.snowball-count");

    }
}
