package xyz.oribuin.eternalcrates.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;

public record PlayerListeners(EternalCrates plugin) implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        // Get a user's cached items when they join.
        this.plugin.getManager(DataManager.class).getItems(event.getPlayer().getUniqueId());
    }
}
