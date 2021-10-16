package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        // Get the block clicked
        final Block block = event.getClickedBlock();
        if (block == null)
            return;

        // Get the crate from the block location
        final Optional<Crate> crate = this.crateManager.getCrate(block.getLocation());
        if (crate.isEmpty())
            return;

        // No interacting with the block :)
        event.setCancelled(true);
        final ItemStack item = event.getItem();
        if (item == null)
            return;

        // Make sure the item has meta.
        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        // Check if the crate key is a valid key
        final PersistentDataContainer cont = meta.getPersistentDataContainer();
        if (!cont.has(key, PersistentDataType.STRING))
            return;

        final String keyID = cont.get(key, PersistentDataType.STRING);
        assert keyID != null;
        if (!keyID.equalsIgnoreCase(crate.get().getId()))
            return;

        if (crate.get().open(plugin, event.getPlayer())) {
            // because bukkit is inconsistent as shit
            if (item.getAmount() > 1)
                item.setAmount(item.getAmount() - 1);
            else
                item.setType(Material.AIR);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrateDestroy(BlockBreakEvent event) {

        final Optional<Crate> crate = this.crateManager.getCrate(event.getBlock().getLocation());
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

        this.data.deleteCrate(crate.get());
    }

}
