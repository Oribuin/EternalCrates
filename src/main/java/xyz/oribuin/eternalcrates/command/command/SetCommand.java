package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;

public class SetCommand extends RoseCommand {

    public SetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate) {
        final var locale = this.rosePlugin.getManager(LocaleManager.class);
        final var manager = this.rosePlugin.getManager(CrateManager.class);

        // Cast the command sender as a player
        var player = (Player) context.getSender();

        // Get the block the player is looking at
        final var targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.isLiquid() || targetBlock.isEmpty()) {
            locale.sendMessage(player, "command-set-no-target");
            return;
        }


        Crate blockCrate = manager.getCrate(targetBlock.getLocation());
        if (blockCrate != null) {
            locale.sendMessage(player, "command-set-already-set", StringPlaceholders.single("crate", blockCrate.getId()));
            return;
        }


        if (!crate.getLocations().contains(targetBlock.getLocation())) {
            crate.getLocations().add(targetBlock.getLocation());
            this.rosePlugin.getManager(CrateManager.class).saveCrate(crate);
        }

        locale.sendMessage(player, "command-set-success", StringPlaceholders.single("crate", crate.getId()));

        final var data = new ParticleData(Particle.REDSTONE);
        data.setDustColor(Color.LIME);

        final var cube = PluginUtils.getCube(targetBlock.getLocation().clone(), targetBlock.getLocation().clone().add(1, 1, 1), 0.5);

        // Spawn particles in the cube and then remove them after 1.5s (35 ticks)
        var task = this.rosePlugin.getServer().getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> cube.forEach(loc -> data.spawn(player, loc, 1)), 0, 2);
        this.rosePlugin.getServer().getScheduler().runTaskLater(this.rosePlugin, task::cancel, 35);
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
        return "eternalcrates.command.set";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
