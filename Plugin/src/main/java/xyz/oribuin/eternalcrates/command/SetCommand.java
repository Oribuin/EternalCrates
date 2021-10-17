package xyz.oribuin.eternalcrates.command;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SubCommand.Info(
        names = {"set"},
        permission = "eternalcrates.set",
        usage = "/crate set <crate>"
)
public class SetCommand extends SubCommand {

    private final EternalCrates plugin;
    private final MessageManager msg;

    public SetCommand(EternalCrates plugin) {
        this.plugin = plugin;
        this.msg = this.plugin.getManager(MessageManager.class);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {
        // Check if player.
        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        // Check argument length
        if (args.length != 2) {
            this.msg.send(sender, "invalid-args", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        final CrateManager crateManager = this.plugin.getManager(CrateManager.class);
        final Optional<Crate> crateOptional = crateManager.getCrate(args[1]);

        // Check if the crate exists.
        if (crateOptional.isEmpty()) {
            this.msg.send(sender, "invalid-crate");
            return;
        }

        // Check if the target block is a correct block.
        final Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null || targetBlock.isLiquid() || targetBlock.isEmpty()) {
            this.msg.send(sender, "invalid-block");
            return;
        }

        final Crate crate = crateOptional.get();
        crate.setLocation(targetBlock.getLocation());

        // The function for spawning particles around the new block.
        final Location corner1 = PluginUtils.getBlockLoc(targetBlock.getLocation());
        final Location corner2 = corner1.clone().add(1, 1, 1);
        final List<Location> hollowCube = this.getHollowCube(corner1, corner2, 0.5);
        final ParticleData data = new ParticleData(Particle.REDSTONE);
        data.setDustColor(Color.LIME);

        final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> hollowCube.forEach(location -> data.spawn(player, location, 1)), 0, 2);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task::cancel, 35);

        this.plugin.getManager(DataManager.class).saveCrate(crate);
        this.msg.send(sender, "saved-crate", StringPlaceholders.single("crate", crateOptional.get().getDisplayName()));
    }

    /**
     * Get a hollow cube particles.
     *
     * @param corner1          The first corner
     * @param corner2          The second corner
     * @param particleDistance The distance between each particle
     * @return The list of particles.
     */
    private List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x += particleDistance) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += particleDistance) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += particleDistance) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }
}
