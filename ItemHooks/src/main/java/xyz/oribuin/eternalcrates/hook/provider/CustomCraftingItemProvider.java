package xyz.oribuin.eternalcrates.hook.provider;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

import java.util.regex.Pattern;

/**
 * @author Esophose via RoseLoot
 */
public class CustomCraftingItemProvider implements ItemProvider {
    @Override
    public String getPluginName() {
        return "CustomCrafting";
    }

    @Override
    public ItemStack getItem(@NotNull final String key, @Nullable final Player player) {

        String[] pieces = key.split(Pattern.quote(":"), 2);
        if (pieces.length != 2)
            return null;

        CustomItem customItem = CustomCrafting.inst().getApi()
                .getRegistries()
                .getCustomItems()
                .get(new NamespacedKey(pieces[0], pieces[1]));

        if (customItem != null)
            return customItem.create();

        return null;
    }

}
