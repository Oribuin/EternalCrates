package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.CrateType;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;

public class ClaimCommand extends RoseCommand {

    public ClaimCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        var player = (Player) context.getSender();
        var manager = this.rosePlugin.getManager(CrateManager.class);
        var locale = this.rosePlugin.getManager(LocaleManager.class);

        var crates = manager.getCratesByType(CrateType.PHYSICAL);
        var keyData = manager.getUserKeys(player.getUniqueId());

        int totalClaimed = 0;
        for (var crate : crates) {
            var amount = keyData.get(crate.getId());
            if (amount == null || amount == 0)
                continue;

            var key = crate.getKey().clone();
            key.setAmount(amount);

            if (player.getInventory().firstEmpty() == -1) {
                continue;
            }

            totalClaimed += amount;

            player.getInventory().addItem(key);
            keyData.remove(crate.getId());
        }

        if (totalClaimed == 0) {
            locale.sendMessage(player, "command-claim-no-keys");
            return;
        }

        this.rosePlugin.getManager(CrateManager.class).saveUserKeys(player.getUniqueId(), keyData);
        locale.sendMessage(player, "command-claim-success", StringPlaceholders.single("total", totalClaimed));
    }

    @Override
    protected String getDefaultName() {
        return "claim";
    }

    @Override
    public String getDescriptionKey() {
        return "command-claim-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrates.command.claim";
    }
}
