package xyz.oribuin.eternalcrates.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    Entity createClientsideEntity(Player player, Location loc, EntityType entityType);

    ItemStack setString(ItemStack item, String key, String value);

    ItemStack setInt(ItemStack item, String key, int value);

    ItemStack setLong(ItemStack item, String key, long value);

    ItemStack setDouble(ItemStack item, String key, double value);

    ItemStack setBoolean(ItemStack item, String key, boolean value);

//    Entity updateEntity(Player player, Entity entity);
}
