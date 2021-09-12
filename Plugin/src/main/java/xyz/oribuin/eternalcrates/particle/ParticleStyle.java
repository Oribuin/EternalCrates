package xyz.oribuin.eternalcrates.particle;

import java.util.List;

public interface ParticleStyle {



    /**
     * The duration in milliseconds of how long the particle is going to spawn
     * @return
     */
    int duration();

    List<Integer> particleLocations();

}
