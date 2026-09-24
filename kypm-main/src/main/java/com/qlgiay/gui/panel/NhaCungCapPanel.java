package com.qlgiay.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.NhaCungCapBUS;
import com.qlgiay.bus.SanPhamBUS;
import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.gui.frame.MainFrame;
import com.qlgiay.util.IconUtil;

public class NhaCungCapPanel extends JPanel implements IRefreshable {
    private final NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMaNCC;
    private JTextField txtTenNCC;
    private JTextField txtSDT;
    private JTextArea txtDiaChi;
    private JComboBox<String> cboTrangThai;

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private String selectedSupplierId;

    private int hoverRow = -1;

    public NhaCungCapPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createRightPanel()
        );
        split.setOpaque(false);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);

        add(split, BorderLayout.CENTER);
        loadTable();
    }

    private JPanel createRightPanel() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createFormPanel(), createSupplierProductPanel());
        split.setResizeWeight(0.55);
        split.setDividerSize(6);
        split.setOpaque(false);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(split, BorderLayout.CENTER);
        return panel;
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập nội dung tìm kiếm...");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { loadTable(); }

            @Override
            public void removeUpdate(DocumentEvent e) { loadTable(); }

            @Override
            public void changedUpdate(DocumentEvent e) { loadTable(); }
        });

        cboFilterTrangThai = new JComboBox<>(new String[]{
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
                "Mã NCC", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setAutoCreateRowSorter(true);
        styleTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRow();
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)
        ));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSupplierProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF");
        panel.setBorder(new EmptyBorder(12, 15, 15, 15));

        JLabel title = new JLabel("Sản phẩm của nhà cung cấp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnAddProduct = new JButton("Thêm sản phẩm");
        btnAddProduct.setIcon(IconUtil.loadPng("/icons/add.png", 18));
        styleActionButton(btnAddProduct, "success");
        btnAddProduct.addActionListener(e -> addProductForSupplier());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(btnAddProduct, BorderLayout.EAST);

        productTableModel = new DefaultTableModel(
                new Object[]{"Mã SP", "Tên sản phẩm", "Loại", "Tồn kho", "Đơn giá"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(30);
        productTable.setAutoCreateRowSorter(true);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100);
      

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadSupplierProducts(String maNCC) {
        selectedSupplierId = maNCC;
        productTableModel.setRowCount(0);
        for (SanPhamDTO sp : sanPhamBUS.getBySupplier(maNCC)) {
            productTableModel.addRow(new Object[]{
                    sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), sp.getSoLuong(),
                    sp.getDonGia() == null ? "" : sp.getDonGia().toPlainString(),
                    sp.getHinhAnh() == null || sp.getHinhAnh().isBlank() ? "Chưa có" : "Có"
            });
        }
    }

    private void addProductForSupplier() {
        if (selectedSupplierId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng click chọn nhà cung cấp trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddProductDialog dialog = new AddProductDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), selectedSupplierId);
        dialog.setVisible(true);
        if (dialog.saved) {
            loadSupplierProducts(selectedSupplierId);
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame mainFrame) {
                mainFrame.refreshView("SANPHAM");
            }
            JOptionPane.showMessageDialog(this,
                    "Đã thêm sản phẩm. Sản phẩm sẽ được liên kết với nhà cung cấp khi tạo phiếu nhập.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
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

        addGridRow(contentPanel, gbc, row++, field("Mã nhà cung cấp", txtMaNCC), field("Tên nhà cung cấp", txtTenNCC));
        addGridRow(contentPanel, gbc, row++, field("Số điện thoại", txtSDT), field("Trạng thái", cboTrangThai));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(buildDiaChiPanel(), gbc);

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
        txtMaNCC = new JTextField();
        txtTenNCC = new JTextField();
        txtSDT = new JTextField();

        txtDiaChi = new JTextArea(3, 20);
        txtDiaChi.setLineWrap(true);
        txtDiaChi.setWrapStyleWord(true);

        cboTrangThai = new JComboBox<>(new String[]{
                "Hoạt động", "Ngừng hoạt động"
        });

        styleComboBox(cboTrangThai);
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

    private JPanel buildDiaChiPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel("Địa chỉ"), BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(txtDiaChi);
        sp.setPreferredSize(new Dimension(0, 55));
        sp.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        p.add(sp, BorderLayout.CENTER);
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
        List<NhaCungCapDTO> list = nhaCungCapBUS.search(keyword);

        if (list != null) {
            for (NhaCungCapDTO ncc : list) {
                if ("Hoạt động".equals(tt) && ncc.getTrangThai() != 1) {
                    continue;
                }
                if ("Ngừng hoạt động".equals(tt) && ncc.getTrangThai() != 0) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        ncc.getMaNCC(),
                        ncc.getTenNCC(),
                        ncc.getSdt(),
                        ncc.getDiaChi(),
                        ncc.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động"
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

        NhaCungCapDTO ncc = nhaCungCapBUS.findById(ma);
        if (ncc == null) {
            return;
        }

        txtMaNCC.setText(ncc.getMaNCC());
        txtTenNCC.setText(ncc.getTenNCC());
        txtSDT.setText(ncc.getSdt());
        txtDiaChi.setText(ncc.getDiaChi());
        cboTrangThai.setSelectedIndex(ncc.getTrangThai() == 1 ? 0 : 1);
        loadSupplierProducts(ncc.getMaNCC());

        txtMaNCC.setEnabled(false);
    }

    private NhaCungCapDTO readForm() {
        String ma = txtMaNCC.getText().trim();
        String ten = txtTenNCC.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng nhập đầy đủ Mã và Tên nhà cung cấp!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNCC(ma);
        ncc.setTenNCC(ten);
        ncc.setSdt(sdt);
        ncc.setDiaChi(txtDiaChi.getText().trim());
        ncc.setTrangThai(cboTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        return ncc;
    }

    private void add() {
        NhaCungCapDTO ncc = readForm();
        if (ncc == null) {
            return;
        }

        if (nhaCungCapBUS.findById(ncc.getMaNCC()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã nhà cung cấp này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (nhaCungCapBUS.addSupplier(ncc)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm nhà cung cấp thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm nhà cung cấp thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn nhà cung cấp cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        NhaCungCapDTO ncc = readForm();
        if (ncc == null) {
            return;
        }

        if (nhaCungCapBUS.updateSupplier(ncc)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật nhà cung cấp thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật nhà cung cấp thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void delete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn nhà cung cấp cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn xóa nhà cung cấp [" + ten + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhaCungCapBUS.deleteSupplier(ma)) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Đã xóa nhà cung cấp thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Xóa nhà cung cấp thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void clear() {
        txtMaNCC.setEnabled(true);
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cboTrangThai.setSelectedIndex(0);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();
        selectedSupplierId = null;
        if (productTableModel != null) {
            productTableModel.setRowCount(0);
        }

        txtMaNCC.requestFocus();
    }

    private class AddProductDialog extends JDialog {
        private final JTextField txtMa = new JTextField();
        private final JTextField txtTen = new JTextField();
        private final JTextField txtLoai = new JTextField("Giày Sneaker");
        private final JTextField txtDonVi = new JTextField("Đôi");
        private final JTextField txtDonGia = new JTextField();
        private final JTextField txtMau = new JTextField();
        private final JTextField txtSize = new JTextField();
        private final JTextField txtChatLieu = new JTextField();
        private final JTextField txtThuongHieu = new JTextField();
        private final JTextField txtNuocSX = new JTextField();
        private final JTextField txtAnh = new JTextField();
        private final JTextArea txtMoTa = new JTextArea(3, 20);
        private boolean saved;

        AddProductDialog(Frame parent, String maNCC) {
            super(parent, "Thêm sản phẩm cho nhà cung cấp " + maNCC, true);
            setSize(650, 620);
            setLocationRelativeTo(parent);

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.setBorder(new EmptyBorder(15, 15, 8, 15));
            addField(form, "Mã sản phẩm", txtMa);
            addField(form, "Tên sản phẩm", txtTen);
            addField(form, "Loại sản phẩm", txtLoai);
            addField(form, "Đơn vị", txtDonVi);
            txtDonGia.setEditable(false);
            txtDonGia.setText("Tự động sau khi nhập hàng");
            addField(form, "Giá bán", txtDonGia);
            addField(form, "Màu sắc", txtMau);
            addField(form, "Size", txtSize);
            addField(form, "Chất liệu", txtChatLieu);
            addField(form, "Thương hiệu", txtThuongHieu);
            addField(form, "Nước sản xuất", txtNuocSX);

            JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
            imagePanel.add(txtAnh, BorderLayout.CENTER);
            JButton chooseImage = new JButton("Chọn ảnh");
            chooseImage.addActionListener(e -> chooseImage());
            imagePanel.add(chooseImage, BorderLayout.EAST);
            addField(form, "Ảnh sản phẩm", imagePanel);

            JPanel center = new JPanel(new BorderLayout());
            center.add(form, BorderLayout.NORTH);
            JPanel description = new JPanel(new BorderLayout(0, 4));
            description.setBorder(new EmptyBorder(0, 15, 8, 15));
            description.add(new JLabel("Mô tả chi tiết"), BorderLayout.NORTH);
            description.add(new JScrollPane(txtMoTa), BorderLayout.CENTER);
            center.add(description, BorderLayout.CENTER);

            JButton save = new JButton("Lưu sản phẩm");
            JButton cancel = new JButton("Hủy");
            save.addActionListener(e -> saveProduct());
            cancel.addActionListener(e -> dispose());
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.add(save);
            buttons.add(cancel);

            add(center, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
        }

        private void addField(JPanel panel, String label, JComponent component) {
            JPanel field = new JPanel(new BorderLayout(0, 3));
            field.add(new JLabel(label), BorderLayout.NORTH);
            field.add(component, BorderLayout.CENTER);
            panel.add(field);
        }

        private void chooseImage() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Ảnh (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtAnh.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void saveProduct() {
            try {
                SanPhamDTO sp = new SanPhamDTO();
                sp.setMaSP(txtMa.getText().trim());
                sp.setTenSP(txtTen.getText().trim());
                sp.setLoaiSP(txtLoai.getText().trim());
                sp.setDonViTinh(txtDonVi.getText().trim());
                sp.setSoLuong(0);
                sp.setPhanTramLoiNhuan(SanPhamDTO.DEFAULT_PROFIT_PERCENT);
                sp.setGiaNhap(null);
                sp.setDonGia(null);
                sp.setMauSac(txtMau.getText().trim());
                sp.setSize(txtSize.getText().trim());
                sp.setChatLieu(txtChatLieu.getText().trim());
                sp.setThuongHieu(txtThuongHieu.getText().trim());
                sp.setNuocSanXuat(txtNuocSX.getText().trim());
                sp.setNgaySanXuat(LocalDate.now());
                sp.setMoTa(txtMoTa.getText().trim());
                sp.setMaNCC(selectedSupplierId);
                sp.setHinhAnh(copyImage(txtAnh.getText().trim()));
                sp.setTrangThai(1);

                if (sp.getMaSP().isEmpty() || sp.getTenSP().isEmpty()) {
                    throw new IllegalArgumentException();
                }
                if (!sanPhamBUS.addProduct(sp)) {
                    JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại hoặc dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                saved = true;
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra mã, số lượng và đơn giá!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }

        private String copyImage(String source) throws IOException {
            if (source.isBlank()) return "";
            Path sourcePath = Path.of(source);
            Path targetDir = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "images", "products");
            Files.createDirectories(targetDir);
            String fileName = System.currentTimeMillis() + "_" + sourcePath.getFileName().toString();
            Files.copy(sourcePath, targetDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            Path runtimeDir = Path.of(System.getProperty("user.dir"), "target", "classes", "images", "products");
            if (Files.isDirectory(runtimeDir)) {
                Files.copy(sourcePath, runtimeDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        }
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
                "arc:8;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0"
        );
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
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        tbl,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

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

                if (column == 0 || column == 2) {
                    setHorizontalAlignment(SwingConstants.CENTER);
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

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}