package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RippleAnimation extends ParticleAnimation {

    private double step = 0;
    private final int numSteps = 120;
    private double ringNumber = 0;

    public RippleAnimation() {
        super("ripple", "Oribuin", 3);
    }

    @Override
    public List<Location> particleLocations(Location crateLocation) {
        final Location loc = crateLocation.clone().subtract(0.0, 0.5, 0.0);
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
            double dx = MathL.cos(Math.PI * 2 * ((double) i / this.numSteps)) * range;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / this.numSteps)) * range;
            locs.add(center.clone().add(dx, 0.0, dz));
        }

        return locs;
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        final Map<String, Object> options = new HashMap<>();
        options.put("animation.particle", "REDSTONE");
        options.put("animation.color", "#FFFFFF");
        options.put("animation.transition", "#ff0000");
        options.put("animation.note", 1);
        options.put("animation.item", "STONE");
        options.put("animation.block", "STONE");
        return options;
    }

    public void load() {
        // Nothing to load
    }

    @Override
    public void finishFunction(Player player, Crate crate) {
        this.ringNumber = 0.0;
    }
}
