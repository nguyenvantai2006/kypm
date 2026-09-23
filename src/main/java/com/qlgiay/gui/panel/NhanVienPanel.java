package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.NhanVienBUS;
import com.qlgiay.bus.QuyenBUS;
import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.util.IconUtil;

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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class NhanVienPanel extends JPanel implements IRefreshable {
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final QuyenBUS quyenBUS = new QuyenBUS();

    private final java.util.Map<String, String> quyenMap = new java.util.HashMap<>();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMa;
    private JTextField txtHo;
    private JTextField txtTen;
    private JComboBox<QuyenItem> cboQuyen;
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JTextField txtLuong;
    private JComboBox<String> cboTrangThai;

    private int hoverRow = -1;

    public NhanVienPanel() {
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã, họ tên, tài khoản hoặc quyền...");

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
                "Tất cả trạng thái", "Hoạt động", "Ngừng hoạt động"
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
                "Mã NV", "Họ", "Tên", "Tài khoản", "Quyền", "Lương", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);

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

        addGridRow(contentPanel, gbc, row++, field("Mã nhân viên", txtMa), field("Quyền", cboQuyen));
        addGridRow(contentPanel, gbc, row++, field("Họ", txtHo), field("Tên", txtTen));
        addGridRow(contentPanel, gbc, row++, field("Tài khoản", txtTaiKhoan), field("Mật khẩu", txtMatKhau));
        addGridRow(contentPanel, gbc, row++, field("Lương", txtLuong), field("Trạng thái", cboTrangThai));

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
        JButton btnLock = new JButton("Khóa");
        JButton btnClear = new JButton("Làm mới");

        btnAdd.setIcon(IconUtil.loadPng("/icons/add.png", 24));
        btnUpdate.setIcon(IconUtil.loadPng("/icons/edit.png", 24));
        btnLock.setIcon(IconUtil.loadPng("/icons/lock.png", 24));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 24));

        int gap = 4;
        btnAdd.setIconTextGap(gap);
        btnUpdate.setIconTextGap(gap);
        btnLock.setIconTextGap(gap);
        btnClear.setIconTextGap(gap);

        styleActionButton(btnAdd, "success");
        styleActionButton(btnUpdate, "default");
        styleActionButton(btnLock, "danger");
        styleActionButton(btnClear, "default");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnLock.addActionListener(e -> lock());
        btnClear.addActionListener(e -> clear());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnLock);
        pnlButtons.add(btnClear);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMa = new JTextField();
        txtHo = new JTextField();
        txtTen = new JTextField();
        txtTaiKhoan = new JTextField();
        txtMatKhau = new JPasswordField();
        txtLuong = new JTextField();

        setNumberOnly(txtLuong);

        cboQuyen = new JComboBox<>();
        styleComboBox(cboQuyen);
        loadQuyenCombo();

        cboTrangThai = new JComboBox<>(new String[] {
                "Hoạt động", "Ngừng hoạt động"
        });
        styleComboBox(cboTrangThai);
    }

    private void loadQuyenCombo() {
        DefaultComboBoxModel<QuyenItem> model = new DefaultComboBoxModel<>();
        quyenMap.clear();

        List<QuyenDTO> list = quyenBUS.getAll();

        if (list != null) {
            for (QuyenDTO q : list) {
                model.addElement(new QuyenItem(q.getMaQuyen(), q.getTenQuyen()));
                quyenMap.put(q.getMaQuyen(), q.getTenQuyen());
            }
        }

        cboQuyen.setModel(model);
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

    private void loadTable() {
        String keyword = txtSearch.getText();
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        String tt = String.valueOf(cboFilterTrangThai.getSelectedItem());

        tableModel.setRowCount(0);
        List<NhanVienDTO> list = nhanVienBUS.search(keyword);

        if (list != null) {
            for (NhanVienDTO nv : list) {
                if ("Hoạt động".equals(tt) && nv.getTrangThai() != 1) {
                    continue;
                }
                if ("Ngừng hoạt động".equals(tt) && nv.getTrangThai() != 0) {
                    continue;
                }

                tableModel.addRow(new Object[] {
                        nv.getMaNV(),
                        nv.getHo(),
                        nv.getTen(),
                        nv.getTaiKhoan(),
                        getTenQuyen(nv.getMaQuyen()),
                        nv.getLuong() == null ? "" : formatMoney(nv.getLuong()),
                        nv.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động"
                });
            }
        }
    }

    private String getTenQuyen(String maQuyen) {
        if (maQuyen == null || maQuyen.isBlank()) {
            return "";
        }
        return quyenMap.getOrDefault(maQuyen, maQuyen);
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));

        NhanVienDTO nv = nhanVienBUS.findById(ma);
        if (nv == null) {
            return;
        }

        txtMa.setText(nv.getMaNV());
        txtHo.setText(nv.getHo());
        txtTen.setText(nv.getTen());
        txtTaiKhoan.setText(nv.getTaiKhoan());
        txtMatKhau.setText("");
        txtLuong.setText(nv.getLuong() == null ? "" : String.valueOf(nv.getLuong().longValue()));
        cboTrangThai.setSelectedIndex(nv.getTrangThai() == 1 ? 0 : 1);
        selectQuyen(nv.getMaQuyen());

        txtMa.setEnabled(false);
        txtTaiKhoan.setEnabled(false);
    }

    private void selectQuyen(String maQuyen) {
        ComboBoxModel<QuyenItem> model = cboQuyen.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            QuyenItem item = model.getElementAt(i);
            if (item != null && item.maQuyen.equals(maQuyen)) {
                cboQuyen.setSelectedIndex(i);
                return;
            }
        }
    }

    private NhanVienDTO readForm() {
        try {
            NhanVienDTO nv = new NhanVienDTO();

            nv.setMaNV(txtMa.getText().trim());
            nv.setHo(txtHo.getText().trim());
            nv.setTen(txtTen.getText().trim());

            QuyenItem quyenItem = (QuyenItem) cboQuyen.getSelectedItem();
            nv.setMaQuyen(quyenItem == null ? "" : quyenItem.maQuyen);

            nv.setTaiKhoan(txtTaiKhoan.getText().trim());

            String matKhau = new String(txtMatKhau.getPassword()).trim();
            nv.setMatKhau(matKhau);

            String luong = txtLuong.getText().trim();
            if (!luong.isEmpty()) {
                nv.setLuong(new BigDecimal(luong));
            } else {
                nv.setLuong(null);
            }

            nv.setTrangThai(cboTrangThai.getSelectedIndex() == 0 ? 1 : 0);

            return nv;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Dữ liệu không hợp lệ. Vui lòng kiểm tra ô lương.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private boolean validateForm(NhanVienDTO nv) {
        if (nv.getMaNV() == null || nv.getMaNV().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã nhân viên không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMa.requestFocus();
            return false;
        }

        if (nv.getHo() == null || nv.getHo().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Họ không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtHo.requestFocus();
            return false;
        }

        if (nv.getTen() == null || nv.getTen().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tên không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }

        if (nv.getTaiKhoan() == null || nv.getTaiKhoan().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tài khoản không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTaiKhoan.requestFocus();
            return false;
        }

        if (nv.getTaiKhoan().contains(" ")) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tài khoản không được chứa khoảng trắng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTaiKhoan.requestFocus();
            return false;
        }

        QuyenItem quyenItem = (QuyenItem) cboQuyen.getSelectedItem();
        if (quyenItem == null || quyenItem.maQuyen.isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn quyền!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            cboQuyen.requestFocus();
            return false;
        }

        if (txtMa.isEnabled()) {
            if (nv.getMatKhau() == null || nv.getMatKhau().isEmpty()) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Mật khẩu không được để trống!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }

            if (nv.getMatKhau().length() < 6) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Mật khẩu phải có ít nhất 6 ký tự!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }
        } else {
            if (nv.getMatKhau() != null && !nv.getMatKhau().isEmpty() && nv.getMatKhau().length() < 6) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Nếu đổi mật khẩu thì mật khẩu phải có ít nhất 6 ký tự!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }
        }

        if (nv.getLuong() != null && nv.getLuong().compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Lương không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtLuong.requestFocus();
            return false;
        }

        return true;
    }

    private void add() {
        NhanVienDTO nv = readForm();
        if (nv == null) {
            return;
        }

        if (!validateForm(nv)) {
            return;
        }

        if (nhanVienBUS.findById(nv.getMaNV()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã nhân viên này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhanVienBUS.findByTaiKhoan(nv.getTaiKhoan()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tài khoản này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nhanVienBUS.addNhanVien(nv)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm nhân viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm nhân viên thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn nhân viên cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhanVienDTO nv = readForm();
        if (nv == null) {
            return;
        }

        if (!validateForm(nv)) {
            return;
        }

        if (nhanVienBUS.updateNhanVien(nv)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật nhân viên thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật nhân viên thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lock() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn nhân viên cần khóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ho = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 2));

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn khóa nhân viên [" + ho + " " + ten + "] không?",
                "Xác nhận khóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienBUS.lockNhanVien(ma)) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Đã khóa nhân viên thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Khóa nhân viên thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clear() {
        txtMa.setEnabled(true);
        txtTaiKhoan.setEnabled(true);

        txtMa.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtTaiKhoan.setText("");
        txtMatKhau.setText("");
        txtLuong.setText("");

        if (cboQuyen.getItemCount() > 0) {
            cboQuyen.setSelectedIndex(0);
        }

        cboTrangThai.setSelectedIndex(0);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMa.requestFocus();
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

                if (column == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 5) {
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

    private static class QuyenItem {
        private final String maQuyen;
        private final String tenQuyen;

        public QuyenItem(String maQuyen, String tenQuyen) {
            this.maQuyen = maQuyen;
            this.tenQuyen = tenQuyen;
        }

        @Override
        public String toString() {
            return tenQuyen;
        }
    }

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}