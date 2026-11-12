package com.qlgiay.bus;

import com.qlgiay.dao.NhanVienDAO;
import com.qlgiay.dao.QuyenDAO;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.util.DBConnect;
import com.qlgiay.util.DemoAuthAccount;

public class AuthBUS {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final QuyenDAO quyenDAO = new QuyenDAO();

    public AuthSession login(String taiKhoan, String matKhauPlain) {
        if (taiKhoan == null || taiKhoan.isBlank()) return null;
        if (matKhauPlain == null || matKhauPlain.isBlank()) return null;

        String tk = taiKhoan.trim();

        if (DBConnect.isDemoMode()) {
            if (!DemoAuthAccount.TAI_KHOAN.equalsIgnoreCase(tk)) return null;
            if (!DemoAuthAccount.MAT_KHAU.equals(matKhauPlain)) return null;

            NhanVienDTO nv = DemoAuthAccount.createNhanVien();
            QuyenDTO quyen = DemoAuthAccount.createQuyen();
            return new AuthSession(nv, quyen);
        }

        NhanVienDTO nv = nhanVienDAO.findByTaiKhoan(tk);
        if (nv == null) return null;
        if (nv.getTrangThai() != 1) return null;

        String storedHash = nv.getMatKhau();
        if (storedHash == null || storedHash.isBlank()) return null;

        boolean ok = matKhauPlain.equals(storedHash);
        if (!ok) return null;

        QuyenDTO quyen = quyenDAO.findById(nv.getMaQuyen());
        if (quyen == null) return null;

        return new AuthSession(nv, quyen);
    }
}