package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;
    private final String author;
    private Crate crate;
    private List<Material> requiredBlocks;
    private boolean canBeVirtual;
    private boolean active;

    public Animation(final String name, final AnimationType animationType, String author, boolean canBeVirtual) {
        this.name = name;
        this.animationType = animationType;
        this.author = author;
        this.requiredBlocks = new ArrayList<>();
        this.canBeVirtual = canBeVirtual;
        this.active = false;
        this.crate = null;
    }

    /**
     * @return The required values for the animation
     */
    public abstract Map<String, Object> getRequiredValues();

    /**
     * The function called after animation default values are set.
     */
    public abstract void load();

    @SuppressWarnings("unchecked")
    public final <T> T get(String path, T def) {
        T value = (T) crate.getConfig().get(path);

        if (value == null) {
            EternalCrates.getInstance().getLogger().warning("Missing option in crate config. (Path: " + path + ")");
            return def;
        }

        return value;
    }

    /**
     * Check if a crate animation can be ran by checking the required blocks
     *
     * @return true if the crate can be opened.
     */
    public boolean isBlockRequired() {
        if (this.getRequiredBlocks().isEmpty())
            return false;

        Material blockType = crate.getLocation().getBlock().getType();
        return !this.getRequiredBlocks().isEmpty() && !this.getRequiredBlocks().contains(blockType);
    }

    /**
     * The function when the crates finishes.
     *
     * @param player The player opening the crate.
     * @param crate  The crate being interacted with
     */
    public void finishFunction(Player player, Crate crate) {
        // Empty Function
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

    public Crate getCrate() {
        return crate;
    }

    public void setCrate(Crate crate) {
        this.crate = crate;
    }
}
