package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public class GiveAction extends Action {

    public GiveAction() {
        super("give");
    }

    @Override
    public void execute(@Nullable final Reward reward, @NotNull final Player player, @NotNull final StringPlaceholders placeholders) {
        if (reward == null) return;

        player.getInventory().addItem(reward.getItemStack());
    }

}
