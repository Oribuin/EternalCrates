package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.util.CrateUtils;

public class ListCommand extends RoseCommand {

    public ListCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final CrateManager manager = this.rosePlugin.getManager(CrateManager.class);
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        locale.sendMessage(context.getSender(), "command-list-header");
        manager.getCachedCrates().values().forEach(crate -> {
            final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crate.getName())
                    .add("type", crate.getType().name())
                    .add("id", crate.getId())
                    .add("reward_count", crate.getRewardMap().size())
                    .add("multiplier", crate.getMultiplier())
                    .add("min_rewards", crate.getMinRewards())
                    .add("max_rewards", crate.getMaxRewards())
                    .add("animation", crate.getAnimation().getName())
                    .add("locations", CrateUtils.getLocationsFormatted(crate.getLocations()))
                    .build();

            locale.sendSimpleMessage(context.getSender(), "command-list-format", placeholders);
        });
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
        return "eternalcrates.command.list";
    }


}
