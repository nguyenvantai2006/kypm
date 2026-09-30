package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import com.qlgiay.bus.VoucherBUS;
import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.util.IconUtil;
import org.apache.commons.text.similarity.LevenshteinDistance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

public class VoucherPanel extends JPanel implements IRefreshable {
    private final VoucherBUS voucherBUS = new VoucherBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMa;
    private JTextField txtTen;
    private JTextField txtPhanTram;
    private JFormattedTextField txtTienGiam;
    private JFormattedTextField txtGiamToiDa;
    private JFormattedTextField txtDieuKien;
    private JTextField txtSoLuong;
    private DatePicker dpNgayBatDau;
    private DatePicker dpNgayKetThuc;
    private JTextField txtTrangThai;
    private boolean isAddingNew;
    private JButton btnLock;

    private int hoverRow = -1;
    private final LevenshteinDistance fuzzyDistance = LevenshteinDistance.getDefaultInstance();

    public VoucherPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createFormPanel());
        split.setOpaque(false);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);

        add(split, BorderLayout.CENTER);
        loadTable();
        clear();
    }

    private JPanel createLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF");
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        p.add(createTopBar(), BorderLayout.NORTH);
        p.add(createTablePanel(), BorderLayout.CENTER);

        return p;
    }

    private JPanel createTopBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã hoặc tên voucher...");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadTable();
            }
        });

        cboFilterTrangThai = new JComboBox<>(new String[] {
                "Tất cả trạng thái", "Hoạt động", "Tạm dừng", "Chưa hoạt động", "Hết hạn"
        });

        styleComboBox(cboFilterTrangThai);
        cboFilterTrangThai.setPreferredSize(new Dimension(150, 32));

        cboFilterTrangThai.addActionListener(e -> loadTable());

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(cboFilterTrangThai);

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã voucher", "Tên voucher", "% giảm", "Tiền giảm", "Giảm tối đa",
                "Điều kiện", "SL", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setAutoCreateRowSorter(true);
        styleTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRow();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formWrapper = new JPanel(new BorderLayout(0, 8));
        formWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF");
        formWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        formWrapper.setMinimumSize(new Dimension(400, 0));

        initFormComponents();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        int row = 0;

        addGridRow(contentPanel, gbc, row++, field("Mã voucher", txtMa), field("Tên voucher", txtTen));
        addGridRow(contentPanel, gbc, row++, field("% giảm", txtPhanTram), field("Tiền giảm", txtTienGiam));
        addGridRow(contentPanel, gbc, row++, field("Giảm tối đa", txtGiamToiDa),
                field("Đơn tối thiểu (VNĐ)", txtDieuKien));
        addGridRow(contentPanel, gbc, row++, field("Số lượng", txtSoLuong), field("Trạng thái", txtTrangThai));
        addGridRow(contentPanel, gbc, row++, field("Ngày bắt đầu", dpNgayBatDau),
                field("Ngày kết thúc", dpNgayKetThuc));

        JPanel alignTopPanel = new JPanel(new BorderLayout());
        alignTopPanel.setOpaque(false);
        alignTopPanel.add(contentPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(alignTopPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 4, 5, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnClear = new JButton("Làm mới");
        btnLock = new JButton("Tạm dừng");
        btnLock.setIcon(IconUtil.loadPng("/icons/lock.png", 24));
        btnLock.setEnabled(false);

        btnAdd.setIcon(IconUtil.loadPng("/icons/add.png", 24));
        btnUpdate.setIcon(IconUtil.loadPng("/icons/edit.png", 24));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 24));

        int gap = 4;
        btnAdd.setIconTextGap(gap);
        btnUpdate.setIconTextGap(gap);
        btnClear.setIconTextGap(gap);
        btnLock.setIconTextGap(gap);

        styleActionButton(btnAdd, "success");
        styleActionButton(btnUpdate, "default");
        styleActionButton(btnClear, "default");
        styleActionButton(btnLock, "danger");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnLock.addActionListener(e -> toggleLock());
        btnClear.addActionListener(e -> clear());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnLock);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMa = new JTextField();
        txtTen = new JTextField();
        txtPhanTram = new JTextField();
        txtTienGiam = new JFormattedTextField(createCurrencyFormatter());
        txtGiamToiDa = new JFormattedTextField(createCurrencyFormatter());
        txtDieuKien = new JFormattedTextField(createCurrencyFormatter());
        txtSoLuong = new JTextField();

        setNumberOnly(txtPhanTram);
        setNumberOnly(txtSoLuong);

        dpNgayBatDau = new DatePicker();
        dpNgayKetThuc = new DatePicker();
        dpNgayBatDau.addDateChangeListener(e -> evaluateVoucherState());
        dpNgayKetThuc.addDateChangeListener(e -> evaluateVoucherState());

        txtTrangThai = new JTextField("Hoạt động");
        txtTrangThai.setEditable(false);

        DocumentListener uxListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handle();
            }

            private void handle() {
                boolean hasPhanTram = !txtPhanTram.getText().trim().isEmpty();
                boolean hasTienGiam = txtTienGiam.getValue() != null;

                txtTienGiam.setEnabled(!hasPhanTram);
                txtPhanTram.setEnabled(!hasTienGiam);

                if (hasTienGiam) {
                    SwingUtilities.invokeLater(() -> txtGiamToiDa.setValue(null));
                    txtGiamToiDa.setEnabled(false);
                } else {
                    txtGiamToiDa.setEnabled(true);
                }
            }
        };

        txtPhanTram.getDocument().addDocumentListener(uxListener);
        txtTienGiam.getDocument().addDocumentListener(uxListener);
    }

    private void addGridRow(JPanel parent, GridBagConstraints gbc, int row, JComponent left, JComponent right) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 4, 8);
        parent.add(left, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        parent.add(right, gbc);
    }

    private JPanel field(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);

        p.setPreferredSize(new Dimension(0, 50));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return p;
    }

    private String normalizeSearchText(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return normalized.toLowerCase();
    }

    private boolean fuzzyMatches(String query, String value) {
        String normalizedQuery = normalizeSearchText(query);
        String normalizedValue = normalizeSearchText(value);

        if (normalizedQuery.isEmpty() || normalizedValue.isEmpty()) {
            return false;
        }
        if (normalizedValue.contains(normalizedQuery)) {
            return true;
        }

        int allowedDistance = Math.max(1, normalizedQuery.length() / 3);
        if (fuzzyDistance.apply(normalizedQuery, normalizedValue) <= allowedDistance) {
            return true;
        }

        for (String token : normalizedValue.split("\\s+")) {
            if (fuzzyDistance.apply(normalizedQuery, token) <= allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesVoucherSearch(String query, VoucherDTO voucher) {
        return fuzzyMatches(query, voucher.getMaVoucher())
                || fuzzyMatches(query, voucher.getTenVoucher());
    }

    private void loadTable() {
        String keyword = normalizeSearchText(txtSearch.getText());

        String tt = String.valueOf(cboFilterTrangThai.getSelectedItem());

        tableModel.setRowCount(0);
        List<VoucherDTO> list = voucherBUS.getAll();

        if (list != null) {
            for (VoucherDTO v : list) {
                if ("Hoạt động".equals(tt) && v.getTrangThai() != 1) {
                    continue;
                }
                if ("Tạm dừng".equals(tt) && v.getTrangThai() != 3) {
                    continue;
                }
                if ("Chưa hoạt động".equals(tt) && v.getTrangThai() != 2) {
                    continue;
                }
                if ("Hết hạn".equals(tt) && v.getTrangThai() != 0) {
                    continue;
                }
                if (!keyword.isEmpty() && !matchesVoucherSearch(keyword, v)) {
                    continue;
                }

                tableModel.addRow(new Object[] {
                        v.getMaVoucher(),
                        v.getTenVoucher(),
                        v.getPhanTramGiam() + "%",
                        v.getSoTienGiam() == null ? "" : formatMoney(v.getSoTienGiam()),
                        v.getGiamToiDa() == null ? "" : formatMoney(v.getGiamToiDa()),
                        v.getDieuKienApDung() == null ? "" : formatMoney(v.getDieuKienApDung()),
                        v.getSoLuong(),
                        statusText(v.getTrangThai())
                });
            }
        }
    }

    private String statusText(int status) {
        return switch (status) {
            case 0 -> "Hết hạn";
            case 1 -> "Hoạt động";
            case 2 -> "Chưa hoạt động";
            case 3 -> "Tạm dừng";
            default -> "Hết hạn";
        };
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));

        VoucherDTO v = voucherBUS.findById(ma);
        if (v == null) {
            return;
        }

        isAddingNew = false;
        txtMa.setText(v.getMaVoucher());
        txtTen.setText(v.getTenVoucher());
        txtPhanTram.setText(String.valueOf(v.getPhanTramGiam()));
        txtTienGiam.setValue(v.getSoTienGiam() == null ? null : v.getSoTienGiam().longValue());
        txtGiamToiDa.setValue(v.getGiamToiDa() == null ? null : v.getGiamToiDa().longValue());
        txtDieuKien.setValue(v.getDieuKienApDung() == null ? null : v.getDieuKienApDung().longValue());
        txtSoLuong.setText(String.valueOf(v.getSoLuong()));
        dpNgayBatDau.setDate(v.getNgayBatDau());
        dpNgayKetThuc.setDate(v.getNgayKetThuc());
        txtTrangThai.setText(statusText(v.getTrangThai()));
        evaluateVoucherState();

        txtTienGiam.setEnabled(v.getPhanTramGiam() == 0);
        txtPhanTram.setEnabled(v.getSoTienGiam() == null || v.getSoTienGiam().compareTo(BigDecimal.ZERO) == 0);

        txtMa.setEnabled(false);

        boolean hasTienGiam = v.getSoTienGiam() != null && v.getSoTienGiam().compareTo(BigDecimal.ZERO) > 0;
        txtTienGiam.setEnabled(v.getPhanTramGiam() == 0);
        txtPhanTram.setEnabled(!hasTienGiam);
        txtGiamToiDa.setEnabled(!hasTienGiam);
    }

    private VoucherDTO readForm() {
        try {
            VoucherDTO v = new VoucherDTO();

            v.setMaVoucher(txtMa.getText().trim());
            v.setTenVoucher(txtTen.getText().trim());

            String phanTram = txtPhanTram.getText().trim();
            v.setPhanTramGiam(phanTram.isEmpty() ? 0 : Integer.parseInt(phanTram));

            Object soTienGiam = txtTienGiam.getValue();
            if (soTienGiam instanceof Number) {
                v.setSoTienGiam(new BigDecimal(((Number) soTienGiam).longValue()));
            }

            Object giamToiDa = txtGiamToiDa.getValue();
            if (giamToiDa instanceof Number) {
                v.setGiamToiDa(new BigDecimal(((Number) giamToiDa).longValue()));
            }

            Object dieuKien = txtDieuKien.getValue();
            if (dieuKien instanceof Number) {
                v.setDieuKienApDung(new BigDecimal(((Number) dieuKien).longValue()));
            }

            String soLuong = txtSoLuong.getText().trim();
            v.setSoLuong(soLuong.isEmpty() ? 0 : Integer.parseInt(soLuong));

            String tt = txtTrangThai.getText();
            if ("Hoạt động".equals(tt)) {
                v.setTrangThai(1);
            } else if ("Chưa hoạt động".equals(tt)) {
                v.setTrangThai(2);
            } else if ("Tạm dừng".equals(tt)) {
                v.setTrangThai(3);
            } else {
                v.setTrangThai(0);
            }
            v.setNgayBatDau(dpNgayBatDau.getDate());
            v.setNgayKetThuc(dpNgayKetThuc.getDate());

            return v;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Dữ liệu không hợp lệ. Vui lòng kiểm tra các ô số/tiền.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private boolean validateForm(VoucherDTO v) {
        if (v.getMaVoucher() == null || v.getMaVoucher().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã voucher không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMa.requestFocus();
            return false;
        }

        if (v.getTenVoucher() == null || v.getTenVoucher().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tên voucher không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }

        if (v.getPhanTramGiam() < 0 || v.getPhanTramGiam() > 100) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "% giảm phải từ 0 đến 100!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtPhanTram.requestFocus();
            return false;
        }

        boolean coPhanTram = v.getPhanTramGiam() > 0;
        boolean coTienGiam = v.getSoTienGiam() != null && v.getSoTienGiam().compareTo(BigDecimal.ZERO) > 0;

        if (!coPhanTram && !coTienGiam) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Phải nhập % giảm hoặc tiền giảm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtPhanTram.requestFocus();
            return false;
        }

        if (coPhanTram && coTienGiam) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Chỉ được nhập % giảm hoặc tiền giảm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtPhanTram.requestFocus();
            return false;
        }

        if (v.getSoTienGiam() != null && v.getSoTienGiam().compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tiền giảm không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTienGiam.requestFocus();
            return false;
        }

        if (v.getGiamToiDa() != null && v.getGiamToiDa().compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Giảm tối đa không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtGiamToiDa.requestFocus();
            return false;
        }

        if (v.getDieuKienApDung() != null && v.getDieuKienApDung().compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Điều kiện áp dụng không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtDieuKien.requestFocus();
            return false;
        }

        if (v.getSoLuong() < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Số lượng không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtSoLuong.requestFocus();
            return false;
        }

        if (isAddingNew && v.getNgayBatDau() != null && v.getNgayBatDau().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Voucher thêm mới không thể có Ngày bắt đầu nằm trong quá khứ!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            dpNgayBatDau.requestFocus();
            return false;
        }

        if (v.getNgayBatDau() != null && v.getNgayKetThuc() != null
                && v.getNgayKetThuc().isBefore(v.getNgayBatDau())) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            dpNgayKetThuc.requestFocus();
            return false;
        }

        LocalDate now = LocalDate.now();
        if (v.getNgayKetThuc() != null && v.getNgayKetThuc().isBefore(now)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Ngày kết thúc không được trước ngày hiện tại!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            dpNgayKetThuc.requestFocus();
            return false;
        }

        return true;
    }

    private void add() {
        isAddingNew = true;
        evaluateVoucherState();
        VoucherDTO v = readForm();
        if (v == null) {
            return;
        }

        if (!validateForm(v)) {
            return;
        }

        if (voucherBUS.findById(v.getMaVoucher()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã voucher này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thực hiện thao tác này?",
                    "Xác nhận",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (voucherBUS.addVoucher(v)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm voucher thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm voucher thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn voucher cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        VoucherDTO v = readForm();
        if (v == null) {
            return;
        }

        if (!validateForm(v)) {
            return;
        }

        {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thực hiện thao tác này?",
                    "Xác nhận",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (voucherBUS.updateVoucher(v)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật voucher thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật voucher thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleLock() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn voucher cần thao tác!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 1));

        VoucherDTO dbVoucher = voucherBUS.findById(ma);
        if (dbVoucher == null)
            return;

        if (dbVoucher.getTrangThai() == 0) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Voucher này đang HẾT HẠN trong hệ thống!\nVui lòng nhấn 'Sửa' để lưu gia hạn trước khi dùng chức năng này.",
                    "Cảnh báo ràng buộc", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dbVoucher.getTrangThai() == 2) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Voucher này CHƯA HOẠT ĐỘNG trong hệ thống!\nVui lòng nhấn 'Sửa' để lưu thay đổi ngày trước khi dùng chức năng này.",
                    "Cảnh báo ràng buộc", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isLocking = txtTrangThai.getText().equals("Hoạt động");
        String actionName = isLocking ? "Tạm dừng" : "Kích hoạt lại";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn " + actionName.toLowerCase() + " voucher [" + ten + "] không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        VoucherDTO v = voucherBUS.findById(ma);
        if (v != null) {
            v.setTrangThai(isLocking ? 3 : 1);
            if (voucherBUS.updateVoucher(v)) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Thao tác thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Thao tác thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clear() {
        isAddingNew = true;
        txtMa.setEnabled(true);

        txtMa.setText("");
        txtTen.setText("");
        txtPhanTram.setText("");
        txtTienGiam.setValue(null);
        txtGiamToiDa.setValue(null);
        txtDieuKien.setValue(null);
        txtSoLuong.setText("");
        txtTrangThai.setText("Hoạt động");
        btnLock.setText("Tạm dừng");
        btnLock.setIcon(IconUtil.loadPng("/icons/lock.png", 24));
        btnLock.setEnabled(false);

        dpNgayBatDau.setDate(LocalDate.now());
        dpNgayKetThuc.setDate(LocalDate.now());
        evaluateVoucherState();

        txtPhanTram.setEnabled(true);
        txtTienGiam.setEnabled(true);
        txtGiamToiDa.setEnabled(true);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMa.requestFocus();
    }

    private void evaluateVoucherState() {
        if (dpNgayBatDau == null || dpNgayKetThuc == null || txtTrangThai == null) {
            return;
        }

        LocalDate start = dpNgayBatDau.getDate();
        LocalDate end = dpNgayKetThuc.getDate();
        LocalDate now = LocalDate.now();

        dpNgayKetThuc.setEnabled(true);
        btnLock.setEnabled(false);

        if (start != null) {
            dpNgayBatDau.setEnabled(isAddingNew || start.isAfter(now));

            if (start.isAfter(now)) {
                txtTrangThai.setText("Chưa hoạt động");
            } else if (end != null && end.isBefore(now)) {
                txtTrangThai.setText("Hết hạn");
            } else if (!isAddingNew) {
                btnLock.setEnabled(true);
                if (txtTrangThai.getText().equals("Tạm dừng")) {
                    btnLock.setText("Mở khóa");
                    btnLock.setIcon(IconUtil.loadPng("/icons/unlock.png", 24));
                } else {
                    txtTrangThai.setText("Hoạt động");
                    btnLock.setText("Tạm dừng");
                    btnLock.setIcon(IconUtil.loadPng("/icons/lock.png", 24));
                }
            } else {
                txtTrangThai.setText("Hoạt động");
            }
        }
    }

    private void setNumberOnly(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null || text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private NumberFormatter createCurrencyFormatter() {
        NumberFormat format = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Long.class);
        formatter.setMinimum(0L);
        formatter.setMaximum(Long.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }

    private void styleActionButton(JButton button, String type) {
        String style = switch (type) {
            case "success" ->
                "arc:10;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "margin:4,6,4,6;" +
                        "background:#E8F5E9;" +
                        "foreground:#2E7D32;" +
                        "hoverBackground:#D7F0DB;" +
                        "pressedBackground:#C2E7C8";
            case "danger" ->
                "arc:10;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "margin:4,6,4,6;" +
                        "background:#FDECEC;" +
                        "foreground:#C62828;" +
                        "hoverBackground:#F9D6D6;" +
                        "pressedBackground:#F4BDBD";
            default ->
                "arc:10;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "margin:4,6,4,6;" +
                        "background:#E8F0FE;" +
                        "foreground:#005A9E;" +
                        "hoverBackground:#DCE8FC;" +
                        "pressedBackground:#C9DCF8";
        };

        button.putClientProperty(FlatClientProperties.STYLE, style);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8; focusWidth:0; innerFocusWidth:0");
    }

    private void styleTable() {
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component c = super.getTableCellRendererComponent(
                        tbl,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column);

                if (isSelected) {
                    c.setBackground(new Color(232, 240, 254));
                    c.setForeground(new Color(0, 90, 158));
                } else if (row == hoverRow) {
                    c.setBackground(new Color(245, 247, 250));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (column == 6) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column >= 3 && column <= 5) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());

                if (row != hoverRow) {
                    hoverRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                table.repaint();
            }
        });
    }

    private String formatMoney(BigDecimal value) {
        if (value == null)
            return "";
        return new DecimalFormat("#,###").format(value) + "đ";
    }

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}