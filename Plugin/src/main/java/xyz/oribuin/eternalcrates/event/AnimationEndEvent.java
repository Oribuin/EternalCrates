package xyz.oribuin.eternalcrates.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.animation.Animation;

public class AnimationEndEvent extends Event {

    private static final HandlerList list = new HandlerList();
    private final Animation animation;

    public AnimationEndEvent(Animation animation) {
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

}
