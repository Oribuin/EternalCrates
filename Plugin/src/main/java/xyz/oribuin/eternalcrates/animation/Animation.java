package xyz.oribuin.eternalcrates.animation;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Reward;

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
     * @param reward The reward that the animation has chosen (mostly only applies to gui animations)
     * @param player The player who gets the reward.
     */
    public void finishFunction(Reward reward, Player player) {
        this.setInAnimation(false);
        EternalCrates.getInstance().getActiveUsers().remove(player.getUniqueId());
        reward.getActions().forEach(action -> action.executeAction(EternalCrates.getInstance(), player));
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
