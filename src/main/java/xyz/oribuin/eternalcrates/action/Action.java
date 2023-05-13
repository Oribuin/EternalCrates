package xyz.oribuin.eternalcrates.action;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.eternalcrates.crate.Reward;

public abstract class Action {

    private final @NotNull String name;
    private String message = "";

    protected Action(@NotNull String name) {
        this.name = name;
    }

    /**
     * Execute the action function
     *
     * @param player       The player
     * @param placeholders Message placeholders
     */
    public abstract void execute(@Nullable Reward reward, @NotNull Player player, @NotNull StringPlaceholders placeholders);

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Action setMessage(String message) {
        this.message = message;
        return this;
    }

}