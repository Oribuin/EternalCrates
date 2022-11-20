package xyz.oribuin.eternalcrates.animation.defaults.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.List;

public class RippleAnimation extends ParticleAnimation {

    private double step = 0;
    private final int numSteps = 120;
    private double ringNumber = 0;

    public RippleAnimation() {
        super("Ripple", "Oribuin", 3);
    }

    @Override
    public List<Location> particleLocations(Location crateLocation) {
        final var loc = crateLocation.clone().subtract(0.0, 0.5, 0.0);
        return this.createCircle(loc, ringNumber);
    }

    @Override
    public void updateTimer() {
        this.step = (this.step + Math.PI * 2 / this.numSteps) % this.numSteps;
        if (this.ringNumber >= 3)
            this.ringNumber = 0;

        this.ringNumber += 0.5;
    }

    /**
     * Create a circle from the center of a location with a specified radius
     *
     * @param center The center location
     * @param range  The radius of the circle
     * @return The list of particle locations
     */
    private List<Location> createCircle(Location center, double range) {
        final List<Location> locs = new ArrayList<>();
        for (int i = 0; i < this.numSteps; i++) {
            var dx = MathL.cos(Math.PI * 2 * ((double) i / this.numSteps)) * range;
            var dz = MathL.sin(Math.PI * 2 * ((double) i / this.numSteps)) * range;
            locs.add(center.clone().add(dx, 0.0, dz));
        }

        return locs;
    }

    @Override
    public void finish(Player player, Crate crate, Location location) {
        this.ringNumber = 0.0;
    }
}
