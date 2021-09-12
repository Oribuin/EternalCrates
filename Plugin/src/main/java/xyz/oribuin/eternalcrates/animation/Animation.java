package xyz.oribuin.eternalcrates.animation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;

    private boolean hasParticles = true;
    private boolean hasSound = true;

    // Consumers for all the animation functions
    private BiConsumer<Player, ParticleAnimation> particleConsumer;
    private Consumer<Player> soundConsumer;

    public Animation(final String name, final AnimationType animationType) {
        this.name = name;
        this.animationType = animationType;

    }

    // Here are a bunch of usual functions for people to use when creating animation.

    /**
     * Spawn an entity using the plugin's internal methods and apply PDC
     * Tags to make sure we can remove the entity later on.
     *
     * @param loc            The location of the entity.
     * @param entityClass    The entity's class.
     * @param entityFunction The function
     */
    public <T extends Entity> T spawnEntity(Location loc, Class<T> entityClass, Consumer<T> entityFunction) {

        final World world = loc.getWorld();
        if (world == null)
            return null;

        return world.spawn(loc, entityClass, t -> t.getPersistentDataContainer().set(new NamespacedKey(EternalCrates.getInstance(), "animationEntity"), PersistentDataType.INTEGER, 1));
    }

    public String getName() {
        return name;
    }

    public BiConsumer<Player, ParticleAnimation> getParticleConsumer() {
        return particleConsumer;
    }

    public void setParticleConsumer(BiConsumer<Player, ParticleAnimation> particleConsumer) {
        this.particleConsumer = particleConsumer;
    }

    public boolean isHasParticles() {
        return hasParticles;
    }

    public void setHasParticles(boolean hasParticles) {
        this.hasParticles = hasParticles;
    }

    public boolean isHasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }

}
