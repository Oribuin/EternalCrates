package xyz.oribuin.eternalcrates.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;

public class AnimationStartEvent extends Event {

    private static final HandlerList list = new HandlerList();
    private final Crate crate;
    private final Animation animation;

    public AnimationStartEvent(Crate crate, Animation animation) {
        this.crate = crate;
        this.animation = animation;
    }

    public static HandlerList getHandlersList() {
        return list;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return list;
    }

    public Animation getAnimation() {
        return animation;
    }

    public Crate getCrate() {
        return crate;
    }
}
