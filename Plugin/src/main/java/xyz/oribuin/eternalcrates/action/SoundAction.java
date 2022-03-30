package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public class SoundAction extends Action {

    @Override
    public void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders) {
        if (this.getMessage().length() == 0)
            return;

        player.playSound(player.getLocation(), Sound.valueOf(this.getMessage()), 100f, 1f);
    }

}
