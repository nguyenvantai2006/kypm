package com.qlgiay.util;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UtilityTest")
@DisplayName("Kiểm thử Kết nối Cơ sở dữ liệu (DBConnect)")
class DBConnectTest {

    @Test
    @DisplayName("Ping SQL Server: Thử thiết lập kết nối")
    void testConnection() {
        System.out.println("==================================================");
        System.out.println("[DBConnectTest] Đang thử kết nối đến SQL Server...");
        System.out.println("==================================================");

        try (Connection conn = DBConnect.getConnection()) {

            // Nếu chạy xuống được dòng này nghĩa là không bị Connection reset
            assertNotNull(conn, "Đối tượng Connection khởi tạo không thành công (null)");

            System.out.println("[THÀNH CÔNG] Đã kết nối thành công!");
            System.out.println(" -> Database Name: " + conn.getCatalog());
            System.out.println(" -> Chế độ AutoCommit: " + conn.getAutoCommit());

        } catch (SQLException e) {
            System.err.println("[THẤT BẠI] Lỗi kết nối CSDL!");
            System.err.println(" -> Mã lỗi (ErrorCode): " + e.getErrorCode());
            System.err.println(" -> Trạng thái (SQLState): " + e.getSQLState());
            System.err.println(" -> Chi tiết: " + e.getMessage());

            fail("Kết nối CSDL văng lỗi: " + e.getMessage());
        }
    }
}