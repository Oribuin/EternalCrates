package xyz.oribuin.eternalcrates.gui;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static xyz.oribuin.eternalcrates.util.PluginUtils.get;
import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class ClaimGUI {

    private final EternalCrates plugin;
    private final DataManager data;

    public ClaimGUI(final EternalCrates plugin) {
        this.plugin = plugin;
        this.data = this.plugin.getManager(DataManager.class);
    }

    public void create(Player player) {
        final List<Integer> pageSlots = new ArrayList<>();
        for (int i = 9; i < 36; i++)
            pageSlots.add(i);

        final PaginatedGui gui = new PaginatedGui(45, PluginUtils.get(this.plugin.getConfig(), "claim-gui.title", "Unclaimed Crate Keys."), pageSlots);

        final List<ItemStack> savedItems = new ArrayList<>(data.getItems(player.getUniqueId()));

        gui.setDefaultClickFunction(event -> {
            gui.getPersonalClickAction().accept(event);

            if (!pageSlots.contains(event.getSlot()))
                return;

            final ItemStack item = event.getCurrentItem();
            if (item == null)
                return;

            final Inventory inv = event.getWhoClicked().getInventory();
            if (inv.firstEmpty() == -1) {
                return;
            }

            savedItems.remove(item);
            this.data.getCachedUsers().put(player.getUniqueId(), savedItems);
            inv.addItem(item);
            this.setUnclaimedItems(gui, player);
        });

        // Stop clicking preview gui
        // TODO, There will be an issue where if the user has too many crate keys stored, They wont get them.
        gui.setCloseAction(e -> data.saveUser(player.getUniqueId(), savedItems));

        // Stop clicking personal inventory
        gui.setPersonalClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();
        });

        // Set the list of border slots
        final List<Integer> borderSlots = new ArrayList<>();
        for (int i = 0; i <= 8; i++)
            borderSlots.add(i);
        for (int i = 36; i <= 44; i++)
            borderSlots.add(i);

        // Add the border items
        gui.setItems(borderSlots, this.getItem("claim-gui.border-item"), e -> {});

        // Next Page Item
        gui.setItem(44, this.getItem("claim-gui.next-page"), e -> gui.next(player));

        // Previous Page Item
        gui.setItem(36, this.getItem("claim-gui.previous-page"), e -> gui.previous(player));

        this.setUnclaimedItems(gui, player);

        // Add all the rewards to the gui.
        gui.open(player);
    }

    /**
     * Set all the unclaimed keys into the gui.
     *
     * @param gui    The Paginated GUI
     * @param player The player who owns the items.
     */
    private void setUnclaimedItems(PaginatedGui gui, Player player) {
        gui.getPageItems().clear();
        data.getItems(player.getUniqueId()).forEach(itemStack -> gui.addPageItem(itemStack, e -> setUnclaimedItems(gui, player)));
        gui.update();
    }

    private ItemStack getItem(String path) {
        final FileConfiguration config = this.plugin.getConfig();

        final String materialName = get(config, path + ".material", "STRUCTURE_VOID");
        final Material material = Optional.ofNullable(Material.matchMaterial(materialName)).orElse(Material.STRUCTURE_VOID);

        return new Item.Builder(material)
                .setName(colorify(get(config, path + ".name", null)))
                .setLore(get(config, path + ".lore", new ArrayList<String>())
                        .stream()
                        .map(HexUtils::colorify)
                        .collect(Collectors.toList()))
                .setAmount(Math.max(PluginUtils.get(config, path + ".amount", 1), 1))
                .glow(PluginUtils.get(config, path + ".glow", false))
                .setTexture(PluginUtils.get(config, path + ".texture", null))
                .create();
    }

}
