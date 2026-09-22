package com.qlgiay.gui.panel;

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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.SanPhamBUS;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.util.IconUtil;

public class SanPhamPanel extends JPanel implements IRefreshable {
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtSearch;
    private JComboBox<String> cboFilterLoai;
    private JComboBox<String> cboFilterGia;
    private JComboBox<String> cboFilterTrangThai;

    private JTextField txtMa;
    private JTextField txtTen;
    private JComboBox<String> cboLoai;
    private JTextField txtDonVi;
    private JTextField txtSoLuong;
    private JTextField txtGiaNhap;
    private JTextField txtLoiNhuan;
    private JTextField txtDonGia;
    private JTextField txtGiaKhuyenMai;
    private JTextField txtMau;
    private JTextField txtSize;
    private JTextField txtChatLieu;
    private JTextField txtThuongHieu;
    private JTextField txtNuocSX;
    private JComboBox<String> cboTrangThai;

    private JSpinner spNgaySX;
    private JTextArea txtMoTa;

    private JLabel lblMainImage;
    private JPanel pnlThumbnails;
    private final List<String> imageList = new ArrayList<>();
    private int currentImageIndex = -1;
    private int hoverRow = -1;

    private static final int MAIN_IMAGE_SIZE = 120;
    private static final int THUMBNAIL_SIZE = 35;

    public SanPhamPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createFormPanel()
        );
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập nội dung tìm kiếm...");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { loadTable(); }

            @Override
            public void removeUpdate(DocumentEvent e) { loadTable(); }

            @Override
            public void changedUpdate(DocumentEvent e) { loadTable(); }
        });

        cboFilterLoai = new JComboBox<>(new String[]{
                "Tất cả loại", "Giày Sneaker", "Giày Chạy Bộ"
        });

        cboFilterGia = new JComboBox<>(new String[]{
                "Tất cả giá", "Dưới 500.000", "500.000 - 1.000.000", "1.000.000 - 2.000.000", "Trên 2.000.000"
        });

        cboFilterTrangThai = new JComboBox<>(new String[]{
                "Tất cả trạng thái", "Hoạt động", "Ngừng bán"
        });

        styleComboBox(cboFilterLoai);
        styleComboBox(cboFilterGia);
        styleComboBox(cboFilterTrangThai);

        Dimension comboSize = new Dimension(120, 32);
        cboFilterLoai.setPreferredSize(comboSize);
        cboFilterGia.setPreferredSize(comboSize);
        cboFilterTrangThai.setPreferredSize(comboSize);

        cboFilterLoai.addActionListener(e -> loadTable());
        cboFilterGia.addActionListener(e -> loadTable());
        cboFilterTrangThai.addActionListener(e -> loadTable());

       


        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(cboFilterLoai);
        pnlFilters.add(cboFilterGia);
        pnlFilters.add(cboFilterTrangThai);
       
       
        p.add(txtSearch, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createTablePanel() {
        String[] cols = {
                "Mã SP", "Tên sản phẩm", "Loại", "Brand",
                "Size", "Màu", "SL", "Giá", "Trạng thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        table.getColumnModel().getColumn(8).setPreferredWidth(90);

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

        addGridRow(contentPanel, gbc, row++, field("Mã sản phẩm", txtMa), field("Tên sản phẩm", txtTen));
        addGridRow(contentPanel, gbc, row++, field("Loại sản phẩm", cboLoai), field("Đơn vị", txtDonVi));
        addGridRow(contentPanel, gbc, row++, field("Số lượng", txtSoLuong), field("Giá nhập", txtGiaNhap));
        addGridRow(contentPanel, gbc, row++, field("% lợi nhuận", txtLoiNhuan), field("Giá bán", txtDonGia));
        addGridRow(contentPanel, gbc, row++, field("Giá khuyến mãi", txtGiaKhuyenMai), field("Màu sắc", txtMau));
        addGridRow(contentPanel, gbc, row++, field("Size", txtSize), field("Chất liệu", txtChatLieu));
        addGridRow(contentPanel, gbc, row++, field("Thương hiệu", txtThuongHieu), field("Nước sản xuất", txtNuocSX));
        addGridRow(contentPanel, gbc, row++, field("Ngày sản xuất", spNgaySX), field("Trạng thái", cboTrangThai));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        contentPanel.add(buildMoTaPanel(), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(buildImageSection(), gbc);

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
        txtDonVi = new JTextField();
        txtSoLuong = new JTextField();
        txtGiaNhap = new JTextField();
        txtLoiNhuan = new JTextField();
        txtDonGia = new JTextField();
        txtGiaKhuyenMai = new JTextField();
        txtMau = new JTextField();
        txtSize = new JTextField();
        txtChatLieu = new JTextField();
        txtThuongHieu = new JTextField();
        txtNuocSX = new JTextField();

        txtGiaNhap.getDocument().addDocumentListener(createPricingListener());
        txtLoiNhuan.getDocument().addDocumentListener(createPricingListener());

        cboLoai = new JComboBox<>(new String[]{
                "Giày Sneaker", "Giày Chạy Bộ"
        });

        spNgaySX = new JSpinner(new SpinnerDateModel());
        spNgaySX.setEditor(new JSpinner.DateEditor(spNgaySX, "yyyy-MM-dd"));
        spNgaySX.putClientProperty(FlatClientProperties.STYLE, "arc:8; focusWidth:0;");

        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        cboTrangThai = new JComboBox<>(new String[]{
                "Hoạt động", "Ngừng bán"
        });

        styleComboBox(cboLoai);
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

    private JPanel buildMoTaPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(new JLabel("Mô tả chi tiết"), BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(txtMoTa);
        sp.setPreferredSize(new Dimension(0, 55));
        sp.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildImageSection() {
        JPanel wrap = new JPanel(new BorderLayout(8, 6));
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(0, 240));
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Hình ảnh"
        );
        border.setTitleColor(new Color(100, 100, 100));

        wrap.setBorder(BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(4, 8, 4, 8)
        ));

        lblMainImage = new JLabel();
        lblMainImage.setPreferredSize(new Dimension(MAIN_IMAGE_SIZE, MAIN_IMAGE_SIZE));
        lblMainImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMainImage.setBackground(Color.WHITE);
        lblMainImage.setOpaque(true);
        lblMainImage.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        lblMainImage.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblMainImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentImageIndex >= 0 && currentImageIndex < imageList.size()) {
                    previewImage(imageList.get(currentImageIndex));
                }
            }
        });

        JPanel pnlMainImg = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlMainImg.setOpaque(false);
        pnlMainImg.add(lblMainImage);
        wrap.add(pnlMainImg, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout(0, 4));
        pnlBottom.setOpaque(false);

        pnlThumbnails = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pnlThumbnails.setOpaque(false);

        JScrollPane scrollThumb = new JScrollPane(pnlThumbnails);
        scrollThumb.setPreferredSize(new Dimension(0, THUMBNAIL_SIZE + 10));
        scrollThumb.setBorder(null);
        scrollThumb.setOpaque(false);
        scrollThumb.getViewport().setOpaque(false);
        scrollThumb.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollThumb.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        pnlBottom.add(scrollThumb, BorderLayout.CENTER);

        JPanel pnlImageActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pnlImageActions.setOpaque(false);

        JButton btnAddImg = new JButton("Thêm ảnh");
        JButton btnDelImg = new JButton("Xóa ảnh");

        styleActionButton(btnAddImg, "success");
        styleActionButton(btnDelImg, "danger");

        btnAddImg.addActionListener(e -> chooseMultiImages());
        btnDelImg.addActionListener(e -> removeCurrentImage());

        pnlImageActions.add(btnAddImg);
        pnlImageActions.add(btnDelImg);

        pnlBottom.add(pnlImageActions, BorderLayout.SOUTH);
        wrap.add(pnlBottom, BorderLayout.SOUTH);

        renderThumbnails();

        return wrap;
    }

    private void chooseMultiImages() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();

            for (File file : files) {
                String path = file.getAbsolutePath();
                if (!imageList.contains(path)) {
                    imageList.add(path);
                }
            }

            if (!imageList.isEmpty() && currentImageIndex == -1) {
                currentImageIndex = 0;
            }

            renderThumbnails();
            showMainImage();
        }
    }

    private void removeCurrentImage() {
        if (imageList.isEmpty() || currentImageIndex < 0) {
            return;
        }

        imageList.remove(currentImageIndex);

        if (imageList.isEmpty()) {
            currentImageIndex = -1;
        } else if (currentImageIndex >= imageList.size()) {
            currentImageIndex = imageList.size() - 1;
        }

        renderThumbnails();
        showMainImage();
    }

    private void renderThumbnails() {
        pnlThumbnails.removeAll();

        for (int i = 0; i < imageList.size(); i++) {
            String imgRef = imageList.get(i);

            JLabel thumb = new JLabel();
            thumb.setPreferredSize(new Dimension(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
            thumb.setBorder(BorderFactory.createLineBorder(
                    i == currentImageIndex ? new Color(0, 90, 158) : Color.LIGHT_GRAY,
                    i == currentImageIndex ? 2 : 1
            ));
            thumb.setCursor(new Cursor(Cursor.HAND_CURSOR));

            ImageIcon icon = loadImageIcon(imgRef, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            if (icon != null) {
                thumb.setIcon(icon);
            }

            final int idx = i;
            thumb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    currentImageIndex = idx;
                    renderThumbnails();
                    showMainImage();
                }
            });

            pnlThumbnails.add(thumb);
        }

        pnlThumbnails.revalidate();
        pnlThumbnails.repaint();
    }

    private void showMainImage() {
        if (currentImageIndex < 0 || currentImageIndex >= imageList.size()) {
            lblMainImage.setIcon(null);
            return;
        }

        ImageIcon icon = loadImageIcon(imageList.get(currentImageIndex), MAIN_IMAGE_SIZE, MAIN_IMAGE_SIZE);
        lblMainImage.setIcon(icon);
    }

    private ImageIcon loadImageIcon(String imageRef, int width, int height) {
        if (imageRef == null || imageRef.isBlank()) {
            return null;
        }

        File file = new File(imageRef);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(imageRef);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }

        URL url = getClass().getResource("/images/products/" + imageRef);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }

        return null;
    }

    private void loadTable() {
        String keyword = txtSearch.getText();
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        String loai = String.valueOf(cboFilterLoai.getSelectedItem());
        String mucGia = String.valueOf(cboFilterGia.getSelectedItem());
        String tt = String.valueOf(cboFilterTrangThai.getSelectedItem());

        tableModel.setRowCount(0);
        List<SanPhamDTO> list = sanPhamBUS.search(keyword);

        if (list != null) {
            for (SanPhamDTO sp : list) {
                if (!"Tất cả loại".equals(loai) && (sp.getLoaiSP() == null || !sp.getLoaiSP().equals(loai))) {
                    continue;
                }

                if (!matchGia(sp.getDonGia(), mucGia)) {
                    continue;
                }

                if ("Hoạt động".equals(tt) && sp.getTrangThai() != 1) {
                    continue;
                }

                if ("Ngừng bán".equals(tt) && sp.getTrangThai() != 0) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        sp.getMaSP(),
                        sp.getTenSP(),
                        sp.getLoaiSP(),
                        sp.getThuongHieu(),
                        sp.getSize(),
                        sp.getMauSac(),
                        sp.getSoLuong(),
                        sp.getDonGia() == null ? "" : formatMoney(sp.getDonGia()),
                        sp.getTrangThai() == 1 ? "Hoạt động" : "Ngừng bán"
                });
            }
        }
    }

    private boolean matchGia(BigDecimal donGia, String mucGia) {
        if (donGia == null || mucGia == null || "Tất cả giá".equals(mucGia)) {
            return true;
        }

        BigDecimal gia500 = new BigDecimal("500000");
        BigDecimal gia1000 = new BigDecimal("1000000");
        BigDecimal gia2000 = new BigDecimal("2000000");

        return switch (mucGia) {
            case "Dưới 500.000" -> donGia.compareTo(gia500) < 0;
            case "500.000 - 1.000.000" ->
                    donGia.compareTo(gia500) >= 0 && donGia.compareTo(gia1000) <= 0;
            case "1.000.000 - 2.000.000" ->
                    donGia.compareTo(gia1000) > 0 && donGia.compareTo(gia2000) <= 0;
            case "Trên 2.000.000" -> donGia.compareTo(gia2000) > 0;
            default -> true;
        };
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));

        SanPhamDTO sp = sanPhamBUS.findById(ma);
        if (sp == null) {
            return;
        }

        txtMa.setText(sp.getMaSP());
        txtTen.setText(sp.getTenSP());
        cboLoai.setSelectedItem(sp.getLoaiSP());
        txtDonVi.setText(sp.getDonViTinh());
        txtSoLuong.setText(String.valueOf(sp.getSoLuong()));
        txtGiaNhap.setText(sp.getGiaNhap() == null ? "" : formatInputMoney(sp.getGiaNhap()));
        txtLoiNhuan.setText(sp.getPhanTramLoiNhuan() == null ? "" : sp.getPhanTramLoiNhuan().stripTrailingZeros().toPlainString());
        txtDonGia.setText(sp.getDonGia() == null ? "" : formatInputMoney(sp.getDonGia()));
        txtGiaKhuyenMai.setText(sp.getGiaKhuyenMai() == null ? "" : formatInputMoney(sp.getGiaKhuyenMai()));
        txtMau.setText(sp.getMauSac());
        txtSize.setText(sp.getSize());
        txtChatLieu.setText(sp.getChatLieu());
        txtThuongHieu.setText(sp.getThuongHieu());
        txtNuocSX.setText(sp.getNuocSanXuat());
        txtMoTa.setText(sp.getMoTa());
        cboTrangThai.setSelectedIndex(sp.getTrangThai() == 1 ? 0 : 1);

        if (sp.getNgaySanXuat() != null) {
            spNgaySX.setValue(Date.from(
                    sp.getNgaySanXuat()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
            ));
        }

        imageList.clear();
        currentImageIndex = -1;

        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isBlank()) {
            String[] arr = sp.getHinhAnh().split(",");
            for (String s : arr) {
                if (!s.trim().isEmpty()) {
                    imageList.add(s.trim());
                }
            }

            if (!imageList.isEmpty()) {
                currentImageIndex = 0;
            }
        }

        renderThumbnails();
        showMainImage();

        txtMa.setEnabled(false);
    }

    private SanPhamDTO readForm() {
        try {
            SanPhamDTO sp = new SanPhamDTO();

            sp.setMaSP(txtMa.getText().trim());
            sp.setTenSP(txtTen.getText().trim());
            sp.setLoaiSP((String) cboLoai.getSelectedItem());
            sp.setDonViTinh(txtDonVi.getText().trim());
            sp.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));

            BigDecimal giaNhap = txtGiaNhap.getText().trim().isEmpty() ? null : new BigDecimal(txtGiaNhap.getText().trim().replace(".", "").replace(",", ""));
            BigDecimal tyLeLoiNhuan = txtLoiNhuan.getText().trim().isEmpty() ? null : new BigDecimal(txtLoiNhuan.getText().trim());
            BigDecimal giaBan = txtDonGia.getText().trim().isEmpty() ? null : new BigDecimal(txtDonGia.getText().trim().replace(".", "").replace(",", ""));
            BigDecimal giaKhuyenMai = txtGiaKhuyenMai.getText().trim().isEmpty() ? null : new BigDecimal(txtGiaKhuyenMai.getText().trim().replace(".", "").replace(",", ""));

            if (giaBan == null && giaNhap != null && tyLeLoiNhuan != null) {
                giaBan = SanPhamDTO.tinhGiaBan(giaNhap, tyLeLoiNhuan);
            }

            sp.setGiaNhap(giaNhap);
            sp.setPhanTramLoiNhuan(tyLeLoiNhuan);
            sp.setDonGia(giaBan);
            sp.setGiaKhuyenMai(giaKhuyenMai);
            sp.setMauSac(txtMau.getText().trim());
            sp.setSize(txtSize.getText().trim());
            sp.setChatLieu(txtChatLieu.getText().trim());
            sp.setThuongHieu(txtThuongHieu.getText().trim());
            sp.setNuocSanXuat(txtNuocSX.getText().trim());
            sp.setMoTa(txtMoTa.getText().trim());

            Date d = (Date) spNgaySX.getValue();
            sp.setNgaySanXuat(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            List<String> savedNames = new ArrayList<>();
            for (String path : imageList) {
                if (path.contains(":") || path.contains("/") || path.contains("\\")) {
                    savedNames.add(copyImageToResources(path));
                } else {
                    savedNames.add(path);
                }
            }
            sp.setHinhAnh(String.join(",", savedNames));

            sp.setTrangThai(cboTrangThai.getSelectedIndex() == 0 ? 1 : 0);

            return sp;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Dữ liệu không hợp lệ. Vui lòng kiểm tra các ô số/tiền.",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
    }

    private boolean validateForm(SanPhamDTO sp) {
        if (sp.getMaSP() == null || sp.getMaSP().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Mã sản phẩm không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            txtMa.requestFocus();
            return false;
        }

        if (sp.getTenSP() == null || sp.getTenSP().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tên sản phẩm không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            txtTen.requestFocus();
            return false;
        }

        if (sp.getDonViTinh() == null || sp.getDonViTinh().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Đơn vị không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            txtDonVi.requestFocus();
            return false;
        }

        if (sp.getSoLuong() < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Số lượng không được âm!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            txtSoLuong.requestFocus();
            return false;
        }

        if (sp.getDonGia() == null || sp.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Đơn giá phải lớn hơn 0!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            txtDonGia.requestFocus();
            return false;
        }

        return true;
    }

    private void add() {
        SanPhamDTO sp = readForm();
        if (sp == null) {
            return;
        }

        if (!validateForm(sp)) {
            return;
        }

        if (sanPhamBUS.findById(sp.getMaSP()) != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Mã sản phẩm này đã tồn tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (sanPhamBUS.addProduct(sp)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Thêm sản phẩm thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Thêm sản phẩm thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void update() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn sản phẩm cần sửa trên bảng!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SanPhamDTO sp = readForm();
        if (sp == null) {
            return;
        }

        if (!validateForm(sp)) {
            return;
        }

        if (sanPhamBUS.updateProduct(sp)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cập nhật sản phẩm thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
            loadTable();
            clear();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Cập nhật sản phẩm thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void delete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn sản phẩm cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String ma = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String ten = String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa sản phẩm [" + ten + "] không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (sanPhamBUS.deleteProduct(ma)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Đã xóa sản phẩm thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
                loadTable();
                clear();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Xóa sản phẩm thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void exportExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("DanhSachSanPham.xlsx"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xlsx");
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sản Phẩm");

            String[] columns = {
                    "Mã SP", "Tên Sản Phẩm", "Loại", "Thương Hiệu",
                    "Size", "Màu Sắc", "Số Lượng", "Đơn Giá", "Trạng Thái"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            CellStyle moneyStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

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
                row.createCell(4).setCellValue(String.valueOf(tableModel.getValueAt(i, 4)));
                row.createCell(5).setCellValue(String.valueOf(tableModel.getValueAt(i, 5)));

                String slStr = String.valueOf(tableModel.getValueAt(i, 6)).replaceAll("[^\\d-]", "");
                int soLuong = slStr.isEmpty() ? 0 : Integer.parseInt(slStr);
                row.createCell(6).setCellValue(soLuong);

                String giaStr = String.valueOf(tableModel.getValueAt(i, 7)).replace("đ", "").replace(",", "").trim();
                double gia = giaStr.isEmpty() ? 0 : Double.parseDouble(giaStr);
                Cell giaCell = row.createCell(7);
                giaCell.setCellValue(gia);
                giaCell.setCellStyle(moneyStyle);

                row.createCell(8).setCellValue(String.valueOf(tableModel.getValueAt(i, 8)));
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
                    JOptionPane.YES_NO_OPTION
            );

            if (open == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToSave);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi xuất file Excel: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void importExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file Excel để nhập");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));

            if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fileChooser.getSelectedFile();
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int successCount = 0;
            int updateCount = 0;
            int rowCount = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                try {
                    String maSP = getCellValue(row.getCell(0));
                    if (maSP.isEmpty()) {
                        continue;
                    }

                    SanPhamDTO sp = new SanPhamDTO();
                    sp.setMaSP(maSP);
                    sp.setTenSP(getCellValue(row.getCell(1)));
                    sp.setLoaiSP(getCellValue(row.getCell(2)));
                    sp.setThuongHieu(getCellValue(row.getCell(3)));
                    sp.setSize(getCellValue(row.getCell(4)));
                    sp.setMauSac(getCellValue(row.getCell(5)));

                    String slStr = getCellValue(row.getCell(6)).replaceAll("[^\\d-]", "");
                    sp.setSoLuong(slStr.isEmpty() ? 0 : Integer.parseInt(slStr));

                    String giaStr = getCellValue(row.getCell(7)).replaceAll("[^\\d]", "");
                    sp.setDonGia(giaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(giaStr));

                    String ttStr = getCellValue(row.getCell(8));
                    sp.setTrangThai(ttStr.equalsIgnoreCase("Ngừng bán") ? 0 : 1);

                    sp.setDonViTinh("Đôi");
                    sp.setNgaySanXuat(LocalDate.now());
                    sp.setChatLieu("");
                    sp.setNuocSanXuat("");
                    sp.setMoTa("");
                    sp.setHinhAnh("");

                    SanPhamDTO old = sanPhamBUS.findById(maSP);
                    if (old != null) {
                        sp.setDonViTinh(old.getDonViTinh());
                        sp.setNgaySanXuat(old.getNgaySanXuat());
                        sp.setChatLieu(old.getChatLieu());
                        sp.setNuocSanXuat(old.getNuocSanXuat());
                        sp.setMoTa(old.getMoTa());
                        sp.setHinhAnh(old.getHinhAnh());

                        if (sanPhamBUS.updateProduct(sp)) {
                            updateCount++;
                        }
                    } else {
                        if (sanPhamBUS.addProduct(sp)) {
                            successCount++;
                        }
                    }

                    rowCount++;
                } catch (Exception ex) {
                    System.err.println("Lỗi đọc dòng " + row.getRowNum() + ": " + ex.getMessage());
                }
            }

            workbook.close();
            fis.close();
            loadTable();

            JOptionPane.showMessageDialog(
                    null,
                    "Nhập Excel thành công!\n- Số dòng đọc được: " + rowCount +
                            "\n- Thêm mới: " + successCount +
                            "\n- Cập nhật: " + updateCount,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Lỗi đọc file Excel. Vui lòng kiểm tra lại định dạng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue().trim();
                        case NUMERIC:
                            double formulaValue = cell.getNumericCellValue();
                            if (formulaValue == (long) formulaValue) {
                                return String.valueOf((long) formulaValue);
                            }
                            return String.valueOf(formulaValue);
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return "";
                    }
                } catch (Exception e) {
                    return "";
                }
            default:
                return "";
        }
    }

    private void clear() {
        txtMa.setEnabled(true);

        txtMa.setText("");
        txtTen.setText("");
        txtDonVi.setText("");
        txtSoLuong.setText("");
        txtGiaNhap.setText("");
        txtLoiNhuan.setText("");
        txtDonGia.setText("");
        txtGiaKhuyenMai.setText("");
        txtMau.setText("");
        txtSize.setText("");
        txtChatLieu.setText("");
        txtThuongHieu.setText("");
        txtNuocSX.setText("");
        txtMoTa.setText("");

        cboLoai.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        spNgaySX.setValue(new Date());

        imageList.clear();
        currentImageIndex = -1;

        renderThumbnails();
        showMainImage();

        hoverRow = -1;
        table.clearSelection();
        table.repaint();

        txtMa.requestFocus();
    }

    private DocumentListener createPricingListener() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recalcBasePrice(); }
            @Override public void removeUpdate(DocumentEvent e) { recalcBasePrice(); }
            @Override public void changedUpdate(DocumentEvent e) { recalcBasePrice(); }
        };
    }

    private void recalcBasePrice() {
        try {
            String giaNhapText = txtGiaNhap.getText() == null ? "" : txtGiaNhap.getText().trim();
            String loiNhuanText = txtLoiNhuan.getText() == null ? "" : txtLoiNhuan.getText().trim();

            if (giaNhapText.isEmpty() || loiNhuanText.isEmpty()) {
                return;
            }

            BigDecimal giaNhap = parseMoneyText(giaNhapText);
            BigDecimal loiNhuan = parsePercentText(loiNhuanText);

            if (giaNhap == null || loiNhuan == null || giaNhap.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            BigDecimal giaBan = SanPhamDTO.tinhGiaBan(giaNhap, loiNhuan);
            if (giaBan != null) {
                txtDonGia.setText(formatInputMoney(giaBan));
            }
        } catch (Exception ignored) {
            // không chặn nhập khi người dùng đang gõ
        }
    }

    private BigDecimal parseMoneyText(String text) {
        if (text == null || text.isBlank()) return null;
        String normalized = text.replace(".", "").replace(",", "");
        if (normalized.isBlank()) return null;
        try {
            return new BigDecimal(normalized);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parsePercentText(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return new BigDecimal(text.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String formatInputMoney(BigDecimal value) {
        if (value == null) return "";
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(value);
    }

    private void previewImage(String imageRef) {
        if (imageRef == null || imageRef.isBlank()) {
            return;
        }

        ImageIcon icon = loadImageIcon(imageRef, 500, 500);
        if (icon == null) {
            return;
        }

        JLabel lbl = new JLabel(icon);
        lbl.setBorder(new EmptyBorder(10, 10, 10, 10));

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(
                parentWindow,
                lbl,
                "Xem ảnh sản phẩm",
                JOptionPane.PLAIN_MESSAGE
        );
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

                if (column == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(new EmptyBorder(0, 8, 0, 8));
                } else if (column == 6 || column == 7) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setBorder(new EmptyBorder(0, 8, 0, 8));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setBorder(new EmptyBorder(0, 8, 0, 8));
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

    private String copyImageToResources(String sourcePath) {
        try {
            File sourceFile = new File(sourcePath);
            if (sourcePath.contains("src/main/resources/images/products")) {
                return sourceFile.getName();
            }

            String destDir = "src/main/resources/images/products/";
            File dir = new File(destDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = sourceFile.getName();
            File destFile = new File(destDir + fileName);

            Files.copy(
                    sourceFile.toPath(),
                    destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;
        } catch (Exception e) {
            System.err.println("Lỗi copy ảnh: " + e.getMessage());
            return sourcePath;
        }
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "";
        return new DecimalFormat("#,###").format(value) + "đ";
    }

    @Override
    public void refreshData() {
        loadTable();
        clear();
    }
}