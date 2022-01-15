package xyz.oribuin.eternalcrates.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;

public class PlayerListeners implements Listener {

    private final EternalCrates plugin;

    public PlayerListeners(final EternalCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        // Get a user's cached items when they join.
        this.plugin.getManager(DataManager.class).saveUserItems(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (!this.plugin.getActiveUsers().contains(player.getUniqueId()))
            return;

        if (PluginUtils.get(this.plugin.getConfig(), "crate-settings.item-pickup-in-animation", false))
            return;

        event.setCancelled(true);
    }
}
