package xyz.oribuin.eternalcrates.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.oldanimations.Animation;
import xyz.oribuin.eternalcrates.manager.AnimationManager;

import java.util.List;
import java.util.Set;

public class AnimationArgumentHandler extends RoseCommandArgumentHandler<Animation> {

    public AnimationArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Animation.class);
    }

    @Override
    protected Animation handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        final String input = argumentParser.next();
        final Animation animation = this.rosePlugin.getManager(AnimationManager.class).getAnimation(input);

        if (animation == null) {
            throw new HandledArgumentException("argument-handler-animation", StringPlaceholders.of("animation", input));
        }

        return animation;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        Set<String> animations = this.rosePlugin.getManager(AnimationManager.class).getCachedAnimations().keySet();
        if (animations.isEmpty()) {
            return List.of("<no loaded animations>");
        }

        return animations.stream().toList();
    }

}
