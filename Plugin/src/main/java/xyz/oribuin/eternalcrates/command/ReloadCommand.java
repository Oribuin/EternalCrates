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

    private final EternalCrates plugin = (EternalCrates) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);

    public ReloadCommand(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        this.plugin.reload();
        this.msg.send(sender, "reload");
    }

}
