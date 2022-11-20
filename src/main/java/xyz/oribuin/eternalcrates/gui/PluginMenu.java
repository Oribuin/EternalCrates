package xyz.oribuin.eternalcrates.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.ScrollingGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public abstract class PluginMenu {

    protected final RosePlugin rosePlugin;
    protected CommentedFileConfiguration config;

    public PluginMenu(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    /**
     * Get the default config values for the GUI
     *
     * @return The default config values
     */
    public abstract Map<String, Object> getDefaultValues();

    /**
     * @return The name of the GUI
     */
    public abstract String getMenuName();

    /**
     * Create the menu file if it doesn't exist and add the default values
     */
    public final void load() {
        final var folder = new File(this.rosePlugin.getDataFolder(), "menus");
        var newFile = false;
        if (!folder.exists()) {
            folder.mkdirs();
        }

        final var file = new File(folder, this.getMenuName() + ".yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
                newFile = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.config = CommentedFileConfiguration.loadConfiguration(file);
        if (newFile) {
            this.getDefaultValues().forEach((path, object) -> {
                if (path.startsWith("#")) {
                    this.config.addPathedComments(path, (String) object);
                } else {
                    this.config.set(path, object);
                }
            });
        }

        this.config.save();
    }


    /**
     * Create a paged GUI for the given player
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull PaginatedGui createPagedGUI(Player player) {

        final var rows = this.config.getInt("gui-settings.rows");
        final var title = this.config.getString("gui-settings.title");

        return Gui.paginated()
                .rows(rows == 0 ? 6 : rows)
                .title(this.format(player, title == null ? "Missing Title" : title))
                .disableAllInteractions()
                .create();
    }

    /**
     * Create a GUI for the given player without pages
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull Gui createGUI(Player player) {
        final var rows = this.config.getInt("gui-settings.rows");
        final var title = this.config.getString("gui-settings.title");

        return Gui.gui()
                .rows(rows == 0 ? 6 : rows)
                .title(this.format(player, title == null ? "Missing Title" : title))
                .disableAllInteractions()
                .create();
    }

    /**
     * Scrolling gui for the given player
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    protected final @NotNull ScrollingGui createScrollingGui(Player player, ScrollType scrollType) {

        final var rows = this.config.getInt("gui-settings.rows");
        final var title = this.config.getString("gui-settings.title");

        return Gui.scrolling()
                .scrollType(scrollType)
                .rows(rows == 0 ? 6 : rows)
                .pageSize(0)
                .title(this.format(player, title == null ? "Missing Title" : title))
                .disableAllInteractions()
                .create();
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    protected final Component format(Player player, String text) {
        return Component.text(PluginUtils.format(player, text));
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    protected final Component format(Player player, String text, StringPlaceholders placeholders) {
        return Component.text(PluginUtils.format(player, text, placeholders));
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The text to format
     * @return The formatted string
     */
    protected final String formatString(Player player, String text) {
        return PluginUtils.format(player, text);
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    protected final String formatString(Player player, String text, StringPlaceholders placeholders) {
        return PluginUtils.format(player, text, placeholders);
    }

}
