package xyz.oribuin.eternalcrates.animation;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import javax.annotation.Nullable;

public abstract class Animation {

    private final String name;
    private final AnimationType animationType;
    private final String author;
    private boolean inAnimation = false;

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
        final Reward finalReward = reward != null ? reward : crate.selectReward();

        this.setInAnimation(false);
        EternalCrates.getInstance().getActiveUsers().remove(player.getUniqueId());
        finalReward.getActions().forEach(action -> action.executeAction(EternalCrates.getInstance(), player));
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isInAnimation() {
        return inAnimation;
    }

    public void setInAnimation(boolean inAnimation) {
        this.inAnimation = inAnimation;
    }

}
