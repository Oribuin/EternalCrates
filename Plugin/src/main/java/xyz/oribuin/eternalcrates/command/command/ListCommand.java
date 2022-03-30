package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;

public class ListCommand extends RoseCommand {

    public ListCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final CrateManager crateManager = this.rosePlugin.getManager(CrateManager.class);
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        crateManager.getCachedCrates().keySet().forEach(crate -> locale.sendCustomMessage(context.getSender(), "Crate: " + crate));
    }

    @Override
    protected String getDefaultName() {
        return "list";
    }

    @Override
    public String getDescriptionKey() {
        return "command-list-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrate.list";
    }
}
