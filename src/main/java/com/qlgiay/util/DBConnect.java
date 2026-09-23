package com.qlgiay.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static final String URL = "jdbc:sqlserver://localhost:1434;" +
            "databaseName=QuanLyCuaHangGiay;" +
            "encrypt=true;" +
            "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASS = "123456";

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

                IF COL_LENGTH('dbo.SAN_PHAM', 'PhanTramLoiNhuan') IS NULL
                BEGIN
                    ALTER TABLE dbo.SAN_PHAM ADD PhanTramLoiNhuan decimal(5, 2) NULL;
                END;

                UPDATE dbo.SAN_PHAM
                SET PhanTramLoiNhuan = 20
                WHERE PhanTramLoiNhuan IS NULL OR PhanTramLoiNhuan <= 0;

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

                IF NOT EXISTS (
                    SELECT 1
                    FROM sys.indexes
                    WHERE name = 'UX_SAN_PHAM_BienThe'
                      AND object_id = OBJECT_ID('dbo.SAN_PHAM')
                )
                BEGIN
                    CREATE UNIQUE NONCLUSTERED INDEX UX_SAN_PHAM_BienThe
                        ON dbo.SAN_PHAM (TenSP, ThuongHieu, MauSac, Size)
                        WHERE MauSac IS NOT NULL AND Size IS NOT NULL;
                END;
                """;

        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ignored) {
            // Keep connection errors visible to the DAO while allowing existing databases
            // to open.
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