package com.qlgiay.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static final String URL = "jdbc:sqlserver://127.0.0.1:1434;" +
            "databaseName=QuanLyCuaHangGiay;" +
            "encrypt=false;" +
            "trustServerCertificate=true;" +
            "loginTimeout=30;";

    private static final String USER = "sa";
    private static final String PASS = "123456";

    private static boolean demoMode = false;

    // Biến kết nối thật sự
    private static Connection realTestConnection = null;
    // Biến bọc kết nối để chặn lệnh close()
    private static Connection proxyTestConnection = null;

    public static Connection getConnection() throws SQLException {
        // Nếu đang trong chế độ Test, trả về kết nối đã bọc Proxy
        if (proxyTestConnection != null && !realTestConnection.isClosed()) {
            return proxyTestConnection;
        }

        int retries = 5; // Tăng lên 5 lần thử
        Connection connection = null;
        while (retries > 0) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASS);
                break;
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("Connection reset")) {
                    retries--;
                    if (retries == 0)
                        throw e;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                    }
                } else {
                    throw e;
                }
            }
        }

        if (connection != null) {
            ensureProductSupplierColumn(connection);
        }

        return connection;
    }

    public static void enableTestConnection() throws SQLException {
        if (realTestConnection == null || realTestConnection.isClosed()) {
            realTestConnection = getConnection();

            // TẠO PROXY: Đánh lừa lệnh c.close() của try-with-resources trong DAO
            proxyTestConnection = (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class[] { Connection.class },
                    (proxy, method, args) -> {
                        if (method.getName().equals("close")) {
                            return null; // Bỏ qua lệnh đóng, giữ kết nối sống sót
                        }
                        return method.invoke(realTestConnection, args);
                    });
        }
    }

    public static void closeTestConnection() {
        if (realTestConnection != null) {
            try {
                realTestConnection.close(); // Chỉ thực sự đóng khi kết thúc file Test
                realTestConnection = null;
                proxyTestConnection = null;
            } catch (SQLException ignored) {
            }
        }
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
        }
    }

    public static boolean isDemoMode() {
        return demoMode;
    }

    public static void setDemoMode(boolean mode) {
        demoMode = mode;
    }

    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null)
                System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}