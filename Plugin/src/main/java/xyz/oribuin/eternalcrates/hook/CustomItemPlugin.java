package xyz.oribuin.eternalcrates.hook;

import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.hook.items.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Esophose
 */
public final class CustomItemPlugin {
    private static final Map<String, ItemProvider> PROVIDERS = new HashMap<>();

    static {
        register("ecoitems", new EcoItemProvider());
        register("itembridge", new ItemBridgeItemProvider());
        register("itemedit", new ItemEditItemProvider());
        register("itemsadder", new ItemsAdderItemProvider());
        register("knokkocustomitems", new KnokkoCustomItemProvider());
        register("mmoitems", new MMOItemProvider());
        register("oraxen", new OraxenItemProvider());
        register("slimefun", new SlimefunItemProvider());
        register("uberitems", new UberItemProvider());
    }

    /**
     * Register a provider
     *
     * @param name     The name of the provider
     * @param provider The provider
     */
    public static void register(String name, ItemProvider provider) {
        // toLowerCase to avoid case-sensitive issues
        PROVIDERS.put(name.toLowerCase(Locale.ROOT), provider);
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
        String providerName = split[0].trim();
        String itemName = split[1].trim();

        // get provider
        ItemProvider provider = CustomItemPlugin.getProvider(providerName);
        if (provider == null) {
            return null;
        }

        System.out.println("[EternalCrates] Plugin " + providerName + " with item " + itemName);
        // get item
        return provider.getItem(itemName);
    }

    /**
     * Get the provider
     *
     * @param name The name of the provider
     * @return The provider
     */
    public static ItemProvider getProvider(String name) {
        return PROVIDERS.get(name.toLowerCase(Locale.ROOT)); // toLowerCase to avoid case-sensitive issues
    }

}
