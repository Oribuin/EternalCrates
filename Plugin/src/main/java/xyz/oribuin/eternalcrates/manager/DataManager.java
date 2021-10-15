package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.manager.DataHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DataManager extends DataHandler {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final CrateManager crateManager;

    public DataManager(EternalCrates plugin) {
        super(plugin);
        this.crateManager = this.plugin.getManager(CrateManager.class);
    }

    @Override
    public void enable() {
        super.enable();

        // Connect to database async.
        this.async((task) -> this.getConnector().connect(connection -> {

            // Create the required tables for the plugin.
            final String query = "CREATE TABLE IF NOT EXISTS " + this.getTableName() + "_crates (crate TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, PRIMARY KEY(crate))";
            connection.prepareStatement(query).executeUpdate();

            // Cache all the physical crate locations.
            this.cacheCrates(connection);
        }));
    }

    /**
     * Cache all the crates in the database into the plugin.
     *
     * @param connection The current open SQL Connection, no reason to make an entirely new one
     * @throws SQLException In case there's an error :)
     */
    private void cacheCrates(final Connection connection) throws SQLException {

        final String query = "SELECT * FROM " + this.getTableName() + "_crates";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            final ResultSet result = statement.executeQuery();

            while (result.next()) {
                final String name = result.getString("crate");
                final World world = Bukkit.getWorld(result.getString("world"));
                final double x = result.getDouble("x");
                final double y = result.getDouble("y");
                final double z = result.getDouble("z");

                final Location loc = PluginUtils.getBlockLoc(new Location(world, x, y, z));
                this.crateManager.getCrate(name.toLowerCase()).ifPresent(crate -> crate.setLocation(loc));
            }
        }
    }

    /**
     * Save a physical crate's location in the config file.
     *
     * @param crate    The crate location;.
     */
    public void saveCrate(Crate crate) {
        if (crate.getLocation() == null)
            return;

        final Location blockLoc = crate.getLocation();
        crateManager.getCachedCrates().put(crate.getId().toLowerCase(), crate);

        this.async(t -> this.getConnector().connect(connection -> {
            final String query = "REPLACE INTO " + this.getTableName() + "_crates (crate, world, x, y, z) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, crate.getId());
                statement.setString(2, blockLoc.getWorld().getName());
                statement.setDouble(3, blockLoc.getX());
                statement.setDouble(4, blockLoc.getY());
                statement.setDouble(5, blockLoc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a crate location from the cache & database.
     *
     * @param crate The crate being deleted.
     */
    public void deleteCrate(Crate crate) {
        if (crate.getLocation() == null)
            return;

        final Location blockLoc = PluginUtils.getBlockLoc(crate.getLocation());
        this.crateManager.getCachedCrates().remove(crate.getId());

        this.async(t -> this.getConnector().connect(connection -> {
            final String query = "DELETE FROM " + this.getTableName() + "_crates WHERE crate = ? AND WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, crate.getId().toLowerCase());
                statement.setString(2, blockLoc.getWorld().getName());
                statement.setDouble(3, blockLoc.getX());
                statement.setDouble(4, blockLoc.getY());
                statement.setDouble(5, blockLoc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    @Override
    public void disable() {
        super.disable();
    }

    /**
     * Run a task asynchronously.
     *
     * @param callback The task callback.
     */
    public void async(Consumer<BukkitTask> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, callback);
    }

}
