package com.qlgiay.bus;

import com.qlgiay.dao.QuyenDAO;
import com.qlgiay.dto.QuyenDTO;

import java.util.List;

public class QuyenBUS {
    private final QuyenDAO quyenDAO = new QuyenDAO();

    private String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isValid(QuyenDTO q) {
        if (q == null) return false;

        String maQuyen = trimOrEmpty(q.getMaQuyen());
        String tenQuyen = trimOrEmpty(q.getTenQuyen());

        if (maQuyen.isEmpty()) return false;
        if (tenQuyen.isEmpty()) return false;

        q.setMaQuyen(maQuyen);
        q.setTenQuyen(tenQuyen);

        return true;
    }

    public List<QuyenDTO> getAll() {
        return quyenDAO.findAll();
    }

    public QuyenDTO findById(String maQuyen) {
        String id = trimOrEmpty(maQuyen);
        if (id.isEmpty()) return null;
        return quyenDAO.findById(id);
    }

    public boolean addQuyen(QuyenDTO q) {
        if (!isValid(q)) return false;

        if (quyenDAO.findById(q.getMaQuyen()) != null) return false;

        return quyenDAO.insert(q);
    }

    public boolean updateQuyen(QuyenDTO q) {
        if (!isValid(q)) return false;

        if (quyenDAO.findById(q.getMaQuyen()) == null) return false;

        return quyenDAO.update(q);
    }

    public boolean deleteQuyen(String maQuyen) {
        String id = trimOrEmpty(maQuyen);
        if (id.isEmpty()) return false;
        return quyenDAO.delete(id);
    }

    public List<QuyenDTO> search(String keyword) {
        return quyenDAO.search(keyword);
    }
}