package xyz.oribuin.eternalcrates.animation.defaults.misc;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.MiscAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
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
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        List<ArmorStand> armorStands = new ArrayList<>();
        var world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i < this.swordCount; i++) {
            var armorStand = location.getWorld().spawn(location.clone().subtract(0.0, 0.5, 0.0), ArmorStand.class, stand -> {
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
                stand.setRightArmPose(new EulerAngle(this.swordRotation * Math.PI / 180, 0.0, 0.0));

                PersistentDataContainer cont = stand.getPersistentDataContainer();
                cont.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            });

            armorStands.add(armorStand);
        }

        var animationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> this.animate(location, armorStands), 0, 1);

        var items = new ArrayList<Item>();
        var rewards = crate.createRewards();

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            animationTask.cancel();
            armorStands.forEach(ArmorStand::remove);

            ParticleData data = new ParticleData(Particle.DUST_COLOR_TRANSITION)
                    .setDustColor(Color.RED)
                    .setTransitionColor(Color.ORANGE)
                    .cacheParticleData();

            for (int i = 0; i < 15; i++) {
                data.spawn(null, location, 2, 0.5, 0.5, 0.5);
            }

            var randomReward = rewards.get(0);
            if (randomReward != null && randomReward.getPreviewItem() != null) {
                var item = world.spawn(location.clone().add(0.0, 1.0, 0.0), Item.class, x -> {
                    x.setItemStack(randomReward.getPreviewItem());
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
        // make sure they're not just standing still
        this.step = (this.step + Math.PI * 2 / this.numSteps) % this.numSteps;

        // spin(spin)
        for (int i = 0; i < values.size(); i++) {
            var armorStand = values.get(i);
            var angle = (2 * Math.PI / values.size()) * i + this.step;
            var x = location.getX() + this.radius * Math.cos(angle);
            var z = location.getZ() + this.radius * Math.sin(angle);

            Location newLoc = new Location(location.getWorld(), x, location.getY() - 0.5, z);
            newLoc.setDirection(location.toVector().subtract(newLoc.toVector()));
            armorStand.teleport(newLoc);
        }

    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("numSteps", 80);
            this.put("sword-rotation", 260);
            this.put("radius", 1);
            this.put("swordCount", 3);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.numSteps = config.getInt("crate-settings.animation.numSteps");
        this.swordRotation = config.getInt("crate-settings.animation.sword-rotation");
        this.radius = config.getInt("crate-settings.animation.radius");
        this.swordCount = config.getInt("crate-settings.animation.swordCount");
    }

    @Override
    public void finish(Player player, Crate crate, Location location) {
        this.step = 0;
    }

}
