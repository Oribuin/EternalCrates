package xyz.oribuin.eternalcrates.hook.provider;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class ItemsXLItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "ItemsXL";
    }

    @Override
    public ItemStack getItem(@NotNull final String key, @Nullable final Player player) {
        ExItem exItem = CaliburnAPI.getInstance().getExItem(key);
        if (exItem != null)
            return exItem.toItemStack();

        return null;
    }

}
