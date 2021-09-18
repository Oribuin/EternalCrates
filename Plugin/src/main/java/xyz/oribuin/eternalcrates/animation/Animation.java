package xyz.oribuin.eternalcrates.animation;

import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import java.util.function.Consumer;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;

    private boolean hasSound = true;

    private Consumer<Player> soundConsumer;
    private TriConsumer<Player, Reward, Crate> rewardConsumer;

    public Animation(final String name, final AnimationType animationType) {
        this.name = name;
        this.animationType = animationType;
    }

    // Here are a bunch of usual functions for people to use when creating animation.

    //    /**
    //     * Spawn an entity using the plugin's internal methods and apply PDC
    //     * Tags to make sure we can remove the entity later on.
    //     *
    //     * If you would like to spawn a clientside entity, You can use
    //     * NMSAdapter#getHandler().createClientsideEntity(player, loc, entityType)
    //     *
    //     * @param loc            The location of the entity.
    //     * @param entityClass    The entity's class.
    //     * @param entityFunction The function
    //     */
    //    public <T extends Entity> T spawnEntity(Location loc, Class<T> entityClass, Consumer<T> entityFunction) {
    //
    //        final World world = loc.getWorld();
    //        if (world == null)
    //            return null;
    //
    //
    //        return world.spawn(loc, entityClass, t -> t.getPersistentDataContainer().set(new NamespacedKey(EternalCrates.getInstance(), "animationEntity"), PersistentDataType.INTEGER, 1));
    //    }

    public Consumer<Player> getSoundConsumer() {
        return soundConsumer;
    }

    public void setSoundConsumer(Consumer<Player> soundConsumer) {
        this.soundConsumer = soundConsumer;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public String getName() {
        return name;
    }

    public boolean isHasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }

}
