package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class WorldParticleSpawner implements AbstractParticleSpawner {

    private final org.bukkit.World world;

    public WorldParticleSpawner(org.bukkit.World world) {
        this.world = world;
    }

    @Override
    public void spawnParticle(org.bukkit.Particle particle, org.bukkit.Location location, int count) {
        this.world.spawnParticle(particle, location, count);
    }

    @Override
    public void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count) {
        this.world.spawnParticle(particle, x, y, z, count);
    }

    @Override
    public <T> void spawnParticle(org.bukkit.Particle particle, org.bukkit.Location location, int count, T data) {
        this.world.spawnParticle(particle, location, count, data);
    }

    @Override
    public <T> void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, T data) {
        this.world.spawnParticle(particle, x, y, z, count, data);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ) {
        this.world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        this.world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ);
    }

    @Override
    public <T> void spawnParticle(org.bukkit.Particle particle, org.bukkit.Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        this.world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(org.bukkit.Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        this.world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this.world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        this.world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public <T> void spawnParticle(org.bukkit.Particle particle, org.bukkit.Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        this.world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
    }

}
