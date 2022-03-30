package xyz.oribuin.eternalcrates.gui;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.action.PluginAction;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.PaginatedGui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PreviewGUI extends OriGUI {

    public PreviewGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void open(Player player, Crate crate) {
        // Create the GUI
        final PaginatedGui gui = this.createPagedGUI(player, this.getPageSlots());
        final List<Integer> borderSlots = this.parseList(this.get("gui-settings.border-slots", List.of("0-8", "36-44")));

        ItemStack item = PluginUtils.getItemStack(this.config, "border-item", player, StringPlaceholders.empty());
        for (int slot : borderSlots) {
            this.put(gui, slot, item);
        }

        this.put(gui, "next-page", player, event -> gui.next(player));
        this.put(gui, "previous-page", player, event -> gui.previous(player));


        final CommentedConfigurationSection section = this.config.getConfigurationSection("extra-items");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                this.put(gui, "extra-items." + key, player, event -> this.get("extra-items." + key + ".actions", new ArrayList<String>())
                        .stream()
                        .map(PluginAction::parse)
                        .filter(Optional::isPresent)
                        .forEach(action -> action.get().execute(player, StringPlaceholders.empty())));
            }
        }

        crate.getRewardMap().forEach((key, value) -> gui.addPageItem(value.getPreviewItem(), event -> {
            // Do nothing
        }));

        gui.open(player);
    }

    @Override
    public int rows() {
        return this.get("gui-settings.rows", 5);
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.name", "Crate Preview");
            this.put("gui-settings.rows", 5);
            this.put("gui-settings.page-slots", List.of("9-35"));
            this.put("gui-settings.border-slots", List.of("0-8", "36-44"));

            this.put("#1", "Border Item");
            this.put("border-item.material", "BLACK_STAINED_GLASS_PANE");
            this.put("border-item.name", " ");

            this.put("#2", "Next Page");
            this.put("next-page.material", "ARROW");
            this.put("next-page.name", "#00B4DB&lNext Page");
            this.put("next-page.lore", List.of(" &f| &7Click to go to", " &f| &7the next page"));

            this.put("#3", "Previous Page");
            this.put("previous-page.material", "ARROW");
            this.put("previous-page.name", "#00B4DB&lPrevious Page");
            this.put("previous-page.lore", List.of(" &f| &7Click to go to", " &f| &7the previous page"));
        }};
    }

    @Override
    public String getMenuName() {
        return "preview-gui";
    }

    @Override
    public List<Integer> getPageSlots() {
        return this.parseList(this.get("gui-settings.page-slots", List.of("9-35")));
    }

    // Parse a List of X-X into a List of Integers
    public List<Integer> parseList(List<String> list) {
        List<Integer> newList = new ArrayList<>();
        for (String s : list) {
            String[] split = s.split("-");
            if (split.length != 2) {
                continue;
            }

            newList.addAll(this.getNumberRange(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        return newList;
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    private List<Integer> getNumberRange(int start, int end) {
        final List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }
}
