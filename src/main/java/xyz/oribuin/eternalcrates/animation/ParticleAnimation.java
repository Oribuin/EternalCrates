package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.List;

public abstract class ParticleAnimation extends Animation {

    private ParticleData particleData = new ParticleData(Particle.FLAME);
    private int speed;
    private int length;

    public ParticleAnimation(String name, String author, int speed) {
        super(name, AnimationType.PARTICLES, author, true);
        this.speed = speed;
        this.length = 60;
    }

    public abstract List<Location> particleLocations(Location crateLocation);

    public abstract void updateTimer();

    public ParticleData getParticleData() {
        return particleData;
    }

    public void setParticleData(ParticleData particleData) {
        this.particleData = particleData;
    }

    /**
     * Spawn a particle at a location.
     *
     * @param loc   The location of the particle
     * @param count the amount of particles being spawned
     */
    public void play(Location loc, int count, Player player) {
        final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(EternalCrates.getInstance(), () -> {
            this.updateTimer();
            this.particleLocations(loc.clone()).forEach(location -> particleData.spawn(player, location, count));
        }, 0, this.speed);

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            task.cancel();
            this.getCrate().finish(player, loc);
        }, this.getLength());

    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
