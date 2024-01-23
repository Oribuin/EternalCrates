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
    private final CrateManager manager;
    private final DataManager data;

    public PlayerListeners(final EternalCrates plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(CrateManager.class);
        this.data = this.plugin.getManager(DataManager.class);
    }

    /**
     * Load the player's userdata when they join the server.
     *
     * @param event The PlayerJoinEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        this.data.user(event.getPlayer().getUniqueId());
    }

    /**
     * Save the player's userdata when they leave the server.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        this.data.user(event.getPlayer().getUniqueId()).thenAccept(data -> {
            if (data == null) return;
            this.data.save(event.getPlayer().getUniqueId(), data);
        });
    }

}
