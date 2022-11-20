package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.crate.Crate;

public abstract class CustomAnimation extends Animation {

    public CustomAnimation(String name, String author, AnimationType type) {
        super(name, type, author, true);
    }

    /**
     * The function for when the player spawns
     *
     * @param location The location of the crate
     * @param player   The player who is opening the crate.
     */
    public abstract void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate);
}
