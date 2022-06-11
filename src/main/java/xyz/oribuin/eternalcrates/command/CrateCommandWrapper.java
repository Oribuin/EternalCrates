package xyz.oribuin.eternalcrates.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class CrateCommandWrapper extends RoseCommandWrapper {

    public CrateCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "crate";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("crates");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.eternalcrates.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
