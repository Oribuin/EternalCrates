package xyz.oribuin.eternalcrates.command;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

@SubCommand.Info(
        names = {"keys"},
        permission = "eternalcrates.keys",
        usage = "/crate keys"
)
public class KeysCommand extends SubCommand {

    private final MessageManager msg;
    private final DataManager data;

    public KeysCommand(EternalCrates plugin) {
        this.msg = plugin.getManager(MessageManager.class);
        this.data = plugin.getManager(DataManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        final StringPlaceholders.Builder builder = StringPlaceholders.builder("prefix", msg.get("prefix"));

        // Send the crate keys the user has
        data.getVirtual(player.getUniqueId()).forEach((key, count) -> {
            final StringPlaceholders placeholders = builder.addPlaceholder("crate", StringUtils.capitalize(key.toLowerCase()))
                    .addPlaceholder("count", count)
                    .build();

            msg.sendRaw(sender, msg.get("keys-format"), placeholders);
        });
    }
}
