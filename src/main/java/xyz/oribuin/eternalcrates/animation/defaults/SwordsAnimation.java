package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.MiscAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwordsAnimation extends MiscAnimation {

    private double step = 0;
    private int numSteps = 80;
    private int swordRotation = 260;
    private int radius = 1;
    private int swordCount = 3;

    public SwordsAnimation() {
        super("Swords", "Oribuin");
    }

    @Override
    public void play(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        List<ArmorStand> armorStands = new ArrayList<>();
        World world = location.getWorld();
        if (world == null)
            return;

        final Location startPoint = location.clone().subtract(0, 0.5, 0);

        for (int i = 0; i < this.swordCount; i++) {
            ArmorStand armorStand = location.getWorld().spawn(startPoint, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setSmall(true);
                EntityEquipment equipment = stand.getEquipment();
                equipment.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                    stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                    stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                });

                // make arm straight ahead
                stand.setRightArmPose(new EulerAngle(this.swordRotation * Math.PI / 180, 0.0, 0.0));

                PersistentDataContainer cont = stand.getPersistentDataContainer();
                cont.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            });

            armorStands.add(armorStand);
        }

        BukkitTask animationTask = Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), () -> this.animate(startPoint, armorStands), 0, 1);

        List<Item> items = new ArrayList<>();
        List<Reward> rewards = crate.createRewards();

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            animationTask.cancel();
            armorStands.forEach(ArmorStand::remove);

            ParticleData data = new ParticleData(Particle.DUST_COLOR_TRANSITION)
                    .setDustTransition(Color.RED, Color.ORANGE);

            for (int i = 0; i < 15; i++) {
                data.spawn(null, location, 2, 0.5, 0.5, 0.5);
            }

            Reward randomReward = rewards.get(0);
            if (randomReward != null) {
                Item item = world.dropItem(location.clone().add(0.0, 1.0, 0.0), randomReward.getPreviewItem(), x -> {
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                    x.setCustomNameVisible(true);
                    x.setVelocity(new Vector());
                });

                items.add(item);
            }

        }, 5 * 20);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            items.forEach(Item::remove);
            crate.finish(player, location);
        }, 7 * 20);
    }

    /**
     * Rotate a list of armor stands around a center location
     *
     * @param location center location
     * @param values   list of armor stands
     */
    public void animate(Location location, List<ArmorStand> values) {
        // TODO: This animation completely breaks when the server is using spigot, i don't know why. It works fine on paper.
        this.step = (this.step + Math.PI * 2 / this.numSteps) % this.numSteps;

        // spin(spin)
        for (int i = 0; i < values.size(); i++) {
            ArmorStand armorStand = values.get(i);
            double angle = (2 * Math.PI / values.size()) * i + this.step;
            double x = location.getX() + this.radius * Math.cos(angle);
            double z = location.getZ() + this.radius * Math.sin(angle);

            Location newLoc = new Location(location.getWorld(), x, location.getY(), z);
            Vector direction = newLoc.toVector().clone().subtract(location.toVector());
            newLoc.setDirection(direction);
            armorStand.teleport(newLoc);
        }

    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("num-steps", 80);
            this.put("sword-rotation", 260);
            this.put("radius", 1);
            this.put("sword-count", 3);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.numSteps = config.getInt("crate-settings.animation.num-steps");
        this.swordRotation = config.getInt("crate-settings.animation.sword-rotation");
        this.radius = config.getInt("crate-settings.animation.radius");
        this.swordCount = config.getInt("crate-settings.animation.sword-count");
    }

    @Override
    public void finish(Player player, Crate crate, Location location) {
        this.step = 0;
    }

}
