package xyz.oribuin.eternalcrates.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface NMSHandler {

    Entity createClientsideEntity(Player player, Location loc, EntityType entityType);

//    Entity updateEntity(Player player, Entity entity);
}
