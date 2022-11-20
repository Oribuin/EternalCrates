package xyz.oribuin.eternalcrates.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;

public class PlayerListeners implements Listener {

    private final EternalCrates plugin;
    private final DataManager data;

    public PlayerListeners(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        // Get a user's cached items when they join.
        this.data.getUser(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        // Save a users unclaimed keys and virtual keys when they leave
        this.data.saveUser(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        final var manager = this.plugin.getManager(CrateManager.class);
        if (!manager.getActiveUsers().contains(player.getUniqueId()))
            return;

        if (!Setting.PICKUP_IN_ANIMATION.getBoolean())
            return;

        event.setCancelled(true);
    }
}
