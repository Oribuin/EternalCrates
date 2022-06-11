package xyz.oribuin.eternalcrates.gui;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.action.Action;
import xyz.oribuin.eternalcrates.action.BroadcastAction;
import xyz.oribuin.eternalcrates.action.CloseAction;
import xyz.oribuin.eternalcrates.action.ConsoleAction;
import xyz.oribuin.eternalcrates.action.GiveAction;
import xyz.oribuin.eternalcrates.action.MessageAction;
import xyz.oribuin.eternalcrates.action.PlayerAction;
import xyz.oribuin.eternalcrates.action.SoundAction;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClaimGUI extends OriMenu {

    private final DataManager data = this.rosePlugin.getManager(DataManager.class);
    private final List<Action> actions = Arrays.asList(
            new BroadcastAction(),
            new CloseAction(),
            new ConsoleAction(),
            new GiveAction(),
            new MessageAction(),
            new PlayerAction(),
            new SoundAction()
    );

    public ClaimGUI(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player, Crate crate) {
        final PaginatedGui gui = this.createPagedGUI(player);

        final ItemStack borderItem = PluginUtils.getItemStack(this.config, "border-item", player, StringPlaceholders.empty());
        List<Integer> borderSlots = this.parseList(this.get("gui-settings.border-slots", List.of("35-54")));
        gui.setItem(borderSlots, new GuiItem(borderItem));
        crate.get


    }
//
//    @Override
//    public void open(Player player, Crate crate) {
//        // Create the GUI
//        final PaginatedGui gui = this.createPagedGUI(player, this.getPageSlots());
//        gui.setCloseAction(event -> this.data.saveUnclaimedKeys(event.getPlayer().getUniqueId()));
//
//        final List<Integer> borderSlots = this.parseList(this.get("gui-settings.border-slots", List.of("0-8", "36-44")));
//
//        ItemStack item = PluginUtils.getItemStack(this.config, "border-item", player, StringPlaceholders.empty());
//        for (int slot : borderSlots) {
//            this.put(gui, slot, item);
//        }
//
//        this.put(gui, "next-page", player, event -> gui.next(player));
//        this.put(gui, "previous-page", player, event -> gui.previous(player));
//
//        final CommentedConfigurationSection section = this.config.getConfigurationSection("extra-items");
//        if (section != null) {
//            for (String key : section.getKeys(false)) {
//                this.put(gui, "extra-items." + key, player, event -> this.get("extra-items." + key + ".actions", new ArrayList<String>())
//                        .stream()
//                        .map(PluginAction::parse)
//                        .filter(Optional::isPresent)
//                        .forEach(action -> action.get().execute(player, StringPlaceholders.empty())));
//            }
//        }
//
//        this.addUnclaimedKeys(gui, player);
//        gui.open(player);
//    }
//
//    /**
//     * Add all unclaimed keys to the gui
//     *
//     * @param gui    The gui to add the keys to
//     * @param player The player who owns the keys
//     */
//    private void addUnclaimedKeys(PaginatedGui gui, Player player) {
//        List<ItemStack> unclaimedKeys = new ArrayList<>(data.getUnclaimedKeys(player.getUniqueId()));
//        unclaimedKeys.forEach(item -> gui.addPageItem(item, event -> {
//            if (!player.getInventory().addItem(item).isEmpty()) {
//                unclaimedKeys.remove(item);
//                data.getUnclaimedKeys().put(player.getUniqueId(), unclaimedKeys);
//            }
//
//            gui.getPageItems().clear();
//            this.addUnclaimedKeys(gui, player);
//            gui.update();
//        }));
//    }

    @Override
    public int rows() {
        return this.get("gui-settings.rows", 5);
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.name", "Claim unclaimed keys");
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
            this.put("next-page.slot", 3);

            this.put("#3", "Previous Page");
            this.put("previous-page.material", "ARROW");
            this.put("previous-page.name", "#00B4DB&lPrevious Page");
            this.put("previous-page.lore", List.of(" &f| &7Click to go to", " &f| &7the previous page"));
            this.put("previous-page.slot", 5);

            this.put("#4", "Extra Item Settings");
            this.put("extra-items.1.material", "SPRUCE_SIGN");
            this.put("extra-items.1.name", "#00B4DB&lUnclaimed Keys");
            this.put("extra-items.1.lore", List.of(" &f| &7Click to claim", " &f| &7any unclaimed keys"));
            this.put("extra-items.1.slot", 4);
            this.put("extra-items.1.glow", true);
        }};
    }

    @Override
    public String getMenuName() {
        return "claim-gui";
    }

    @Override
    public List<Integer> getPageSlots() {
        return this.parseList(this.get("gui-settings.page-slots", List.of("9-35")));
    }

}
