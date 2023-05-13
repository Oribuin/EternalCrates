package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public class CloseAction extends Action {

    public CloseAction() {
        super("close");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders) {
        player.closeInventory();
    }


}
