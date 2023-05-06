package xyz.oribuin.eternalcrates.hook.provider;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thirtyvirus.uber.UberItem;
import thirtyvirus.uber.UberItems;
import thirtyvirus.uber.UberMaterial;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class UberItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "UberItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {

        UberItem item = UberItems.getItem(key);
        if (item != null) {
            return item.makeItem(1);
        }

        UberMaterial material = UberItems.getMaterial(key);
        if (material != null) {
            return material.makeItem(1);
        }

        return null;
    }
}
