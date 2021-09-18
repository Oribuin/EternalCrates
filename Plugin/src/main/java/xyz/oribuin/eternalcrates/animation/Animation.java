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

    public TriConsumer<Player, Reward, Crate> getRewardConsumer() {
        return rewardConsumer;
    }

    public void setRewardConsumer(TriConsumer<Player, Reward, Crate> rewardConsumer) {
        this.rewardConsumer = rewardConsumer;
    }
}
