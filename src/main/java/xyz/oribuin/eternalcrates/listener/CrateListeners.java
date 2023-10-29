package xyz.oribuin.eternalcrates.listener;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final LocaleManager locale;
    private final CrateManager crateManager;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;

        this.locale = this.plugin.getManager(LocaleManager.class);
        this.crateManager = this.plugin.getManager(CrateManager.class);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Crate crate = this.crateManager.getCrate(block);
        if (crate == null) return;

        event.setCancelled(true);

        if (!player.isSneaking() && !player.hasPermission("eternalcrates.admin")) return;

        CrateDestroyEvent destroyEvent = new CrateDestroyEvent(crate, player);
        this.plugin.getServer().getPluginManager().callEvent(destroyEvent);
        if (destroyEvent.isCancelled()) return;

        crate.getLocations().remove(block.getLocation());
        this.crateManager.saveCrate(crate, crate.getFile());
        this.locale.sendMessage(player, "crate-remove-success", StringPlaceholders.of("crate", crate.getId()));

        final ParticleData data = new ParticleData(Particle.REDSTONE);
        data.setDustOptions(Color.RED);

        final List<Location> cube = CrateUtils.getCube(block.getLocation().clone(), block.getLocation().clone().add(1, 1, 1), 0.5);

        // Spawn particles in the cube and then remove them after 1.5s (35 ticks)
        BukkitTask particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> cube.forEach(loc -> data.spawn(player, loc, 1)), 0, 5);
        Bukkit.getScheduler().runTaskLater(this.plugin, particleTask::cancel, 35);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCratePreview(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Crate crate = this.crateManager.getCrate(block);
        if (crate == null) {
            return;
        }

        // Don't cancel the event if the player is sneaking (destroying the crate) and has the permission to do so.
        if (player.hasPermission("eternalcrates.admin") && event.getPlayer().isSneaking()) {
            return;
        }

        event.setCancelled(true);
        this.plugin.getManager(MenuManager.class).getGUI(PreviewGUI.class).open(player, crate);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Location location = block.getLocation();
        Crate crate = this.crateManager.getCrate(block);
        if (crate == null) {
            return;
        }

        // Check if the crate key is the correct item
        event.setCancelled(true);

        // CHeck if the user has enough slots for the items
        if (CrateUtils.getSpareSlots(player) < crate.getMinGuiSlots()) {
            this.locale.sendMessage(player, "crate-open-no-slots");
            return;
        }

        // Check if the user is already opening a crate.
        if (this.crateManager.getActiveUsers().contains(player.getUniqueId())) {
            this.locale.sendMessage(player, "crate-open-using-crate");
            return;
        }

        // Check if the crate is in animation
        if (crate.getAnimation().isActive()) {
            this.locale.sendMessage(player, "crate-open-animation-active");
            return;
        }

        switch (crate.getType()) {
            case PHYSICAL -> this.crateManager.usePhysicalKey(player, crate, event.getItem(), location);
            case VIRTUAL -> this.crateManager.useVirtualKey(crate, player, location);
        }

    }

}
