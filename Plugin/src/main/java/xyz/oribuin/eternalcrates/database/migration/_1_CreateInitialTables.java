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
        final String itemsQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "items (player VARCHAR(50), items VARBINARY(2456), PRIMARY KEY(player))";
        try (var statement = connection.prepareStatement(itemsQuery)) {
            statement.executeUpdate();
        }

        // The table for virtual crate keys
        final String virtualQuery = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "virtual (player VARCHAR(50), keys TEXT, PRIMARY KEY(player))";
        try (var statement = connection.prepareStatement(virtualQuery)) {
            statement.executeUpdate();
        }

    }
}
