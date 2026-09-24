package com.qlgiay.bus;

import com.qlgiay.dao.NhanVienDAO;
import com.qlgiay.dao.QuyenDAO;
import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.QuyenDTO;

import java.math.BigDecimal;
import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final QuyenDAO quyenDAO = new QuyenDAO();

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isValidCommon(NhanVienDTO nv) {
        if (nv == null) return false;

        String maNV = trimOrEmpty(nv.getMaNV());
        String ho = trimOrEmpty(nv.getHo());
        String ten = trimOrEmpty(nv.getTen());
        String maQuyen = trimOrEmpty(nv.getMaQuyen());
        String taiKhoan = trimOrEmpty(nv.getTaiKhoan());

        if (maNV.isEmpty()) return false;
        if (ho.isEmpty()) return false;
        if (ten.isEmpty()) return false;
        if (maQuyen.isEmpty()) return false;

        if (taiKhoan.isEmpty() || taiKhoan.contains(" ")) return false;

        if (nv.getLuong() != null && nv.getLuong().compareTo(BigDecimal.ZERO) < 0) return false;

        nv.setMaNV(maNV);
        nv.setHo(ho);
        nv.setTen(ten);
        nv.setMaQuyen(maQuyen);
        nv.setTaiKhoan(taiKhoan);

        return true;
    }

    private boolean isValidForAdd(NhanVienDTO nv) {
        if (!isValidCommon(nv)) return false;

        String mk = trimOrEmpty(nv.getMatKhau());
        if (mk.isEmpty()) return false;
        if (mk.length() < 6) return false;

        nv.setMatKhau(mk);
        return true;
    }

    private boolean isValidForUpdate(NhanVienDTO nv) {
        if (!isValidCommon(nv)) return false;

        String mk = trimOrEmpty(nv.getMatKhau());
        if (!mk.isEmpty() && mk.length() < 6) return false;

        nv.setMatKhau(mk.isEmpty() ? null : mk);
        return true;
    }

    public List<NhanVienDTO> getAll() {
        return nhanVienDAO.findAll();
    }

    public List<NhanVienDTO> getAllActive() {
        return nhanVienDAO.findAllActive();
    }

    public NhanVienDTO findById(String maNV) {
        String id = trimOrEmpty(maNV);
        if (id.isEmpty()) return null;
        return nhanVienDAO.findById(id);
    }

    public NhanVienDTO findByTaiKhoan(String taiKhoan) {
        String tk = trimOrEmpty(taiKhoan);
        if (tk.isEmpty()) return null;
        return nhanVienDAO.findByTaiKhoan(tk);
    }

    public boolean addNhanVien(NhanVienDTO nv) {
        if (!isValidForAdd(nv)) return false;

        if (nhanVienDAO.findById(nv.getMaNV()) != null) return false;
        if (nhanVienDAO.findByTaiKhoan(nv.getTaiKhoan()) != null) return false;

        QuyenDTO q = quyenDAO.findById(nv.getMaQuyen());
        if (q == null) return false;

        if (nv.getTrangThai() != 0 && nv.getTrangThai() != 1) nv.setTrangThai(1);

        return nhanVienDAO.insert(nv);
    }

    public boolean updateNhanVien(NhanVienDTO nv) {
        if (!isValidForUpdate(nv)) return false;

        NhanVienDTO current = nhanVienDAO.findById(nv.getMaNV());
        if (current == null) return false;

        QuyenDTO q = quyenDAO.findById(nv.getMaQuyen());
        if (q == null) return false;

        NhanVienDTO exist = nhanVienDAO.findByTaiKhoan(nv.getTaiKhoan());
        if (exist != null && !exist.getMaNV().equals(nv.getMaNV())) return false;

        if (nv.getTrangThai() != 0 && nv.getTrangThai() != 1) {
            nv.setTrangThai(current.getTrangThai());
        }

        return nhanVienDAO.update(nv);
    }

    public boolean lockNhanVien(String maNV) {
        String id = trimOrEmpty(maNV);
        if (id.isEmpty()) return false;
        return nhanVienDAO.updateTrangThai(id, 0);
    }

    public boolean unlockNhanVien(String maNV) {
        String id = trimOrEmpty(maNV);
        if (id.isEmpty()) return false;
        return nhanVienDAO.updateTrangThai(id, 1);
    }

    public List<NhanVienDTO> search(String keyword) {
        return nhanVienDAO.search(keyword);
    }

    public List<NhanVienDTO> searchActive(String keyword) {
        return nhanVienDAO.searchActive(keyword);
    }
}