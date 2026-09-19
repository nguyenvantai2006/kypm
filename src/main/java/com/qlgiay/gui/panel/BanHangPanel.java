package com.qlgiay.gui.panel;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.BanHangBUS;
import com.qlgiay.bus.KhachHangBUS;
import com.qlgiay.bus.SanPhamBUS;
import com.qlgiay.bus.VoucherBUS;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.ChiTietHoaDonDTO;
import com.qlgiay.dto.HoaDonDTO;
import com.qlgiay.dto.KhachHangDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.dto.VoucherDTO;
import com.qlgiay.gui.component.ProductGridPanel;
import com.qlgiay.gui.dialog.ThemKhachHangDialog;
import com.qlgiay.util.IconUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BanHangPanel extends JPanel implements IRefreshable {
    private static final BigDecimal DIEM_TO_VND = new BigDecimal("1000");
    private static final BigDecimal MAX_POINT_DISCOUNT_RATE = new BigDecimal("0.30");

    private final AuthSession session;
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final BanHangBUS banHangBUS = new BanHangBUS();
    private final VoucherBUS voucherBUS = new VoucherBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    private JTextField txtSearch;
    private ProductGridPanel productGridPanel;

    private JTable tableCart;
    private DefaultTableModel cartModel;

    private JTextField txtMaHD;
    private JTextField txtNgayLap;
    private JTextField txtNhanVien;
    private JComboBox<String> txtMaKH;
    private JComboBox<String> txtMaVoucher;
    private JTextField txtDiemDung;

    private JLabel lblTamTinh;
    private JLabel lblGiamGia;
    private JLabel lblTongTien;

    public BanHangPanel(AuthSession session) {
        this.session = session;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createRightPanel());
        split.setResizeWeight(0.62);
        split.setDividerSize(6);
        split.setOpaque(false);

        add(split, BorderLayout.CENTER);

        initInvoice();
        loadProducts();
    }

    private JPanel createLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.putClientProperty(FlatClientProperties.STYLE, "arc:15;background:#FFFFFF");
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(2, 0, 8, 0));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(-1, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã hoặc tên sản phẩm để tìm kiếm...");
        topBar.add(txtSearch, BorderLayout.CENTER);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadProducts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadProducts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadProducts();
            }
        });

        productGridPanel = new ProductGridPanel();
        productGridPanel.setProductClickListener(this::addProductToCart);

        JScrollPane scroll = new JScrollPane(productGridPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        p.add(topBar, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private JPanel createRightPanel() {
        JPanel formWrapper = new JPanel(new BorderLayout(0, 8));
        formWrapper.putClientProperty(FlatClientProperties.STYLE, "arc:15;background:#FFFFFF");
        formWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        formWrapper.setMinimumSize(new Dimension(400, 0));

        formWrapper.add(createInvoiceInfo(), BorderLayout.NORTH);
        formWrapper.add(createCartTable(), BorderLayout.CENTER);
        formWrapper.add(createBottomPanel(), BorderLayout.SOUTH);

        return formWrapper;
    }

    private JPanel createInvoiceInfo() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        txtMaHD = new JTextField();
        txtNgayLap = new JTextField();
        txtNhanVien = new JTextField();
        txtMaKH = createEditableComboBox("SĐT / Mã KH...");
        txtMaVoucher = createEditableComboBox("Mã voucher...");
        txtDiemDung = new JTextField();

        txtMaHD.setEnabled(false);
        txtNgayLap.setEnabled(false);
        txtNhanVien.setEnabled(false);
        txtDiemDung.setEnabled(false);

        txtDiemDung.putClientProperty("JTextField.placeholderText", "Số điểm...");

        txtDiemDung.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    validatePointsInput(true);
                }
            }
        });
        txtDiemDung.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePointsInput(true);
            }
        });

        loadCustomerOptions();
        loadVoucherOptions();

        DocumentListener calcListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateTotals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateTotals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateTotals();
            }
        };

        getComboEditor(txtMaKH).getDocument().addDocumentListener(calcListener);
        getComboEditor(txtMaVoucher).getDocument().addDocumentListener(calcListener);
        txtDiemDung.getDocument().addDocumentListener(calcListener);

        getComboEditor(txtMaKH).getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePointsFieldState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePointsFieldState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePointsFieldState();
            }
        });

        int row = 0;
        addGridRow(contentPanel, gbc, row++, field("Mã hóa đơn", txtMaHD), field("Ngày lập", txtNgayLap));
        addGridRow(contentPanel, gbc, row++, field("Nhân viên", txtNhanVien), customerField());
        addGridRow(contentPanel, gbc, row++, field("Voucher", txtMaVoucher), field("Sử dụng điểm", txtDiemDung));

        return contentPanel;
    }

    private JPanel customerField() {
        JPanel customerInput = new JPanel(new BorderLayout(5, 0));
        customerInput.setOpaque(false);

        JButton btnAddCustomer = new JButton("+");
        btnAddCustomer.setToolTipText("Thêm khách hàng mới");
        btnAddCustomer.setPreferredSize(new Dimension(34, 32));
        btnAddCustomer.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAddCustomer.setMargin(new Insets(0, 0, 0, 0));
        btnAddCustomer.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;focusWidth:0;background:#E8F0FE;foreground:#005A9E;hoverBackground:#D5E5FC");
        btnAddCustomer.addActionListener(e -> openAddCustomerDialog());

        customerInput.add(txtMaKH, BorderLayout.CENTER);
        customerInput.add(btnAddCustomer, BorderLayout.EAST);

        return field("Khách hàng", customerInput);
    }

    private JComboBox<String> createEditableComboBox(String placeholder) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        comboBox.setPreferredSize(new Dimension(0, 32));
        comboBox.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        comboBox.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;focusWidth:0;buttonBackground:#FFFFFF;buttonHoverBackground:#E8F0FE");
        return comboBox;
    }

    private JTextField getComboEditor(JComboBox<String> comboBox) {
        return (JTextField) comboBox.getEditor().getEditorComponent();
    }

    private String getComboText(JComboBox<String> comboBox) {
        Object value = comboBox.getEditor().getItem();
        return value == null ? "" : value.toString().trim();
    }

    private void loadCustomerOptions() {
        txtMaKH.removeAllItems();
        List<KhachHangDTO> customers = khachHangBUS.getAll();
        if (customers != null) {
            for (KhachHangDTO customer : customers) {
                if (customer.getTrangThai() == 1 && customer.getMaKH() != null) {
                    txtMaKH.addItem(customer.getMaKH());
                }
            }
        }
        getComboEditor(txtMaKH).setText("");
    }

    private void loadVoucherOptions() {
        txtMaVoucher.removeAllItems();
        List<VoucherDTO> vouchers = voucherBUS.getAllActive();
        if (vouchers != null) {
            for (VoucherDTO voucher : vouchers) {
                if (voucher.getMaVoucher() != null) {
                    txtMaVoucher.addItem(voucher.getMaVoucher());
                }
            }
        }
        getComboEditor(txtMaVoucher).setText("");
    }

    private void openAddCustomerDialog() {
        new ThemKhachHangDialog(SwingUtilities.getWindowAncestor(this), customer -> {
            getComboEditor(txtMaKH).setText(customer.getMaKH());
            updatePointsFieldState();
            getComboEditor(txtMaKH).requestFocusInWindow();
        }).setVisible(true);
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

    private JScrollPane createCartTable() {
        String[] cols = { "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền" };

        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableCart = new JTable(cartModel);
        tableCart.setRowHeight(34);
        tableCart.setShowGrid(false);
        tableCart.setIntercellSpacing(new Dimension(0, 0));
        tableCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tableCart.getColumnModel().getColumn(0).setMinWidth(0);
        tableCart.getColumnModel().getColumn(0).setMaxWidth(0);
        tableCart.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableCart.getColumnModel().getColumn(1).setPreferredWidth(180);
        tableCart.getColumnModel().getColumn(2).setPreferredWidth(45);
        tableCart.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableCart.getColumnModel().getColumn(4).setPreferredWidth(110);

        JTableHeader header = tableCart.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 247, 250));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (value instanceof BigDecimal) {
                    value = formatMoney((BigDecimal) value);
                }

                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(232, 240, 254));
                    c.setForeground(new Color(0, 90, 158));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));

                if (column == 2 || column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };

        for (int i = 1; i < tableCart.getColumnCount(); i++) {
            tableCart.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane sp = new JScrollPane(tableCart);
        sp.setPreferredSize(new Dimension(0, 200));
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)));

        return sp;
    }

    private JPanel createBottomPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel totals = new JPanel(new GridLayout(4, 2, 4, 6));
        totals.setOpaque(false);
        totals.setBorder(new EmptyBorder(10, 8, 15, 8));

        lblTamTinh = new JLabel("0đ", SwingConstants.RIGHT);
        lblGiamGia = new JLabel("0đ", SwingConstants.RIGHT);
        lblTongTien = new JLabel("0đ", SwingConstants.RIGHT);

        lblTamTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGiamGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setForeground(new Color(198, 40, 40));

        totals.add(new JLabel("Tạm tính:"));
        totals.add(lblTamTinh);

        totals.add(new JLabel("Giảm giá:"));
        totals.add(lblGiamGia);

        totals.add(new JLabel(""));

        JSeparator line = new JSeparator();
        line.setForeground(new Color(210, 210, 210));
        line.setPreferredSize(new Dimension(0, 2));

        totals.add(line);

        totals.add(new JLabel("<html><b style='font-size:14px'>THÀNH TIỀN:</b></html>"));
        totals.add(lblTongTien);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 4, 5, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnUpdate = new JButton("Sửa SL");
        JButton btnRemove = new JButton("Xóa SP");
        JButton btnClear = new JButton("Làm mới");
        JButton btnPay = new JButton("Thanh toán");

        btnUpdate.setIcon(IconUtil.loadPng("/icons/edit.png", 20));
        btnRemove.setIcon(IconUtil.loadPng("/icons/delete.png", 20));
        btnClear.setIcon(IconUtil.loadPng("/icons/refresh.png", 20));

        int gap = 4;
        btnUpdate.setIconTextGap(gap);
        btnRemove.setIconTextGap(gap);
        btnClear.setIconTextGap(gap);

        styleActionButton(btnUpdate, "default");
        styleActionButton(btnRemove, "danger");
        styleActionButton(btnClear, "default");
        styleActionButton(btnPay, "success");

        btnUpdate.addActionListener(e -> updateQuantity());
        btnRemove.addActionListener(e -> removeProduct());
        btnClear.addActionListener(e -> clearInvoice());
        btnPay.addActionListener(e -> checkout());

        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnRemove);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnPay);

        p.add(totals, BorderLayout.NORTH);
        p.add(pnlButtons, BorderLayout.SOUTH);

        return p;
    }

    private void initInvoice() {
        txtNhanVien.setText(safe(session.getNhanVien().getHo()) + " " + safe(session.getNhanVien().getTen()));
        txtNgayLap.setText(LocalDate.now().toString());
        txtMaHD.setText(generateInvoiceId());
    }

    private String generateInvoiceId() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "HD" + LocalDateTime.now().format(fmt);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String safePdfText(String s) {
        if (s == null) {
            return "";
        }
        return s
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

    private KhachHangDTO findKhachHang(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        KhachHangDTO kh = khachHangBUS.findById(input);
        if (kh != null && kh.getTrangThai() == 1) {
            return kh;
        }

        List<KhachHangDTO> allKH = khachHangBUS.getAll();
        if (allKH != null) {
            for (KhachHangDTO k : allKH) {
                if (k.getTrangThai() == 1 && input.equals(k.getSdt())) {
                    return k;
                }
            }
        }
        return null;
    }

    private void updatePointsFieldState() {
        KhachHangDTO customer = findKhachHang(getComboText(txtMaKH));
        boolean enabled = customer != null;
        txtDiemDung.setEnabled(enabled);

        if (customer == null) {
            txtDiemDung.putClientProperty("JTextField.placeholderText", "Số điểm...");
            txtDiemDung.setText("");
            return;
        }

        int maxPoints = customer.getDiemTichLuy();
        txtDiemDung.putClientProperty("JTextField.placeholderText", "Tối đa là " + maxPoints + " điểm");
    }

    private boolean validatePointsInput(boolean showWarning) {
        KhachHangDTO customer = findKhachHang(getComboText(txtMaKH));
        if (customer == null || !txtDiemDung.isEnabled()) {
            return true;
        }

        String pointsText = txtDiemDung.getText() == null ? "" : txtDiemDung.getText().trim();
        int points = 0;

        if (!pointsText.isEmpty()) {
            try {
                points = Integer.parseInt(pointsText);
                if (points < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                if (showWarning) {
                    JOptionPane.showMessageDialog(this,
                            "Số điểm sử dụng không hợp lệ.",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                return false;
            }
        }

        if (points > customer.getDiemTichLuy()) {
            if (showWarning) {
                JOptionPane.showMessageDialog(this,
                        "Số điểm sử dụng vượt quá số điểm hiện có: " + customer.getDiemTichLuy(),
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }

        return true;
    }

    private void loadProducts() {
        String keyword = txtSearch.getText();
        List<SanPhamDTO> list;
        if (keyword == null || keyword.isEmpty()) {
            list = sanPhamBUS.getAllActive();
        } else {
            list = sanPhamBUS.searchActive(keyword);
        }
        productGridPanel.loadProducts(list);
    }

    private void addProductToCart(SanPhamDTO sp) {
        String input = (String) JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(this),
                "Nhập số lượng cho sản phẩm:\n" + sp.getTenSP(),
                "Thêm vào giỏ",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "1");
        if (input == null) {
            return;
        }

        int sl;
        try {
            sl = Integer.parseInt(input);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Số lượng phải là số nguyên!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sl <= 0) {
            return;
        }

        int rowIndex = -1;
        int currentQty = 0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0).equals(sp.getMaSP())) {
                currentQty = (int) cartModel.getValueAt(i, 2);
                rowIndex = i;
                break;
            }
        }

        if (currentQty + sl > sp.getSoLuong()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Không đủ tồn kho! Kho còn " + sp.getSoLuong() + " sản phẩm", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (rowIndex != -1) {
            int newSL = currentQty + sl;
            cartModel.setValueAt(newSL, rowIndex, 2);
            cartModel.setValueAt(sp.getDonGia().multiply(new BigDecimal(newSL)), rowIndex, 4);
        } else {
            cartModel.addRow(new Object[] {
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sl,
                    sp.getDonGia(),
                    sp.getDonGia().multiply(new BigDecimal(sl))
            });
        }
        calculateTotals();
    }

    private void updateQuantity() {
        int r = tableCart.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn sản phẩm cần sửa trong giỏ hàng!");
            return;
        }

        String maSP = (String) cartModel.getValueAt(r, 0);
        SanPhamDTO sp = sanPhamBUS.findById(maSP);
        if (sp == null) {
            return;
        }

        String input = (String) JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cập nhật số lượng mới:",
                "Sửa số lượng",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                cartModel.getValueAt(r, 2).toString());
        if (input == null) {
            return;
        }

        int sl;
        try {
            sl = Integer.parseInt(input);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Số lượng phải là số nguyên!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sl <= 0) {
            removeProduct();
            return;
        }

        if (sl > sp.getSoLuong()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Không đủ tồn kho! Kho còn " + sp.getSoLuong() + " sản phẩm", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal gia = (BigDecimal) cartModel.getValueAt(r, 3);
        cartModel.setValueAt(sl, r, 2);
        cartModel.setValueAt(gia.multiply(new BigDecimal(sl)), r, 4);
        calculateTotals();
    }

    private void removeProduct() {
        int r = tableCart.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Vui lòng chọn sản phẩm cần xóa khỏi giỏ!");
            return;
        }
        cartModel.removeRow(r);
        calculateTotals();
    }

    private BigDecimal calculatePointDiscountPreview(BigDecimal tongSauVoucher) {
        String inputKH = getComboText(txtMaKH);
        String diemStr = txtDiemDung.getText().trim();

        if (inputKH.isEmpty() || diemStr.isEmpty() || !diemStr.matches("\\d+")) {
            return BigDecimal.ZERO;
        }
        if (tongSauVoucher.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        KhachHangDTO kh = findKhachHang(inputKH);
        if (kh == null) {
            return BigDecimal.ZERO;
        }

        int diemKhachNhap;
        try {
            diemKhachNhap = diemStr.isEmpty() ? 0 : Integer.parseInt(diemStr);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }

        BigDecimal tienDiem = new BigDecimal(diemKhachNhap).multiply(DIEM_TO_VND);
        BigDecimal max = tongSauVoucher.multiply(MAX_POINT_DISCOUNT_RATE).setScale(0, RoundingMode.FLOOR);
        if (diemKhachNhap > kh.getDiemTichLuy()) {
            return BigDecimal.ZERO;
        }

        if (tienDiem.compareTo(max) > 0) {
            tienDiem = max;
        }
        if (tienDiem.compareTo(tongSauVoucher) > 0) {
            tienDiem = tongSauVoucher;
        }

        return tienDiem.max(BigDecimal.ZERO);
    }

    private void calculateTotals() {
        BigDecimal tamTinh = BigDecimal.ZERO;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            tamTinh = tamTinh.add((BigDecimal) cartModel.getValueAt(i, 4));
        }

        BigDecimal giamVoucher = calculateVoucherDiscountPreview(tamTinh);
        BigDecimal tongSauVoucher = tamTinh.subtract(giamVoucher);
        if (tongSauVoucher.compareTo(BigDecimal.ZERO) < 0) {
            tongSauVoucher = BigDecimal.ZERO;
        }

        BigDecimal giamDiem = calculatePointDiscountPreview(tongSauVoucher);
        BigDecimal giamGia = giamVoucher.add(giamDiem);

        BigDecimal thanhTien = tongSauVoucher.subtract(giamDiem);
        if (thanhTien.compareTo(BigDecimal.ZERO) < 0) {
            thanhTien = BigDecimal.ZERO;
        }

        lblTamTinh.setText(formatMoney(tamTinh));
        lblGiamGia.setText("-" + formatMoney(giamGia));
        lblTongTien.setText(formatMoney(thanhTien));
    }

    private BigDecimal calculateVoucherDiscountPreview(BigDecimal tamTinh) {
        String maVoucher = getComboText(txtMaVoucher);
        if (maVoucher.isEmpty()) {
            return BigDecimal.ZERO;
        }
        if (tamTinh.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        VoucherDTO v = voucherBUS.findById(maVoucher);
        if (v == null || v.getTrangThai() != 1 || v.getSoLuong() <= 0) {
            return BigDecimal.ZERO;
        }

        LocalDate now = LocalDate.now();
        if (v.getNgayBatDau() != null && now.isBefore(v.getNgayBatDau())) {
            return BigDecimal.ZERO;
        }
        if (v.getNgayKetThuc() != null && now.isAfter(v.getNgayKetThuc())) {
            return BigDecimal.ZERO;
        }

        if (v.getDieuKienApDung() != null && tamTinh.compareTo(v.getDieuKienApDung()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (v.getPhanTramGiam() > 0) {
            discount = tamTinh.multiply(new BigDecimal(v.getPhanTramGiam())).divide(new BigDecimal("100"), 0,
                    RoundingMode.FLOOR);
            if (v.getGiamToiDa() != null && v.getGiamToiDa().compareTo(BigDecimal.ZERO) > 0
                    && discount.compareTo(v.getGiamToiDa()) > 0) {
                discount = v.getGiamToiDa();
            }
        } else if (v.getSoTienGiam() != null && v.getSoTienGiam().compareTo(BigDecimal.ZERO) > 0) {
            discount = v.getSoTienGiam();
        }

        if (discount.compareTo(tamTinh) > 0) {
            discount = tamTinh;
        }
        return discount.max(BigDecimal.ZERO);
    }

    private void checkout() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Giỏ hàng đang trống!", "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validatePointsInput(true)) {
            txtDiemDung.requestFocusInWindow();
            return;
        }

        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaHD(txtMaHD.getText().trim());
        hd.setMaNV(session.getNhanVien().getMaNV());

        String inputKH = getComboText(txtMaKH);
        if (!inputKH.isEmpty()) {
            KhachHangDTO kh = findKhachHang(inputKH);
            if (kh == null) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Không tìm thấy khách hàng này trong hệ thống!\nVui lòng tạo mới hoặc để trống nếu là khách vãng lai.",
                        "Khách hàng không tồn tại",
                        JOptionPane.WARNING_MESSAGE);
                getComboEditor(txtMaKH).requestFocus();
                return;
            } else {
                hd.setMaKH(kh.getMaKH());
            }
        } else {
            hd.setMaKH(null);
        }

        String maVoucher = getComboText(txtMaVoucher);
        hd.setMaVoucher(maVoucher.isEmpty() ? null : maVoucher);
        hd.setNgayLap(LocalDate.now());

        List<ChiTietHoaDonDTO> items = new ArrayList<>();
        BigDecimal tamTinh = BigDecimal.ZERO;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            ChiTietHoaDonDTO ct = new ChiTietHoaDonDTO();
            ct.setMaHD(hd.getMaHD());
            ct.setMaSP((String) cartModel.getValueAt(i, 0));
            ct.setDonGia((BigDecimal) cartModel.getValueAt(i, 3));
            ct.setSoLuong((int) cartModel.getValueAt(i, 2));
            items.add(ct);
            tamTinh = tamTinh.add((BigDecimal) cartModel.getValueAt(i, 4));
        }

        BigDecimal giamVoucher = calculateVoucherDiscountPreview(tamTinh);
        BigDecimal tongSauVoucher = tamTinh.subtract(giamVoucher).max(BigDecimal.ZERO);
        BigDecimal giamDiem = calculatePointDiscountPreview(tongSauVoucher);
        int diemThucTe = giamDiem.divide(DIEM_TO_VND, 0, RoundingMode.FLOOR).intValue();
        BigDecimal tongTien = tongSauVoucher.subtract(giamDiem).max(BigDecimal.ZERO);
        hd.setTongTien(tongTien);

        boolean ok = banHangBUS.createInvoice(hd, items, diemThucTe);

        if (ok) {
            int confirm = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thanh toán thành công!\nBạn có muốn in hóa đơn không?",
                    "In Hóa Đơn",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                inHoaDonPDF(hd, items);
            }

            clearInvoice();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Thanh toán thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inHoaDonPDF(HoaDonDTO hd, List<ChiTietHoaDonDTO> items) {
        try {
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

            String tenNhanVien = safePdfText(
                    safe(session.getNhanVien().getHo()) + " " + safe(session.getNhanVien().getTen()));
            document.add(new Paragraph("Nhan vien: " + tenNhanVien, fontNormal));

            if (hd.getMaKH() != null && !hd.getMaKH().isEmpty()) {
                document.add(new Paragraph("Khach hang: " + safePdfText(hd.getMaKH()), fontNormal));
            } else {
                document.add(new Paragraph("Khach hang: Khach le", fontNormal));
            }

            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4f, 1f, 2.5f, 2.8f });

            PdfPCell h1 = new PdfPCell(new Phrase("Ten SP", fontBold));
            PdfPCell h2 = new PdfPCell(new Phrase("SL", fontBold));
            PdfPCell h3 = new PdfPCell(new Phrase("Gia", fontBold));
            PdfPCell h4 = new PdfPCell(new Phrase("Tong", fontBold));

            h1.setHorizontalAlignment(Element.ALIGN_LEFT);
            h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h4.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(h1);
            table.addCell(h2);
            table.addCell(h3);
            table.addCell(h4);

            BigDecimal tamTinh = BigDecimal.ZERO;
            DecimalFormat df = new DecimalFormat("#,###");

            for (ChiTietHoaDonDTO ct : items) {
                SanPhamDTO sp = sanPhamBUS.findById(ct.getMaSP());
                String tenSP = sp != null ? safePdfText(sp.getTenSP()) : ct.getMaSP();
                BigDecimal tong = ct.getDonGia().multiply(new BigDecimal(ct.getSoLuong()));
                tamTinh = tamTinh.add(tong);

                PdfPCell c1 = new PdfPCell(new Phrase(tenSP, fontNormal));
                PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                PdfPCell c3 = new PdfPCell(new Phrase(df.format(ct.getDonGia()) + " VND", fontNormal));
                PdfPCell c4 = new PdfPCell(new Phrase(df.format(tong) + " VND", fontNormal));

                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);

                table.addCell(c1);
                table.addCell(c2);
                table.addCell(c3);
                table.addCell(c4);
            }
            document.add(table);

            document.add(new Paragraph(
                    "---------------------------------------------------------------------------------------\n",
                    fontNormal));

            BigDecimal giamGia = tamTinh.subtract(hd.getTongTien());
            if (giamGia.compareTo(BigDecimal.ZERO) < 0) {
                giamGia = BigDecimal.ZERO;
            }

            document.add(new Paragraph("Tam tinh: " + df.format(tamTinh) + " VND", fontNormal));
            document.add(new Paragraph("Giam gia: " + df.format(giamGia) + " VND", fontNormal));
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

    private void clearInvoice() {
        cartModel.setRowCount(0);
        getComboEditor(txtMaKH).setText("");
        getComboEditor(txtMaVoucher).setText("");
        txtDiemDung.setText("");
        txtDiemDung.setEnabled(false);
        txtDiemDung.putClientProperty("JTextField.placeholderText", "Số điểm...");
        txtMaHD.setText(generateInvoiceId());
        txtNgayLap.setText(LocalDate.now().toString());
        calculateTotals();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0đ";
        }
        return new DecimalFormat("#,###").format(value) + "đ";
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

    @Override
    public void refreshData() {
        loadProducts();
        loadCustomerOptions();
        loadVoucherOptions();
        clearInvoice();
    }
}