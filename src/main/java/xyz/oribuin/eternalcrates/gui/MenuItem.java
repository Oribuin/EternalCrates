package xyz.oribuin.eternalcrates.gui;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MenuItem {

    private CommentedFileConfiguration config; // The config file for the menu
    private ItemStack customItem; // The item to be displayed
    private String itemPath; // The path to the item in the config
    private StringPlaceholders placeholders; // The custom placeholders for the item
    private Player player; //  The player who is viewing the menu, this is used for placeholders
    private Consumer<InventoryClickEvent> action; // The action to be performed when the item is clicked
    private List<Integer> slots; // The slots the item should be placed in
    private Predicate<MenuItem> condition; // The condition for the item to be displayed

    public MenuItem() {
        throw new UnsupportedOperationException("This class cannot be instantiated, Use MenuItem.create() instead.");
    }

    private MenuItem(CommentedFileConfiguration config) {
        this.config = config;
        this.customItem = null;
        this.itemPath = null;
        this.placeholders = StringPlaceholders.empty();
        this.player = null;
        this.action = inventoryClickEvent -> {
        }; // Do nothing
        this.slots = new ArrayList<>();
        this.condition = menuItem -> true;
    }

    /**
     * Create a new MenuItem instance
     *
     * @param config The config file for the menu
     * @return A new MenuItem instance
     */
    public static MenuItem create(CommentedFileConfiguration config) {
        return new MenuItem(config);
    }

    public final void place(BaseGui gui) {

        // Make sure there is a config file
        if (this.config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }

        // Make sure the item is not null
        if (this.customItem == null && this.itemPath == null) {
            throw new IllegalArgumentException("Item path and custom item cannot both be null");
        }

        // Check if the item path is null
        if (this.config.get(this.itemPath) == null && this.customItem == null) {
            return; // The item path is null and the custom item is null, so we don't need to do anything
        }

        // Check if the item is enabled
        if (!this.config.getBoolean(this.itemPath + ".enabled", true)) {
            return;
        }

        if (!this.isConditional())
            return;

        // Add any slots that were not added
        if (this.slots.isEmpty()) {
            // We check for the singular slot first
            int slot = (int) this.config.get(this.itemPath + ".slot", -1);
            if (slot != -1) {
                this.slot(slot);
            }

            // Then we check for the multiple slots
            boolean hasMultiSlots = this.config.get(this.itemPath + ".slots") != null;
            if (hasMultiSlots) {
                this.slots(CrateUtils.parseList(this.config.getStringList(this.itemPath + ".slots")));
            }
        }

        ItemStack item = this.customItem != null
                ? this.customItem
                : CrateUtils.deserialize(this.config, this.player, this.itemPath, this.placeholders);

        if (item == null) {
            EternalCrates.get().getLogger().warning("Item [" + this.itemPath + "] in the [" + this.config.getName() + "] menu is invalid.");
            return;
        }

        this.slots.forEach(slot -> gui.setItem(slot, new GuiItem(item, this.action::accept)));
        gui.update();

    }

    /**
     * Get the path to the item in the config
     *
     * @return The path
     */
    public String getItemPath() {
        return itemPath;
    }

    /**
     * Set the path to the item in the config
     *
     * @param path The path
     * @return The MenuItem
     */
    public final MenuItem path(String path) {
        this.itemPath = path;
        return this;
    }

    public ItemStack getCustomItem() {
        return customItem;
    }

    public final MenuItem item(ItemStack item) {
        this.customItem = item;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public final MenuItem player(Player player) {
        this.player = player;
        return this;
    }

    public StringPlaceholders getPlaceholders() {
        return placeholders;
    }

    public final MenuItem placeholders(StringPlaceholders placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public Consumer<InventoryClickEvent> getAction() {
        return action;
    }

    public final MenuItem action(Consumer<InventoryClickEvent> action) {
        this.action = action;
        return this;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public final MenuItem slots(List<Integer> slots) {
        this.slots = slots;
        return this;
    }

    public final MenuItem slot(int slot) {
        this.slots = List.of(slot);
        return this;
    }

    public CommentedFileConfiguration getConfig() {
        return config;
    }

    public final MenuItem config(CommentedFileConfiguration config) {
        this.config = config;
        return this;
    }

    public boolean isConditional() {
        return condition.test(this);
    }

    public final MenuItem condition(Predicate<MenuItem> condition) {
        this.condition = condition;
        return this;
    }

}
