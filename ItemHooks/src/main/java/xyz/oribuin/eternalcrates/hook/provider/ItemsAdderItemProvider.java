package xyz.oribuin.eternalcrates.hook.provider;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class ItemsAdderItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "ItemsAdder";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        CustomStack customStack = CustomStack.getInstance(key);
        if (customStack != null)
            return customStack.getItemStack();

        return null;
    }

}
