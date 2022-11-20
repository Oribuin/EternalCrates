package xyz.oribuin.eternalcrates.hook;

import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.hook.item.EcoItemProvider;
import xyz.oribuin.eternalcrates.hook.item.ItemBridgeItemProvider;
import xyz.oribuin.eternalcrates.hook.item.ItemEditItemProvider;
import xyz.oribuin.eternalcrates.hook.item.ItemProvider;
import xyz.oribuin.eternalcrates.hook.item.ItemsAdderItemProvider;
import xyz.oribuin.eternalcrates.hook.item.KnokkoCustomItemProvider;
import xyz.oribuin.eternalcrates.hook.item.MMOItemProvider;
import xyz.oribuin.eternalcrates.hook.item.OraxenItemProvider;
import xyz.oribuin.eternalcrates.hook.item.SlimefunItemProvider;
import xyz.oribuin.eternalcrates.hook.item.UberItemProvider;

/**
 * @author Esophose
 */
public enum CustomItemPlugin {
    ECOITEMS(new EcoItemProvider()),
    ITEMBRIDGE(new ItemBridgeItemProvider()),
    ITEMEDIT(new ItemEditItemProvider()),
    ITEMSADDER(new ItemsAdderItemProvider()),
    KNOKKOCUSTOMITEMS(new KnokkoCustomItemProvider()),
    MMOITEMS(new MMOItemProvider()),
    ORAXEN(new OraxenItemProvider()),
    SLIMEFUN(new SlimefunItemProvider()),
    UBERITEMS(new UberItemProvider());


    private final ItemProvider provider;

    CustomItemPlugin(ItemProvider provider) {
        this.provider = provider;
    }

    /**
     * Parse the text into a custom plugin item
     *
     * @param text The text to parse
     * @return The custom plugin item
     */
    public static ItemStack parse(String text) {
        if (text == null)
            return null;

        var split = text.split(":");
        if (split.length != 2) {
            return null;
        }

        // get plugin
        var plugin = CustomItemPlugin.fromString(split[0]);
        if (plugin == null) {
            return null;
        }

        // get item
        return plugin.provider.getItem(split[1]);
    }

    /**
     * Test if the text is a custom plugin item
     *
     * @param text The text to test
     * @return True if the text is a custom plugin item
     */
    public ItemStack test(String text) {
        return this.provider.getItem(text);
    }

    /**
     * Get the provider
     *
     * @param name The name of the provider
     * @return The provider
     */
    public static CustomItemPlugin fromString(String name) {
        for (CustomItemPlugin plugin : values()) {
            if (plugin.name().equalsIgnoreCase(name)) {
                return plugin;
            }
        }

        return null;
    }

}
