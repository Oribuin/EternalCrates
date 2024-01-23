package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Animation {

    protected final String id;
    private Duration duration;

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     *
     * @param id This is your unique id for the animation
     */
    public Animation(final String id) {
        this.id = id;
        this.duration = Duration.ofSeconds(5);
    }

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     *
     * @param id       This is your unique id for the animation
     * @param duration The duration of the animation
     */
    public Animation(final String id, final Duration duration) {
        this.id = id;
        this.duration = duration;
    }

    /**
     * Start the animation for the player. This is called when a player opens a crate.
     * This method will be called when the crate is opened
     * It will be done synchronously, make sure to use async methods if needed.
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    public void start(Crate crate, Player player, Location location) {
    }

    /**
     * Tick the animation for the player. This is called every 3 ticks asynchronously.
     * This method is called while the crate is being opened
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    public void tick(Crate crate, Player player, Location location) {
    }

    /**
     * Stop the animation for the player. This is called when the animation is finished.
     * Use this method to clean up any blocks or entities created by the animation.
     * This method will be called when the crate is finished the animation automatically
     * and will be called synchronously.
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    public void stop(Crate crate, Player player, Location location) {
        crate.reward(player);
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     */
    public Map<String, Object> settings() {
        return new HashMap<>();
    }

    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    public void load(Map<String, Object> configValues) {
    }

    /**
     * Define the required blocks for the animation. This means that the animation will only be played
     * If the crate block is within the required blocks.
     *
     * @return The required blocks for the animation
     */
    public List<Material> getRequiredBlocks() {
        return new ArrayList<>();
    }

    /**
     * Mark an entity as part of the animation. This is to prevent an entity being skipped over on disable
     *
     * @param entity The entity to mark
     */
    public final void markEntity(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
    }

    /**
     * Get the time the animation should last for.
     * After this time, Animation#stop will be called.
     * If you want to handle the duration yourself, return Duration#ZERO
     *
     * @return The duration of the animation
     */
    public Duration getDuration() {
        return this.duration;
    }

    /**
     * Set the duration of the animation.
     * If you want to handle the duration yourself, set this to Duration#ZERO
     *
     * @param duration The duration of the animation
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Get the id of the animation
     *
     * @return The id of the animation
     */
    public String getId() {
        return id;
    }

}
