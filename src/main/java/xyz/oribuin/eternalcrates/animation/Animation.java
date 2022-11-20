package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

    public Animation(final String name, final AnimationType type, String author, boolean canBeVirtual) {
        this.name = name;
        this.type = type;
        this.author = author;
        this.requiredBlocks = new ArrayList<>();
        this.canBeVirtual = canBeVirtual;
        this.active = false;
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
