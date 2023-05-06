package xyz.oribuin.eternalcrates.gui;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PreviewGUI extends PluginMenu {

    public PreviewGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Open the preview GUI for a crate
     *
     * @param player The player to open the GUI for
     * @param crate  The crate to open the GUI for
     */
    public void open(Player player, Crate crate) {
        PaginatedGui gui = this.createPagedGUI(player);

        final StringPlaceholders cratePlaceholders = StringPlaceholders.builder("crate_id", crate.getId())
                .add("crate", crate.getName())
                .add("crate_rewards", String.valueOf(crate.getRewardMap().size()))
                .add("crate_type", StringUtils.capitalize(crate.getType().name().toLowerCase()))
                .add("crate_min", crate.getMinRewards())
                .add("crate_max", crate.getMaxRewards())
                .add("crate_multiplier", crate.getMultiplier())
                .build();

        final CommentedConfigurationSection extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            for (String key : extraItems.getKeys(false)) {
                MenuItem.create(this.config)
                        .path("extra-items." + key)
                        .placeholders(cratePlaceholders)
                        .player(player)
                        .place(gui);
            }
        }

        MenuItem.create(this.config)
                .path("next-page")
                .player(player)
                .placeholders(cratePlaceholders)
                .action(event -> gui.next())
                .condition(menuItem -> gui.getNextPageNum() > gui.getCurrentPageNum())
                .place(gui);

        MenuItem.create(this.config)
                .path("previous-page")
                .player(player)
                .placeholders(cratePlaceholders)
                .action(event -> gui.previous())
                .condition(menuItem -> gui.getPrevPageNum() < gui.getCurrentPageNum())
                .place(gui);

        crate.getRewardMap().forEach((slot, reward) -> gui.addItem(new GuiItem(reward.getPreviewItem())));
        gui.open(player);
    }

//    public Map<String, Object> getDefaultValues() {
//        return new LinkedHashMap<>() {{
//            this.put("#0", "GUI Settings");
//            this.put("gui-settings.title", "Crate Preview");
//            this.put("gui-settings.rows", 5);
//
//            this.put("#2", "Page Settings");
//            this.put("next-page.material", "PAPER");
//            this.put("next-page.name", "#00B4DB&lNext Page");
//            this.put("next-page.slot", 5);
//
//            this.put("previous-page.material", "PAPER");
//            this.put("previous-page.name", "#00B4DB&lPrevious Page");
//            this.put("previous-page.slot", 3);
//
//            this.put("#3", "Extra Items");
//            this.put("extra-items.border-item.material", "BLACK_STAINED_GLASS_PANE");
//            this.put("extra-items.border-item.name", " ");
//            this.put("extra-items.border-item.slots", List.of("0-8", "36-44"));
//
//            this.put("extra-items.crate-info.material", "OAK_SIGN");
//            this.put("extra-items.crate-info.name", "#00B4DB&lCrate Info");
//            this.put("extra-items.crate-info.lore", List.of(
//                    " &f| #00B4DB&lName: &7%crate%",
//                    " &f| #00B4DB&lType: &7%crate_type%",
//                    " &f| #00B4DB&lRewards: &7%crate_rewards%",
//                    " &f| #00B4DB&lMin Rewards: &7%crate_min%",
//                    " &f| #00B4DB&lMax Rewards: &7%crate_max%",
//                    " &f| #00B4DB&lMultiplier: &7%crate_multiplier%"
//            ));
//            this.put("extra-items.crate-info.slot", 4);
//
//
//        }};
//    }

    @Override
    public String getMenuName() {
        return "preview-gui";
    }

}
