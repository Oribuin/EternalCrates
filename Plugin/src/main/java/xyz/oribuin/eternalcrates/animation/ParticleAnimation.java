package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Location;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.List;

public interface ParticleAnimation {

    ParticleData particleData();

    List<Location> particleLocs();

}
