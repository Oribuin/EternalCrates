package xyz.oribuin.eternalcrates.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.gui.ClaimGUI;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.Arrays;

@SubCommand.Info(
        names = {"claim"},
        permission = "eternalcrates.claim",
        usage = "/crate claim"
)
public class ClaimCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public ClaimCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        new ClaimGUI(this.plugin).create(player);
    }

}
