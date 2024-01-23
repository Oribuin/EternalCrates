package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.LocaleManager;

public class GiveCommand extends RoseCommand {

    public GiveCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate, @Optional Integer amount, @Optional Player target) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        // Make sure console isnt trying to give themselves a crate
        if (target == null && !(context.getSender() instanceof Player)) {
            locale.sendMessage(context.getSender(), "only-player");
            return;
        }

        // Make sure the amount is valid
        int newAmount = amount == null ? 1 : amount;
        if (newAmount <= 0) {
            locale.sendMessage(context.getSender(), "invalid-amount");
            return;
        }

        // Give the player the crate
        crate.give(target != null ? target : (Player) context.getSender(), newAmount);
        locale.sendMessage(context.getSender(), "command-give-success", StringPlaceholders.of(
                        "amount", newAmount,
                        "crate", crate.getName(),
                        "player", target != null ? target.getName() : context.getSender().getName()
                )
        );
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
        return "eternalcrates.give";
    }

}
