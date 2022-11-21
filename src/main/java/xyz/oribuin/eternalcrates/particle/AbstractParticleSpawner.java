package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public interface AbstractParticleSpawner {

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     */
    void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param x        the position on the x axis to spawn at
     * @param y        the position on the y axis to spawn at
     * @param z        the position on the z axis to spawn at
     * @param count    the number of particles
     */
    void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T>      type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     * @param data     the data to use for the particle or null,
     *                 the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, @org.jetbrains.annotations.Nullable T data);


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T>      type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x        the position on the x axis to spawn at
     * @param y        the position on the y axis to spawn at
     * @param z        the position on the z axis to spawn at
     * @param count    the number of particles
     * @param data     the data to use for the particle or null,
     *                 the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, @org.jetbrains.annotations.Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     */
    void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x        the position on the x axis to spawn at
     * @param y        the position on the y axis to spawn at
     * @param z        the position on the z axis to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     */
    void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T>      type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     * @param data     the data to use for the particle or null,
     *                 the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, @org.jetbrains.annotations.Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T>      type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x        the position on the x axis to spawn at
     * @param y        the position on the y axis to spawn at
     * @param z        the position on the z axis to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     * @param data     the data to use for the particle or null,
     *                 the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, @org.jetbrains.annotations.Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     * @param extra    the extra data for this particle, depends on the
     *                 particle used (normally speed)
     */
    void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x        the position on the x axis to spawn at
     * @param y        the position on the y axis to spawn at
     * @param z        the position on the z axis to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     * @param extra    the extra data for this particle, depends on the
     *                 particle used (normally speed)
     */
    void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T>      type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count    the number of particles
     * @param offsetX  the maximum random offset on the X axis
     * @param offsetY  the maximum random offset on the Y axis
     * @param offsetZ  the maximum random offset on the Z axis
     * @param extra    the extra data for this particle, depends on the
     *                 particle used (normally speed)
     * @param data     the data to use for the particle or null,
     *                 the type of this depends on {@link Particle#getDataType()}
     */
    <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @org.jetbrains.annotations.Nullable T data);

}
