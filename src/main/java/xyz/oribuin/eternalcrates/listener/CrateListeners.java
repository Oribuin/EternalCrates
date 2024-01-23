package xyz.oribuin.eternalcrates.listener;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
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
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.event.CrateDestroyEvent;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.CrateUtils;

public class CrateListeners implements Listener {

    private final EternalCrates plugin;
    private final LocaleManager locale;
    private final CrateManager manager;

    public CrateListeners(EternalCrates plugin) {
        this.plugin = plugin;

        this.locale = this.plugin.getManager(LocaleManager.class);
        this.manager = this.plugin.getManager(CrateManager.class);
    }

    /**
     * Called when the player tries to break the crate.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCrateDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Crate crate = this.manager.get(block.getLocation());
        if (crate == null) return;

        event.setCancelled(true);

        // Check if the player is sneaking and has the permission to destroy the crate.
        if (!player.isSneaking() && !player.hasPermission("eternalcrates.admin"))
            return;

        // Trigger the crate destroy event
        CrateDestroyEvent destroyEvent = new CrateDestroyEvent(crate, player);
        this.plugin.getServer().getPluginManager().callEvent(destroyEvent);
        final ParticleData data = new ParticleData(Particle.REDSTONE);
        data.setDustOptions(Color.RED);
        CrateUtils.outline(
                data,
                block,
                player
        );

        crate.getLocations().remove(block.getLocation());
        this.manager.save(crate);
        this.locale.sendMessage(player, "crate-remove-success", StringPlaceholders.of("crate", crate.getId()));
    }

    /**
     * Allow the player to preview the crate
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrateInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (block == null) return;

        Crate crate = this.manager.get(block.getLocation());
        if (crate == null) return;

        // Preview the crate on left click
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.hasPermission("eternalcrates.admin") && player.isSneaking()) return;

            event.setCancelled(true);
            this.plugin.getManager(MenuManager.class).getGUI(PreviewGUI.class).open(player, crate);
            return;
        }

        event.setCancelled(true);

        // Player is trying to open the crate
        // Make sure the player has enough slots for the items
        if (CrateUtils.getSpareSlots(player) < crate.getSettings().requiredSlots()) {
            this.locale.sendMessage(player, "crate-open-no-slots");
        }

        // Check if the crate location is already in use
        if (crate.isActive(block.getLocation())) {
            this.locale.sendMessage(player, "crate-open-animation-active");
            return;
        }

        if (!crate.hasKey(player)) {
            this.locale.sendMessage(player, "crate-open-no-keys");
            return;
        }

        // GIve the player a crate key
        crate.use(player);
    }

}
