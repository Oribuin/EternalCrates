package xyz.oribuin.eternalcrates.hook.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thirtyvirus.uber.UberItems;

public class UberItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "UberItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        var item = UberItems.getItem(key);
        if (item != null) {
            return item.makeItem(1);
        }

        var material = UberItems.getMaterial(key);
        if (material != null) {
            return material.makeItem(1);
        }

        return null;
    }
}
