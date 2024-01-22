package xyz.oribuin.eternalcrates.animation.impl;

import xyz.oribuin.eternalcrates.animation.Animation;

import java.time.Duration;

public class DefaultAnimation extends Animation {

    /**
     * Create a new animation with a designated id, This is used to identify the animation in the config
     * and when creating a crate. Make sure this is unique
     */
    public DefaultAnimation() {
        super("none", Duration.ZERO);
    }


}
