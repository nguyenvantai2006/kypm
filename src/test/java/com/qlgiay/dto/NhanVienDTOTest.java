package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng NhanVienDTO")
class NhanVienDTOTest {

    private NhanVienDTO nv;

    @BeforeEach
    void setUp() {
        nv = new NhanVienDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Nhân Viên")
    void testAllProperties() {
        BigDecimal luong = new BigDecimal("15000000");

        nv.setMaNV("NV001");
        nv.setHo("Nguyễn");
        nv.setTen("B");
        nv.setMaQuyen("Q01");
        nv.setTaiKhoan("admin");
        nv.setMatKhau("123456");
        nv.setLuong(luong);
        nv.setTrangThai(1);

        assertEquals("NV001", nv.getMaNV());
        assertEquals("Nguyễn", nv.getHo());
        assertEquals("Q01", nv.getMaQuyen());
        assertEquals(luong, nv.getLuong());
        assertEquals(1, nv.getTrangThai());
        System.out.println("[NhanVienDTOTest] Pass toàn bộ thuộc tính");
    }
}