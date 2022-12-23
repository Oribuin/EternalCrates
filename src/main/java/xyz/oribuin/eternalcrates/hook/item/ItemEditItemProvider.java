package xyz.oribuin.eternalcrates.hook.item;

import emanondev.itemedit.ItemEdit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemEditItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "ItemEdit";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        return ItemEdit.get().getServerStorage().getItem(key);
    }

}
