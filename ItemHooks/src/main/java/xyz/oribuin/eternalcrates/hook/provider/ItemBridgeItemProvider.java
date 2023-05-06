package xyz.oribuin.eternalcrates.hook.provider;

import com.jojodmo.itembridge.ItemBridge;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.hook.ItemProvider;

public class ItemBridgeItemProvider implements ItemProvider {

    @Override
    public String getPluginName() {
        return "ItemBridge";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        return ItemBridge.getItemStack(key);
    }

}
