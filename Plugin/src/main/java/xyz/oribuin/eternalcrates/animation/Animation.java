package xyz.oribuin.eternalcrates.animation;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;
    private final String author;

    private boolean hasSound = true;

    private Consumer<Player> soundConsumer;

    public Animation(final String name, final AnimationType animationType, String author) {
        this.name = name;
        this.animationType = animationType;
        this.author = author;
    }

    /**
     * Run the functions for giving the player the reward.
     *
     * @param crate  The crate the animation is being played for
     * @param reward The reward that the animation has chosen (mostly only applies to gui animations)
     * @param player The player who gets the reward.
     */
    public void finishFunction(Crate crate, @Nullable Reward reward, Player player) {
        crate.getActiveUsers().remove(player.getUniqueId());

        final Reward finalReward = reward != null ? reward : crate.selectReward();
        finalReward.getActions().forEach(action -> action.executeAction(EternalCrates.getInstance(), player));
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

    public String getAuthor() {
        return author;
    }
}
