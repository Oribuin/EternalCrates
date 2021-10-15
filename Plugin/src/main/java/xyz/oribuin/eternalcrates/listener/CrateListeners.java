package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.manager.DataManager;

import java.util.Optional;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final DataManager data;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Block block = event.getClickedBlock();
        if (block == null)
            return;

        final Optional<Crate> crate = this.data.getCrate(block.getLocation());
        if (crate.isEmpty())
            return;



        // todo add crate keys
        crate.get().open(plugin, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrateDestroy(BlockBreakEvent event) {

        final Optional<Crate> crate = this.data.getCrate(event.getBlock().getLocation());
        if (crate.isEmpty())
            return;

        event.setCancelled(true);

        if (!event.getPlayer().isSneaking())
            return;

        if (event.getPlayer().hasPermission("eternalcrates.destroy"))
            return;

        final CrateDestroyEvent crateDestroyEvent = new CrateDestroyEvent(crate.get(), event.getPlayer());
        Bukkit.getPluginManager().callEvent(crateDestroyEvent);
        if (crateDestroyEvent.isCancelled())
            return;

        this.data.deleteCrate(crate.get().getLocation());
    }

}
