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
    private boolean canBeVirtual;
    private boolean active;

    public Animation(final String name, final AnimationType animationType, String author, boolean canBeVirtual) {
        this.name = name;
        this.animationType = animationType;
        this.author = author;
        this.canBeVirtual = canBeVirtual;
        this.active = false;
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

    public boolean isCanBeVirtual() {
        return canBeVirtual;
    }

    public void setCanBeVirtual(boolean canBeVirtual) {
        this.canBeVirtual = canBeVirtual;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
