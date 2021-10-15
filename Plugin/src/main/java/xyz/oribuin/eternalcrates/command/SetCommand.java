package xyz.oribuin.eternalcrates.command;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.Optional;

@SubCommand.Info(
        names = {"set"},
        permission = "eternalcrates.set",
        usage = "/crate set <crate>"
)
public class SetCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public SetCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        // Check if player.
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

        // Check if the target block is a correct block.
        final Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.isLiquid() || targetBlock.isEmpty()) {
            this.msg.send(sender, "invalid-block");
            return;
        }

        final Crate crate = crateOptional.get();
        crate.setLocation(targetBlock.getLocation());

        this.plugin.getManager(DataManager.class).saveCrate(crate);
        this.msg.send(sender, "saved-crate", StringPlaceholders.single("crate", crateOptional.get().getDisplayName()));
    }
}
