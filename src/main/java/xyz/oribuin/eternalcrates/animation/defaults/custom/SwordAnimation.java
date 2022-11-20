package xyz.oribuin.eternalcrates.animation.defaults.custom;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
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

public class SwordAnimation extends CustomAnimation {

    public SwordAnimation() {
        super("sword", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        List<ArmorStand> armorStands = new ArrayList<>();
        var world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i <= 3; i++) {
            var armorStand = location.getWorld().spawn(location, ArmorStand.class, stand -> {
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setSmall(true);
                EntityEquipment equipment = stand.getEquipment();
                if (equipment != null) {
                    equipment.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    equipment.setItemInMainHandDropChance(0.0f);
                    Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                        stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING_OR_CHANGING);
                        stand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                    });
                }

                PersistentDataContainer cont = stand.getPersistentDataContainer();
                cont.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
            });

            armorStands.add(armorStand);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> this.animate(location, armorStands), 0, 1);
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> armorStands.forEach(ArmorStand::remove), 5 * 20);
    }

    /**
     * Rotate a list of armorstands around a center location
     *
     * @param location center location
     * @param values   list of armorstands
     */
    public void animate(Location location, List<ArmorStand> values) {
        var radius = 2.0;
        var angle = 0.0;
        for (ArmorStand armorStand : values) {
            var x = radius * Math.cos(angle);
            var z = radius * Math.sin(angle);
            var y = 0;
            angle += Math.PI / 4;
            armorStand.teleport(location.clone().add(x, y, z));
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

}
