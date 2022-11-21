package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.crate.Crate;

public abstract class MiscAnimation extends Animation {

    public MiscAnimation(String name, String author) {
        super(name, AnimationType.MISC, author, true);
    }

    /**
     * The function for when the player spawns
     *
     * @param location The location of the crate
     * @param player   The player who is opening the crate.
     */
    public abstract void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate);

}
