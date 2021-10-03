package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
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
        if (args.length >= 2 && args[0].equalsIgnoreCase("crate")) {
            final String name = String.join(" ", args).substring(args[0].length() + 1);

            System.out.println(name);
            Optional<Crate> optional = crateManager.getCrate(name);
            optional.ifPresent(crate -> {

                event.setCancelled(true);
                if (crate.getAnimation().getAnimationType() == AnimationType.GUI)
                    Bukkit.getScheduler().runTask(plugin, () -> new AnimatedGUI(plugin, crate, event.getPlayer()));

                else if (crate.getAnimation().getAnimationType() == AnimationType.PARTICLES) {
                    if (!(crate.getAnimation() instanceof ParticleAnimation animation))
                        return;

                    animation.spawn(event.getPlayer().getLocation(), 1);
                }

            });

        }

    }
}
