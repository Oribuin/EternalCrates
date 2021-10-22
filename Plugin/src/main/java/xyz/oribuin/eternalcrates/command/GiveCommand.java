package xyz.oribuin.eternalcrates.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.List;
import java.util.Optional;

@SubCommand.Info(
        names = {"give"},
        permission = "eternalcrates.give",
        usage = "/crate give <player> <crate> [amount]"
)
public class GiveCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public GiveCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        // Check argument length
        if (args.length < 3) {
            this.msg.send(sender, "invalid-args", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        // Check if the player exists
        final Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            this.msg.send(sender, "invalid-player");
            return;
        }

        final CrateManager crateManager = this.plugin.getManager(CrateManager.class);
        final Optional<Crate> crateOptional = crateManager.getCrate(args[2]);

        // Check if the crate exists.
        if (crateOptional.isEmpty()) {
            this.msg.send(sender, "invalid-crate");
            return;
        }

        // Get the amount if they provided it.
        int amount = 1;
        if (args.length == 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignored) {
            }
        }

        // Check custom amount.
        final Crate crate = crateOptional.get();
        final ItemStack item = crate.getKey().clone();
        int finalAmount = Math.max(Math.min(amount, 64), 1);

        final StringPlaceholders placeholders = StringPlaceholders.builder("crate", crateOptional.get().getDisplayName())
                .addPlaceholder("amount", finalAmount)
                .addPlaceholder("player", target.getName())
                .addPlaceholder("sender", sender.getName())
                .build();

        item.setAmount(finalAmount);

        this.msg.send(target, "given-key", placeholders);
        this.msg.send(sender, "gave-key", placeholders);

        if (target.getInventory().firstEmpty() == -1) {
            final DataManager data = this.plugin.getManager(DataManager.class);
            List<ItemStack> items = data.getItems(target.getUniqueId());

            items.add(item);
            data.saveUser(target.getUniqueId(), items);
            this.msg.send(target, "saved-key", placeholders);
        } else {
            target.getInventory().addItem(item);
        }

    }
}
