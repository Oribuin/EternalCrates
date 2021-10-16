package xyz.oribuin.eternalcrates.gui;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;
import xyz.oribuin.orilibrary.util.HexUtils;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.*;
import java.util.stream.Collectors;

import static xyz.oribuin.eternalcrates.util.PluginUtils.get;
import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class PreviewGUI {

    private final Crate crate;

    public PreviewGUI(final EternalCrates plugin, Crate crate) {
        this.crate = crate;
    }

    public void create(Player player) {
        final List<Integer> pageSlots = new ArrayList<>();
        for (int i = 9; i < 35; i++)
            pageSlots.add(i);

        final PaginatedGui gui = new PaginatedGui(45, crate.getDisplayName(), pageSlots);

        // Stop clicking preview gui
        gui.setDefaultClickFunction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();
        });

        // Stop clicking personal inventory
        gui.setPersonalClickAction(e -> gui.getDefaultClickFunction().accept(e));

        // Set the list of border slots
        final List<Integer> borderSlots = new ArrayList<>();
        for (int i = 0; i <= 8; i++)
            borderSlots.add(i);
        for (int i = 36; i <= 44; i++)
            borderSlots.add(i);

        // Add the border items
        this.getItem("preview-gui.border-item").ifPresent(item -> gui.setItems(borderSlots, item, e -> {}));

        // Next Page Item
        if (gui.getPage() < gui.getTotalPages()) {
            this.getItem("preview-gui.next-page").ifPresent(item -> gui.setItem(36, item, e -> {}));
        }

        // Previous Page Item
        if (gui.getPage() > 1) {
            this.getItem("next-gui.previous-page").ifPresent(item -> gui.setItem(44, item, e -> {}));
        }

        // Add all the rewards to the gui.
        new HashMap<>(crate.getRewardMap()).entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getValue)) // sort by lowest chance first
                .forEach(entry -> {
                    final ItemStack item = entry.getKey().getDisplayItem().clone();
                    final ItemMeta meta = item.getItemMeta();
                    if (meta == null)
                        return;

                    List<String> newLore = new ArrayList<>(this.crate.getConfig().getStringList("preview-gui.item-lore"))
                            .stream().
                            map(s -> StringPlaceholders.single("chance", entry.getValue()).apply(s))
                            .collect(Collectors.toList());

                    if (meta.getLore() != null)
                        newLore.addAll(meta.getLore());

                    newLore = newLore.stream().map(HexUtils::colorify)
                            .collect(Collectors.toList());
                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                    gui.addPageItem(item, event -> {});
                });

        gui.open(player);
    }

    private Optional<ItemStack> getItem(String path) {
        final FileConfiguration config = this.crate.getConfig();
        if (config == null)
            return Optional.empty();

        final String materialName = get(config, path + ".material", "STRUCTURE_VOID");
        final Material material = Optional.ofNullable(Material.matchMaterial(materialName)).orElse(Material.STRUCTURE_VOID);

        return Optional.of(new Item.Builder(material)
                .setName(colorify(get(config, path + ".name", null)))
                .setLore(get(config, path + ".lore", new ArrayList<String>())
                        .stream()
                        .map(HexUtils::colorify)
                        .collect(Collectors.toList()))
                .setAmount(Math.max(PluginUtils.get(config, path + ".amount", 1), 1))
                .glow(PluginUtils.get(config, path + ".glow", false))
                .setTexture(PluginUtils.get(config, path + ".texture", null))
                .create());
    }

}
