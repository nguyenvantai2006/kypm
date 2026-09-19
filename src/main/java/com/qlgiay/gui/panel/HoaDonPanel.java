package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.KhachHangBUS;
import com.qlgiay.bus.NhanVienBUS;
import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.IconUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import org.apache.commons.text.similarity.LevenshteinDistance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoaDonPanel extends JPanel implements IRefreshable {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    private JTable tableHoaDon;
    private DefaultTableModel hoaDonModel;

    private JTable tableChiTiet;
    private DefaultTableModel chiTietModel;

    private JTextField txtSearch;
    private JButton btnCalendar;
    private JComboBox<String> cboClear;
    private JTextField txtSelectedDate;
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;

    private JTextField txtMaHD;
    private JTextField txtNgay;
    private JTextField txtNV;
    private JTextField txtKH;
    private JTextField txtVoucher;
    private JTextField txtTongTien;

    private int hoverRowHD = -1;
    private int hoverRowCT = -1;
    private boolean loadingTable = false;
    private final LevenshteinDistance fuzzyDistance = LevenshteinDistance.getDefaultInstance();

    public HoaDonPanel() {
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
        p.add(createTableHoaDonPanel(), BorderLayout.CENTER);

        return p;
    }

    private JPanel createTopBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã HĐ, mã NV, mã KH, tên KH hoặc SDT...");

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

        btnCalendar = new JButton();
        btnCalendar.setToolTipText("Chọn ngày hoặc khoảng thời gian");
        Icon calendarIcon = IconUtil.loadPng("/icons/calendar.png", 20);
        btnCalendar.setIcon(calendarIcon);
        if (calendarIcon == null) {
            btnCalendar.setText("Lịch");
        }
        btnCalendar.setPreferredSize(new Dimension(34, 32));
        styleActionButton(btnCalendar, "default");
        btnCalendar.addActionListener(e -> {
            try {
                openDatePickerDialog();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Không thể mở bộ chọn ngày: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cboClear = new JComboBox<>(new String[] { "Clear" });
        styleComboBox(cboClear);
        cboClear.setPreferredSize(new Dimension(45, 32));
        cboClear.addActionListener(e -> clearDateFilter());

        txtSelectedDate = new JTextField("Tất cả");
        txtSelectedDate.setEditable(false);
        txtSelectedDate.setFocusable(false);
        txtSelectedDate.setHorizontalAlignment(SwingConstants.CENTER);
        txtSelectedDate.setPreferredSize(new Dimension(150, 32));
        txtSelectedDate.putClientProperty(FlatClientProperties.STYLE, "arc:8;focusWidth:0");

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(btnCalendar);
        pnlFilters.add(cboClear);
        pnlFilters.add(Box.createHorizontalStrut(5));
        pnlFilters.add(txtSelectedDate);

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTableHoaDonPanel() {
        String[] cols = {
                "Mã HĐ", "Mã KH", "Tên KH", "SDT", "Mã NV", "Tên NV", "Voucher", "Tổng tiền", "Ngày lập"
        };

        hoaDonModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableHoaDon = new JTable(hoaDonModel);

        int[] widths = { 110, 90, 170, 110, 90, 170, 110, 120, 100 };
        for (int i = 0; i < widths.length; i++) {
            tableHoaDon.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        tableHoaDon.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        styleTable(tableHoaDon, true);

        tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loadingTable) {
                loadSelectedRow();
            }
        });

        JScrollPane sp = new JScrollPane(tableHoaDon);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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

        addGridRow(contentPanel, gbc, row++, field("Mã hóa đơn", txtMaHD), field("Ngày lập", txtNgay));
        addGridRow(contentPanel, gbc, row++, field("Mã nhân viên", txtNV), field("Mã khách hàng", txtKH));
        addGridRow(contentPanel, gbc, row++, field("Mã voucher", txtVoucher), field("Tổng tiền", txtTongTien));

        JPanel alignTopPanel = new JPanel(new BorderLayout(0, 10));
        alignTopPanel.setOpaque(false);
        alignTopPanel.add(contentPanel, BorderLayout.NORTH);
        alignTopPanel.add(createDetailTable(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(alignTopPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnRefresh = new JButton("Làm mới");
        JButton btnPrint = new JButton("In hóa đơn");

        btnRefresh.setIcon(IconUtil.loadPng("/icons/refresh.png", 22));
        btnPrint.setIcon(IconUtil.loadPng("/icons/print.png", 22));

        btnRefresh.setIconTextGap(6);
        btnPrint.setIconTextGap(6);

        styleActionButton(btnRefresh, "default");
        styleActionButton(btnPrint, "success");

        btnRefresh.addActionListener(e -> refreshData());
        btnPrint.addActionListener(e -> printInvoice());

        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnPrint);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMaHD = new JTextField();
        txtNgay = new JTextField();
        txtNV = new JTextField();
        txtKH = new JTextField();
        txtVoucher = new JTextField();
        txtTongTien = new JTextField();

        JTextField[] arr = { txtMaHD, txtNgay, txtNV, txtKH, txtVoucher, txtTongTien };
        for (JTextField t : arr) {
            t.setEnabled(false);
        }
    }

    private JScrollPane createDetailTable() {
        String[] cols = { "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền" };

        chiTietModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableChiTiet = new JTable(chiTietModel);

        tableChiTiet.getColumnModel().getColumn(0).setMinWidth(0);
        tableChiTiet.getColumnModel().getColumn(0).setMaxWidth(0);
        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(160);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(40);
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(100);

        tableChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        styleTable(tableChiTiet, false);

        JScrollPane sp = new JScrollPane(tableChiTiet);
        sp.setPreferredSize(new Dimension(0, 180));
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)));

        return sp;
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

    private boolean matchesInvoiceSearch(String query, HoaDonDTO invoice, String customerName, String customerPhone) {
        return fuzzyMatches(query, invoice.getMaHD())
                || fuzzyMatches(query, invoice.getMaNV())
                || fuzzyMatches(query, invoice.getMaKH())
                || fuzzyMatches(query, customerName)
                || fuzzyMatches(query, customerPhone);
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

        int distance = fuzzyDistance.apply(normalizedQuery, normalizedValue);
        int allowedDistance = Math.max(1, normalizedQuery.length() / 3);
        if (distance <= allowedDistance) {
            return true;
        }

        for (String token : normalizedValue.split("\\s+")) {
            if (fuzzyDistance.apply(normalizedQuery, token) <= allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private DatePicker createMouseOnlyDatePicker(LocalDate date) {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setAllowKeyboardEditing(false);
        DatePicker picker = new DatePicker(settings);

        picker.getSettings().setDateRangeLimits(null, LocalDate.now());
        picker.setDate(date);
        return picker;
    }

    private void openDatePickerDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Chọn ngày lọc hóa đơn", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        DatePicker singleDatePicker = createMouseOnlyDatePicker(LocalDate.now());
        DatePicker startDatePicker = createMouseOnlyDatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDatePicker = createMouseOnlyDatePicker(LocalDate.now());

        startDatePicker.addDateChangeListener(event -> {
            LocalDate start = event.getNewDate();
            if (start != null) {
                endDatePicker.getSettings().setDateRangeLimits(start, LocalDate.now());
            }
        });
        endDatePicker.addDateChangeListener(event -> {
            LocalDate end = event.getNewDate();
            if (end != null) {
                startDatePicker.getSettings().setDateRangeLimits(null, end);
            }
        });

        JPanel singlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 18));
        singlePanel.add(new JLabel("Ngày:"));
        singlePanel.add(singleDatePicker);

        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 18));
        rangePanel.add(new JLabel("Từ ngày:"));
        rangePanel.add(startDatePicker);
        rangePanel.add(new JLabel("Đến ngày:"));
        rangePanel.add(endDatePicker);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ngày", singlePanel);
        tabs.addTab("Khoảng thời gian", rangePanel);

        JButton btnCancel = new JButton("Hủy");
        JButton btnOk = new JButton("OK");
        styleActionButton(btnCancel, "default");
        styleActionButton(btnOk, "success");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnOk.addActionListener(e -> {
            LocalDate start;
            LocalDate end;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (tabs.getSelectedIndex() == 0) {
                start = singleDatePicker.getDate();
                end = start;
            } else {
                start = startDatePicker.getDate();
                end = endDatePicker.getDate();
                if (start == null || end == null || end.isBefore(start)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Khoảng thời gian không hợp lệ.",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (start == null) {
                return;
            }

            selectedStartDate = start;
            selectedEndDate = end;
            txtSelectedDate.setText(start.equals(end)
                    ? formatter.format(start)
                    : formatter.format(start) + " - " + formatter.format(end));
            dialog.dispose();
            loadTable();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(btnCancel);
        buttons.add(btnOk);

        dialog.add(tabs, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setSize(520, 230);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void clearDateFilter() {
        selectedStartDate = null;
        selectedEndDate = null;
        txtSelectedDate.setText("Tất cả");
        loadTable();
    }

    private void loadTable() {
        loadingTable = true;
        try {
            String keyword = normalizeSearchText(txtSearch.getText());

            tableHoaDon.clearSelection();
            hoaDonModel.setRowCount(0);
            chiTietModel.setRowCount(0);

            txtMaHD.setText("");
            txtNgay.setText("");
            txtNV.setText("");
            txtKH.setText("");
            txtVoucher.setText("");
            txtTongTien.setText("");

            List<HoaDonDTO> list = hoaDonDAO.findAll();
            Map<String, NhanVienDTO> employees = new HashMap<>();
            Map<String, KhachHangDTO> customers = new HashMap<>();

            if (list != null) {
                for (HoaDonDTO hd : list) {
                    if (hd.getNgayLap() != null) {
                        if (selectedStartDate != null && hd.getNgayLap().isBefore(selectedStartDate)) {
                            continue;
                        }
                        if (selectedEndDate != null && hd.getNgayLap().isAfter(selectedEndDate)) {
                            continue;
                        }
                    }

                    NhanVienDTO employee = employees.computeIfAbsent(hd.getMaNV(), nhanVienBUS::findById);
                    KhachHangDTO customer = null;
                    if (hd.getMaKH() != null && !hd.getMaKH().trim().isEmpty()) {
                        customer = customers.computeIfAbsent(hd.getMaKH(), khachHangBUS::findById);
                    }

                    String customerName = customer == null ? "" : customer.getTenKH();
                    String customerPhone = customer == null ? "" : customer.getSdt();
                    if (!keyword.isEmpty() && !matchesInvoiceSearch(keyword, hd, customerName, customerPhone)) {
                        continue;
                    }

                    hoaDonModel.addRow(new Object[] {
                            hd.getMaHD(),
                            hd.getMaKH() != null ? hd.getMaKH() : "Khách lẻ",
                            customerName.isBlank() ? "Khách lẻ" : customerName,
                            customerPhone,
                            hd.getMaNV(),
                            employee == null ? "" : (safe(employee.getHo()) + " " + safe(employee.getTen())).trim(),
                            (hd.getMaVoucher() != null && !hd.getMaVoucher().trim().isEmpty()) ? hd.getMaVoucher()
                                    : "Không dùng",
                            hd.getTongTien(),
                            hd.getNgayLap()
                    });
                }
            }
        } finally {
            loadingTable = false;
        }
    }

    private void loadSelectedRow() {
        if (loadingTable) {
            return;
        }

        int viewRow = tableHoaDon.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        if (viewRow >= tableHoaDon.getRowCount()) {
            return;
        }

        int modelRow = viewRow;
        if (modelRow < 0 || modelRow >= hoaDonModel.getRowCount()) {
            return;
        }

        String maHD = String.valueOf(hoaDonModel.getValueAt(modelRow, 0));

        HoaDonDTO hd = hoaDonDAO.findById(maHD);
        if (hd == null) {
            return;
        }

        txtMaHD.setText(hd.getMaHD());
        txtNV.setText(hd.getMaNV());
        txtKH.setText(hd.getMaKH() != null ? hd.getMaKH() : "Khách lẻ");
        txtVoucher.setText(
                (hd.getMaVoucher() != null && !hd.getMaVoucher().trim().isEmpty()) ? hd.getMaVoucher() : "Không dùng");
        txtTongTien.setText(formatPrice(hd.getTongTien()));
        txtNgay.setText(String.valueOf(hd.getNgayLap()));

        loadDetailTable(maHD);
    }

    private void loadDetailTable(String maHD) {
        chiTietModel.setRowCount(0);
        List<ChiTietHoaDonDTO> list = chiTietHoaDonDAO.findByMaHD(maHD);

        if (list != null) {
            for (ChiTietHoaDonDTO ct : list) {
                SanPhamDTO sp = sanPhamDAO.findById(ct.getMaSP());
                String tenSP = sp != null ? sp.getTenSP() : ct.getMaSP();
                BigDecimal thanhTien = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));

                chiTietModel.addRow(new Object[] {
                        ct.getMaSP(),
                        tenSP,
                        ct.getSoLuong(),
                        ct.getDonGia(),
                        thanhTien
                });
            }
        }
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8;focusWidth:0;innerFocusWidth:0");
    }

    private void styleActionButton(JButton button, String type) {
        String style = switch (type) {
            case "success" ->
                "arc:10;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "margin:4,8,4,8;" +
                        "background:#E8F5E9;" +
                        "foreground:#2E7D32;" +
                        "hoverBackground:#D7F0DB;" +
                        "pressedBackground:#C2E7C8";
            default ->
                "arc:10;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "margin:4,8,4,8;" +
                        "background:#E8F0FE;" +
                        "foreground:#005A9E;" +
                        "hoverBackground:#DCE8FC;" +
                        "pressedBackground:#C9DCF8";
        };

        button.putClientProperty(FlatClientProperties.STYLE, style);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable targetTable, boolean isHoaDonTable) {
        targetTable.setRowHeight(34);
        targetTable.setShowGrid(false);
        targetTable.setIntercellSpacing(new Dimension(0, 0));
        targetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetTable.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTableHeader header = targetTable.getTableHeader();
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
                if (value instanceof BigDecimal) {
                    value = formatPrice((BigDecimal) value);
                }

                Component c = super.getTableCellRendererComponent(
                        tbl,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column);

                int currentHoverRow = isHoaDonTable ? hoverRowHD : hoverRowCT;

                if (isSelected) {
                    c.setBackground(new Color(232, 240, 254));
                    c.setForeground(new Color(0, 90, 158));
                } else if (row == currentHoverRow) {
                    c.setBackground(new Color(245, 247, 250));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (isHoaDonTable) {
                    if (column == 0 || column == 1 || column == 3 || column == 4) {
                        setHorizontalAlignment(SwingConstants.CENTER);
                    } else if (column == 7) {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        setHorizontalAlignment(SwingConstants.LEFT);
                    }
                } else {
                    if (column == 2 || column == 3 || column == 4) {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < targetTable.getColumnCount(); i++) {
            targetTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        targetTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = targetTable.rowAtPoint(e.getPoint());
                if (isHoaDonTable) {
                    if (row != hoverRowHD) {
                        hoverRowHD = row;
                        targetTable.repaint();
                    }
                } else {
                    if (row != hoverRowCT) {
                        hoverRowCT = row;
                        targetTable.repaint();
                    }
                }
            }
        });

        targetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (isHoaDonTable) {
                    hoverRowHD = -1;
                } else {
                    hoverRowCT = -1;
                }
                targetTable.repaint();
            }
        });
    }

    private String formatPrice(BigDecimal p) {
        if (p == null) {
            return "0đ";
        }
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(p) + "đ";
    }

    private String safePdfText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("Đ", "D").replace("đ", "d")
                .replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                .replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a")
                .replace("ặ", "a")
                .replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a")
                .replace("ậ", "a")
                .replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                .replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e")
                .replace("ệ", "e")
                .replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                .replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                .replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o")
                .replace("ộ", "o")
                .replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o")
                .replace("ợ", "o")
                .replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                .replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u")
                .replace("ự", "u")
                .replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                .replace("Á", "A").replace("À", "A").replace("Ả", "A").replace("Ã", "A").replace("Ạ", "A")
                .replace("Ă", "A").replace("Ắ", "A").replace("Ằ", "A").replace("Ẳ", "A").replace("Ẵ", "A")
                .replace("Ặ", "A")
                .replace("Â", "A").replace("Ấ", "A").replace("Ầ", "A").replace("Ẩ", "A").replace("Ẫ", "A")
                .replace("Ậ", "A")
                .replace("É", "E").replace("È", "E").replace("Ẻ", "E").replace("Ẽ", "E").replace("Ẹ", "E")
                .replace("Ê", "E").replace("Ế", "E").replace("Ề", "E").replace("Ể", "E").replace("Ễ", "E")
                .replace("Ệ", "E")
                .replace("Í", "I").replace("Ì", "I").replace("Ỉ", "I").replace("Ĩ", "I").replace("Ị", "I")
                .replace("Ó", "O").replace("Ò", "O").replace("Ỏ", "O").replace("Õ", "O").replace("Ọ", "O")
                .replace("Ô", "O").replace("Ố", "O").replace("Ồ", "O").replace("Ổ", "O").replace("Ỗ", "O")
                .replace("Ộ", "O")
                .replace("Ơ", "O").replace("Ớ", "O").replace("Ờ", "O").replace("Ở", "O").replace("Ỡ", "O")
                .replace("Ợ", "O")
                .replace("Ú", "U").replace("Ù", "U").replace("Ủ", "U").replace("Ũ", "U").replace("Ụ", "U")
                .replace("Ư", "U").replace("Ứ", "U").replace("Ừ", "U").replace("Ử", "U").replace("Ữ", "U")
                .replace("Ự", "U")
                .replace("Ý", "Y").replace("Ỳ", "Y").replace("Ỷ", "Y").replace("Ỹ", "Y").replace("Ỵ", "Y");
    }

    private void printInvoice() {
        int viewRow = tableHoaDon.getSelectedRow();
        if (viewRow < 0 || viewRow >= tableHoaDon.getRowCount()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn 1 hóa đơn để in!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = viewRow;
        if (modelRow < 0 || modelRow >= hoaDonModel.getRowCount()) {
            return;
        }

        String maHD = String.valueOf(hoaDonModel.getValueAt(modelRow, 0));

        try {
            HoaDonDTO hd = hoaDonDAO.findById(maHD);
            if (hd == null) {
                return;
            }

            List<ChiTietHoaDonDTO> items = chiTietHoaDonDAO.findByMaHD(maHD);

            String path = "HoaDon_" + hd.getMaHD() + ".pdf";
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.BOLD);

            Paragraph title = new Paragraph("HOA DON BAN HANG\n\n", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Ma HD: " + hd.getMaHD(), fontNormal));
            document.add(new Paragraph("Ngay lap: " + hd.getNgayLap(), fontNormal));

            NhanVienDTO nv = nhanVienBUS.findById(hd.getMaNV());
            String tenNhanVien = nv != null ? safePdfText((nv.getHo() + " " + nv.getTen()).trim())
                    : safePdfText(hd.getMaNV());
            document.add(new Paragraph("Nhan vien: " + tenNhanVien, fontNormal));

            if (hd.getMaKH() != null && !hd.getMaKH().isEmpty()) {
                KhachHangDTO kh = khachHangBUS.findById(hd.getMaKH());
                String tenKH = kh != null ? safePdfText(kh.getTenKH()) : safePdfText(hd.getMaKH());
                document.add(new Paragraph("Khach hang: " + tenKH, fontNormal));
            } else {
                document.add(new Paragraph("Khach hang: Khach le", fontNormal));
            }

            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));

            PdfPTable pdfTable = new PdfPTable(4);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[] { 4f, 1f, 2.5f, 2.5f });

            PdfPCell h1 = new PdfPCell(new Phrase("Ten SP", fontBold));
            PdfPCell h2 = new PdfPCell(new Phrase("SL", fontBold));
            PdfPCell h3 = new PdfPCell(new Phrase("Gia", fontBold));
            PdfPCell h4 = new PdfPCell(new Phrase("Tong", fontBold));

            h1.setHorizontalAlignment(Element.ALIGN_LEFT);
            h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h4.setHorizontalAlignment(Element.ALIGN_RIGHT);

            pdfTable.addCell(h1);
            pdfTable.addCell(h2);
            pdfTable.addCell(h3);
            pdfTable.addCell(h4);

            BigDecimal tamTinh = BigDecimal.ZERO;
            DecimalFormat df = new DecimalFormat("#,###");

            for (ChiTietHoaDonDTO ct : items) {
                SanPhamDTO sp = sanPhamDAO.findById(ct.getMaSP());
                String tenSP = sp != null ? safePdfText(sp.getTenSP()) : safePdfText(ct.getMaSP());

                BigDecimal tong1SP = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));

                PdfPCell c1 = new PdfPCell(new Phrase(tenSP, fontNormal));
                PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                PdfPCell c3 = new PdfPCell(new Phrase(df.format(ct.getDonGia()) + " VND", fontNormal));
                PdfPCell c4 = new PdfPCell(new Phrase(df.format(tong1SP) + " VND", fontNormal));

                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);

                pdfTable.addCell(c1);
                pdfTable.addCell(c2);
                pdfTable.addCell(c3);
                pdfTable.addCell(c4);

                tamTinh = tamTinh.add(tong1SP);
            }
            document.add(pdfTable);

            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));
            document.add(new Paragraph("Tam tinh: " + df.format(tamTinh) + " VND", fontNormal));

            BigDecimal giamGia = tamTinh.subtract(hd.getTongTien());
            if (giamGia.compareTo(BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Giam gia: " + df.format(giamGia) + " VND", fontNormal));
            }

            document.add(new Paragraph("Thanh tien: " + df.format(hd.getTongTien()) + " VND", fontTitle));

            document.close();

            File file = new File(path);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo file PDF!");
        }
    }

    @Override
    public void refreshData() {
        txtSearch.setText("");
        selectedStartDate = null;
        selectedEndDate = null;
        txtSelectedDate.setText("Tất cả");

        txtMaHD.setText("");
        txtNgay.setText("");
        txtNV.setText("");
        txtKH.setText("");
        txtVoucher.setText("");
        txtTongTien.setText("");

        hoverRowHD = -1;
        hoverRowCT = -1;

        tableHoaDon.clearSelection();
        chiTietModel.setRowCount(0);
        loadTable();
    }
}