package xyz.oribuin.eternalcrates.animation.defaults.custom;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwordsAnimation extends CustomAnimation {

    private double step = 0;
    private int numSteps = 80;

    public SwordsAnimation() {
        super("Swords", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        List<ArmorStand> armorStands = new ArrayList<>();
        var world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i <= 3; i++) {
            var armorStand = location.getWorld().spawn(location.clone().subtract(0.0, 1.0, 0.0), ArmorStand.class, stand -> {
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
//                stand.setLeftArmPose(new EulerAngle(260f, 0f, 0f));
                stand.setRightArmPose(new EulerAngle(260 * Math.PI / 180, 0.0, 0.0));

                PersistentDataContainer cont = stand.getPersistentDataContainer();
                cont.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            });

            armorStands.add(armorStand);
        }

        var animationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> this.animate(location, armorStands), 0, 1);
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {

            new ParticleData(Particle.EXPLOSION_LARGE)
                    .spawn(player, location.clone().add(0.0, 1.0, 0.0), 3, 0.1, 0.1, 0.1);

            animationTask.cancel();
            armorStands.forEach(ArmorStand::remove);
            crate.finish(player, location);
        }, 5 * 20);
    }

    /**
     * Rotate a list of armor stands around a center location
     *
     * @param location center location
     * @param values   list of armor stands
     */
    public void animate(Location location, List<ArmorStand> values) {
        var radius = 1.0; // Radius of circle

        // make sure they're not just standing still
        this.step = (this.step + Math.PI * 2 / this.numSteps) % this.numSteps;

        // spin(spin)
        for (int i = 0; i < values.size(); i++) {
            var armorStand = values.get(i);
            var angle = (2 * Math.PI / values.size()) * i + this.step;
            var x = location.getX() + radius * Math.cos(angle);
            var z = location.getZ() + radius * Math.sin(angle);

            Location newLoc = new Location(location.getWorld(), x, location.getY() - 1, z);
            newLoc.setDirection(location.toVector().subtract(newLoc.toVector()));
            armorStand.teleport(newLoc);
        }

    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>();
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        // Nothing to load here (tumbleweed passes by)
    }

    @Override
    public void finish(Player player, Crate crate, Location location) {
        this.step = 0;
    }
}
