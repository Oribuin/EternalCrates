package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public class TitleAction extends Action {

    // P
    @Override
    public void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders) {
        // Parse the title and subtitle through title:"title" subtitle:"subtitle" from this.getmessage()
        String title = this.getMessage().split(" ")[1];
        String subtitle = this.getMessage().split(" ")[3];
        player.sendTitle(title, subtitle, 10, 60, 10);
    }
}
