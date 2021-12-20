package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.event.AnimationEndEvent;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;
    private final String author;
    private boolean active = false;

    public Animation(final String name, final AnimationType animationType, String author) {
        this.name = name;
        this.animationType = animationType;
        this.author = author;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
