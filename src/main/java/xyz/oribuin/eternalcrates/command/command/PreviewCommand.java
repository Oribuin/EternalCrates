package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.gui.PreviewGUI;
import xyz.oribuin.eternalcrates.manager.MenuManager;

public class PreviewCommand extends RoseCommand {

    public PreviewCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate) {
        this.rosePlugin.getManager(MenuManager.class)
                .getGUI(PreviewGUI.class)
                .open((Player) context.getSender(), crate);
    }

    @Override
    protected String getDefaultName() {
        return "preview";
    }

    @Override
    public String getDescriptionKey() {
        return "command-preview-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrates.command.preview";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
