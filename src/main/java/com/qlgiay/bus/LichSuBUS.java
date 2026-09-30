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

    public List<LichSuDTO> findByPhanLoai(String phanLoai, LocalDate tuNgay, LocalDate denNgay) {
        if (phanLoai == null || phanLoai.isBlank() || tuNgay == null || denNgay == null
                || tuNgay.isAfter(denNgay)) {
            return List.of();
        }
        return lichSuDAO.findByPhanLoai(phanLoai.trim(), tuNgay, denNgay);
    }
}
