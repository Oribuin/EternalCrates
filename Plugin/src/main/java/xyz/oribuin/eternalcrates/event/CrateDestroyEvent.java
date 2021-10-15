package xyz.oribuin.eternalcrates.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.crate.Crate;

public class CrateDestroyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList list = new HandlerList();
    private final Crate crate;
    private boolean cancelled = false;

    public CrateDestroyEvent(Crate crate, Player player) {
        super(player);
        this.crate = crate;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return list;
    }

    public static HandlerList getHandlersList() {
        return list;
    }

    public Crate getCrate() {
        return crate;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
