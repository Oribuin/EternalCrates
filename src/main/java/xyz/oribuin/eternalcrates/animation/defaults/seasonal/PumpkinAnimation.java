package xyz.oribuin.eternalcrates.animation.defaults.seasonal;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Oribuin
 */
public class PumpkinAnimation extends CustomAnimation {

    private int pumpkinCount = 10;
    private int smokeCount = 10;

    public PumpkinAnimation() {
        super("Pumpkin", "Oribuin", AnimationType.SEASONAL);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final var world = location.getWorld();
        if (world == null)
            return;

        this.setActive(true);
        final List<Entity> entitiesToRemove = new ArrayList<>();

        // pomkin
        final var entityLoc = location.clone().subtract(0.0, 1.0, 0.0);
        entityLoc.setDirection(player.getLocation().getDirection().clone().multiply(-1));

        var pomkin = world.spawn(entityLoc, ArmorStand.class, x -> {
            x.setGravity(false);
            x.setVisible(false);
            x.setInvulnerable(true);

            final var equipment = x.getEquipment();
            equipment.setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);

            Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            });
        });

        // yeehaw
        final var atomic = new AtomicInteger(Math.round(pomkin.getLocation().clone().getYaw()));
        final var task = Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), () -> {

            final var loc = pomkin.getLocation();
            loc.add(0, 0.2, 0.0);
            if (atomic.get() == 360f)
                atomic.set(-1);

            loc.setYaw(atomic.addAndGet(28));
            pomkin.teleport(loc);
            world.spawnParticle(Particle.FALLING_DUST, loc.clone().add(0.0, 1.5, 0.0), 3, 0.0, 0.0, 0.0, 0, Material.ORANGE_CONCRETE.createBlockData());
        }, 0, 3);

        entitiesToRemove.add(pomkin);
        final var random = ThreadLocalRandom.current();
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            crate.finish(player, location);

            task.cancel();
            pomkin.remove();
            // add spawn particles.
            for (int i = 0; i <= this.smokeCount; i++)
                world.spawnParticle(Particle.SMOKE_LARGE, pomkin.getLocation(), 2, 0.3, 0.3, 0.3, 0);

            for (int i = 0; i <= this.pumpkinCount; i++) {

                var item = world.spawn(pomkin.getLocation(), Item.class, x -> {
                    x.setItemStack(new ItemStack(Material.JACK_O_LANTERN));
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                });

                var vectorX = random.nextDouble(-0.2, 0.2);
                var vectorY = random.nextDouble(0.5);
                var vectorZ = random.nextDouble(-0.2, 0.2);

                item.setVelocity(item.getVelocity().clone().add(new Vector(vectorX, vectorY, vectorZ)));
                entitiesToRemove.add(item);
            }
        }, 40);


        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            this.setActive(false);
            entitiesToRemove.forEach(Entity::remove);
        }, 70);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("pumpkin-count", 10);
            this.put("smoke-count", 10);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.pumpkinCount = config.getInt("crate-settings.animation.pumpkin-count");
        this.smokeCount = config.getInt("crate-settings.animation.smoke-count");
    }
}
