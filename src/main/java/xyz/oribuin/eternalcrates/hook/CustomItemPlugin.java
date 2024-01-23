package xyz.oribuin.eternalcrates.hook;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.provider.EcoItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.ExecutableItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.ItemBridgeItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.ItemEditItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.ItemsAdderItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.KnokkoCustomItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.MMOItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.OraxenItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.SlimefunItemProvider;
import xyz.oribuin.eternalcrates.hook.provider.UberItemProvider;
import xyz.oribuin.eternalcrates.util.CrateUtils;

/**
 * @author Esophose
 */
public enum CustomItemPlugin {
    ECO(new EcoItemProvider()),
    EXECUTABLEITEMS(new ExecutableItemProvider()),
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
    public static ItemStack parse(@Nullable String text, @Nullable Player player) {

        if (text == null) return null;

        String[] split = text.split(":");
        if (split.length != 2) return null;

        // get plugin
        CustomItemPlugin plugin = CrateUtils.getEnum(CustomItemPlugin.class, split[0]);
        if (plugin == null) return null;

        // get item
        return plugin.provider.getItem(split[1], player);
    }

}
