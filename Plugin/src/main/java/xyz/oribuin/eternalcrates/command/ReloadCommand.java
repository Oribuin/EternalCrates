package xyz.oribuin.eternalcrates.command;

import org.bukkit.command.CommandSender;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;

@SubCommand.Info(
        names = {"reload"},
        permission = "eternalcrates.reload",
        usage = "/crate reload"
)
public class ReloadCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public ReloadCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        this.plugin.reload();
        this.msg.send(sender, "reload");
    }

}
