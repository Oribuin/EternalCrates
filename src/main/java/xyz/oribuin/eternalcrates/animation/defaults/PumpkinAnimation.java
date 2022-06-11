package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;

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

    private int pumpkinCount;
    private int smokeCount;

    public PumpkinAnimation() {
        super("pumpkin", "Oribuin", AnimationType.SEASONAL);
    }

    @Override
    public void spawn(Location location, Player player) {
        final World world = location.getWorld();
        if (world == null)
            return;

        this.setActive(true);
        final List<Entity> entitiesToRemove = new ArrayList<>();

        // pomkin
        final Location entityLoc = location.clone().subtract(0.0, 1.0, 0.0);
        entityLoc.setDirection(player.getLocation().getDirection().clone().multiply(-1));

        ArmorStand pomkin = world.spawn(entityLoc, ArmorStand.class, x -> {
            x.setGravity(false);
            x.setVisible(false);
            x.setInvulnerable(true);

            final EntityEquipment equipment = x.getEquipment();
            if (equipment == null)
                return;

            equipment.setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);

            Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            });
        });

        // yeehaw
        final AtomicInteger atomic = new AtomicInteger(Math.round(pomkin.getLocation().clone().getYaw()));
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), () -> {

            final Location loc = pomkin.getLocation();
            loc.add(0, 0.2, 0.0);
            if (atomic.get() == 360f)
                atomic.set(-1);

            loc.setYaw(atomic.addAndGet(28));
            pomkin.teleport(loc);
            world.spawnParticle(Particle.FALLING_DUST, loc.clone().add(0.0, 1.5, 0.0), 3, 0.0, 0.0, 0.0, 0, Material.ORANGE_CONCRETE.createBlockData());
        }, 0, 3);

        entitiesToRemove.add(pomkin);
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            this.getCrate().finish(player, location);

            task.cancel();
            pomkin.remove();
            // add spawn particles.
            for (int i = 0; i <= this.smokeCount; i++)
                world.spawnParticle(Particle.SMOKE_LARGE, pomkin.getLocation(), 2, 0.3, 0.3, 0.3, 0);

            for (int i = 0; i <= this.pumpkinCount; i++) {

                Item item = world.spawn(pomkin.getLocation(), Item.class, x -> {
                    x.setItemStack(new ItemStack(Material.JACK_O_LANTERN));
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                });

                double vectorX = random.nextDouble(-0.2, 0.2);
                double vectorY = random.nextDouble(0.5);
                double vectorZ = random.nextDouble(-0.2, 0.2);

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
        final Map<String, Object> options = new HashMap<>();
        options.put("animation.pumpkin-count", 10);
        options.put("animation.smoke-count", 10);
        return options;
    }

    @Override
    public void load() {
        this.pumpkinCount = this.get("animation.pumpkin-count", 10);
        this.smokeCount = this.get("animation.smoke-count", 10);
    }
}
