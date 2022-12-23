package xyz.oribuin.eternalcrates.hook.item;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExecutableItemProvider implements ItemProvider{

    @Override
    public String getPluginName() {
        return "ExecutableItems";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        return ExecutableItemsAPI.getExecutableItemsManager()
                .getExecutableItem(key)
                .map(x -> x.buildItem(1, Optional.ofNullable(player)))
                .orElse(null);
    }

}
