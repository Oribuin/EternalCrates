package xyz.oribuin.eternalcrates.hook;

import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.hook.items.EcoItemProvider;
import xyz.oribuin.eternalcrates.hook.items.ItemBridgeItemProvider;
import xyz.oribuin.eternalcrates.hook.items.ItemEditItemProvider;
import xyz.oribuin.eternalcrates.hook.items.ItemProvider;
import xyz.oribuin.eternalcrates.hook.items.ItemsAdderItemProvider;
import xyz.oribuin.eternalcrates.hook.items.KnokkoCustomItemProvider;
import xyz.oribuin.eternalcrates.hook.items.MMOItemProvider;
import xyz.oribuin.eternalcrates.hook.items.OraxenItemProvider;
import xyz.oribuin.eternalcrates.hook.items.SlimefunItemProvider;
import xyz.oribuin.eternalcrates.hook.items.UberItemProvider;

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

        String[] split = text.split(":");
        if (split.length != 2) {
            return null;
        }

        // get plugin
        CustomItemPlugin plugin = CustomItemPlugin.fromString(split[0]);
        if (plugin == null) {
            return null;
        }

        System.out.println("[EternalCrates] Plugin " + split[0] + " with item " + split[1]);
        // get item
        return plugin.provider.getItem(split[1]);
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
