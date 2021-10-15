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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DataManager extends DataHandler {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<Location, Crate> cachedCrates = new HashMap<>();
    public DataManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        super.enable();

        // Connect to database async.
        this.async((task) -> this.getConnector().connect(connection -> {

            // Create the required tables for the plugin.
            final String query = "CREATE TABLE IF NOT EXISTS " + this.getTableName() + "_crates (world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, crate TEXT, PRIMARY KEY(world, x, y, z))";
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
        this.cachedCrates.clear();
        final String query = "SELECT * FROM " + this.getTableName() + "_crates";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            final ResultSet result = statement.executeQuery();

            while (result.next()) {
                final World world = Bukkit.getWorld(result.getString("world"));
                final double x = result.getDouble("x");
                final double y = result.getDouble("y");
                final double z = result.getDouble("z");

                final Location loc = PluginUtils.getBlockLoc(new Location(world, x, y, z));
                final Optional<Crate> crate = this.plugin.getManager(CrateManager.class).getCrate(result.getString("crate"));
                if (crate.isEmpty())
                    continue;

                crate.get().setLocation(loc);
                this.cachedCrates.put(loc, crate.get());
            }
        }
    }

    /**
     * Save a physical crate's location in the config file.
     *
     * @param crate    The crate location;.
     * @param location The location of the crate.
     */
    public void saveCrate(Crate crate, Location location) {
        final Location blockLoc = PluginUtils.getBlockLoc(location);
        this.cachedCrates.put(blockLoc, crate);
        crate.setLocation(blockLoc);

        this.async(t -> this.getConnector().connect(connection -> {
            final String query = "REPLACE INTO " + this.getTableName() + "_crates (world, x, y, z, crate) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, blockLoc.getWorld().getName());
                statement.setDouble(2, blockLoc.getX());
                statement.setDouble(3, blockLoc.getY());
                statement.setDouble(4, blockLoc.getZ());
                statement.setString(5, crate.getId());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a crate location from the cache & database.
     *
     * @param location The location of the crate.
     */
    public void deleteCrate(Location location) {
        final Location blockLoc = PluginUtils.getBlockLoc(location);
        this.cachedCrates.remove(blockLoc);

        this.async(t -> this.getConnector().connect(connection -> {
            final String query = "DELETE FROM " + this.getTableName() + "_crates WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, blockLoc.getWorld().getName());
                statement.setDouble(2, blockLoc.getX());
                statement.setDouble(3, blockLoc.getY());
                statement.setDouble(4, blockLoc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Get a cached crate from the location of it.
     *
     * @param location The location of the crate
     * @return The optional crate.
     */
    public Optional<Crate> getCrate(Location location) {
        return this.cachedCrates.entrySet().stream()
                .filter(entry -> entry.getKey().equals(PluginUtils.getBlockLoc(location)))
                .map(Map.Entry::getValue)
                .findFirst();
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

    public Map<Location, Crate> getCachedCrates() {
        return cachedCrates;
    }

}
