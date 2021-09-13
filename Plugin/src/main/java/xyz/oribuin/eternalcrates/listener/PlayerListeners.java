package xyz.oribuin.eternalcrates.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.nms.NMSAdapter;

public class PlayerListeners implements Listener {

    private final EternalCrates plugin;

    public PlayerListeners(EternalCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        final String[] args = event.getMessage().toLowerCase().split(" ");

        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            final EntityType entityType = EntityType.valueOf(args[1].toUpperCase());

            // Spawn entity async or else it'll screech at you.
            this.plugin.getServer().getScheduler().runTask(this.plugin, () ->
                    NMSAdapter.getHandler().createClientsideEntity(event.getPlayer(), event.getPlayer().getLocation(), entityType));
        }
    }
}
