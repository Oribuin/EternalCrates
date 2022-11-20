package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.List;

public class AnimationCommand extends RoseCommand {

    public AnimationCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, AnimationType type) {
        final var manager = this.rosePlugin.getManager(AnimationManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        // wow this is a lot of mess
        List<Animation> animations = manager.getAnimationsFromType(type);
        if (animations.isEmpty()) {
            locale.sendMessage(context.getSender(), "command-animation-empty");
            return;
        }

        // send the header for the animation type
        locale.sendSimpleMessage(context.getSender(), "command-animation-header", StringPlaceholders.single("type", PluginUtils.formatEnum(type.name())));


        animations.forEach(animation -> {
            var plc = StringPlaceholders.builder()
                    .addPlaceholder("name", animation.getName())
                    .addPlaceholder("author", animation.getAuthor())
                    .build();

            locale.sendSimpleMessage(context.getSender(), "command-animation-format", plc);
        });
    }

    @Override
    protected String getDefaultName() {
        return "animation";
    }

    @Override
    public String getDescriptionKey() {
        return "command-animation-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrates.command.animation";
    }


}
