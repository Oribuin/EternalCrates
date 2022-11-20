package xyz.oribuin.eternalcrates.animation.defaults.custom;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ChickenAnimation extends CustomAnimation {

    private int chickenCount;

    public ChickenAnimation() {
        super("Chicken", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final var world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i < chickenCount; i++) {
            double xOffset = ThreadLocalRandom.current().nextDouble(-3, 3);
            double zOffset = ThreadLocalRandom.current().nextDouble(-3, 3);

            Chicken chicken = world.spawn(location.clone().add(xOffset, 5, zOffset), Chicken.class, x -> {
                x.setInvulnerable(true);
                x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                x.setCollidable(false);
            });

            Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
                chicken.setHealth(0);
                chicken.getWorld().spawnParticle(Particle.BLOCK_CRACK, chicken.getLocation().clone().add(0.0, 0.25, 0.0), 10, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
            }, 60);
        }


        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> crate.finish(player, location), 60);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("chicken-count", 10);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.chickenCount = config.getInt("animation.chicken-count", 10);
    }

}
