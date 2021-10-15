package xyz.oribuin.eternalcrates.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.Arrays;

@SubCommand.Info(
        names = {"animations"},
        permission = "eternalcrates.animations",
        usage = "/crate animations"
)
public class AnimationsCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;
    private final AnimationManager animations;

    public AnimationsCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
        this.animations = this.plugin.getManager(AnimationManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        // Go through all the animation types
        Arrays.stream(AnimationType.values()).forEach(animationType -> {

            // Tell the player the animation type.
            msg.sendRaw(sender, msg.get("prefix") + "Animations Type: " + StringUtils.capitalize(animationType.name().toLowerCase()));

            // All the registered animations in that category.
            animations.getAnimationFromType(animationType).forEach(animation -> msg.sendRaw(sender, "&f- #99ff99" + StringUtils.capitalize(animation.getName()) + "&f by " + animation.getAuthor()));
        });

    }

}
