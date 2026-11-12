package com.qlgiay.dao;

import com.qlgiay.util.DBConnect;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Tag("DataAccessObjectTest")
@DisplayName("Kiểm thử lớp ThongKeDAO (Tương tác CSDL thật)")
class ThongKeDAOTest {

    private ThongKeDAO thongKeDAO;
    private final String TEST_HD_ID = "HD_TEST_99";
    private final String TEST_PN_ID = "PN_TEST_99";
    private final String TEST_SP_ID = "SP_TEST_99";
    private final String TEST_KH_ID = "KH_TEST_99";
    private final String TEST_NV_ID = "NV_TEST_99";
    private final String TEST_Q_ID = "Q_TEST_99";
    private final String TEST_NCC_ID = "NCC_TEST_99";

    // MỞ RỘNG KHOẢNG THỜI GIAN ĐỂ BAO TRỌN KHUNG GIỜ CỦA 'GETDATE()'
    private final LocalDate START_DATE = LocalDate.now().minusDays(1);
    private final LocalDate END_DATE = LocalDate.now().plusDays(1);

    @BeforeAll
    static void initAll() {
        try {
            DBConnect.enableTestConnection();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        DBConnect.closeTestConnection();
    }

    @BeforeEach
    void setUp() {
        thongKeDAO = new ThongKeDAO();
        hardDeleteTestRecords();
        insertDummyTransactions();
        System.out.print(" -> Dang chay test case: ");
    }

    @AfterEach
    void tearDown() {
        hardDeleteTestRecords();
    }

    private void hardDeleteTestRecords() {
        try (Connection c = DBConnect.getConnection()) {
            c.prepareStatement("DELETE FROM CHI_TIET_HOA_DON WHERE MaHD = '" + TEST_HD_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM HOA_DON WHERE MaHD = '" + TEST_HD_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM CHI_TIET_PHIEU_NHAP WHERE MaPN = '" + TEST_PN_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM PHIEU_NHAP WHERE MaPN = '" + TEST_PN_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM KHACH_HANG WHERE MaKH = '" + TEST_KH_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM SAN_PHAM WHERE MaSP = '" + TEST_SP_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM NHA_CUNG_CAP WHERE MaNCC = '" + TEST_NCC_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM NHAN_VIEN WHERE MaNV = '" + TEST_NV_ID + "'").executeUpdate();
            c.prepareStatement("DELETE FROM QUYEN WHERE MaQuyen = '" + TEST_Q_ID + "'").executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private void insertDummyTransactions() {
        try (Connection c = DBConnect.getConnection()) {
            c.prepareStatement("INSERT INTO QUYEN (MaQuyen, TenQuyen) VALUES ('" + TEST_Q_ID + "', 'Quyen Test')")
                    .executeUpdate();
            c.prepareStatement("INSERT INTO NHAN_VIEN (MaNV, MaQuyen, TaiKhoan, MatKhau) VALUES ('" + TEST_NV_ID
                    + "', '" + TEST_Q_ID + "', 'tk_test', '123')").executeUpdate();
            c.prepareStatement("INSERT INTO NHA_CUNG_CAP (MaNCC, TenNCC) VALUES ('" + TEST_NCC_ID + "', 'NCC Test')")
                    .executeUpdate();
            c.prepareStatement("INSERT INTO SAN_PHAM (MaSP, TenSP, LoaiSP, MaNCC) VALUES ('" + TEST_SP_ID
                    + "', 'SP Test', 'Loai Test', '" + TEST_NCC_ID + "')").executeUpdate();
            c.prepareStatement(
                    "INSERT INTO KHACH_HANG (MaKH, TenKH, TrangThai) VALUES ('" + TEST_KH_ID + "', 'KH Test', 1)")
                    .executeUpdate();

            // Lập Phiếu nhập
            c.prepareStatement("INSERT INTO PHIEU_NHAP (MaPN, MaNV, MaNCC, NgayNhap, TongTien) VALUES ('" + TEST_PN_ID
                    + "', '" + TEST_NV_ID + "', '" + TEST_NCC_ID + "', GETDATE(), 1000000)").executeUpdate();
            c.prepareStatement("INSERT INTO CHI_TIET_PHIEU_NHAP (MaPN, MaSP, SoLuong, GiaNhap) VALUES ('" + TEST_PN_ID
                    + "', '" + TEST_SP_ID + "', 10, 100000)").executeUpdate();

            // Lập Hóa đơn
            c.prepareStatement("INSERT INTO HOA_DON (MaHD, MaNV, MaKH, NgayLap, TongTien) VALUES ('" + TEST_HD_ID
                    + "', '" + TEST_NV_ID + "', '" + TEST_KH_ID + "', GETDATE(), 1000000)").executeUpdate();
            c.prepareStatement("INSERT INTO CHI_TIET_HOA_DON (MaHD, MaSP, SoLuong, DonGia) VALUES ('" + TEST_HD_ID
                    + "', '" + TEST_SP_ID + "', 5, 200000)").executeUpdate();
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("Kiểm tra Thống kê Tổng quan (Doanh thu, Tiền vốn, Số lượng)")
    void testTongQuanStats() {
        BigDecimal doanhThu = thongKeDAO.getTongDoanhThu(START_DATE, END_DATE);
        BigDecimal tienVon = thongKeDAO.getTongTienVon(START_DATE, END_DATE);
        int soHD = thongKeDAO.getSoHoaDon(START_DATE, END_DATE);
        int soSP = thongKeDAO.getTongSanPhamBan(START_DATE, END_DATE);

        assertTrue(doanhThu.compareTo(new BigDecimal("1000000")) >= 0, "Doanh thu phải cộng dồn ít nhất 1.000.000");
        assertTrue(tienVon.compareTo(new BigDecimal("1000000")) >= 0, "Tiền vốn phải cộng dồn ít nhất 1.000.000");
        assertTrue(soHD >= 1, "Phải có ít nhất 1 hóa đơn");
        assertTrue(soSP >= 5, "Phải có ít nhất 5 sản phẩm bán ra");
    }

    @Test
    @DisplayName("Kiểm tra Thống kê Top Bán Chạy")
    void testTopStats() {
        var topSP = thongKeDAO.getTopSanPham(START_DATE, END_DATE);
        assertFalse(topSP.isEmpty(), "Danh sách top sản phẩm không được rỗng");
        assertTrue(topSP.stream().anyMatch(s -> s.getMaSP().equals(TEST_SP_ID)), "SP Test phải lọt top bán chạy");

        var topKH = thongKeDAO.getTopKhachHang(START_DATE, END_DATE);
        assertFalse(topKH.isEmpty(), "Danh sách top khách hàng không được rỗng");
        assertTrue(topKH.stream().anyMatch(k -> k.getMaKH().equals(TEST_KH_ID)), "KH Test phải lọt top chi tiêu");
    }
}