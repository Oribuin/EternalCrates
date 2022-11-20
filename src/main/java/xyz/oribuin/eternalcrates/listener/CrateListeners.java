package xyz.oribuin.eternalcrates.listener;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;

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
        var player = event.getPlayer();
        var block = event.getBlock();

        var crate = this.crateManager.getCrate(block);
        if (crate == null) {
            return;
        }

        event.setCancelled(true);

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (!player.hasPermission("eternalcrates.admin")) {
            return;
        }

        var destroyEvent = new CrateDestroyEvent(crate, player);
        this.plugin.getServer().getPluginManager().callEvent(destroyEvent);
        if (destroyEvent.isCancelled())
            return;

        crate.getLocations().remove(block.getLocation());
        this.crateManager.saveCrate(crate);
        this.locale.sendMessage(player, "crate-remove-success", StringPlaceholders.single("crate", crate.getId()));

        final var data = new ParticleData(Particle.REDSTONE);
        data.setDustColor(Color.RED);

        final var cube = PluginUtils.getCube(block.getLocation().clone(), block.getLocation().clone().add(1, 1, 1), 0.5);

        // Spawn particles in the cube and then remove them after 1.5s (35 ticks)
        var task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> cube.forEach(loc -> data.spawn(player, loc, 1)), 0, 2);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, task::cancel, 35);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCratePreview(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        var block = event.getClickedBlock();
        if (block == null)
            return;

        var crate = this.crateManager.getCrate(block);
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
        var player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        var block = event.getClickedBlock();
        if (block == null)
            return;

        var location = block.getLocation();
        var crate = this.crateManager.getCrate(block);
        if (crate == null) {
            return;
        }

        event.setCancelled(true);

        // CHeck if the user has enough slots for the items
        if (PluginUtils.getSpareSlots(player) < crate.getMinGuiSlots()) {
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
