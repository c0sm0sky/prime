package pl.demo.prime.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers(disabledWithoutDocker = true)
class DatabaseCascadeIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18");

    @BeforeAll
    static void migrateSchema() {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    @Test
    void seedsSeveralLinkedRowsForEachDashboardTab() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            assertEquals(3, count(connection, "customers", "email like '%@example.com'"));
            assertEquals(3, count(connection, "customer_orders", "name like '% — %'"));
            assertEquals(3, count(connection, "revenues", "description like '% — %' and order_id is not null"));
        }
    }

    @Test
    void databaseRejectsDuplicateCustomerEmailsRegardlessOfCase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into customers (name, email, status) values (?, ?, ?)")) {
                statement.setString(1, "Duplikat");
                statement.setString(2, "ANNA.NOWAK@EXAMPLE.COM");
                statement.setString(3, "Nowy");
                assertThrows(SQLException.class, statement::executeUpdate);
            }
        }
    }

    @Test
    void deletingOrderAndCustomerCascadesDependentsButKeepsUnassignedRevenue() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            connection.setAutoCommit(false);
            long initialCustomers = count(connection, "customers");
            long initialOrders = count(connection, "customer_orders");
            long initialRevenues = count(connection, "revenues");
            long initialUnassignedRevenues = count(connection, "revenues", "order_id is null");

            try {
                long firstCustomer = insertCustomer(connection, "Klient pierwszy", "first@example.com");
                long secondCustomer = insertCustomer(connection, "Klient drugi", "second@example.com");
                long firstOrder = insertOrder(connection, firstCustomer, "Zamówienie pierwsze");
                long secondOrder = insertOrder(connection, secondCustomer, "Zamówienie drugie");
                insertRevenue(connection, firstOrder);
                insertRevenue(connection, secondOrder);
                insertRevenue(connection, null);

                delete(connection, "customer_orders", secondOrder);
                assertEquals(initialOrders + 1, count(connection, "customer_orders"));
                assertEquals(initialRevenues + 2, count(connection, "revenues"));
                assertEquals(initialCustomers + 2, count(connection, "customers"));

                delete(connection, "customers", firstCustomer);
                assertEquals(initialCustomers + 1, count(connection, "customers"));
                assertEquals(initialOrders, count(connection, "customer_orders"));
                assertEquals(initialRevenues + 1, count(connection, "revenues"));
                assertEquals(initialUnassignedRevenues + 1, count(connection, "revenues", "order_id is null"));
            } finally {
                connection.rollback();
            }
        }
    }

    private long insertCustomer(Connection connection, String name, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into customers (name, email, status) values (?, ?, 'Nowy') returning id")) {
            statement.setString(1, name);
            statement.setString(2, email);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private long insertOrder(Connection connection, long customerId, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into customer_orders (customer_id, name, status) values (?, ?, 'Nowe') returning id")) {
            statement.setLong(1, customerId);
            statement.setString(2, name);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private void insertRevenue(Connection connection, Long orderId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into revenues (description, amount, received_on, order_id) "
                        + "values ('Wpływ', 10.00, current_date, ?)")) {
            if (orderId == null) statement.setNull(1, Types.BIGINT);
            else statement.setLong(1, orderId);
            statement.executeUpdate();
        }
    }

    private void delete(Connection connection, String table, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from " + table + " where id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private long count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("select count(*) from " + table)) {
            result.next();
            return result.getLong(1);
        }
    }

    private long count(Connection connection, String table, String whereClause) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("select count(*) from " + table + " where " + whereClause)) {
            result.next();
            return result.getLong(1);
        }
    }
}
