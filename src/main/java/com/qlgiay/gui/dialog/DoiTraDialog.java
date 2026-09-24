package com.qlgiay.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.DoiTraBUS;
import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.SanPhamDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoiTraDialog extends JDialog {
    private final DoiTraBUS doiTraBUS = new DoiTraBUS();
    private final ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO spDAO = new SanPhamDAO();
    
    private String maHD;
    private String maNV;
    
    private JComboBox<ProductComboItem> cboSanPham;
    private JTextField txtSoLuong;
    private JTextField txtTongHoan;
    private JComboBox<String> cboTinhTrang;
    private JTextArea txtLyDo;

    public DoiTraDialog(Window owner, String maHD) {
        super(owner, "Tạo Phiếu Đổi Trả", ModalityType.APPLICATION_MODAL);
        this.maHD = maHD;
        
        HoaDonDTO hd = new HoaDonDAO().findById(maHD);
        this.maNV = hd != null ? hd.getMaNV() : "";

        initUI();
        loadProductsFromInvoice();
        setSize(450, 500);
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnl.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5); gbc.weightx = 1.0;

        cboSanPham = new JComboBox<>();
        txtSoLuong = new JTextField();
        txtTongHoan = new JTextField("0đ"); txtTongHoan.setEditable(false);
        cboTinhTrang = new JComboBox<>(new String[]{"Còn nguyên", "Lỗi nhẹ", "Lỗi nặng"});
        txtLyDo = new JTextArea(3, 20); txtLyDo.setLineWrap(true);
        
        ((AbstractDocument) txtSoLuong.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        int row = 0;
        gbc.gridy = row++; pnl.add(new JLabel("Hóa đơn: " + maHD), gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Sản phẩm đổi/trả:"), gbc);
        gbc.gridy = row++; pnl.add(cboSanPham, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Số lượng:"), gbc);
        gbc.gridy = row++; pnl.add(txtSoLuong, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridy = row++; pnl.add(cboTinhTrang, gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Lý do:"), gbc);
        gbc.gridy = row++; pnl.add(new JScrollPane(txtLyDo), gbc);
        gbc.gridy = row++; pnl.add(new JLabel("Tổng tiền hoàn:"), gbc);
        gbc.gridy = row++; pnl.add(txtTongHoan, gbc);

        // Auto calculate
        txtSoLuong.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calc(); }
            public void removeUpdate(DocumentEvent e) { calc(); }
            public void changedUpdate(DocumentEvent e) { calc(); }
        });
        cboSanPham.addActionListener(e -> calc());

        JButton btnSave = new JButton("Xác nhận");
        btnSave.putClientProperty(FlatClientProperties.STYLE, "background:#E8F5E9;foreground:#2E7D32;arc:8;");
        btnSave.addActionListener(e -> submit());

        add(pnl, BorderLayout.CENTER);
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(Color.WHITE); pnlBot.add(btnSave);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private void loadProductsFromInvoice() {
        List<ChiTietHoaDonDTO> items = chiTietDAO.findByMaHD(maHD);
        if (items != null) {
            for (ChiTietHoaDonDTO ct : items) {
                SanPhamDTO sp = spDAO.findById(ct.getMaSP());
                if (sp != null) cboSanPham.addItem(new ProductComboItem(ct, sp));
            }
        }
    }

    private void calc() {
        ProductComboItem item = (ProductComboItem) cboSanPham.getSelectedItem();
        String qtyText = txtSoLuong.getText().trim();
        if (item == null || qtyText.isEmpty()) { txtTongHoan.setText("0đ"); return; }
        
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty > item.ct.getSoLuong()) {
                txtTongHoan.setText("Vượt quá SL mua!"); return;
            }
            BigDecimal total = item.ct.getDonGia().multiply(new BigDecimal(qty));
            txtTongHoan.setText(new DecimalFormat("#,###").format(total) + "đ");
        } catch (Exception ex) { txtTongHoan.setText("0đ"); }
    }

    private void submit() {
        ProductComboItem item = (ProductComboItem) cboSanPham.getSelectedItem();
        String qtyText = txtSoLuong.getText().trim();
        if (item == null || qtyText.isEmpty() || txtLyDo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!"); return;
        }
        
        int qty = Integer.parseInt(qtyText);
        if (qty <= 0 || qty > item.ct.getSoLuong()) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!"); return;
        }

        DoiTraDTO dt = new DoiTraDTO();
        dt.setMaDT("DT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")));
        dt.setMaHD(maHD);
        dt.setMaNV(maNV);
        dt.setMaSP(item.sp.getMaSP());
        dt.setNgayDoiTra(LocalDate.now());
        dt.setSoLuong(qty);
        dt.setTinhTrang(cboTinhTrang.getSelectedItem().toString());
        dt.setLyDo(txtLyDo.getText().trim());

        if (doiTraBUS.createReturn(dt)) {
            JOptionPane.showMessageDialog(this, "Tạo thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo!");
        }
    }

    class ProductComboItem {
        ChiTietHoaDonDTO ct; SanPhamDTO sp;
        public ProductComboItem(ChiTietHoaDonDTO ct, SanPhamDTO sp) { this.ct = ct; this.sp = sp; }
        @Override public String toString() { return sp.getTenSP() + " (SL mua: " + ct.getSoLuong() + ")"; }
    }
}