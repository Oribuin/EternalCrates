package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.MySQLConnector;
import xyz.oribuin.orilibrary.database.SQLiteConnector;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;

import java.util.HashMap;
import java.util.Map;
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
            : config.getString("mysql.tablename") ;

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

            // TODO Add crate location logging.
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
