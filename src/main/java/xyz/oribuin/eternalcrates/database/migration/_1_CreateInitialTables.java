package xyz.oribuin.eternalcrates.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {

        // Create the required tables unclaimed keys.
        final var itemsQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "keys (player VARCHAR(50), items TEXT, PRIMARY KEY(player))";
        try (var statement = connection.prepareStatement(itemsQuery)) {
            statement.executeUpdate();
        }

        // The table for crate locations, crateName, x, y, z, world
        final var crateQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "crates (crateName VARCHAR(50), x INT, y INT, z INT, world VARCHAR(50))";
        try (var statement = connection.prepareStatement(crateQuery)) {
            statement.executeUpdate();
        }

    }

}
