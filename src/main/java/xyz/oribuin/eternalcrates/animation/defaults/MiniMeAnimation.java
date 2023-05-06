package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MiniMeAnimation extends Animation {

    private long rotationSpeed;
    private String texture;

    public MiniMeAnimation() {
        super("Mini-Me", "Oribuin");
    }

    @Override
    public void play(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final World world = location.getWorld();
        if (world == null)
            return;

        Block crateBlock = location.getBlock();
        this.setActive(true);

        Location entityLoc = location.clone().subtract(0.0, 0.5, 0.0);
        entityLoc.setDirection(crateBlock.getLocation().getDirection().clone());

        if (crateBlock.getState() instanceof Lidded lid)
            lid.open();

        String headText = this.texture;
        if (headText == null || headText.equalsIgnoreCase("DEFAULT"))
            headText = this.getBlockSkin(crateBlock.getType());

        String finalHeadText = headText;
        ArmorStand stand = world.spawn(entityLoc, ArmorStand.class, x -> {
            x.setGravity(false);
            x.setVisible(false);
            x.setInvulnerable(true);
            x.setSmall(true);
            final EntityEquipment equipment = x.getEquipment();
            final ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                    .setTexture(finalHeadText)
                    .create();

            equipment.setHelmet(item);
            x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                x.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            });
        });

        final AtomicInteger atomic = new AtomicInteger(Math.round(stand.getLocation().clone().getYaw()));
        final BukkitTask task = Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), () -> {
            final Location loc = stand.getLocation();
            if (loc.clone().getY() <= location.getY() + 0.5)
                loc.add(0, 0.075, 0.0);

            if (atomic.get() == 360f)
                atomic.set(-1);

            loc.setYaw(atomic.addAndGet(25));
            stand.teleport(loc);
            world.spawnParticle(Particle.CRIT_MAGIC, loc.clone(), 5, 0.1, 0.5, 0.1, 0);
        }, 0, this.rotationSpeed);


        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {

            crate.finish(player, location);
            task.cancel();

            final Location loc = stand.getLocation().clone();

            ParticleData data = new ParticleData(Particle.FLAME);

            // Send crit particles outwards from the crate in star shape
            // add each number of 0.1 from -0.5 to 0.5
            for (double x = -0.5; x <= 0.5; x += 0.1) {

                Location newLoc = loc.clone().add(x, 0, 0);
                data.spawn(null, newLoc, 5);
            }

            for (double z = -0.5; z <= 0.5; z += 0.1) {
                Location newLoc = loc.clone().add(0, 0, z);
                data.spawn(null, newLoc, 5);
            }

            stand.remove();
        }, 60);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            this.setActive(false);
            if (crateBlock.getState() instanceof Lidded lid)
                lid.close();
        }, 70);

    }

    /**
     * Get the base64 equivalent of supported materials
     *
     * @param material The material.
     * @return The base64 code.
     */
    private String getBlockSkin(Material material) {
        return switch (material) {
            case CHEST, TRAPPED_CHEST -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19";
            case ENDER_CHEST -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ==";
            case BARREL -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlNzUxNzcwYTcwYTAwOGEwM2UxMzk3Y2JjNWJlOGFlM2Y3ODI5ODZhODE0ZjA0OTQzY2Y2MmE3MTIxYmMzZiJ9fX0=";
            default -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19";
        };
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("rotation-speed", 3L);
            this.put("texture", "DEFAULT");
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.rotationSpeed = config.getLong("crate-settings.animation.rotation-speed");
        this.texture = config.getString("crate-settings.animation.texture");
    }
}
