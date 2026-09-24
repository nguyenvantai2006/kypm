package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.KhachHangBUS;
import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.util.IconUtil;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KhachHangPanel extends JPanel implements IRefreshable {
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JTextField txtDiem;
    private JTextArea txtDiaChi;
    private JComboBox<String> cboTrangThai;

    private int hoverRow = -1;
    private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile("^KH(\\d+)$", Pattern.CASE_INSENSITIVE);
    private final LevenshteinDistance fuzzyDistance = LevenshteinDistance.getDefaultInstance();

    public KhachHangPanel() {
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã KH, tên khách hàng hoặc số điện thoại...");

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

        JButton btnExport = new JButton("Xuất Excel");
        styleActionButton(btnExport, "success");
        btnExport.setPreferredSize(new Dimension(100, 32));
        btnExport.addActionListener(e -> exportExcel());

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(cboFilterTrangThai);
        pnlFilters.add(new JLabel(" | "));
        pnlFilters.add(btnExport);

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã KH", "Tên khách hàng", "Số điện thoại", "Địa chỉ", "Điểm", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(230);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

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

        addGridRow(contentPanel, gbc, row++, field("Mã khách hàng", txtMaKH), field("Tên khách hàng", txtTenKH));
        addGridRow(contentPanel, gbc, row++, field("Số điện thoại", txtSDT), field("Điểm tích lũy", txtDiem));
        addGridRow(contentPanel, gbc, row++, field("Trạng thái", cboTrangThai), new JLabel());

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
        txtMaKH = new JTextField();
        txtMaKH.setEnabled(false);
        txtMaKH.setToolTipText("Mã khách hàng được tự động tạo khi thêm mới");
        txtTenKH = new JTextField();
        txtSDT = new JTextField();

        txtDiem = new JTextField();
        txtDiem.setEnabled(false);
        txtDiem.setText("0");

        txtDiaChi = new JTextArea(3, 20);
        txtDiaChi.setLineWrap(true);
        txtDiaChi.setWrapStyleWord(true);

        cboTrangThai = new JComboBox<>(new String[] {
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

    private boolean matchesCustomerSearch(String query, KhachHangDTO customer) {
        return fuzzyMatches(query, customer.getMaKH())
                || fuzzyMatches(query, customer.getTenKH())
                || fuzzyMatches(query, customer.getSdt());
    }

    private void loadTable() {
        String keyword = normalizeSearchText(txtSearch.getText());

        String tt = String.valueOf(cboFilterTrangThai.getSelectedItem());

        tableModel.setRowCount(0);
        List<KhachHangDTO> list = khachHangBUS.getAll();

        if (list != null) {
            for (KhachHangDTO kh : list) {
                if ("Hoạt động".equals(tt) && kh.getTrangThai() != 1) {
                    continue;
                }
                if ("Ngừng hoạt động".equals(tt) && kh.getTrangThai() != 0) {
                    continue;
                }
                if (!keyword.isEmpty() && !matchesCustomerSearch(keyword, kh)) {
                    continue;
                }

                tableModel.addRow(new Object[] {
                        kh.getMaKH(),
                        kh.getTenKH(),
                        kh.getSdt(),
                        kh.getDiaChi(),
                        kh.getDiemTichLuy(),
                        kh.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động"
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

        KhachHangDTO kh = khachHangBUS.findById(ma);
        if (kh == null) {
            return;
        }

        txtMaKH.setText(kh.getMaKH());
        txtTenKH.setText(kh.getTenKH());
        txtSDT.setText(kh.getSdt());
        txtDiaChi.setText(kh.getDiaChi());
        txtDiem.setText(String.valueOf(kh.getDiemTichLuy()));
        cboTrangThai.setSelectedIndex(kh.getTrangThai() == 1 ? 0 : 1);

        txtMaKH.setEnabled(false);
    }

    private KhachHangDTO readForm() {
        String ten = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng nhập tên khách hàng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (sdt.isEmpty() || !sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng nhập số điện thoại gồm 10 chữ số và bắt đầu bằng số 0!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        KhachHangDTO kh = new KhachHangDTO();
        kh.setMaKH(txtMaKH.getText().trim());
        kh.setTenKH(ten);
        kh.setSdt(sdt);
        kh.setDiaChi(txtDiaChi.getText().trim());

        int diem = 0;
        try {
            if (!txtDiem.getText().trim().isEmpty()) {
                diem = Integer.parseInt(txtDiem.getText().trim());
            }
        } catch (NumberFormatException e) {
            diem = 0;
        }
        kh.setDiemTichLuy(diem);

        kh.setTrangThai(cboTrangThai.getSelectedIndex() == 0 ? 1 : 0);

        return kh;
    }

    private void add() {
        String generatedId = generateCustomerId();
        txtMaKH.setText(generatedId);

        KhachHangDTO kh = readForm();
        if (kh == null) {
            return;
        }

        if (khachHangBUS.findByPhone(kh.getSdt()) != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Số điện thoại này đã tồn tại trong hệ thống!",
                    "Trùng số điện thoại",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (khachHangBUS.addCustomer(kh)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Thêm khách hàng thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Thêm khách hàng thất bại! Số điện thoại có thể đã tồn tại hoặc dữ liệu không hợp lệ.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn khách hàng cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHangDTO kh = readForm();
        if (kh == null) {
            return;
        }

        if (khachHangBUS.updateCustomer(kh)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cập nhật khách hàng thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Cập nhật khách hàng thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn khách hàng cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa khách hàng [" + ten + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangBUS.deleteCustomer(ma)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Đã xóa khách hàng thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Xóa khách hàng thất bại! Khách hàng có thể đang có hóa đơn.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("DanhSachKhachHang.xlsx"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xlsx");
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Khách Hàng");

            String[] columns = {
                    "Mã KH", "Tên Khách Hàng", "Số Điện Thoại", "Địa Chỉ", "Điểm", "Trạng Thái"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(String.valueOf(tableModel.getValueAt(i, 0)));
                row.createCell(1).setCellValue(String.valueOf(tableModel.getValueAt(i, 1)));
                row.createCell(2).setCellValue(String.valueOf(tableModel.getValueAt(i, 2)));
                row.createCell(3).setCellValue(String.valueOf(tableModel.getValueAt(i, 3)));

                String diemStr = String.valueOf(tableModel.getValueAt(i, 4)).replaceAll("[^\\d-]", "");
                int diem = diemStr.isEmpty() ? 0 : Integer.parseInt(diemStr);
                row.createCell(4).setCellValue(diem);

                row.createCell(5).setCellValue(String.valueOf(tableModel.getValueAt(i, 5)));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fileOut = new FileOutputStream(fileToSave);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            int open = JOptionPane.showConfirmDialog(
                    null,
                    "Xuất file Excel thành công! Bạn có muốn mở file ngay không?",
                    "Thành công",
                    JOptionPane.YES_NO_OPTION);

            if (open == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToSave);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi xuất file Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clear() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtDiem.setText("0");
        cboTrangThai.setSelectedIndex(0);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtTenKH.requestFocus();
    }

    private String generateCustomerId() {
        int max = 0;
        List<KhachHangDTO> customers = khachHangBUS.getAll();

        if (customers != null) {
            for (KhachHangDTO customer : customers) {
                Matcher matcher = CUSTOMER_ID_PATTERN
                        .matcher(customer.getMaKH() == null ? "" : customer.getMaKH().trim());
                if (matcher.matches()) {
                    try {
                        max = Math.max(max, Integer.parseInt(matcher.group(1)));
                    } catch (NumberFormatException ignored) {
                        // Ignore IDs outside the numeric format used for automatic IDs.
                    }
                }
            }
        }

        return String.format("KH%03d", max + 1);
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
                        "innerFocusWidth:0");
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

                if (column == 0 || column == 2) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 4) {
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

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}