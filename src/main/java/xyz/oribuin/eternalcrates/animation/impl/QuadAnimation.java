package xyz.oribuin.eternalcrates.animation.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.MathL;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuadAnimation extends ParticleAnimation {

    private final static int MAX_STEPS = 120;

    private int orbs;
    private double radius;
    private double height;

    public QuadAnimation() {
        super("quad");

        this.setDuration(Duration.ofSeconds(5));
    }

    /**
     * This is where you should do the math for getting particle locations
     * This method will be called on {@link Animation#tick(Crate, Player, Location)}
     *
     * @param player The player opening the crate
     * @param crate  The crate being opened
     */
    @Override
    public List<Location> getLocations(Player player, Location crate) {
        List<Location> locations = new ArrayList<>();
        Location newLoc = crate.clone().subtract(0.0, 0.5, 0.0);
        for (int i = 0; i < this.orbs; i++) {
            double dx = -(MathL.cos((this.step / (double) MAX_STEPS) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * this.radius;
            double dz = -(MathL.sin((this.step / (double) MAX_STEPS) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * this.radius;
            locations.add(newLoc.clone().add(dx, height, dz));
        }

        return locations;
    }

    /**
     * Tick the animation for the player. This is called every 3 ticks.
     * This method is called while the crate is being opened
     *
     * @param crate    The crate being opened
     * @param player   The player opening the crate
     * @param location The location of the crate
     */
    @Override
    public void tick(Crate crate, Player player, Location location) {
        this.step = (this.step + 3) % MAX_STEPS;
        this.radius += 0.02;
        this.height += 0.02;
    }

    /**
     * This method is called when an animation is saved to the config for the first time
     * This is used to create default values for the animation
     */
    @Override
    public Map<String, Object> settings() {
        return Map.of(
                "orbs", 4,
                "radius", 1.0,
                "height", 0.0
        );
    }


    /**
     * This method is called when an animation is loaded from the config.
     * This will be loaded when the animation is registered.
     *
     * @param configValues The values to load
     */
    @Override
    public void load(Map<String, Object> configValues) {
        this.orbs = (int) configValues.get("orbs");
        this.radius = (double) configValues.get("radius");
        this.height = (double) configValues.get("height");
    }

}

