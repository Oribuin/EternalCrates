package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.gui.AnimatedGUI;
import xyz.oribuin.eternalcrates.manager.CrateManager;

import java.util.Optional;

public class PlayerListeners implements Listener {

    private final EternalCrates plugin;

    public PlayerListeners(EternalCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        final CrateManager crateManager = this.plugin.getManager(CrateManager.class);
        if (event.getMessage().equalsIgnoreCase("list")) {
            crateManager.getCachedCrates().forEach((s, crate) -> event.getPlayer().sendMessage(s));
            return;
        }

        final String[] args = event.getMessage().toLowerCase().split(" ");
        Optional<Crate> optional = crateManager.getCrate(event.getMessage());
        Bukkit.getScheduler().runTask(plugin, () -> optional.ifPresentOrElse(x -> new AnimatedGUI(plugin, x, event.getPlayer()), () -> event.getPlayer().sendMessage("unknown crate")));

    }
}
