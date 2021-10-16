package xyz.oribuin.eternalcrates.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;

import java.util.ArrayList;
import java.util.List;

public class PreviewGUI {

    private final EternalCrates plugin;
    private final Crate crate;

    public PreviewGUI(final EternalCrates plugin, Crate crate) {
        this.plugin = plugin;
        this.crate = crate;
    }

    public void create(Player player) {
        final List<Integer> pageSlots = new ArrayList<>();
        for (int i = 9; i < 35; i++)
            pageSlots.add(i);

        // TODO
        final PaginatedGui gui = new PaginatedGui(45, crate.getDisplayName(), pageSlots);


        gui.open(player);
    }

    private ItemStack getItem(String path) {
        return new Item.Builder(Material.ACACIA_BOAT).create();
    }

}
