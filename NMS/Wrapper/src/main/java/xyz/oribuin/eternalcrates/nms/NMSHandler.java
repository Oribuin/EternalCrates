package xyz.oribuin.eternalcrates.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

public interface NMSHandler {

    Entity createClientsideEntity(Player player, Location loc, EntityType entityType);

}
