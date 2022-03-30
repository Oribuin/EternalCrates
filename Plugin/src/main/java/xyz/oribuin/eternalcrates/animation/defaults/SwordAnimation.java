package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SwordAnimation extends CustomAnimation {

    public SwordAnimation() {
        super("sword", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return Map.of();
    }

    @Override
    public void load() {
        // Nothing to load
    }

    @Override
    public void spawn(Location location, Player player) {
        List<ArmorStand> stands = this.spawnArmorStands(location, 3);
        Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> this.animate(location, stands), 0, 1);
        this.despawn(stands);
    }

    /**
     * Spawns 3 armorstands at a center location
     *
     * @param location center location
     * @param amount   amount of armorstands to spawn
     * @return list of armorstands
     */
    public List<ArmorStand> spawnArmorStands(Location location, int amount) {
        List<ArmorStand> armorStands = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setSmall(true);
                EntityEquipment equipment = stand.getEquipment();
                if (equipment == null) {
                    return;
                }

                equipment.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                equipment.setItemInMainHandDropChance(0.0f);
                Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                    stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                    stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                });

                PersistentDataContainer cont = stand.getPersistentDataContainer();
                cont.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            });

            armorStands.add(armorStand);
        }

        return armorStands;
    }

    /**
     * Rotate a list of armorstands around a center location
     *
     * @param location center location
     * @param values   list of armorstands
     */
    public void animate(Location location, List<ArmorStand> values) {
        double radius = 2.0;
        double angle = 0.0;
        for (ArmorStand armorStand : values) {
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double y = 0;
            angle += Math.PI / 4;
            armorStand.teleport(location.clone().add(x, y, z));
        }
    }

    /**
     * Despawns a list of armorstands
     *
     * @param values list of armorstands
     */
    public void despawn(List<ArmorStand> values) {
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> values.forEach(ArmorStand::remove), 5 * 20);
    }
}
