package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.QuyenBUS;
import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.util.IconUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PhanQuyenPanel extends JPanel implements IRefreshable {
    private final QuyenBUS quyenBUS = new QuyenBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;

    private JTextField txtMa;
    private JTextField txtTen;
    private JCheckBox chkBanHang;
    private JCheckBox chkKhachHang;
    private JCheckBox chkSanPham;
    private JCheckBox chkNhapHang;
    private JCheckBox chkNhanVien;
    private JCheckBox chkThongKe;

    private int hoverRow = -1;

    public PhanQuyenPanel() {
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã hoặc tên quyền...");

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

        p.add(txtSearch, BorderLayout.CENTER);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã quyền", "Tên quyền", "Bán hàng", "Khách hàng",
                "Sản phẩm", "Nhập hàng", "Nhân viên", "Thống kê"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 2) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        table.getColumnModel().getColumn(5).setPreferredWidth(85);
        table.getColumnModel().getColumn(6).setPreferredWidth(85);
        table.getColumnModel().getColumn(7).setPreferredWidth(85);

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

        addGridRow(contentPanel, gbc, row++, field("Mã quyền", txtMa), field("Tên quyền", txtTen));
        addGridRow(contentPanel, gbc, row++, buildPermissionPanel("Quản lý bán hàng", chkBanHang),
                buildPermissionPanel("Quản lý khách hàng", chkKhachHang));
        addGridRow(contentPanel, gbc, row++, buildPermissionPanel("Quản lý sản phẩm", chkSanPham),
                buildPermissionPanel("Quản lý nhập hàng", chkNhapHang));
        addGridRow(contentPanel, gbc, row++, buildPermissionPanel("Quản lý nhân viên", chkNhanVien),
                buildPermissionPanel("Quản lý thống kê", chkThongKe));

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
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");

        btnAdd.setIcon(IconUtil.loadPng("/icons/add.png", 24));
        btnUpdate.setIcon(IconUtil.loadPng("/icons/edit.png", 24));
        btnDelete.setIcon(IconUtil.loadPng("/icons/delete.png", 24));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 24));

        int gap = 4;
        btnAdd.setIconTextGap(gap);
        btnUpdate.setIconTextGap(gap);
        btnDelete.setIconTextGap(gap);
        btnClear.setIconTextGap(gap);

        styleActionButton(btnAdd, "success");
        styleActionButton(btnUpdate, "default");
        styleActionButton(btnDelete, "danger");
        styleActionButton(btnClear, "default");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMa = new JTextField();
        txtTen = new JTextField();

        chkBanHang = createPermissionCheckBox();
        chkKhachHang = createPermissionCheckBox();
        chkSanPham = createPermissionCheckBox();
        chkNhapHang = createPermissionCheckBox();
        chkNhanVien = createPermissionCheckBox();
        chkThongKe = createPermissionCheckBox();
    }

    private JCheckBox createPermissionCheckBox() {
        JCheckBox chk = new JCheckBox("Cho phép");
        chk.setOpaque(false);
        chk.setFocusPainted(false);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return chk;
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

    private JPanel buildPermissionPanel(String label, JCheckBox checkBox) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(0, 32));
        wrap.add(checkBox);

        p.add(wrap, BorderLayout.CENTER);
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

        tableModel.setRowCount(0);
        List<QuyenDTO> list = quyenBUS.search(keyword);

        if (list != null) {
            for (QuyenDTO q : list) {
                tableModel.addRow(new Object[] {
                        q.getMaQuyen(),
                        q.getTenQuyen(),
                        q.getQlBanHang() == 1,
                        q.getQlKhachHang() == 1,
                        q.getQlSanPham() == 1,
                        q.getQlNhapHang() == 1,
                        q.getQlNhanVien() == 1,
                        q.getQlThongKe() == 1
                });
            }
        }
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));

        QuyenDTO q = quyenBUS.findById(ma);
        if (q == null) {
            return;
        }

        txtMa.setText(q.getMaQuyen());
        txtTen.setText(q.getTenQuyen());

        chkBanHang.setSelected(q.getQlBanHang() == 1);
        chkKhachHang.setSelected(q.getQlKhachHang() == 1);
        chkSanPham.setSelected(q.getQlSanPham() == 1);
        chkNhapHang.setSelected(q.getQlNhapHang() == 1);
        chkNhanVien.setSelected(q.getQlNhanVien() == 1);
        chkThongKe.setSelected(q.getQlThongKe() == 1);

        txtMa.setEnabled(false);
    }

    private QuyenDTO readForm() {
        QuyenDTO q = new QuyenDTO();

        q.setMaQuyen(txtMa.getText().trim());
        q.setTenQuyen(txtTen.getText().trim());
        q.setQlBanHang(chkBanHang.isSelected() ? 1 : 0);
        q.setQlKhachHang(chkKhachHang.isSelected() ? 1 : 0);
        q.setQlSanPham(chkSanPham.isSelected() ? 1 : 0);
        q.setQlNhapHang(chkNhapHang.isSelected() ? 1 : 0);
        q.setQlNhanVien(chkNhanVien.isSelected() ? 1 : 0);
        q.setQlThongKe(chkThongKe.isSelected() ? 1 : 0);

        return q;
    }

    private boolean validateForm(QuyenDTO q) {
        if (q.getMaQuyen() == null || q.getMaQuyen().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã quyền không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMa.requestFocus();
            return false;
        }

        if (q.getTenQuyen() == null || q.getTenQuyen().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tên quyền không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }

        List<QuyenDTO> list = quyenBUS.getAll();
        if (list != null) {
            for (QuyenDTO item : list) {
                if (item == null) {
                    continue;
                }

                String ma = item.getMaQuyen() == null ? "" : item.getMaQuyen().trim();
                String ten = item.getTenQuyen() == null ? "" : item.getTenQuyen().trim();

                if (!ma.equalsIgnoreCase(q.getMaQuyen()) && ten.equalsIgnoreCase(q.getTenQuyen().trim())) {
                    JOptionPane.showMessageDialog(
                            SwingUtilities.getWindowAncestor(this),
                            "Tên quyền này đã tồn tại!",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    txtTen.requestFocus();
                    return false;
                }
            }
        }

        return true;
    }

    private void add() {
        QuyenDTO q = readForm();
        if (!validateForm(q)) {
            return;
        }

        if (quyenBUS.findById(q.getMaQuyen()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã quyền này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quyenBUS.addQuyen(q)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm quyền thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm quyền thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn quyền cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuyenDTO q = readForm();
        if (!validateForm(q)) {
            return;
        }

        if (quyenBUS.updateQuyen(q)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật quyền thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật quyền thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn quyền cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 1));

        if (ma.equalsIgnoreCase("ADMIN") || ma.equalsIgnoreCase("Q01") || ten.equalsIgnoreCase("Quản trị viên")) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tuyệt đối không được xóa quyền Quản Trị Hệ Thống!",
                    "Cảnh báo an ninh",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn xóa quyền [" + ten + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (quyenBUS.deleteQuyen(ma)) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Đã xóa quyền thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Xóa thất bại! Quyền này có thể đang được cấp cho một số nhân viên.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clear() {
        txtMa.setEnabled(true);

        txtMa.setText("");
        txtTen.setText("");

        chkBanHang.setSelected(false);
        chkKhachHang.setSelected(false);
        chkSanPham.setSelected(false);
        chkNhapHang.setSelected(false);
        chkNhanVien.setSelected(false);
        chkThongKe.setSelected(false);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMa.requestFocus();
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

        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
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
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        };

        DefaultTableCellRenderer booleanRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                JCheckBox cb = new JCheckBox();
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setSelected(Boolean.TRUE.equals(value));
                cb.setOpaque(true);
                cb.setEnabled(true);

                if (isSelected) {
                    cb.setBackground(new Color(232, 240, 254));
                } else if (row == hoverRow) {
                    cb.setBackground(new Color(245, 247, 250));
                } else {
                    cb.setBackground(Color.WHITE);
                }

                return cb;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(textRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(textRenderer);

        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(booleanRenderer);
        }

        DefaultTableCellRenderer headerCenter = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerCenter.setHorizontalAlignment(SwingConstants.CENTER);

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

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}