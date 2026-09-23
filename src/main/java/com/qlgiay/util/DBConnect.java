package com.qlgiay.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static final boolean DEMO_MODE =
            Boolean.parseBoolean(System.getProperty("qlgiay.demo", "true"));

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=QuanLyCuaHangGiay;" +
                    "encrypt=true;" +
                    "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASS = "123";

    public static boolean isDemoMode() {
        return DEMO_MODE;
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASS);
        ensureProductSupplierColumn(connection);
        return connection;
    }

    private static void ensureProductSupplierColumn(Connection connection) {
        String sql = """
                IF COL_LENGTH('dbo.SAN_PHAM', 'MaNCC') IS NULL
                BEGIN
                    ALTER TABLE dbo.SAN_PHAM ADD MaNCC nvarchar(50) NULL;
                END;

                IF COL_LENGTH('dbo.SAN_PHAM', 'MaNCC') IS NOT NULL
                   AND NOT EXISTS (
                       SELECT 1 FROM sys.foreign_keys
                       WHERE name = 'FK_SAN_PHAM_NHA_CUNG_CAP'
                   )
                BEGIN
                    ALTER TABLE dbo.SAN_PHAM
                        ADD CONSTRAINT FK_SAN_PHAM_NHA_CUNG_CAP
                        FOREIGN KEY (MaNCC) REFERENCES dbo.NHA_CUNG_CAP(MaNCC);
                END;
                """;

        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
            // Keep connection errors visible to the DAO while allowing existing databases to open.
        }
    }
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("Kết nối cơ sở dữ liệu thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}