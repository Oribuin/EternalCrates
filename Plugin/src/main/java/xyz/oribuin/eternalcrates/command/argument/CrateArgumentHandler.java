package xyz.oribuin.eternalcrates.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CrateArgumentHandler extends RoseCommandArgumentHandler<Crate> {

    public CrateArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Crate.class);
    }

    @Override
    protected Crate handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();
        Optional<Crate> crate = this.rosePlugin.getManager(CrateManager.class).getCrate(input);
        if (crate.isEmpty()) {
            throw new HandledArgumentException("argument-handler-crate", StringPlaceholders.single("crate", input));
        }

        return crate.get();
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        Set<String> crates = this.rosePlugin.getManager(CrateManager.class).getCachedCrates().keySet();
        if (crates.isEmpty()) {
            return List.of("<no loaded crates>");
        }

        return crates.stream().toList();
    }

}