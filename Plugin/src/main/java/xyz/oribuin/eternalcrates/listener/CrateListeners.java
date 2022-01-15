package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateType;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.Map;
import java.util.Optional;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final DataManager data;
    private final CrateManager crateManager;
    private final MessageManager msg;

    // Namespace key.
    private final NamespacedKey key;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
        this.crateManager = this.plugin.getManager(CrateManager.class);
        this.msg = this.plugin.getManager(MessageManager.class);

        this.key = new NamespacedKey(this.plugin, "crateKey");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateOpen(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

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

        if (crate.get().getType() == CrateType.PHYSICAL) {
            final ItemStack item = event.getItem();
            if (item == null) {
                this.sendPlayerVrooming(player, crate.get());
                return;
            }

            // Make sure the item has meta.
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                this.sendPlayerVrooming(player, crate.get());
                return;
            }

            // Check if the crate key is a valid key
            final PersistentDataContainer cont = meta.getPersistentDataContainer();
            if (!cont.has(key, PersistentDataType.STRING)) {
                this.sendPlayerVrooming(player, crate.get());
                return;
            }

            final String keyID = cont.get(key, PersistentDataType.STRING);
            assert keyID != null;
            if (!keyID.equalsIgnoreCase(crate.get().getId())) {
                this.sendPlayerVrooming(player, crate.get());
                return;
            }
        }

        if (crate.get().getType() == CrateType.VIRTUAL) {
            Map<String, Integer> usersKeys = this.data.getVirtual(player.getUniqueId());

            // check if the user has any crate keys
            int totalKeys = usersKeys.getOrDefault(crate.get().getId().toLowerCase(), 0);
            if (totalKeys <= 0) {
                this.sendPlayerVrooming(player, crate.get());
                return;
            }
        }

        // Check if they have enough slots to open it.
        if (PluginUtils.getSpareSlots(player) < crate.get().getMinGuiSlots()) {
            this.msg.send(player, "not-enough-slots");
            return;
        }

        // Check if the user can open ac rate
        if (plugin.getActiveUsers().contains(player.getUniqueId())) {
            this.msg.send(player, "using-crate");
            return;
        }

        // Check if the crate is in animation.
        if (crate.get().getAnimation().isActive()) {
            this.msg.send(player, "in-animation");
            return;
        }

        if (crate.get().getType() == CrateType.PHYSICAL) {
            ItemStack item = event.getItem();
            assert item != null;

            // because bukkit is inconsistent as shit
            if (item.getAmount() == 1)
                event.getPlayer().getInventory().setItemInMainHand(null); // setting item type doesn't work apparently, thank you spigot for your amazing api
            else
                item.setAmount(item.getAmount() - 1);
        } else {
            this.data.saveVirtual(player.getUniqueId(), this.data.getVirtual(player.getUniqueId()));
        }

        if (crate.get().getType() == CrateType.VIRTUAL) {
            Map<String, Integer> keys = this.data.getVirtual(player.getUniqueId());
            int crateKeys = keys.getOrDefault(crate.get().getId().toLowerCase(), 0);
            keys.put(crate.get().getId().toLowerCase(), crateKeys - 1);

            data.saveVirtual(player.getUniqueId(), keys);
        }

        crate.get().open(plugin, player);

    }

    @EventHandler
    public void onKeyInteract(PlayerInteractEvent event) {

        final ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        // Make sure the item has meta.
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        // Check if the crate key is a valid key
        final PersistentDataContainer cont = meta.getPersistentDataContainer();
        if (!cont.has(key, PersistentDataType.STRING)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCratePreview(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (player.isSneaking())
            return;

        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.LEFT_CLICK_BLOCK)
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
        new PreviewGUI(plugin, crate.get()).create(player);
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrateDestroy(BlockBreakEvent event) {

        final Optional<Crate> crate = this.crateManager.getCrate(event.getBlock().getLocation());
        if (crate.isEmpty())
            return;

        event.setCancelled(true);

        if (!event.getPlayer().isSneaking())
            return;

        if (!event.getPlayer().hasPermission("eternalcrates.destroy"))
            return;

        final CrateDestroyEvent crateDestroyEvent = new CrateDestroyEvent(crate.get(), event.getPlayer());
        Bukkit.getPluginManager().callEvent(crateDestroyEvent);
        if (crateDestroyEvent.isCancelled())
            return;

        this.msg.send(event.getPlayer(), "unset-crate", StringPlaceholders.single("crate", crate.get().getId()));
        this.data.deleteCrate(crate.get());
    }

    /**
     * Send a player vrooooming.
     *
     * @param player The player
     * @param crate  The crate
     */
    private void sendPlayerVrooming(Player player, Crate crate) {
        this.msg.send(player, "invalid-key", StringPlaceholders.single("crate", crate.getId()));
        Vector vector = player.getLocation().getDirection().clone();
        vector = vector.clone().multiply(-1);
        vector.multiply(1);
        player.setVelocity(vector);
    }

}
