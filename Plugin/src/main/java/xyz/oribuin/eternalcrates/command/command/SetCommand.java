package xyz.oribuin.eternalcrates.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;

import java.util.ArrayList;
import java.util.List;

public class SetCommand extends RoseCommand {

    public SetCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Crate crate) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        // Cast the command sender as a player
        Player player = (Player) context.getSender();

        // Get the block the player is looking at
        final Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.isLiquid() || targetBlock.isEmpty()) {
            locale.sendMessage(player, "command-set-no-target");
            return;
        }

        crate.setLocation(targetBlock.getLocation());
        locale.sendMessage(player, "command-set-success");
        this.rosePlugin.getManager(CrateManager.class).saveCrate(crate);

        final ParticleData data = new ParticleData(Particle.REDSTONE);
        data.setDustColor(Color.LIME);

        final List<Location> cube = getCube(targetBlock.getLocation().clone(), targetBlock.getLocation().clone().add(1, 1, 1), 0.5);

        // Spawn particles in the cube and then remove them after 1.5s (35 ticks)
        BukkitTask task = this.rosePlugin.getServer().getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> cube.forEach(loc -> data.spawn(player, loc, 1)), 0, 2);
        this.rosePlugin.getServer().getScheduler().runTaskLater(this.rosePlugin, task::cancel, 35);
    }

    // Create a 3d hollow cube from 2 org.bukkit.Location objects with distance between them

    /**
     * Create a 3d hollow cube from 2 org.bukkit.Location objects with distance between them
     *
     * @param corner1          The first corner of the cube
     * @param corner2          The second corner of the cube
     * @param particleDistance The distance between particles
     * @return A list of blocks that make up the cube
     */
    public List<Location> getCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
        for (double x = minX; x <= maxX; x += particleDistance) {
            for (double y = minY; y <= maxY; y += particleDistance) {
                for (double z = minZ; z <= maxZ; z += particleDistance) {
                    if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
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
