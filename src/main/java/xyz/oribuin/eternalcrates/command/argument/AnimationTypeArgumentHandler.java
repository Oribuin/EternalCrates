package xyz.oribuin.eternalcrates.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.animation.AnimationType;

import java.util.Arrays;
import java.util.List;

public class AnimationTypeArgumentHandler extends RoseCommandArgumentHandler<AnimationType> {

    public AnimationTypeArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, AnimationType.class);
    }

    @Override
    protected AnimationType handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        final String input = argumentParser.next();
        final AnimationType type = AnimationType.fromString(input);

        if (type == null) {
            throw new HandledArgumentException("argument-handler-animation-type", StringPlaceholders.of("type", input));
        }

        return type;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();
        return Arrays.stream(AnimationType.values())
                .map(AnimationType::name)
                .map(String::toLowerCase)
                .toList();
    }

}
