package xyz.oribuin.eternalcrates.manager;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.VirtualKeys;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.orilibrary.manager.DataHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class DataManager extends DataHandler {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final CrateManager crateManager;
    private final Gson gson = new Gson();
    private Map<UUID, List<ItemStack>> cachedUsers;
    private Map<UUID, Map<String, Integer>> cachedVirtual;

    public DataManager(EternalCrates plugin) {
        super(plugin);
        this.crateManager = this.plugin.getManager(CrateManager.class);
    }

    @Override
    public void enable() {
        super.enable();
        this.cachedUsers = new HashMap<>();
        this.cachedVirtual = new HashMap<>();

        // Connect to database async.
        this.async((task) -> this.getConnector().connect(connection -> {

            // Create the required tables for the plugin.
            final String query = "CREATE TABLE IF NOT EXISTS " + this.getTableName() + "_crates (crate TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, PRIMARY KEY(x, y, z, world))";
            connection.prepareStatement(query).executeUpdate();

            final String itemsQuery = "CREATE TABLE IF NOT EXISTS " + this.getTableName() + "_items (player VARCHAR(50), items VARBINARY(2456), PRIMARY KEY(player))";
            connection.prepareStatement(itemsQuery).executeUpdate();

            // The table for virtual crate keys
            final String virtualQuery = "CREATE TABLE IF NOT EXISTS " + this.getTableName() + "_virtual (player VARCHAR(50), keys TEXT, PRIMARY KEY(player))";
            connection.prepareStatement(virtualQuery).executeUpdate();

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
     * @param crate The crate location;.
     */
    public void saveCrate(Crate crate) {
        if (crate.getLocation() == null)
            return;

        final Location blockLoc = PluginUtils.getBlockLoc(crate.getLocation());
        // delete any crates that are already there
        this.crateManager.getCachedCrates().values()
                .stream()
                .filter(x -> !x.getId().equalsIgnoreCase(crate.getId()))
                .filter(x -> x.getLocation() != null && x.getLocation().equals(blockLoc))
                .findAny()
                .ifPresent(this::deleteCrate);

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
        crate.setLocation(null);
        this.crateManager.getCachedCrates().put(crate.getId(), crate);

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
     * Save and cache a user's unclaimed crate keys.
     *
     * @param uuid  The user's uuid
     * @param items The unclaimed crate keys.
     */
    public void saveUserItems(UUID uuid, List<ItemStack> items) {
        this.cachedUsers.put(uuid, items);

        final String query = "REPLACE INTO " + this.getTableName() + "_items (player, items) VALUES (?, ?)";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                statement.setBytes(2, compressItems(items));
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Save and cache a large group of users
     *
     * @param users The map of users and their items.
     */
    public void massSaveItems(Map<UUID, List<ItemStack>> users) {
        this.cachedUsers.putAll(users);

        final String query = "REPLACE INTO " + this.getTableName() + "_items (player, items) VALUES (?, ?)";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                users.forEach((uuid, itemStacks) -> {
                    try {
                        statement.setString(1, uuid.toString());
                        statement.setBytes(2, compressItems(itemStacks));
                        statement.addBatch();
                    } catch (SQLException ignored) {
                    }
                });

                statement.executeBatch();
            }
        }));
    }

    /**
     * Get a user's current unclaimed items.
     *
     * @param uuid The User's UUID
     * @return The list of items.
     */
    public List<ItemStack> getUserItems(UUID uuid) {
        if (this.cachedUsers.get(uuid) != null)
            return this.cachedUsers.get(uuid);

        final String query = "SELECT items FROM " + this.getTableName() + "_items WHERE player = ?";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                final ResultSet result = statement.executeQuery();
                if (result.next()) {
                    this.cachedUsers.put(uuid, decompressItems(result.getBytes(1)));
                }
            }
        }));

        return this.cachedUsers.getOrDefault(uuid, new ArrayList<>());
    }

    /**
     * Save a player's virtual crate keys.
     *
     * @param uuid The UUID of the player
     * @param keys A map of crate ids and key count.
     */
    public void saveVirtual(UUID uuid, Map<String, Integer> keys) {
        this.cachedVirtual.put(uuid, keys);

        final String query = "REPLACE INTO " + this.getTableName() + "_virtual (player, keys) VALUES (?, ?)";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, gson.toJson(new VirtualKeys(keys)));
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Mass save virtual crate keys.
     *
     * @param keys The keys being saved.
     */
    public void massSaveVirtual(Map<UUID, VirtualKeys> keys) { // didnt wanna nest maps
        keys.forEach((uuid, obj) -> this.cachedVirtual.put(uuid, obj.getKeys()));

        final String query = "REPLACE INTO " + this.getTableName() + "_virtual (player, keys) VALUES (?, ?)";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                keys.forEach((uuid, obj) -> {
                    try {
                        statement.setString(1, uuid.toString());
                        statement.setString(2, gson.toJson(obj));
                        statement.addBatch();
                    } catch (SQLException ignored) {
                    }
                });

                statement.executeBatch();
            }
        }));
    }

    /**
     * Get all the virtual keys that ap layer owns
     *
     * @param uuid The UUID of the player.
     * @return The list of crate ids and the amount of keys they own
     */
    public Map<String, Integer> getVirtual(UUID uuid) {
        if (this.cachedVirtual.get(uuid) != null)
            return this.cachedVirtual.get(uuid);

        final String query = "SELECT keys FROM " + this.getTableName() + "_virtual WHERE player = ?";
        this.async(task -> this.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                final ResultSet result = statement.executeQuery();
                if (result.next()) {
                    VirtualKeys keys = gson.fromJson(result.getString(1), VirtualKeys.class);
                    this.cachedVirtual.put(uuid, keys.getKeys());
                }
            }
        }));

        return this.cachedVirtual.getOrDefault(uuid, new HashMap<>());
    }


    /**
     * Compress a list of ItemStacks into a byte array.
     *
     * @param items The list of items.
     * @return The new byte array.
     */
    public byte[] compressItems(List<ItemStack> items) {
        byte[] data = new byte[0];

        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(os)) {
            oos.writeInt(items.size());
            for (ItemStack item : items)
                oos.writeObject(item);

            data = os.toByteArray();

        } catch (IOException ignored) {
        }

        return data;
    }

    /**
     * Decompress a byte array into a list of ItemStacks
     *
     * @param data The byte array
     * @return The list of items.
     */
    public List<ItemStack> decompressItems(byte[] data) {
        final List<ItemStack> items = new ArrayList<>();
        try (ByteArrayInputStream is = new ByteArrayInputStream(data); BukkitObjectInputStream ois = new BukkitObjectInputStream(is)) {
            int amount = ois.readInt();
            for (int i = 0; i < amount; i++)
                items.add((ItemStack) ois.readObject());
        } catch (IOException | ClassNotFoundException ignored) {
        }

        return items;
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

    public Map<UUID, List<ItemStack>> getCachedUsers() {
        return cachedUsers;
    }

    public Map<UUID, Map<String, Integer>> getCachedVirtual() {
        return cachedVirtual;
    }
}
