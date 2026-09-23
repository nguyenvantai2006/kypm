package com.qlgiay.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class TestDB {
    private TestDB() {
    }

    public static boolean testConnection() {
        try (Connection connection = DBConnect.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            System.out.println("Ket noi SQL Server thanh cong.");
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Driver: " + metadata.getDriverName());

            printProductCount(connection);
            printSkuIndexStatus(connection);
            return true;
        } catch (SQLException e) {
            System.err.println("Ket noi database that bai: " + e.getMessage());
            return false;
        }
    }

    private static void printProductCount(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.SAN_PHAM";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                System.out.println("So san pham trong SAN_PHAM: " + resultSet.getInt(1));
            }
        }
    }

    private static void printSkuIndexStatus(Connection connection) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM sys.indexes
                WHERE object_id = OBJECT_ID(N'dbo.SAN_PHAM')
                  AND name = N'UX_SAN_PHAM_BienThe'
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                boolean exists = resultSet.getInt(1) > 0;
                System.out.println("Filtered unique index UX_SAN_PHAM_BienThe: "
                        + (exists ? "DA TON TAI" : "CHUA TON TAI"));
            }
        }
    }

    public static void main(String[] args) {
        System.exit(testConnection() ? 0 : 1);
    }
}
