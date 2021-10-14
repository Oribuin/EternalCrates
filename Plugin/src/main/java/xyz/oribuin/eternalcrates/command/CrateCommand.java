package xyz.oribuin.eternalcrates.command;

import org.bukkit.command.CommandSender;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.orilibrary.command.Command;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command.Info(
        name = "crate",
        aliases = {"crates"},
        description = "The main command for EternalCrates",
        usage = "/crates",
        permission = "eternalcrates.use",
        playerOnly = false,
        subCommands = {}
)
public class CrateCommand extends Command {

    private final EternalCrates plugin = (EternalCrates) this.getOriPlugin();

    public CrateCommand(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void runFunction(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            this.runSubCommands(sender, args, x -> {}, x -> {});
            return;
        }

        // TODO
        this.getSubCommands().stream()
                .map(SubCommand::getInfo)
                .filter(info -> sender.hasPermission(info.permission()))
                .collect(Collectors.toList());

    }

    @Override
    public List<String> completeString(CommandSender sender, String label, String[] args) {
        return new ArrayList<>();
    }

}
