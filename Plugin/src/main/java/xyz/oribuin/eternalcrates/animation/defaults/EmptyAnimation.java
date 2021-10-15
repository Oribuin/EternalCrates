package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.crate.Crate;

public class EmptyAnimation extends Animation {

    public EmptyAnimation() {
        super("none", AnimationType.NONE, "Oribuin");
    }

    public void play(Crate crate, Player player) {
        this.finishFunction(crate, crate.selectReward(), player);
    }

}
