package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;

public abstract class CustomAnimation extends Animation {

    public CustomAnimation(String name, String author, AnimationType type) {
        super(name, type, author, true);
    }

    /**
     * The function for when the player spawns
     *
     * @param crate    The crate being opened.
     * @param location The location of the crate
     * @param player   The player who is opening the crate.
     */
    public abstract void spawn(Crate crate, Location location, Player player);
}
