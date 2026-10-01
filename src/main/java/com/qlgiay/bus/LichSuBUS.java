package com.qlgiay.bus;

import com.qlgiay.dao.LichSuDAO;
import com.qlgiay.dto.LichSuDTO;

import java.time.LocalDate;
import java.util.List;

public class LichSuBUS {
    private final LichSuDAO lichSuDAO = new LichSuDAO();

    public boolean insert(LichSuDTO log) {
        if (log == null || log.getPhanLoai() == null || log.getPhanLoai().isBlank()
                || log.getHanhDong() == null || log.getHanhDong().isBlank()) {
            return false;
        }
        return lichSuDAO.insert(log);
    }

    public boolean ghiNhatKy(String maNV, String phanLoai, String hanhDong,
            String doiTuong, String chiTiet) {
        LichSuDTO log = new LichSuDTO();
        log.setMaNV(maNV);
        log.setPhanLoai(phanLoai);
        log.setHanhDong(hanhDong);
        log.setDoiTuong(doiTuong);
        log.setThoiGian(java.time.LocalDateTime.now());
        log.setChiTiet(chiTiet);
        return insert(log);
    }

    public List<LichSuDTO> findByPhanLoaiAndDate(String phanLoai, LocalDate tuNgay, LocalDate denNgay) {
        if (phanLoai == null || phanLoai.isBlank() || tuNgay == null || denNgay == null
                || tuNgay.isAfter(denNgay)) {
            return List.of();
        }
        return lichSuDAO.findByPhanLoaiAndDate(phanLoai.trim(), tuNgay, denNgay);
    }
}
