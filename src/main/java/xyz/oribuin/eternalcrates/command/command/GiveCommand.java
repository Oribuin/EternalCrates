package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;

public class GiveCommand extends RoseCommand {

    public GiveCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate, Player player, int amount) {
        final CrateManager manager = this.rosePlugin.getManager(CrateManager.class);

        switch (crate.getType()) {
            case PHYSICAL -> manager.givePhysicalKey(player, crate, amount);
            case VIRTUAL -> manager.giveVirtualKey(player, crate, amount);
        }

        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getName())
                .add("amount", amount)
                .add("player", player.getName())
                .build();

        this.rosePlugin.getManager(LocaleManager.class).sendMessage(context.getSender(), "command-give-success", placeholders);
    }

    @Override
    protected String getDefaultName() {
        return "give";
    }

    @Override
    public String getDescriptionKey() {
        return "command-give-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrates.command.give";
    }

}
