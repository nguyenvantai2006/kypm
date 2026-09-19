package com.qlgiay.bus;

import com.qlgiay.dao.PhieuBaoHanhDAO;
import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.util.DBConnect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BaoHanhBUS {
    private final PhieuBaoHanhDAO phieuBaoHanhDAO = new PhieuBaoHanhDAO();

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isValidCommon(PhieuBaoHanhDTO pbh) {
        if (pbh == null) return false;

        String maPBH = trimOrEmpty(pbh.getMaPBH());
        String maHD = trimOrEmpty(pbh.getMaHD());
        String maSP = trimOrEmpty(pbh.getMaSP());
        String maKH = trimOrEmpty(pbh.getMaKH());

        if (maPBH.isEmpty()) return false;
        if (maHD.isEmpty()) return false;
        if (maSP.isEmpty()) return false;
        if (maKH.isEmpty()) return false;

        pbh.setMaPBH(maPBH);
        pbh.setMaHD(maHD);
        pbh.setMaSP(maSP);
        pbh.setMaKH(maKH);

        if (pbh.getLoiCanBaoHanh() != null) pbh.setLoiCanBaoHanh(pbh.getLoiCanBaoHanh().trim());

        if (pbh.getChiPhiPhatSinh() != null && pbh.getChiPhiPhatSinh().compareTo(BigDecimal.ZERO) < 0) return false;

        LocalDate nhan = pbh.getNgayNhan();
        LocalDate tra = pbh.getNgayTraDuKien();
        if (nhan != null && tra != null && tra.isBefore(nhan)) return false;

        return true;
    }

    public List<PhieuBaoHanhDTO> getAll() {
        return phieuBaoHanhDAO.findAll();
    }

    public List<PhieuBaoHanhDTO> getAllActive() {
        return phieuBaoHanhDAO.findAllActive();
    }

    public PhieuBaoHanhDTO findById(String maPBH) {
        String id = trimOrEmpty(maPBH);
        if (id.isEmpty()) return null;
        return phieuBaoHanhDAO.findById(id);
    }

    public List<PhieuBaoHanhDTO> findByMaHD(String maHD) {
        String id = trimOrEmpty(maHD);
        if (id.isEmpty()) return List.of();
        return phieuBaoHanhDAO.findByMaHD(id);
    }

    public List<PhieuBaoHanhDTO> findByMaKH(String maKH) {
        String id = trimOrEmpty(maKH);
        if (id.isEmpty()) return List.of();
        return phieuBaoHanhDAO.findByMaKH(id);
    }

    public List<PhieuBaoHanhDTO> search(String keyword) {
        return phieuBaoHanhDAO.search(keyword);
    }

    public boolean create(PhieuBaoHanhDTO pbh) {
        if (!isValidCommon(pbh)) return false;

        if (pbh.getChiPhiPhatSinh() == null) pbh.setChiPhiPhatSinh(BigDecimal.ZERO);

        if (pbh.getTrangThai() != 0 && pbh.getTrangThai() != 1 && pbh.getTrangThai() != 2) {
            pbh.setTrangThai(1);
        }

        try (Connection c = DBConnect.getConnection()) {
            c.setAutoCommit(false);

            if (phieuBaoHanhDAO.findById(c, pbh.getMaPBH()) != null) {
                c.rollback();
                return false;
            }

            boolean ok = phieuBaoHanhDAO.insert(c, pbh);
            if (!ok) {
                c.rollback();
                return false;
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(PhieuBaoHanhDTO pbh) {
        if (!isValidCommon(pbh)) return false;

        if (pbh.getChiPhiPhatSinh() == null) pbh.setChiPhiPhatSinh(BigDecimal.ZERO);

        if (pbh.getTrangThai() != 0 && pbh.getTrangThai() != 1 && pbh.getTrangThai() != 2) {
            return false;
        }

        try (Connection c = DBConnect.getConnection()) {
            c.setAutoCommit(false);

            if (phieuBaoHanhDAO.findById(c, pbh.getMaPBH()) == null) {
                c.rollback();
                return false;
            }

            boolean ok = phieuBaoHanhDAO.update(c, pbh);
            if (!ok) {
                c.rollback();
                return false;
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maPBH, int trangThai) {
        String id = trimOrEmpty(maPBH);
        if (id.isEmpty()) return false;
        if (trangThai != 0 && trangThai != 1 && trangThai != 2) return false;

        try (Connection c = DBConnect.getConnection()) {
            c.setAutoCommit(false);

            if (phieuBaoHanhDAO.findById(c, id) == null) {
                c.rollback();
                return false;
            }

            boolean ok = phieuBaoHanhDAO.updateTrangThai(c, id, trangThai);
            if (!ok) {
                c.rollback();
                return false;
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}