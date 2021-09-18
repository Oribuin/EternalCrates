package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Location;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esophose
 * https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleSpiral.java
 */
public class SpiralAnimation extends ParticleAnimation {

    private int stepX;

    public SpiralAnimation() {
        super("spiral", 3);
    }

    @Override
    public List<Location> particleLocations(Location crateLocation) {
        final List<Location> locations = new ArrayList<>();

        for (double stepY = -60; stepY < 60; stepY += 120D / 12) {
            double dx = -(MathL.cos(((this.stepX + stepY) / (double) 90) * Math.PI * 2)) * 0.8;
            double dy = stepY / 90 / 2D;
            double dz = -(MathL.sin(((this.stepX + stepY) / (double) 90) * Math.PI * 2)) * 0.8;
            locations.add(crateLocation.clone().add(dx, dy, dz));
        }

        return locations;
    }

    @Override
    public void updateTimer() {
        this.stepX++;
    }

}
