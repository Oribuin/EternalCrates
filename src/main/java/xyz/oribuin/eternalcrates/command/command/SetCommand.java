package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.CrateUtils;

public class SetCommand extends RoseCommand {

    public SetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        CrateManager manager = this.rosePlugin.getManager(CrateManager.class);

        Player player = (Player) context.getSender();
        Block target = player.getTargetBlockExact(5);

        // Make sure the player is looking at a valid block
        if (target == null || target.isEmpty() || target.isLiquid()) {
            locale.sendMessage(player, "command-set-no-target");
            return;
        }

        // Make sure the crate isnt already set
        if (manager.get(target.getLocation()) != null) {
            locale.sendMessage(player, "command-set-already-set");
            return;
        }

        // Set the crate
        crate.getLocations().add(target.getLocation());
        manager.save(crate);
        locale.sendMessage(player, "command-set-success", StringPlaceholders.of("crate", crate.getName()));

        ParticleData data = new ParticleData(Particle.REDSTONE).setDustOptions(Color.LIME);
        CrateUtils.outline(data, target, player);
    }

    @Override
    protected String getDefaultName() {
        return "set";
    }

    @Override
    public String getDescriptionKey() {
        return "command-set-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalcrates.set";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }


}
