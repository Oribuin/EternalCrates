package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.MySQLConnector;
import xyz.oribuin.orilibrary.database.SQLiteConnector;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DataManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<Location, Crate> cachedCrates = new HashMap<>();
    private String tableName;

    private DatabaseConnector connector = null;

    public DataManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        // I really should make the manager include this method

        final FileConfiguration config = this.plugin.getConfig();

        // Get the default table name or get the default value.
        this.tableName = config.getString("mysql.tablename") == null
                ? "eternalcrates"
                : config.getString("mysql.tablename");

        if (config.getBoolean("mysql.enabled")) {

            // Define all the MySQL Values
            String hostName = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String dbname = config.getString("mysql.dbname");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            boolean ssl = config.getBoolean("mysql.ssl");

            // Connect to MySQL
            this.connector = new MySQLConnector(this.plugin, hostName, port, dbname, username, password, ssl);
        } else {

            // Connect to SQLite
            FileUtils.createFile(this.plugin, "eternalcrates.db");
            this.connector = new SQLiteConnector(this.plugin, "eternalcrates.db");
        }

        this.plugin.getLogger().info("Connected to the database using " + this.connector.connectorName());

        // Connect to database async.
        this.async((task) -> this.connector.connect(connection -> {

            // Create the required tables for the plugin.
            final String query = "CREATE TABLE IF NOT EXISTS " + tableName + "_crates (world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, crate TEXT, PRIMARY KEY(world, x, y, z))";
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
        final String query = "SELECT * FROM " + tableName + "_crates";
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

        this.async(t -> this.connector.connect(connection -> {
            final String query = "REPLACE INTO " + tableName + "_crates (world, x, y, z, crate) VALUES (?, ?, ?, ?, ?)";
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

        this.async(t -> this.connector.connect(connection -> {
            final String query = "DELETE FROM " + tableName + "_crates WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, blockLoc.getWorld().getName());
                statement.setDouble(2, blockLoc.getX());
                statement.setDouble(3, blockLoc.getY());
                statement.setDouble(4, blockLoc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    @Override
    public void disable() {
        // Disable the Database Connector if possible.
        if (this.connector == null) {
            this.plugin.getLogger().info("Disconnecting from the database connector (" + connector.connectorName() + ")");
            this.connector.closeConnection();
            this.connector = null;
        }

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
