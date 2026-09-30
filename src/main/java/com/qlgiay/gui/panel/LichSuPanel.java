package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.qlgiay.bus.LichSuBUS;
import com.qlgiay.dto.LichSuDTO;
import com.qlgiay.util.ScrollUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LichSuPanel extends JPanel implements IRefreshable {
    private static final String GIAO_DICH = "Giao dịch";
    private static final String DANH_MUC = "Danh mục";
    private static final String HE_THONG = "Hệ thống";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LichSuBUS lichSuBUS = new LichSuBUS();

    private DatePicker dpTuNgay;
    private DatePicker dpDenNgay;
    private DefaultTableModel modelGiaoDich;
    private DefaultTableModel modelDanhMuc;
    private DefaultTableModel modelHeThong;

    public LichSuPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        DatePickerSettings fromSettings = new DatePickerSettings();
        fromSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dpTuNgay = new DatePicker(fromSettings);

        DatePickerSettings toSettings = new DatePickerSettings();
        toSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dpDenNgay = new DatePicker(toSettings);

        dpTuNgay.setPreferredSize(new Dimension(125, 32));
        dpDenNgay.setPreferredSize(new Dimension(125, 32));
        dpTuNgay.putClientProperty(FlatClientProperties.STYLE, "arc:8; focusWidth:0;");
        dpDenNgay.putClientProperty(FlatClientProperties.STYLE, "arc:8; focusWidth:0;");

        left.add(new JLabel("Từ ngày"));
        left.add(dpTuNgay);
        left.add(new JLabel("Đến ngày"));
        left.add(dpDenNgay);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnLoc = new JButton("Lọc");
        JButton btnLamMoi = new JButton("Làm mới");
        styleActionButton(btnLoc, true);
        styleActionButton(btnLamMoi, false);
        btnLoc.addActionListener(e -> loadData());
        btnLamMoi.addActionListener(e -> refreshData());

        right.add(btnLoc);
        right.add(btnLamMoi);
        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane createTabs() {
        modelGiaoDich = createTableModel();
        modelDanhMuc = createTableModel();
        modelHeThong = createTableModel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(GIAO_DICH, createTableScrollPane(modelGiaoDich));
        tabs.addTab(DANH_MUC, createTableScrollPane(modelDanhMuc));
        tabs.addTab(HE_THONG, createTableScrollPane(modelHeThong));
        return tabs;
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {
                "Mã log", "Mã NV", "Phân loại", "Hành động",
                "Đối tượng", "Thời gian", "Chi tiết"
        };
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JScrollPane createTableScrollPane(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int[] widths = { 65, 75, 100, 120, 125, 150, 300 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        ScrollUtil.applySmoothScroll(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        return scrollPane;
    }

    private void loadData() {
        LocalDate tuNgay = getTuNgay();
        LocalDate denNgay = getDenNgay();
        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Từ ngày không được lớn hơn đến ngày!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadCategory(GIAO_DICH, modelGiaoDich, tuNgay, denNgay);
        loadCategory(DANH_MUC, modelDanhMuc, tuNgay, denNgay);
        loadCategory(HE_THONG, modelHeThong, tuNgay, denNgay);
    }

    private void loadCategory(String category, DefaultTableModel model, LocalDate tuNgay, LocalDate denNgay) {
        model.setRowCount(0);
        List<LichSuDTO> logs = lichSuBUS.findByPhanLoai(category, tuNgay, denNgay);
        for (LichSuDTO log : logs) {
            model.addRow(new Object[] {
                    log.getMaLog(),
                    log.getMaNV(),
                    log.getPhanLoai(),
                    log.getHanhDong(),
                    log.getDoiTuong(),
                    formatDateTime(log.getThoiGian()),
                    log.getChiTiet()
            });
        }
    }

    private LocalDate getTuNgay() {
        LocalDate value = dpTuNgay.getDate();
        return value == null ? LocalDate.now().minusDays(7) : value;
    }

    private LocalDate getDenNgay() {
        LocalDate value = dpDenNgay.getDate();
        return value == null ? LocalDate.now() : value;
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private void loadDefaultFilter() {
        LocalDate now = LocalDate.now();
        dpTuNgay.setDate(now.minusDays(7));
        dpDenNgay.setDate(now);
    }

    private void styleActionButton(JButton button, boolean primary) {
        button.putClientProperty(
                FlatClientProperties.STYLE,
                primary
                        ? "arc:10; focusWidth:0; innerFocusWidth:0; margin:4,10,4,10; background:#E8F5E9; foreground:#2E7D32; hoverBackground:#D7F0DB; pressedBackground:#C2E7C8"
                        : "arc:10; focusWidth:0; innerFocusWidth:0; margin:4,10,4,10; background:#E8F0FE; foreground:#005A9E; hoverBackground:#DCE8FC; pressedBackground:#C9DCF8");
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean selected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component component = super.getTableCellRendererComponent(
                        table, value, selected, hasFocus, row, column);
                component.setBackground(selected ? new Color(232, 240, 254) : Color.WHITE);
                component.setForeground(selected ? new Color(0, 90, 158) : Color.BLACK);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return component;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    @Override
    public void refreshData() {
        loadDefaultFilter();
        loadData();
    }
}
