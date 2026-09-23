package com.qlgiay.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.ThongKeBUS;
import com.qlgiay.dto.DoanhThuTheoNgayDTO;
import com.qlgiay.dto.LoaiSanPhamThongKeDTO;
import com.qlgiay.dto.LoiNhuanTheoKyDTO;
import com.qlgiay.dto.TopKhachHangDTO;
import com.qlgiay.dto.TopSanPhamDTO;

public class ThongKePanel extends JPanel implements IRefreshable {
    private final ThongKeBUS thongKeBUS = new ThongKeBUS();

    private JSpinner spTuNgay;
    private JSpinner spDenNgay;
    private JButton btnThongKe;
    private JButton btnLamMoi;
    private JComboBox<String> cboThang;
    private JComboBox<String> cboQuy;

    private JLabel lblTongTienVon;
    private JLabel lblTongDoanhThu;
    private JLabel lblLoiNhuan;
    private JLabel lblSoHoaDon;
    private JLabel lblTongSanPham;
    private JLabel lblSoKhachMua;

    private JPanel pnlChartContainer;
    private JPanel pnlPieChartContainer;

    private JTable tblTopSanPham;
    private DefaultTableModel modelTopSanPham;

    private JTable tblTopKhachHang;
    private DefaultTableModel modelTopKhachHang;

    private DefaultTableModel modelLoiNhuanThang;
    private DefaultTableModel modelLoiNhuanQuy;
    private List<LoiNhuanTheoKyDTO> loiNhuanTheoThang = new ArrayList<>();
    private List<LoiNhuanTheoKyDTO> loiNhuanTheoQuy = new ArrayList<>();

    private int hoverRowTopSP = -1;
    private int hoverRowTopKH = -1;

    public ThongKePanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setOpaque(false);

        main.add(createTopSection(), BorderLayout.NORTH);
        main.add(createCenterSection(), BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(main);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getViewport().setOpaque(false);
        add(mainScrollPane, BorderLayout.CENTER);

        loadDefaultFilter();
        refreshData();
    }

    private JPanel createTopSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        wrapper.add(createFilterPanel(), BorderLayout.NORTH);
        wrapper.add(createSummaryCards(), BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        spTuNgay = new JSpinner(new SpinnerDateModel());
        spDenNgay = new JSpinner(new SpinnerDateModel());

        spTuNgay.setEditor(new JSpinner.DateEditor(spTuNgay, "yyyy-MM-dd"));
        spDenNgay.setEditor(new JSpinner.DateEditor(spDenNgay, "yyyy-MM-dd"));

        spTuNgay.putClientProperty(FlatClientProperties.STYLE, "arc:8; focusWidth:0;");
        spDenNgay.putClientProperty(FlatClientProperties.STYLE, "arc:8; focusWidth:0;");

        Dimension dateSize = new Dimension(120, 32);
        spTuNgay.setPreferredSize(dateSize);
        spDenNgay.setPreferredSize(dateSize);

        left.add(new JLabel("Từ ngày"));
        left.add(spTuNgay);
        left.add(new JLabel("Đến ngày"));
        left.add(spDenNgay);

        cboThang = new JComboBox<>();
        cboQuy = new JComboBox<>();
        cboThang.setPreferredSize(new Dimension(110, 32));
        cboQuy.setPreferredSize(new Dimension(110, 32));
        cboThang.addActionListener(e -> updateProfitTables());
        cboQuy.addActionListener(e -> updateProfitTables());
        left.add(new JLabel("Tháng"));
        left.add(cboThang);
        left.add(new JLabel("Quý"));
        left.add(cboQuy);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        btnThongKe = new JButton("Xem thống kê");
        btnLamMoi = new JButton("Làm mới");

        styleActionButton(btnThongKe, "success");
        styleActionButton(btnLamMoi, "default");

        btnThongKe.setPreferredSize(new Dimension(120, 32));
        btnLamMoi.setPreferredSize(new Dimension(100, 32));

        btnThongKe.addActionListener(e -> loadData());
        btnLamMoi.addActionListener(e -> refreshData());

        right.add(btnThongKe);
        right.add(btnLamMoi);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    private JPanel createSummaryCards() {
        JPanel p = new JPanel(new GridLayout(2, 4, 10, 10));
        p.setOpaque(false);

        lblTongTienVon = new JLabel("0");
        lblTongDoanhThu = new JLabel("0");
        lblLoiNhuan = new JLabel("0");
        lblSoHoaDon = new JLabel("0");
        lblTongSanPham = new JLabel("0");
        lblSoKhachMua = new JLabel("0");

        p.add(createSummaryCard("Tiền vốn", lblTongTienVon));
        p.add(createSummaryCard("Tổng doanh thu", lblTongDoanhThu));
        p.add(createSummaryCard("Lợi nhuận", lblLoiNhuan));
        p.add(createSummaryCard("Số hóa đơn", lblSoHoaDon));
        p.add(createSummaryCard("Sản phẩm đã bán", lblTongSanPham));
        p.add(createSummaryCard("Khách mua hàng", lblSoKhachMua));

        return p;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 90, 158));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCenterSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel charts = new JPanel(new GridLayout(1, 2, 10, 0));
        charts.setOpaque(false);
        charts.setPreferredSize(new Dimension(0, 320));
        charts.add(createBarChartPanel());
        charts.add(createPieChartPanel());

        wrapper.add(charts, BorderLayout.CENTER);
        wrapper.add(createBottomTables(), BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createBarChartPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Biểu đồ doanh thu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        pnlChartContainer = new JPanel(new BorderLayout());
        pnlChartContainer.setOpaque(false);

        JLabel placeholder = new JLabel("Chưa có biểu đồ", SwingConstants.CENTER);
        placeholder.setForeground(new Color(130, 130, 130));
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlChartContainer.add(placeholder, BorderLayout.CENTER);

        card.add(title, BorderLayout.NORTH);
        card.add(pnlChartContainer, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPieChartPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Cơ cấu bán theo loại");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        pnlPieChartContainer = new JPanel(new BorderLayout());
        pnlPieChartContainer.setOpaque(false);

        JLabel placeholder = new JLabel("Chưa có biểu đồ", SwingConstants.CENTER);
        placeholder.setForeground(new Color(130, 130, 130));
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        pnlPieChartContainer.add(placeholder, BorderLayout.CENTER);

        card.add(title, BorderLayout.NORTH);
        card.add(pnlPieChartContainer, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBottomTables() {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setOpaque(false);
        p.setLayout(new GridLayout(2, 2, 10, 10));
        p.setPreferredSize(new Dimension(0, 530));

        p.add(createTopSanPhamCard());
        p.add(createTopKhachHangCard());
        p.add(createProfitTableCard("Lợi nhuận theo tháng", true));
        p.add(createProfitTableCard("Lợi nhuận theo quý", false));

        return p;
    }

    private JPanel createProfitTableCard(String title, boolean monthly) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String[] cols = { "Kỳ", "Doanh thu", "Tiền vốn", "Lợi nhuận" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        styleTable(table, false);

        if (monthly) {
            modelLoiNhuanThang = model;
        } else {
            modelLoiNhuanQuy = model;
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopSanPhamCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Top 5 sản phẩm bán chạy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String[] cols = { "Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu" };
        modelTopSanPham = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblTopSanPham = new JTable(modelTopSanPham);
        tblTopSanPham.setAutoCreateRowSorter(true);
        styleTable(tblTopSanPham, true);

        JScrollPane sp = new JScrollPane(tblTopSanPham);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));

        card.add(title, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTopKhachHangCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:15; background:#FFFFFF");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Top khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        String[] cols = { "Mã KH", "Tên khách hàng", "Số HĐ", "Chi tiêu" };
        modelTopKhachHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblTopKhachHang = new JTable(modelTopKhachHang);
        tblTopKhachHang.setAutoCreateRowSorter(true);
        styleTable(tblTopKhachHang, false);

        JScrollPane sp = new JScrollPane(tblTopKhachHang);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));

        card.add(title, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private void loadDefaultFilter() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);

        spTuNgay.setValue(Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        spDenNgay.setValue(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private LocalDate getTuNgay() {
        Date d = (Date) spTuNgay.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDate getDenNgay() {
        Date d = (Date) spDenNgay.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadData() {
        LocalDate tuNgay = getTuNgay();
        LocalDate denNgay = getDenNgay();

        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Từ ngày không được lớn hơn đến ngày!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadSummary(tuNgay, denNgay);
        loadTopSanPham(tuNgay, denNgay);
        loadTopKhachHang(tuNgay, denNgay);
        loadBarChart(tuNgay, denNgay);
        loadPieChart(tuNgay, denNgay);
        loadProfitTables(tuNgay, denNgay);
    }

    private void loadSummary(LocalDate tuNgay, LocalDate denNgay) {
        BigDecimal doanhThu = thongKeBUS.getTongDoanhThu(tuNgay, denNgay);
        BigDecimal tienVon = thongKeBUS.getTongTienVon(tuNgay, denNgay);
        lblTongTienVon.setText(formatMoney(tienVon));
        lblTongDoanhThu.setText(formatMoney(doanhThu));
        lblLoiNhuan.setText(formatMoney(doanhThu.subtract(tienVon)));
        lblSoHoaDon.setText(String.valueOf(thongKeBUS.getSoHoaDon(tuNgay, denNgay)));
        lblTongSanPham.setText(String.valueOf(thongKeBUS.getTongSanPhamBan(tuNgay, denNgay)));
        lblSoKhachMua.setText(String.valueOf(thongKeBUS.getSoKhachMua(tuNgay, denNgay)));
    }

    private void loadProfitTables(LocalDate tuNgay, LocalDate denNgay) {
        loiNhuanTheoThang = thongKeBUS.getLoiNhuanTheoThang(tuNgay, denNgay);
        loiNhuanTheoQuy = thongKeBUS.getLoiNhuanTheoQuy(tuNgay, denNgay);

        cboThang.removeAllItems();
        cboThang.addItem("Tất cả");
        for (LoiNhuanTheoKyDTO item : loiNhuanTheoThang) {
            cboThang.addItem(formatThang(item));
        }

        cboQuy.removeAllItems();
        cboQuy.addItem("Tất cả");
        for (LoiNhuanTheoKyDTO item : loiNhuanTheoQuy) {
            cboQuy.addItem(formatQuy(item));
        }

        updateProfitTables();
    }

    private void updateProfitTables() {
        if (modelLoiNhuanThang == null || modelLoiNhuanQuy == null) {
            return;
        }
        fillProfitTable(modelLoiNhuanThang, loiNhuanTheoThang, cboThang, true);
        fillProfitTable(modelLoiNhuanQuy, loiNhuanTheoQuy, cboQuy, false);
    }

    private void fillProfitTable(DefaultTableModel model, List<LoiNhuanTheoKyDTO> data,
            JComboBox<String> comboBox, boolean monthly) {
        model.setRowCount(0);
        String selected = comboBox.getSelectedItem() == null ? "Tất cả" : comboBox.getSelectedItem().toString();
        for (LoiNhuanTheoKyDTO item : data) {
            String period = monthly ? formatThang(item) : formatQuy(item);
            if (!"Tất cả".equals(selected) && !selected.equals(period)) {
                continue;
            }
            model.addRow(new Object[] {
                    period,
                    formatMoney(item.getDoanhThu()),
                    formatMoney(item.getTienVon()),
                    formatMoney(item.getLoiNhuan())
            });
        }
    }

    private String formatThang(LoiNhuanTheoKyDTO item) {
        return String.format("%04d-%02d", item.getNam(), item.getKy());
    }

    private String formatQuy(LoiNhuanTheoKyDTO item) {
        return String.format("%04d - Quý %d", item.getNam(), item.getKy());
    }

    private void loadTopSanPham(LocalDate tuNgay, LocalDate denNgay) {
        modelTopSanPham.setRowCount(0);

        for (TopSanPhamDTO item : thongKeBUS.getTopSanPham(tuNgay, denNgay)) {
            modelTopSanPham.addRow(new Object[] {
                    item.getMaSP(),
                    item.getTenSP(),
                    item.getSoLuongBan(),
                    formatMoney(item.getDoanhThu())
            });
        }
    }

    private void loadTopKhachHang(LocalDate tuNgay, LocalDate denNgay) {
        modelTopKhachHang.setRowCount(0);

        for (TopKhachHangDTO item : thongKeBUS.getTopKhachHang(tuNgay, denNgay)) {
            modelTopKhachHang.addRow(new Object[] {
                    item.getMaKH(),
                    item.getTenKH(),
                    item.getSoHoaDon(),
                    formatMoney(item.getTongChiTieu())
            });
        }
    }

    private void loadBarChart(LocalDate tuNgay, LocalDate denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (DoanhThuTheoNgayDTO item : thongKeBUS.getDoanhThuTheoNgay(tuNgay, denNgay)) {
            String ngay = String.format(
                    "%02d/%02d",
                    item.getNgay().getDayOfMonth(),
                    item.getNgay().getMonthValue());

            double doanhThu = item.getDoanhThu() == null ? 0 : item.getDoanhThu().doubleValue();
            dataset.addValue(doanhThu, "Doanh thu", ngay);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo ngày",
                "Ngày",
                "Doanh thu",
                dataset);

        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlinePaint(null);

        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));

        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(Color.WHITE);

        pnlChartContainer.removeAll();
        pnlChartContainer.add(chartPanel, BorderLayout.CENTER);
        pnlChartContainer.revalidate();
        pnlChartContainer.repaint();
    }

    private void loadPieChart(LocalDate tuNgay, LocalDate denNgay) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (LoaiSanPhamThongKeDTO item : thongKeBUS.getThongKeTheoLoai(tuNgay, denNgay)) {
            String loai = item.getLoaiSP() == null || item.getLoaiSP().isBlank() ? "Khác" : item.getLoaiSP();
            dataset.setValue(loai, item.getSoLuongBan());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Tỷ trọng bán theo loại sản phẩm",
                dataset,
                true,
                true,
                false);

        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        plot.setCircular(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(Color.WHITE);

        pnlPieChartContainer.removeAll();
        pnlPieChartContainer.add(chartPanel, BorderLayout.CENTER);
        pnlPieChartContainer.revalidate();
        pnlPieChartContainer.repaint();
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

    private void styleTable(JTable table, boolean isTopSanPham) {
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
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                int hoverRow = isTopSanPham ? hoverRowTopSP : hoverRowTopKH;

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

                if (column == 2 || column == 3) {
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
                if (isTopSanPham) {
                    if (row != hoverRowTopSP) {
                        hoverRowTopSP = row;
                        table.repaint();
                    }
                } else {
                    if (row != hoverRowTopKH) {
                        hoverRowTopKH = row;
                        table.repaint();
                    }
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (isTopSanPham) {
                    hoverRowTopSP = -1;
                } else {
                    hoverRowTopKH = -1;
                }
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
        loadDefaultFilter();
        loadData();
    }
}