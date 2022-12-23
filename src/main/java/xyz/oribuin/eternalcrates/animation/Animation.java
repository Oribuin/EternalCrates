package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Animation {

    private final String name;
    private final AnimationType type;
    private final String author;
    private List<Material> requiredBlocks;
    private boolean canBeVirtual;
    private boolean active;

    public Animation(final String name, String author, final AnimationType type, boolean canBeVirtual) {
        this.name = name;
        this.type = type;
        this.author = author;
        this.requiredBlocks = new ArrayList<>();
        this.canBeVirtual = canBeVirtual;
        this.active = false;
    }

    /**
     * Define the animation values with virtual crate option predefined
     *
     * @param name   The name of the animation
     * @param author The author of the animation
     * @param type   The type of the animation
     */
    public Animation(final String name, String author, final AnimationType type) {
        this(name, author, type, true);
    }

    /**
     * Define the animation values with bare minimum values.
     *
     * @param name   The name of the animation.
     * @param author The author of the animation.
     */
    public Animation(final String name, String author) {
        this(name, author, AnimationType.UNKNOWN, true);
    }

    /**
     * @return The required values for the animation
     */
    public abstract Map<String, Object> getRequiredValues();

    /**
     * The function called after animation default values are set.
     *
     * @param config The configuration of the animation
     */
    public abstract void load(CommentedConfigurationSection config);


    /**
     * Play an animation at a location
     *
     * @param loc    The location to play the animation at
     * @param player The player who is opening the crate
     * @param crate  The crate being opened
     */
    public abstract void play(@NotNull Location loc, @NotNull Player player, @NotNull Crate crate);

    /**
     * Check if a crate animation can be ran by checking the required blocks
     *
     * @return true if the crate can be opened.
     */
    public boolean isBlockRequired(Location location) {
        if (this.getRequiredBlocks().isEmpty())
            return false;

        Material blockType = location.getBlock().getType();
        return !this.getRequiredBlocks().isEmpty() && !this.getRequiredBlocks().contains(blockType);
    }

    /**
     * The function when the crate animation finishes.
     *
     * @param player   The player opening the crate.
     * @param crate    The crate being interacted with
     * @param location The location of the crate
     */
    public void finish(Player player, Crate crate, Location location) {
        // Empty Function
    }

    public AnimationType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public boolean canBeVirtual() {
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

    public List<Material> getRequiredBlocks() {
        return requiredBlocks;
    }

    public void setRequiredBlocks(List<Material> requiredBlocks) {
        this.requiredBlocks = requiredBlocks;
    }

}
