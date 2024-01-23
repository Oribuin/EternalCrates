package xyz.oribuin.eternalcrates.animation.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RippleAnimation extends ParticleAnimation {

    private final static int MAX_STEPS = 120;
    private static double CURRENT_RING = 0;

    private double maxRadius = 3;

    public RippleAnimation() {
        super("ripple");
    }

    /**
     * This is where you should do the math for getting particle locations
     * This method will be called on {@link Animation#tick(Crate, Player, Location)}
     * The order when this method is called will be Animation#tick -> ParticleAnimation#getLocations
     *
     * @param player The player opening the crate
     * @param crate  The crate being opened
     */
    @Override
    public List<Location> getLocations(Player player, Location crate) {
        List<Location> locations = new ArrayList<>();
        Location newLoc = crate.clone().subtract(0.0, 0.5, 0.0);

        for (int i = 0; i < this.step; i++) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / MAX_STEPS)) * CURRENT_RING;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / MAX_STEPS)) * CURRENT_RING;
            locations.add(newLoc.clone().add(dx, 0.0, dz));
        }

        return locations;
    }

    @Override
    public void tick(Crate crate, Player player, Location location) {
        this.step = (this.step + Math.PI * 2 / MAX_STEPS) % MAX_STEPS;
        if (CURRENT_RING >= this.maxRadius)
            CURRENT_RING = 0;

        CURRENT_RING += 0.5;
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     *
     * @param configValues The values to save
     */
    @Override
    public void save(Map<String, Object> configValues) {
        configValues.put("max-radius", 3);
    }

    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation starts to ensure each animation performs the same.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        CURRENT_RING = 0;

        this.maxRadius = (double) configValues.getOrDefault("max-radius", 3);
    }

}

