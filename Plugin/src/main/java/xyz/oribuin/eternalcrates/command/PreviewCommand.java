package xyz.oribuin.eternalcrates.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.Optional;

@SubCommand.Info(
        names = {"preview"},
        permission = "eternalcrates.preview",
        usage = "/crate preview <crate>"
)
public class PreviewCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public PreviewCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        // Check argument length
        if (args.length != 2) {
            this.msg.send(sender, "invalid-args", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        final CrateManager crateManager = this.plugin.getManager(CrateManager.class);
        final Optional<Crate> crateOptional = crateManager.getCrate(args[1]);

        // Check if the crate exists.
        if (crateOptional.isEmpty()) {
            this.msg.send(sender, "invalid-crate");
            return;
        }

        // Open the crate for the user, bypassing any keys or anything of the sort
        crateOptional.get().open(plugin, player);
        this.msg.send(sender, "previewing-crate", StringPlaceholders.single("%crate%", crateOptional.get().getDisplayName()));
    }

}
