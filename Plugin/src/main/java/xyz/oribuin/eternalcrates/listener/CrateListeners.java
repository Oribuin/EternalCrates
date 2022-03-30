package xyz.oribuin.eternalcrates.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;

import java.util.Optional;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final DataManager data;
    private final CrateManager crateManager;

    // Namespace key.
    private final NamespacedKey key;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
        this.crateManager = this.plugin.getManager(CrateManager.class);
        this.key = new NamespacedKey(this.plugin, "crateKey");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCrateInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Optional<Crate> crate = this.crateManager.getCrateFromBlock(block);
        if (crate.isPresent()) {
            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCratePreview(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        Optional<Crate> crate = this.crateManager.getCrateFromBlock(block);
        if (crate.isEmpty()) {
            return;
        }

        this.plugin.getManager(MenuManager.class).getGUI("preview-gui").ifPresent(gui -> gui.open(player, crate.get()));
    }


}
