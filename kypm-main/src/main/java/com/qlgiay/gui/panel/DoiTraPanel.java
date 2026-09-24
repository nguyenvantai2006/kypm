package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.DoiTraBUS;
import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.DoiTraDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.SanPhamDAO;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.DoiTraDTO;
import com.qlgiay.dto.HoaDonDTO;
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
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoiTraPanel extends JPanel implements IRefreshable {
    private final AuthSession session;
    private final DoiTraBUS doiTraBUS = new DoiTraBUS();
    private final DoiTraDAO doiTraDAO = new DoiTraDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JButton btnCalendar;
    private JComboBox<String> cboClear;
    private JTextField txtSelectedDate;
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;
    private JComboBox<String> cboFilterTinhTrang;

    private JTextField txtMaDT;
    private JTextField txtNgayDoiTra;
    private JTextField txtNhanVien;
    private JTextField txtMaHD;
    private JTextField txtMaSP;
    private JTextField txtSoLuong;
    private JTextField txtTongTienHoan;
    private JComboBox<String> cboTinhTrang;
    private JTextArea txtLyDo;

    private int hoverRow = -1;
    private boolean loadingTable = false;
    private final LevenshteinDistance fuzzyDistance = LevenshteinDistance.getDefaultInstance();

    public DoiTraPanel(AuthSession session) {
        this.session = session;

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

        initFormData();
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã đổi trả hoặc mã hóa đơn...");

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
        if (calendarIcon == null)
            btnCalendar.setText("Lịch");
        btnCalendar.setPreferredSize(new Dimension(34, 32));
        styleActionButton(btnCalendar, "default");
        btnCalendar.addActionListener(e -> openDatePickerDialog());

        cboClear = new JComboBox<>(new String[] { "Clear" });
        styleComboBox(cboClear);
        cboClear.setPreferredSize(new Dimension(45, 32));
        cboClear.addActionListener(e -> clearDateFilter());

        txtSelectedDate = new JTextField("Tất cả");
        txtSelectedDate.setEditable(false);
        txtSelectedDate.setFocusable(false);
        txtSelectedDate.setHorizontalAlignment(SwingConstants.CENTER);
        txtSelectedDate.setPreferredSize(new Dimension(66, 32));
        txtSelectedDate.putClientProperty(FlatClientProperties.STYLE, "arc:8;focusWidth:0");

        cboFilterTinhTrang = new JComboBox<>(new String[] {
                "Tất cả tình trạng", "Còn nguyên", "Lỗi nhẹ", "Lỗi nặng"
        });
        styleComboBox(cboFilterTinhTrang);
        cboFilterTinhTrang.setPreferredSize(new Dimension(140, 32));
        cboFilterTinhTrang.addActionListener(e -> loadTable());

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(btnCalendar);
        pnlFilters.add(cboClear);
        pnlFilters.add(Box.createHorizontalStrut(5));
        pnlFilters.add(txtSelectedDate);
        pnlFilters.add(Box.createHorizontalStrut(10));
        pnlFilters.add(cboFilterTinhTrang);

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã ĐT", "Mã HĐ", "Ngày đổi trả", "Mã NV", "Mã SP", "SL", "Tổng tiền hoàn", "Tình trạng"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(115);
        table.getColumnModel().getColumn(1).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(45);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        styleTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loadingTable) {
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

        addGridRow(contentPanel, gbc, row++, field("Mã đổi trả", txtMaDT), field("Ngày đổi trả", txtNgayDoiTra));
        addGridRow(contentPanel, gbc, row++, field("Nhân viên", txtNhanVien), field("Tình trạng", cboTinhTrang));
        addGridRow(contentPanel, gbc, row++, field("Mã hóa đơn", txtMaHD), field("Mã sản phẩm", txtMaSP));
        addGridRow(contentPanel, gbc, row++, field("Số lượng", txtSoLuong), field("Tổng tiền hoàn", txtTongTienHoan));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(buildLyDoPanel(), gbc);

        JPanel alignTopPanel = new JPanel(new BorderLayout());
        alignTopPanel.setOpaque(false);
        alignTopPanel.add(contentPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(alignTopPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnAdd = new JButton("Thêm");
        JButton btnClear = new JButton("Làm mới");

        btnAdd.setIcon(IconUtil.loadPng("/icons/add.png", 24));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 24));

        int gap = 4;
        btnAdd.setIconTextGap(gap);
        btnClear.setIconTextGap(gap);

        styleActionButton(btnAdd, "success");
        styleActionButton(btnClear, "default");

        btnAdd.addActionListener(e -> add());
        btnClear.addActionListener(e -> clear());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnClear);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMaDT = new JTextField();
        txtNgayDoiTra = new JTextField();
        txtNhanVien = new JTextField();
        txtMaHD = new JTextField();
        txtMaSP = new JTextField();
        txtSoLuong = new JTextField();
        txtTongTienHoan = new JTextField();

        txtNgayDoiTra.setEnabled(false);
        txtNhanVien.setEnabled(false);
        txtTongTienHoan.setEnabled(false);
        txtMaDT.setEnabled(false);

        txtMaHD.putClientProperty("JTextField.placeholderText", "Nhập mã hóa đơn...");
        txtMaSP.putClientProperty("JTextField.placeholderText", "Nhập mã sản phẩm...");
        txtSoLuong.putClientProperty("JTextField.placeholderText", "Nhập số lượng...");

        setNumberOnly(txtSoLuong);

        cboTinhTrang = new JComboBox<>(new String[] {
                "Còn nguyên", "Lỗi nhẹ", "Lỗi nặng"
        });
        styleComboBox(cboTinhTrang);

        txtLyDo = new JTextArea(3, 20);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);

        DocumentListener previewListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateRefundPreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateRefundPreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateRefundPreview();
            }
        };

        txtMaHD.getDocument().addDocumentListener(previewListener);
        txtMaSP.getDocument().addDocumentListener(previewListener);
        txtSoLuong.getDocument().addDocumentListener(previewListener);
    }

    private void initFormData() {
        txtMaDT.setText(generateReturnId());
        txtNgayDoiTra.setText(LocalDate.now().toString());
        txtNhanVien.setText(getNhanVienName());
        txtTongTienHoan.setText("0đ");
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

    private JPanel buildLyDoPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel("Lý do"), BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(txtLyDo);
        sp.setPreferredSize(new Dimension(0, 55));
        sp.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private String normalizeSearchText(String value) {
        if (value == null)
            return "";
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return normalized.toLowerCase();
    }

    private boolean fuzzyMatches(String query, String value) {
        String normalizedQuery = normalizeSearchText(query);
        String normalizedValue = normalizeSearchText(value);
        if (normalizedQuery.isEmpty() || normalizedValue.isEmpty())
            return false;
        if (normalizedValue.contains(normalizedQuery))
            return true;

        int allowedDistance = Math.max(1, normalizedQuery.length() / 3);
        if (fuzzyDistance.apply(normalizedQuery, normalizedValue) <= allowedDistance)
            return true;
        for (String token : normalizedValue.split("\\s+")) {
            if (fuzzyDistance.apply(normalizedQuery, token) <= allowedDistance)
                return true;
        }
        return false;
    }

    private boolean matchesReturnSearch(String query, DoiTraDTO doiTra) {
        return fuzzyMatches(query, doiTra.getMaDT()) || fuzzyMatches(query, doiTra.getMaHD());
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
        JDialog dialog = new JDialog(parent, "Chọn ngày lọc đổi trả", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        DatePicker singleDatePicker = createMouseOnlyDatePicker(LocalDate.now());
        DatePicker startDatePicker = createMouseOnlyDatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDatePicker = createMouseOnlyDatePicker(LocalDate.now());

        startDatePicker.addDateChangeListener(event -> {
            if (event.getNewDate() != null) {
                endDatePicker.getSettings().setDateRangeLimits(event.getNewDate(), LocalDate.now());
            }
        });
        endDatePicker.addDateChangeListener(event -> {
            if (event.getNewDate() != null) {
                startDatePicker.getSettings().setDateRangeLimits(null, event.getNewDate());
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
            LocalDate start = tabs.getSelectedIndex() == 0 ? singleDatePicker.getDate() : startDatePicker.getDate();
            LocalDate end = tabs.getSelectedIndex() == 0 ? start : endDatePicker.getDate();
            if (start == null || end == null || end.isBefore(start)) {
                JOptionPane.showMessageDialog(dialog, "Khoảng thời gian không hợp lệ.", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            selectedStartDate = start;
            selectedEndDate = end;
            txtSelectedDate.setText(start.equals(end) ? formatter.format(start)
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
            String filterTinhTrang = String.valueOf(cboFilterTinhTrang.getSelectedItem());

            table.clearSelection();
            tableModel.setRowCount(0);

            List<DoiTraDTO> list = doiTraDAO.findAll();

            if (list != null) {
                for (DoiTraDTO dt : list) {
                    if (dt.getNgayDoiTra() != null) {
                        if (selectedStartDate != null && dt.getNgayDoiTra().isBefore(selectedStartDate)) {
                            continue;
                        }
                        if (selectedEndDate != null && dt.getNgayDoiTra().isAfter(selectedEndDate)) {
                            continue;
                        }
                    }

                    if (!keyword.isEmpty() && !matchesReturnSearch(keyword, dt))
                        continue;

                    if (!"Tất cả tình trạng".equals(filterTinhTrang)) {
                        if (dt.getTinhTrang() == null || !dt.getTinhTrang().equalsIgnoreCase(filterTinhTrang)) {
                            continue;
                        }
                    }

                    tableModel.addRow(new Object[] {
                            dt.getMaDT(),
                            dt.getMaHD(),
                            dt.getNgayDoiTra(),
                            dt.getMaNV(),
                            dt.getMaSP(),
                            dt.getSoLuong(),
                            dt.getTongTienHoan(),
                            dt.getTinhTrang()
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

        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        if (viewRow >= table.getRowCount()) {
            return;
        }

        int modelRow = viewRow;
        if (modelRow < 0 || modelRow >= tableModel.getRowCount()) {
            return;
        }

        String maDT = String.valueOf(tableModel.getValueAt(modelRow, 0));

        DoiTraDTO dt = doiTraDAO.findById(maDT);
        if (dt == null) {
            return;
        }

        txtMaDT.setText(dt.getMaDT());
        txtNgayDoiTra.setText(String.valueOf(dt.getNgayDoiTra()));
        txtNhanVien.setText(dt.getMaNV());
        txtMaHD.setText(dt.getMaHD());
        txtMaSP.setText(dt.getMaSP());
        txtSoLuong.setText(String.valueOf(dt.getSoLuong()));
        txtTongTienHoan.setText(formatMoney(dt.getTongTienHoan()));
        txtLyDo.setText(dt.getLyDo());
        cboTinhTrang.setSelectedItem(dt.getTinhTrang());

        txtMaHD.setEnabled(false);
        txtMaSP.setEnabled(false);
        txtSoLuong.setEnabled(false);
        txtLyDo.setEnabled(false);
        cboTinhTrang.setEnabled(false);
    }

    private DoiTraDTO readForm() {
        try {
            DoiTraDTO dt = new DoiTraDTO();

            dt.setMaDT(txtMaDT.getText().trim());
            dt.setNgayDoiTra(LocalDate.now());

            if (session != null && session.getNhanVien() != null) {
                dt.setMaNV(session.getNhanVien().getMaNV());
            }

            dt.setMaHD(txtMaHD.getText().trim());
            dt.setMaSP(txtMaSP.getText().trim());
            dt.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            dt.setLyDo(txtLyDo.getText().trim());
            dt.setTinhTrang(String.valueOf(cboTinhTrang.getSelectedItem()));

            return dt;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại số lượng.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private boolean validateForm(DoiTraDTO dt) {
        if (dt.getMaDT() == null || dt.getMaDT().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã đổi trả không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (dt.getMaNV() == null || dt.getMaNV().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không xác định được nhân viên thực hiện!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (dt.getMaHD() == null || dt.getMaHD().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã hóa đơn không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }

        HoaDonDTO hd = hoaDonDAO.findById(dt.getMaHD());
        if (hd == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy hóa đơn này!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }

        if (dt.getMaSP() == null || dt.getMaSP().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã sản phẩm không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        SanPhamDTO sp = sanPhamDAO.findById(dt.getMaSP());
        if (sp == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy sản phẩm này!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        List<ChiTietHoaDonDTO> list = chiTietHoaDonDAO.findByMaHD(dt.getMaHD());
        ChiTietHoaDonDTO ct = null;
        if (list != null) {
            for (ChiTietHoaDonDTO item : list) {
                if (item.getMaSP() != null && item.getMaSP().equalsIgnoreCase(dt.getMaSP())) {
                    ct = item;
                    break;
                }
            }
        }

        if (ct == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Sản phẩm này không tồn tại trong hóa đơn đã nhập!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        if (dt.getSoLuong() <= 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Số lượng đổi trả phải lớn hơn 0!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtSoLuong.requestFocus();
            return false;
        }

        if (dt.getSoLuong() > ct.getSoLuong()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Số lượng đổi trả không được vượt quá số lượng đã mua!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtSoLuong.requestFocus();
            return false;
        }

        if (dt.getLyDo() == null || dt.getLyDo().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Lý do không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtLyDo.requestFocus();
            return false;
        }

        if (dt.getTinhTrang() == null || dt.getTinhTrang().isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn tình trạng sản phẩm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            cboTinhTrang.requestFocus();
            return false;
        }

        return true;
    }

    private void add() {
        DoiTraDTO dt = readForm();
        if (dt == null) {
            return;
        }

        if (!validateForm(dt)) {
            return;
        }

        if (doiTraDAO.findById(dt.getMaDT()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã đổi trả này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (doiTraBUS.createReturn(dt)) {
            int confirm = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tạo phiếu đổi trả thành công!\nBạn có muốn in phiếu đổi trả không?",
                    "In Phiếu Đổi Trả",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                DoiTraDTO saved = doiTraDAO.findById(dt.getMaDT());
                if (saved != null) {
                    inPhieuDoiTraPDF(saved);
                } else {
                    inPhieuDoiTraPDF(dt);
                }
            }

            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Tạo phiếu đổi trả thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inPhieuDoiTraPDF(DoiTraDTO dt) {
        try {
            String path = "PhieuDoiTra_" + dt.getMaDT() + ".pdf";
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.BOLD);

            Paragraph title = new Paragraph("PHIEU DOI TRA HANG\n\n", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Ma DT: " + dt.getMaDT(), fontNormal));
            document.add(new Paragraph("Ngay doi tra: " + dt.getNgayDoiTra(), fontNormal));
            document.add(new Paragraph("Nhan vien: " + safePdfText(getNhanVienName()), fontNormal));
            document.add(new Paragraph("Ma HD goc: " + dt.getMaHD(), fontNormal));
            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));

            PdfPTable tablePdf = new PdfPTable(3);
            tablePdf.setWidthPercentage(100);
            tablePdf.setWidths(new float[] { 4.5f, 1.2f, 2.8f });

            PdfPCell h1 = new PdfPCell(new Phrase("Ten SP", fontBold));
            PdfPCell h2 = new PdfPCell(new Phrase("SL", fontBold));
            PdfPCell h3 = new PdfPCell(new Phrase("Tong hoan", fontBold));

            h1.setHorizontalAlignment(Element.ALIGN_LEFT);
            h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h3.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablePdf.addCell(h1);
            tablePdf.addCell(h2);
            tablePdf.addCell(h3);

            DecimalFormat df = new DecimalFormat("#,###");
            SanPhamDTO sp = sanPhamDAO.findById(dt.getMaSP());
            String tenSP = sp != null ? safePdfText(sp.getTenSP()) : dt.getMaSP();

            PdfPCell c1 = new PdfPCell(new Phrase(tenSP, fontNormal));
            PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(dt.getSoLuong()), fontNormal));
            PdfPCell c3 = new PdfPCell(new Phrase(df.format(dt.getTongTienHoan()) + " VND", fontNormal));

            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c3.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablePdf.addCell(c1);
            tablePdf.addCell(c2);
            tablePdf.addCell(c3);

            document.add(tablePdf);

            document.add(new Paragraph(
                    "\n---------------------------------------------------------------------------------------\n",
                    fontNormal));
            document.add(new Paragraph("Tinh trang: " + safePdfText(dt.getTinhTrang()), fontNormal));
            document.add(new Paragraph("Ly do: " + safePdfText(dt.getLyDo()), fontNormal));
            document.add(new Paragraph("\nSo tien hoan lai: " + df.format(dt.getTongTienHoan()) + " VND", fontTitle));

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

    private void clear() {
        txtMaDT.setEnabled(false);

        txtMaHD.setEnabled(true);
        txtMaSP.setEnabled(true);
        txtSoLuong.setEnabled(true);
        txtLyDo.setEnabled(true);
        cboTinhTrang.setEnabled(true);

        txtMaDT.setText(generateReturnId());
        txtNgayDoiTra.setText(LocalDate.now().toString());
        txtNhanVien.setText(getNhanVienName());
        txtMaHD.setText("");
        txtMaSP.setText("");
        txtSoLuong.setText("");
        txtTongTienHoan.setText("0đ");
        txtLyDo.setText("");
        cboTinhTrang.setSelectedIndex(0);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMaHD.requestFocus();
    }

    private void calculateRefundPreview() {
        String maHD = txtMaHD.getText().trim();
        String maSP = txtMaSP.getText().trim();
        String soLuongText = txtSoLuong.getText().trim();

        if (maHD.isEmpty() || maSP.isEmpty() || soLuongText.isEmpty()) {
            txtTongTienHoan.setText("0đ");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongText);
        } catch (Exception e) {
            txtTongTienHoan.setText("0đ");
            return;
        }

        if (soLuong <= 0) {
            txtTongTienHoan.setText("0đ");
            return;
        }

        List<ChiTietHoaDonDTO> list = chiTietHoaDonDAO.findByMaHD(maHD);
        if (list == null || list.isEmpty()) {
            txtTongTienHoan.setText("0đ");
            return;
        }

        for (ChiTietHoaDonDTO ct : list) {
            if (ct.getMaSP() != null && ct.getMaSP().equalsIgnoreCase(maSP) && ct.getDonGia() != null) {
                BigDecimal tongTien = ct.getDonGia().multiply(new BigDecimal(soLuong));
                txtTongTienHoan.setText(formatMoney(tongTien));
                return;
            }
        }

        txtTongTienHoan.setText("0đ");
    }

    private String generateReturnId() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "DT" + LocalDateTime.now().format(fmt);
    }

    private String getNhanVienName() {
        if (session == null || session.getNhanVien() == null) {
            return "";
        }
        String ho = session.getNhanVien().getHo() == null ? "" : session.getNhanVien().getHo().trim();
        String ten = session.getNhanVien().getTen() == null ? "" : session.getNhanVien().getTen().trim();
        return (ho + " " + ten).trim();
    }

    private String safePdfText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("Đ", "D")
                .replace("đ", "d")
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
                "arc:8;focusWidth:0;innerFocusWidth:0");
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
                if (value instanceof BigDecimal) {
                    value = formatMoney((BigDecimal) value);
                }

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

                if (column == 5 || column == 6) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (column == 7) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
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
        if (value == null) {
            return "0đ";
        }
        return new DecimalFormat("#,###").format(value) + "đ";
    }

    @Override
    public void refreshData() {
        selectedStartDate = null;
        selectedEndDate = null;
        txtSelectedDate.setText("Tất cả");
        clear();
        loadTable();
    }
}