package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;

public class ListCommand extends RoseCommand {

    public ListCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final var manager = this.rosePlugin.getManager(CrateManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        locale.sendMessage(context.getSender(), "command-list-header");
        manager.getCachedCrates().values().forEach(crate -> {
            final var placeholders = StringPlaceholders.builder("crate", crate.getName())
                    .addPlaceholder("type", crate.getType().name())
                    .addPlaceholder("id", crate.getId())
                    .addPlaceholder("reward_count", crate.getRewardMap().size())
                    .addPlaceholder("multiplier", crate.getMultiplier())
                    .addPlaceholder("min_rewards", crate.getMinRewards())
                    .addPlaceholder("max_rewards", crate.getMaxRewards())
                    .addPlaceholder("animation", crate.getAnimation().getName())
                    .addPlaceholder("locations", PluginUtils.getLocationsFormatted(crate.getLocations()))
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
