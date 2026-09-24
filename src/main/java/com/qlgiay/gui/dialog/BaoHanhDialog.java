package com.qlgiay.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.qlgiay.bus.BaoHanhBUS;
import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.dto.SanPhamDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BaoHanhDialog extends JDialog {
    private final BaoHanhBUS baoHanhBUS = new BaoHanhBUS();
    private String maHD;
    private String maKH;
    
    private JComboBox<DoiTraDialog.ProductComboItem> cboSanPham; // Reusing ComboItem logic
    private DatePicker dpNgayTra;
    private JTextField txtChiPhi;
    private JTextArea txtLoi;

    public BaoHanhDialog(Window owner, String maHD) {
        super(owner, "Tạo Phiếu Sửa Chữa", ModalityType.APPLICATION_MODAL);
        this.maHD = maHD;
        
        HoaDonDTO hd = new HoaDonDAO().findById(maHD);
        this.maKH = hd != null ? hd.getMaKH() : "";
        if (this.maKH == null || this.maKH.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Hóa đơn khách lẻ không áp dụng sửa chữa/bảo hành!");
            dispose(); return;
        }

        initUI();
        setSize(450, 450);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnl.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5); gbc.weightx = 1.0;

        cboSanPham = new JComboBox<>();
        loadProductsFromInvoice();

        DatePickerSettings ds = new DatePickerSettings();
        ds.setFormatForDatesCommonEra("yyyy-MM-dd");
        dpNgayTra = new DatePicker(ds);
        dpNgayTra.setDate(LocalDate.now().plusDays(7));

        txtChiPhi = new JTextField("0");
        txtLoi = new JTextArea(3, 20); txtLoi.setLineWrap(true);

        int row = 0;
        gbc.gridy = row++; pnl.add(new JLabel("Hóa đơn: " + maHD), gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Sản phẩm cần sửa chữa:"), gbc);
        gbc.gridy = row++; pnl.add(cboSanPham, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Ngày trả dự kiến:"), gbc);
        gbc.gridy = row++; pnl.add(dpNgayTra, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Chi phí phát sinh (nếu có):"), gbc);
        gbc.gridy = row++; pnl.add(txtChiPhi, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Tình trạng / Lỗi cần sửa:"), gbc);
        gbc.gridy = row++; pnl.add(new JScrollPane(txtLoi), gbc);

        JButton btnSave = new JButton("Xác nhận");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:#E8F5E9;foreground:#2E7D32;arc:8;");
        btnSave.addActionListener(e -> submit());

        add(pnl, BorderLayout.CENTER);
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(Color.WHITE); pnlBot.add(btnSave);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void loadProductsFromInvoice() {
        List<ChiTietHoaDonDTO> items = new ChiTietHoaDonDAO().findByMaHD(maHD);
        SanPhamDAO spDAO = new SanPhamDAO();
        if (items != null) {
            for (ChiTietHoaDonDTO ct : items) {
                SanPhamDTO sp = spDAO.findById(ct.getMaSP());
                if (sp != null) {
                    DoiTraDialog dialog = new DoiTraDialog(null, maHD); // Dummy instance to access inner class
                    cboSanPham.addItem(dialog.new ProductComboItem(ct, sp));
                }
            }
        }
    }

    private void submit() {
        DoiTraDialog.ProductComboItem item = (DoiTraDialog.ProductComboItem) cboSanPham.getSelectedItem();
        if (item == null || txtLoi.getText().trim().isEmpty() || dpNgayTra.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!"); return;
        }

        PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO();
        pbh.setMaPBH("SC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")));
        pbh.setMaHD(maHD);
        pbh.setMaKH(maKH);
        pbh.setMaSP(item.sp.getMaSP());
        pbh.setNgayNhan(LocalDate.now());
        pbh.setNgayTraDuKien(dpNgayTra.getDate());
        pbh.setLoiCanBaoHanh(txtLoi.getText().trim());
        pbh.setTrangThai(0); // 0 = Đang sửa chữa

        try {
            pbh.setChiPhiPhatSinh(new BigDecimal(txtChiPhi.getText().trim()));
        } catch (Exception ex) { pbh.setChiPhiPhatSinh(BigDecimal.ZERO); }

        if (baoHanhBUS.create(pbh)) {
            JOptionPane.showMessageDialog(this, "Tạo thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo!");
        }
    }
}