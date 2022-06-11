package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.hook.PAPI;

public class ConsoleAction extends Action {

    @Override
    public void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders) {
        if (this.getMessage().length() == 0)
            return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), HexUtils.colorify(PAPI.apply(player, placeholders.apply(this.getMessage()))));
    }

}
