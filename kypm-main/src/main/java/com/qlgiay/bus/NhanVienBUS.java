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
    /*1 */    if (nv == null) 
    /*2 */      return false;

    /*3 */    String maNV = trimOrEmpty(nv.getMaNV());
    /*3 */    String ho = trimOrEmpty(nv.getHo());
    /*3 */    String ten = trimOrEmpty(nv.getTen());
    /*3 */    String maQuyen = trimOrEmpty(nv.getMaQuyen());
    /*3 */    String taiKhoan = trimOrEmpty(nv.getTaiKhoan());

    /*4 */    if (maNV.isEmpty()) 
    /*2 */    return false;
    /*5 */    if (ho.isEmpty()) 
    /*2 */    return false;
    /*6 */    if (ten.isEmpty()) 
    /*2 */    return false;
    /*7 */    if (maQuyen.isEmpty()) 
    /*2 */    return false;

    /*8 */    if (taiKhoan.isEmpty() || taiKhoan.contains(" ")) 
    /*2 */    return false;

    /*9 */    if (nv.getLuong() != null && nv.getLuong().compareTo(BigDecimal.ZERO) < 0) 
    /*2 */    return false;

    /*10 */    nv.setMaNV(maNV);
    /*10 */    nv.setHo(ho);
    /*10 */    nv.setTen(ten);
    /*10 */    nv.setMaQuyen(maQuyen);
    /*10 */    nv.setTaiKhoan(taiKhoan);

    /*11 */    return true;
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