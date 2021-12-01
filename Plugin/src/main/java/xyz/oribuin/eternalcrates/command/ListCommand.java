package xyz.oribuin.eternalcrates.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

@SubCommand.Info(
        names = {"list"},
        permission = "eternalcrates.list",
        usage = "/crate list"
)
public class ListCommand extends SubCommand {

    private final MessageManager msg;
    private final CrateManager crateManager;

    public ListCommand(EternalCrates plugin) {
        this.msg = plugin.getManager(MessageManager.class);
        this.crateManager = plugin.getManager(CrateManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        final StringPlaceholders.Builder builder = StringPlaceholders.builder("prefix", msg.get("prefix"));

        // Send the help command to the user.
        crateManager.getCachedCrates().values().forEach(crate -> {
            final StringPlaceholders placeholders = builder.addPlaceholder("crate", StringUtils.capitalize(crate.getId().toLowerCase()))
                    .addPlaceholder("rewards", crate.getRewardMap().size())
                    .addPlaceholder("location", PluginUtils.formatLocation(crate.getLocation()))
                    .build();

            msg.sendRaw(sender, msg.get("list-format"), placeholders);
        });
    }

}
