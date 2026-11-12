package com.qlgiay.dto;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Tag("DataTransferObjectTest")
@DisplayName("Kiểm thử đối tượng PhieuTraNccDTO")
class PhieuTraNccDTOTest {

    private PhieuTraNccDTO phieuTra;

    private void assertEqualsResult(String testCase, String input, Object expected, Object actual) {
        System.out.printf("[PhieuTraNccDTOTest] %s | input=%s | expected=%s | actual=%s%n",
                testCase, input, expected, actual);
        assertEquals(expected, actual);
    }

    @BeforeEach
    void setUp() {
        phieuTra = new PhieuTraNccDTO();
        System.out.print(" -> Dang chay test case: ");
    }

    @Test
    @DisplayName("Kiểm tra toàn bộ thuộc tính Phiếu Trả NCC")
    void testAllProperties() {
        LocalDate ngayTao = LocalDate.of(2026, 1, 1);
        LocalDate ngayXuLy = LocalDate.now();
        BigDecimal tongTien = new BigDecimal("5000000");

        phieuTra.setMaPT("PT001");
        phieuTra.setMaPN("PN001");
        phieuTra.setMaNV("NV001");
        phieuTra.setMaNCC("NCC001");
        phieuTra.setNgayTao(ngayTao);
        phieuTra.setTongSoMatHang(10);
        phieuTra.setTongTien(tongTien);
        phieuTra.setLyDo("Hàng lỗi");
        phieuTra.setTrangThai("Đã duyệt");
        phieuTra.setNgayXuLy(ngayXuLy);
        phieuTra.setNguoiXuLy("NV002");

        assertEqualsResult("MaPT", "PT001", "PT001", phieuTra.getMaPT());
        assertEqualsResult("MaPN", "PN001", "PN001", phieuTra.getMaPN());
        assertEqualsResult("TrangThai", "Đã duyệt", "Đã duyệt", phieuTra.getTrangThai());
        assertEqualsResult("NgayTao", ngayTao.toString(), ngayTao, phieuTra.getNgayTao());
        assertEqualsResult("NgayXuLy", ngayXuLy.toString(), ngayXuLy, phieuTra.getNgayXuLy());
        assertEqualsResult("TongTien", "5M", tongTien, phieuTra.getTongTien());
    }
}