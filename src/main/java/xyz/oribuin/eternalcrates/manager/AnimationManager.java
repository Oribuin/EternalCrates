package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.eternalcrates.animation.factory.AnimationFactory;

public class AnimationManager extends Manager {

    public AnimationManager(RosePlugin plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        AnimationFactory.register("xyz.oribuin.eternalcrates.animation.impl");
    }

    @Override
    public void disable() {

    }

}