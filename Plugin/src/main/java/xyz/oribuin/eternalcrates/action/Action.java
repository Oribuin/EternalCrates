package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public abstract class Action {

    private String message = "";

    /**
     * Execute the action function
     *
     * @param reward       Reward instance
     * @param player       The player
     * @param placeholders Message placeholders
     */
    public abstract void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders);

    /**
     * Execute action function with null reward
     *
     * @param player       The player
     * @param placeholders Message placeholders
     */
    public final void execute(Player player, StringPlaceholders placeholders) {
        this.execute(null, player, placeholders);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
