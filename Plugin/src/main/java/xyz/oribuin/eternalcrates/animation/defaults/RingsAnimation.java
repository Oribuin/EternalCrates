package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esophose
 * https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleRings.java
 */
public class RingsAnimation extends ParticleAnimation {

    private final int maxStep = 32;
    private int step = 0;

    public RingsAnimation() {
        super("rings", 1);
    }

    @Override
    public List<Location> particleLocations(Location crateLocation) {

        final List<Location> locations = new ArrayList<>();

        double ring1 = Math.PI / (maxStep / 2D) * this.step;
        double ring2 = Math.PI / (maxStep / 2D) * ((((this.step + this.maxStep / 2D) % this.maxStep)));

        locations.add(crateLocation.clone().add(MathL.cos(ring1), MathL.sin(ring1), MathL.sin(ring1)));
        locations.add(crateLocation.clone().add(MathL.cos(ring2), MathL.sin(ring2), MathL.sin(ring2)));

        locations.add(crateLocation.clone().add(MathL.cos(ring1 + Math.PI), MathL.sin(ring1), MathL.sin(ring1 + Math.PI)));
        locations.add(crateLocation.clone().add(MathL.cos(ring2 + Math.PI), MathL.sin(ring2), MathL.sin(ring2 + Math.PI)));

        return locations;
    }

    @Override
    public void updateTimer() {
        this.step = (this.step + 1) % this.maxStep;
    }

}
