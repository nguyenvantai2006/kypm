package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.BaoHanhBUS;
import com.qlgiay.bus.KhachHangBUS;
import com.qlgiay.bus.SanPhamBUS;
import com.qlgiay.dao.ChiTietHoaDonDAO;
import com.qlgiay.dao.HoaDonDAO;
import com.qlgiay.dao.PhieuBaoHanhDAO;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.dto.PhieuBaoHanhDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.IconUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BaoHanhPanel extends JPanel implements IRefreshable {
    private final BaoHanhBUS baoHanhBUS = new BaoHanhBUS();
    private final PhieuBaoHanhDAO phieuBaoHanhDAO = new PhieuBaoHanhDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterNgay;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMaPBH;
    private JTextField txtNgayNhan;
    private JTextField txtMaHD;
    private JTextField txtNgayTraDuKien;
    private JTextField txtMaSP;
    private JTextField txtMaKH;
    private JTextField txtChiPhiPhatSinh;
    private JComboBox<String> cboTrangThai;
    private JTextArea txtLoiCanBaoHanh;

    private int hoverRow = -1;

    public BaoHanhPanel() {
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
        txtSearch.putClientProperty("JTextField.placeholderText",
                "Nhập mã PBH, mã HĐ, mã SP, mã KH hoặc lỗi cần bảo hành...");

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

        cboFilterNgay = new JComboBox<>(new String[] {
                "Tất cả thời gian", "Hôm nay", "Tháng này"
        });
        cboFilterTrangThai = new JComboBox<>(new String[] {
                "Tất cả trạng thái", "Đang bảo hành", "Hoàn thành", "Hủy"
        });

        styleComboBox(cboFilterNgay);
        styleComboBox(cboFilterTrangThai);

        cboFilterNgay.setPreferredSize(new Dimension(140, 32));
        cboFilterTrangThai.setPreferredSize(new Dimension(150, 32));

        cboFilterNgay.addActionListener(e -> loadTable());
        cboFilterTrangThai.addActionListener(e -> loadTable());

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(cboFilterNgay);
        pnlFilters.add(cboFilterTrangThai);

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã PBH", "Mã HĐ", "Mã SP", "Mã KH", "Ngày nhận", "Ngày trả dự kiến", "Chi phí", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(115);
        table.getColumnModel().getColumn(1).setPreferredWidth(115);
        table.getColumnModel().getColumn(2).setPreferredWidth(65);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(4).setPreferredWidth(95);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
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

        addGridRow(contentPanel, gbc, row++, field("Mã phiếu bảo hành", txtMaPBH), field("Ngày nhận", txtNgayNhan));
        addGridRow(contentPanel, gbc, row++, field("Mã hóa đơn", txtMaHD), field("Ngày trả dự kiến", txtNgayTraDuKien));
        addGridRow(contentPanel, gbc, row++, field("Mã sản phẩm", txtMaSP), field("Mã khách hàng", txtMaKH));
        addGridRow(contentPanel, gbc, row++, field("Chi phí phát sinh", txtChiPhiPhatSinh),
                field("Trạng thái", cboTrangThai));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(buildLoiPanel(), gbc);

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
        JButton btnPrint = new JButton("In phiếu");

        btnAdd.setIcon(IconUtil.loadPng("/icons/add.png", 24));
        btnUpdate.setIcon(IconUtil.loadPng("/icons/edit.png", 24));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 24));
        btnPrint.setIcon(IconUtil.loadPng("/icons/print.png", 24));

        btnAdd.setIconTextGap(4);
        btnUpdate.setIconTextGap(4);
        btnClear.setIconTextGap(4);
        btnPrint.setIconTextGap(4);

        styleActionButton(btnAdd, "success");
        styleActionButton(btnUpdate, "default");
        styleActionButton(btnClear, "default");
        styleActionButton(btnPrint, "default");

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnClear.addActionListener(e -> clear());
        btnPrint.addActionListener(e -> printWarranty());

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnPrint);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initFormComponents() {
        txtMaPBH = new JTextField();
        txtNgayNhan = new JTextField();
        txtMaHD = new JTextField();
        txtNgayTraDuKien = new JTextField();
        txtMaSP = new JTextField();
        txtMaKH = new JTextField();
        txtChiPhiPhatSinh = new JTextField();
        cboTrangThai = new JComboBox<>(new String[] { "Đang sửa chữa", "Hoàn thành", "Hủy" });
        txtLoiCanBaoHanh = new JTextArea(3, 20);
        txtLoiCanBaoHanh.setLineWrap(true);
        txtLoiCanBaoHanh.setWrapStyleWord(true);

        // KHÓA TOÀN BỘ READ ONLY
        txtMaPBH.setEnabled(false);
        txtNgayNhan.setEnabled(false);
        txtMaHD.setEnabled(false);
        txtNgayTraDuKien.setEnabled(false);
        txtMaSP.setEnabled(false);
        txtMaKH.setEnabled(false);
        txtChiPhiPhatSinh.setEnabled(false);
        cboTrangThai.setEnabled(false);
        txtLoiCanBaoHanh.setEnabled(false);
    }

    private void initFormData() {
        txtMaPBH.setText(generateWarrantyId());
        txtNgayNhan.setText(LocalDate.now().toString());
        txtNgayTraDuKien.setText(LocalDate.now().plusDays(7).toString());
        txtChiPhiPhatSinh.setText("0");
        cboTrangThai.setSelectedIndex(0);
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

    private JPanel buildLoiPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel("Lỗi cần bảo hành"), BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(txtLoiCanBaoHanh);
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

        String filterDate = String.valueOf(cboFilterNgay.getSelectedItem());
        String filterTrangThai = String.valueOf(cboFilterTrangThai.getSelectedItem());

        table.clearSelection();
        tableModel.setRowCount(0);

        List<PhieuBaoHanhDTO> list = keyword.isEmpty() ? phieuBaoHanhDAO.findAll() : baoHanhBUS.search(keyword);

        if (list != null) {
            LocalDate today = LocalDate.now();

            for (PhieuBaoHanhDTO pbh : list) {
                if (pbh.getNgayNhan() != null) {
                    if ("Hôm nay".equals(filterDate) && !pbh.getNgayNhan().equals(today)) {
                        continue;
                    }
                    if ("Tháng này".equals(filterDate)
                            && (pbh.getNgayNhan().getMonthValue() != today.getMonthValue()
                                    || pbh.getNgayNhan().getYear() != today.getYear())) {
                        continue;
                    }
                }

                if (!"Tất cả trạng thái".equals(filterTrangThai)) {
                    if (!mapTrangThaiIntToText(pbh.getTrangThai()).equals(filterTrangThai)) {
                        continue;
                    }
                }

                tableModel.addRow(new Object[] {
                        pbh.getMaPBH(),
                        pbh.getMaHD(),
                        pbh.getMaSP(),
                        pbh.getMaKH(),
                        pbh.getNgayNhan(),
                        pbh.getNgayTraDuKien(),
                        pbh.getChiPhiPhatSinh(),
                        mapTrangThaiIntToText(pbh.getTrangThai())
                });
            }
        }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= table.getRowCount()) {
            return;
        }

        String maPBH = String.valueOf(tableModel.getValueAt(row, 0));
        PhieuBaoHanhDTO pbh = phieuBaoHanhDAO.findById(maPBH);
        if (pbh == null) {
            return;
        }

        txtMaPBH.setText(pbh.getMaPBH());
        txtNgayNhan.setText(String.valueOf(pbh.getNgayNhan()));
        txtMaHD.setText(pbh.getMaHD());
        txtNgayTraDuKien.setText(pbh.getNgayTraDuKien() != null ? String.valueOf(pbh.getNgayTraDuKien()) : "");
        txtMaSP.setText(pbh.getMaSP());
        txtMaKH.setText(pbh.getMaKH());
        txtChiPhiPhatSinh.setText(formatInputMoney(pbh.getChiPhiPhatSinh()));
        txtLoiCanBaoHanh.setText(pbh.getLoiCanBaoHanh());
        cboTrangThai.setSelectedItem(mapTrangThaiIntToText(pbh.getTrangThai()));
    }

    private PhieuBaoHanhDTO readForm() {
        try {
            PhieuBaoHanhDTO pbh = new PhieuBaoHanhDTO();

            pbh.setMaPBH(txtMaPBH.getText().trim());
            pbh.setNgayNhan(LocalDate.parse(txtNgayNhan.getText().trim()));
            pbh.setMaHD(txtMaHD.getText().trim());
            pbh.setMaSP(txtMaSP.getText().trim());
            pbh.setMaKH(txtMaKH.getText().trim());

            String ngayTra = txtNgayTraDuKien.getText().trim();
            pbh.setNgayTraDuKien(ngayTra.isEmpty() ? null : LocalDate.parse(ngayTra));

            String chiPhi = txtChiPhiPhatSinh.getText().trim();
            pbh.setChiPhiPhatSinh(chiPhi.isEmpty() ? BigDecimal.ZERO : new BigDecimal(chiPhi));

            pbh.setLoiCanBaoHanh(txtLoiCanBaoHanh.getText().trim());
            pbh.setTrangThai(mapTrangThaiTextToInt(String.valueOf(cboTrangThai.getSelectedItem())));

            return pbh;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Dữ liệu không hợp lệ. Vui lòng kiểm tra ngày và chi phí phát sinh.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private boolean validateForm(PhieuBaoHanhDTO pbh, boolean isAdd) {
        if (pbh == null) {
            return false;
        }

        if (pbh.getMaPBH() == null || pbh.getMaPBH().isBlank()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã phiếu bảo hành không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (pbh.getMaHD() == null || pbh.getMaHD().isBlank()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã hóa đơn không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }

        HoaDonDTO hd = hoaDonDAO.findById(pbh.getMaHD());
        if (hd == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy hóa đơn này!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }

        if (hd.getMaKH() == null || hd.getMaKH().isBlank()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Hóa đơn khách lẻ không thể tạo phiếu bảo hành!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }

        if (pbh.getMaSP() == null || pbh.getMaSP().isBlank()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã sản phẩm không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        SanPhamDTO sp = sanPhamBUS.findById(pbh.getMaSP());
        if (sp == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy sản phẩm này!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        if (hd.getMaKH() != null && !hd.getMaKH().isBlank()) {
            if (pbh.getMaKH() == null || pbh.getMaKH().isBlank()) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Mã khách hàng không được để trống!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMaHD.requestFocus();
                return false;
            }

            KhachHangDTO kh = khachHangBUS.findById(pbh.getMaKH());
            if (kh == null) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Không tìm thấy khách hàng này!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMaHD.requestFocus();
                return false;
            }

            if (!hd.getMaKH().equalsIgnoreCase(pbh.getMaKH())) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Mã khách hàng không khớp với hóa đơn đã nhập!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                txtMaHD.requestFocus();
                return false;
            }
        }

        List<ChiTietHoaDonDTO> list = chiTietHoaDonDAO.findByMaHD(pbh.getMaHD());
        boolean exists = false;
        if (list != null) {
            for (ChiTietHoaDonDTO ct : list) {
                if (ct.getMaSP() != null && ct.getMaSP().equalsIgnoreCase(pbh.getMaSP())) {
                    exists = true;
                    break;
                }
            }
        }

        if (!exists) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Sản phẩm này không tồn tại trong hóa đơn đã nhập!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        if (pbh.getLoiCanBaoHanh() == null || pbh.getLoiCanBaoHanh().isBlank()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Lỗi cần bảo hành không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtLoiCanBaoHanh.requestFocus();
            return false;
        }

        if (pbh.getChiPhiPhatSinh() != null && pbh.getChiPhiPhatSinh().compareTo(BigDecimal.ZERO) < 0) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Chi phí phát sinh không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtChiPhiPhatSinh.requestFocus();
            return false;
        }

        if (pbh.getNgayNhan() != null && pbh.getNgayTraDuKien() != null
                && pbh.getNgayTraDuKien().isBefore(pbh.getNgayNhan())) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Ngày trả dự kiến không được trước ngày nhận!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            txtNgayTraDuKien.requestFocus();
            return false;
        }

        if (isAdd && baoHanhBUS.findById(pbh.getMaPBH()) != null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Mã phiếu bảo hành này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isAdd && baoHanhBUS.findById(pbh.getMaPBH()) == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy phiếu bảo hành để sửa!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void add() {
        PhieuBaoHanhDTO pbh = readForm();
        if (!validateForm(pbh, true)) {
            return;
        }

        if (baoHanhBUS.create(pbh)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm phiếu bảo hành thành công!");
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm phiếu bảo hành thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        PhieuBaoHanhDTO pbh = readForm();
        if (!validateForm(pbh, false)) {
            return;
        }

        if (baoHanhBUS.update(pbh)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật phiếu bảo hành thành công!");
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Cập nhật phiếu bảo hành thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printWarranty() {
        String maPBH = txtMaPBH.getText().trim();
        if (maPBH.isEmpty()) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn hoặc nhập phiếu bảo hành để in!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PhieuBaoHanhDTO pbh = phieuBaoHanhDAO.findById(maPBH);
        if (pbh == null) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Không tìm thấy phiếu bảo hành này!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        inPhieuBaoHanhPDF(pbh);
    }

    private void inPhieuBaoHanhPDF(PhieuBaoHanhDTO pbh) {
        try {
            String path = "PhieuBaoHanh_" + pbh.getMaPBH() + ".pdf";
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                    12, com.itextpdf.text.Font.BOLD);

            Paragraph title = new Paragraph("PHIEU BAO HANH\n\n", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Ma PBH: " + pbh.getMaPBH(), fontNormal));
            document.add(new Paragraph("Ma HD: " + safePdfText(pbh.getMaHD()), fontNormal));
            document.add(new Paragraph("Ma SP: " + safePdfText(pbh.getMaSP()), fontNormal));
            document.add(new Paragraph("Ma KH: " + safePdfText(pbh.getMaKH()), fontNormal));
            document.add(new Paragraph("Ngay nhan: " + pbh.getNgayNhan(), fontNormal));
            document.add(new Paragraph(
                    "Ngay tra du kien: " + (pbh.getNgayTraDuKien() == null ? "" : pbh.getNgayTraDuKien()), fontNormal));
            document.add(
                    new Paragraph("Trang thai: " + safePdfText(mapTrangThaiIntToText(pbh.getTrangThai())), fontNormal));
            document.add(
                    new Paragraph("Chi phi phat sinh: "
                            + new DecimalFormat("#,###")
                                    .format(pbh.getChiPhiPhatSinh() == null ? BigDecimal.ZERO : pbh.getChiPhiPhatSinh())
                            + " VND", fontBold));
            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));
            document.add(new Paragraph("Loi can bao hanh:", fontBold));
            document.add(new Paragraph(safePdfText(pbh.getLoiCanBaoHanh()), fontNormal));

            document.close();

            File file = new File(path);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Lỗi khi tạo file PDF!");
        }
    }

    private void clear() {
        txtMaPBH.setText(generateWarrantyId());
        txtNgayNhan.setText(LocalDate.now().toString());
        txtMaHD.setText("");
        txtNgayTraDuKien.setText(LocalDate.now().plusDays(7).toString());
        txtMaSP.setText("");
        txtMaKH.setText("");
        txtChiPhiPhatSinh.setText("0");
        txtLoiCanBaoHanh.setText("");
        cboTrangThai.setSelectedIndex(0);

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMaHD.requestFocus();
    }

    private void fillCustomerByInvoice() {
        String maHD = txtMaHD.getText().trim();
        if (maHD.isEmpty()) {
            txtMaKH.setText("");
            return;
        }

        HoaDonDTO hd = hoaDonDAO.findById(maHD);
        if (hd == null || hd.getMaKH() == null || hd.getMaKH().isBlank()) {
            txtMaKH.setText("");
            return;
        }

        txtMaKH.setText(hd.getMaKH());
    }

    private void formatChiPhiField() {
        String text = txtChiPhiPhatSinh.getText().trim();
        if (text.isEmpty()) {
            txtChiPhiPhatSinh.setText("0");
            return;
        }

        try {
            BigDecimal value = new BigDecimal(text);
            if (value.compareTo(BigDecimal.ZERO) < 0) {
                value = BigDecimal.ZERO;
            }
            txtChiPhiPhatSinh.setText(formatInputMoney(value));
        } catch (Exception e) {
            txtChiPhiPhatSinh.setText("0");
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

    private String generateWarrantyId() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "PBH" + LocalDateTime.now().format(fmt);
    }

    private String mapTrangThaiIntToText(int trangThai) {
        return switch (trangThai) {
            case 0 -> "Đang bảo hành";
            case 1 -> "Hoàn thành";
            case 2 -> "Hủy";
            default -> "Đang bảo hành";
        };
    }

    private int mapTrangThaiTextToInt(String text) {
        if ("Hoàn thành".equals(text)) {
            return 1;
        }
        if ("Hủy".equals(text)) {
            return 2;
        }
        return 0;
    }

    private String formatInputMoney(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        value = value.stripTrailingZeros();
        if (value.scale() < 0) {
            value = value.setScale(0);
        }
        return value.toPlainString();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0đ";
        }
        return new DecimalFormat("#,###").format(value) + "đ";
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

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:8;focusWidth:0;innerFocusWidth:0");
    }

    private void styleActionButton(JButton button, String type) {
        String style = switch (type) {
            case "success" ->
                "arc:10;focusWidth:0;innerFocusWidth:0;margin:4,6,4,6;background:#E8F5E9;foreground:#2E7D32;hoverBackground:#D7F0DB;pressedBackground:#C2E7C8";
            case "danger" ->
                "arc:10;focusWidth:0;innerFocusWidth:0;margin:4,6,4,6;background:#FDECEC;foreground:#C62828;hoverBackground:#F9D6D6;pressedBackground:#F4BDBD";
            default ->
                "arc:10;focusWidth:0;innerFocusWidth:0;margin:4,6,4,6;background:#E8F0FE;foreground:#005A9E;hoverBackground:#DCE8FC;pressedBackground:#C9DCF8";
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

                if (column == 6) {
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

    @Override
    public void refreshData() {
        txtSearch.setText("");
        cboFilterNgay.setSelectedIndex(0);
        cboFilterTrangThai.setSelectedIndex(0);
        clear();
        loadTable();
    }
}