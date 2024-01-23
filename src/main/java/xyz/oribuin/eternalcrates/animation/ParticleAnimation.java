package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.List;
import java.util.Map;

public abstract class ParticleAnimation extends Animation {

    protected double step = 0;

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     */
    public ParticleAnimation(String id) {
        super(id);
    }

    /**
     * This is where you should do the math for getting particle locations
     * This method will be called on {@link Animation#tick(Crate, Player, Location)}
     *
     * @param crate  The crate being opened
     * @param player The player opening the crate
     */
    public abstract List<Location> getLocations(Player player, Location crate);

    /**
     * Tick the animation for the player. This is called every 3 ticks asynchronously.
     * This method is called while the crate is being opened
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    @Override
    public void tick(Crate crate, Player player, Location location) {
        List<Location> locations = this.getLocations(player, location);
        ParticleData particle = new ParticleData(Particle.FLAME);

        for (Location loc : locations) {
            particle.spawn(null, loc, 1);
        }
    }

    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        // TODO: Load the particle style from the config
    }

    @Override
    public Map<String, Object> settings() {
        return Map.of();
    }

}
