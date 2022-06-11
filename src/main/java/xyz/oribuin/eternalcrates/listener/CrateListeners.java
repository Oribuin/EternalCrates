package xyz.oribuin.eternalcrates.listener;

import org.bukkit.Location;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.CrateType;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.Map;
import java.util.Optional;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final DataManager data;
    private final LocaleManager locale;
    private final CrateManager crateManager;

    // Namespace key.
    private final NamespacedKey key;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;

        this.data = this.plugin.getManager(DataManager.class);
        this.locale = this.plugin.getManager(LocaleManager.class);
        this.crateManager = this.plugin.getManager(CrateManager.class);

        this.key = new NamespacedKey(this.plugin, "crateKey");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCratePreview(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        Block block = event.getClickedBlock();
        Optional<Crate> crate = this.crateManager.getCrateFromBlock(block);
        if (crate.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        this.plugin.getManager(MenuManager.class).getGUI("preview-gui").ifPresent(gui -> gui.open(player, crate.get()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Location location = block.getLocation();
        Optional<Crate> crate = this.crateManager.getCrateFromBlock(block);
        if (crate.isEmpty()) {
            return;
        }

        // Crate preview already handles cancels the event.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (crate.get().getType() == CrateType.PHYSICAL) {
            final ItemStack item = event.getItem();
            if (item == null) {
                this.locale.sendMessage(player, "crate-open-invalid-key");
                this.vroomPlayer(player);
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                this.locale.sendMessage(player, "crate-open-invalid-key");
                this.vroomPlayer(player);
                return;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!container.has(this.key, PersistentDataType.STRING)) {
                this.locale.sendMessage(player, "crate-open-invalid-key");
                this.vroomPlayer(player);
                return;
            }

            final String keyId = container.get(this.key, PersistentDataType.STRING);
            if (keyId == null || !keyId.equalsIgnoreCase(crate.get().getId())) {
                this.locale.sendMessage(player, "crate-open-invalid-key");
                this.vroomPlayer(player);
                return;
            }
        }

        if (crate.get().getType() == CrateType.VIRTUAL) {
            Map<String, Integer> usersKeys = this.data.getUsersVirtualKeys(player.getUniqueId());

            int totalKeys = usersKeys.getOrDefault(crate.get().getId().toLowerCase(), 0);
            if (totalKeys <= 0) {
                this.locale.sendMessage(player, "crate-open-no-keys");
                this.vroomPlayer(player);
                return;
            }
        }

        // CHeck if the user has enough slots for the items
        if (PluginUtils.getSpareSlots(player) < crate.get().getMinGuiSlots()) {
            this.locale.sendMessage(player, "crate-open-no-slots");
            return;
        }

        // Check if the user is already opening a crate.
        if (this.crateManager.getActiveUsers().contains(player.getUniqueId())) {
            this.locale.sendMessage(player, "crate-open-using-crate");
            return;
        }

        // Check if the crate is in animation
        if (crate.get().getAnimation().isActive()) {
            this.locale.sendMessage(player, "crate-open-animation-active");
            return;
        }

        switch (crate.get().getType()) {
            case PHYSICAL -> {
                ItemStack item = event.getItem();
                if (item == null)
                    return;

                if (crate.get().open(player, location)) {
                    // remove 1 of the Item from the player inventory
                    if (item.getAmount() == 1)
                        event.getPlayer().getInventory().setItemInMainHand(null);
                    else
                        item.setAmount(item.getAmount() - 1);
                }
            }

            case VIRTUAL -> {
                Map<String, Integer> usersKeys = this.data.getUsersVirtualKeys(player.getUniqueId());
                // Remove a key from the user
                int totalKeys = usersKeys.getOrDefault(crate.get().getId().toLowerCase(), 0);
                if (totalKeys > 0 && crate.get().open(player, location)) {
                    usersKeys.put(crate.get().getId().toLowerCase(), totalKeys - 1);
                    this.data.saveVirtualKeys(player.getUniqueId(), usersKeys);
                }
            }
        }

    }

    /**
     * Send the player vrooming
     *
     * @param player Player to send the effect to
     */
    private void vroomPlayer(Player player) {
        Vector vector = player.getLocation().getDirection().clone();
        vector = vector.clone().multiply(-1);
        vector.multiply(1);
        player.setVelocity(vector);
    }


}
