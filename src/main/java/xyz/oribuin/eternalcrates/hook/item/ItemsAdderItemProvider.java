package xyz.oribuin.eternalcrates.hook.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "ItemsAdder";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        var customStack = CustomStack.getInstance(key);
        if (customStack == null) {
            return null;
        }

        return customStack.getItemStack();
    }

}
