package xyz.oribuin.eternalcrates.command;

import org.bukkit.command.CommandSender;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
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
        subCommands = {
                PreviewCommand.class,
                SetCommand.class
        }
)
public class CrateCommand extends Command {

    private final EternalCrates plugin = (EternalCrates) this.getOriPlugin();

    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final CrateManager crateManager = this.plugin.getManager(CrateManager.class);

    public CrateCommand(EternalCrates plugin) {
        super(plugin);

        this.register(sender -> msg.send(sender, "player-only"), sender -> msg.send(sender, "no-perm"));
    }

    @Override
    public void runFunction(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            this.runSubCommands(sender, args, x -> {}, x -> {});
            return;
        }

        // Send the help command to the user.
        this.getSubCommands().stream()
                .map(SubCommand::getInfo)
                .filter(info -> sender.hasPermission(info.permission()))
                .map(info -> msg.get("prefix") + "&f| " + info.usage())
                .forEach(s -> msg.sendRaw(sender, s));

    }

    @Override
    public List<String> completeString(CommandSender sender, String label, String[] args) {
        final List<String> tabComplete = new ArrayList<>();
        switch (args.length) {
            case 0, 1 -> tabComplete.addAll(this.getSubCommands()
                    .stream()
                    .map(SubCommand::getInfo)
                    .map(info -> info.names()[0])
//                    .filter(s -> args[0].startsWith(s))
                    .collect(Collectors.toList()));
            case 2 -> {
                return crateManager.getCachedCrates()
                        .values()
                        .stream()
                        .map(Crate::getId)
                        .collect(Collectors.toList());
            }
        }

        return tabComplete;
    }

}
