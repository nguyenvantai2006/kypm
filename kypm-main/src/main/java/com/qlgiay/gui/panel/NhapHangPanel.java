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
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatClientProperties;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.qlgiay.bus.NhaCungCapBUS;
import com.qlgiay.bus.NhapHangBUS;
import com.qlgiay.bus.SanPhamBUS;
import com.qlgiay.bus.TraHangNccBUS;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.ChiTietPhieuNhapDTO;
import com.qlgiay.dto.ChiTietTraNccDTO;
import com.qlgiay.dto.NhaCungCapDTO;
import com.qlgiay.dto.PhieuNhapDTO;
import com.qlgiay.dto.PhieuTraNccDTO;
import com.qlgiay.dto.SanPhamDTO;
import com.qlgiay.gui.component.ProductGridPanel;
import com.qlgiay.util.IconUtil;

public class NhapHangPanel extends JPanel implements IRefreshable {
    private final AuthSession session;
    private final SanPhamBUS sanPhamBUS = new SanPhamBUS();
    private final NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();
    private final NhapHangBUS nhapHangBUS = new NhapHangBUS();
    private final TraHangNccBUS traHangNccBUS = new TraHangNccBUS();

    private final JTabbedPane tabbedPane;

    private JTextField txtSearch;
    private ProductGridPanel productGridPanel;

    private JTable tableCart;
    private DefaultTableModel cartModel;

    private JTextField txtMaPN;
    private JTextField txtNgayNhap;
    private JTextField txtNhanVien;
    private JComboBox<NhaCungCapItem> cboNCC;

    private JLabel lblTongMatHang;
    private JLabel lblTongTien;

    private JTextField txtSearchHistory;
    private JComboBox<String> cboFilterNgayHistory;

    private JTable tablePhieuNhap;
    private DefaultTableModel phieuNhapModel;

    private JTable tableChiTietNhap;
    private DefaultTableModel chiTietNhapModel;

    private JTextField txtMaPNHistory;
    private JTextField txtNgayNhapHistory;
    private JTextField txtNVHistory;
    private JTextField txtNCCHistory;
    private JTextField txtTongTienHistory;

    private JComboBox<PhieuNhapItem> cboReturnImport;
    private JTable tableReturnSource;
    private DefaultTableModel returnSourceModel;
    private JTable tableReturnRequests;
    private DefaultTableModel returnRequestModel;

    private int hoverRowCart = -1;
    private int hoverRowPN = -1;
    private int hoverRowCT = -1;
    private boolean loadingHistoryTable = false;

    public NhapHangPanel(AuthSession session) {
        this.session = session;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 243, 245));

        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabType:card");

        tabbedPane.addTab("Tạo phiếu nhập", createCreateTab());
        tabbedPane.addTab("Hoàn trả NCC", createReturnTab());
        tabbedPane.addTab("Lịch sử nhập hàng", createHistoryTab());

        add(tabbedPane, BorderLayout.CENTER);

        initFormData();
        loadSupplierCombo();
        loadProducts();
        calculateTotals();
        loadHistoryTable();
        loadReturnData();
    }

    private JPanel createCreateTab() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createRightPanel()
        );
        split.setResizeWeight(0.62);
        split.setDividerSize(6);
        split.setOpaque(false);

        wrapper.add(split, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createHistoryTab() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createHistoryLeftPanel(),
                createHistoryRightPanel()
        );
        split.setOpaque(false);
        split.setResizeWeight(0.62);
        split.setDividerSize(6);

        wrapper.add(split, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createReturnTab() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createReturnRequestPanel(),
                createReturnHistoryPanel()
        );
        split.setOpaque(false);
        split.setResizeWeight(0.5);
        split.setDividerSize(6);

        wrapper.add(split, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createReturnRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:15;background:#FFFFFF");
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        top.add(new JLabel("Chọn phiếu nhập cần hoàn trả"), BorderLayout.NORTH);
        cboReturnImport = new JComboBox<>();
        styleComboBox(cboReturnImport);
        cboReturnImport.addActionListener(e -> loadReturnSourceDetails());
        top.add(cboReturnImport, BorderLayout.CENTER);

        String[] columns = {"Mã SP", "Tên sản phẩm", "SL nhập", "Giá nhập"};
        returnSourceModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReturnSource = new JTable(returnSourceModel);
        styleHistoryTable(tableReturnSource, false);
        tableReturnSource.getColumnModel().getColumn(0).setMinWidth(0);
        tableReturnSource.getColumnModel().getColumn(0).setMaxWidth(0);
        tableReturnSource.getColumnModel().getColumn(0).setPreferredWidth(0);

        JPanel note = new JPanel(new BorderLayout());
        note.setOpaque(false);
        note.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnCreate = new JButton("Tạo yêu cầu hoàn trả");
        btnCreate.setIcon(IconUtil.loadPng("/icons/add.png", 20));
        styleActionButton(btnCreate, "danger");
        btnCreate.addActionListener(e -> createReturnRequest());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.setOpaque(false);
        buttons.add(btnCreate);
        note.add(buttons, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableReturnSource), BorderLayout.CENTER);
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReturnHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:15;background:#FFFFFF");
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"Mã yêu cầu", "Mã PN", "Mã NCC", "Ngày tạo", "SL", "Tổng tiền", "Trạng thái", "Lý do"};
        returnRequestModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableReturnRequests = new JTable(returnRequestModel);
        styleHistoryTable(tableReturnRequests, false);
        tableReturnRequests.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableReturnRequests.getSelectionModel().addListSelectionListener(e -> updateReturnButtons());

        JButton btnApprove = new JButton("Duyệt hoàn trả");
        JButton btnReject = new JButton("Từ chối");
        JButton btnRefresh = new JButton("Làm mới");
        styleActionButton(btnApprove, "success");
        styleActionButton(btnReject, "danger");
        styleActionButton(btnRefresh, "default");
        btnApprove.addActionListener(e -> processReturn(true));
        btnReject.addActionListener(e -> processReturn(false));
        btnRefresh.addActionListener(e -> loadReturnData());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(btnApprove); actions.add(btnReject); actions.add(btnRefresh);
        panel.add(new JScrollPane(tableReturnRequests), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
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
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã hoặc tên sản phẩm...");
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

        formWrapper.add(createImportInfo(), BorderLayout.NORTH);
        formWrapper.add(createCartTable(), BorderLayout.CENTER);
        formWrapper.add(createBottomPanel(), BorderLayout.SOUTH);

        return formWrapper;
    }

    private JPanel createImportInfo() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        txtMaPN = new JTextField();
        txtNgayNhap = new JTextField();
        txtNhanVien = new JTextField();
        cboNCC = new JComboBox<>();

        txtMaPN.setEnabled(false);
        txtNgayNhap.setEnabled(false);
        txtNhanVien.setEnabled(false);

        styleComboBox(cboNCC);
        cboNCC.addActionListener(e -> loadProducts());

        int row = 0;
        addGridRow(contentPanel, gbc, row++, field("Mã phiếu nhập", txtMaPN), field("Ngày nhập", txtNgayNhap));
        addGridRow(contentPanel, gbc, row++, field("Nhân viên", txtNhanVien), field("Nhà cung cấp", cboNCC));

        return contentPanel;
    }

    private JScrollPane createCartTable() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "SL", "Giá nhập", "Thành tiền"};

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
        tableCart.getColumnModel().getColumn(3).setPreferredWidth(95);
        tableCart.getColumnModel().getColumn(4).setPreferredWidth(110);

        JTableHeader header = tableCart.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 247, 250));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                if (value instanceof BigDecimal) {
                    value = formatMoney((BigDecimal) value);
                }

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
                } else if (row == hoverRowCart) {
                    c.setBackground(new Color(245, 247, 250));
                    c.setForeground(Color.BLACK);
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

        tableCart.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tableCart.rowAtPoint(e.getPoint());
                if (row != hoverRowCart) {
                    hoverRowCart = row;
                    tableCart.repaint();
                }
            }
        });

        tableCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRowCart = -1;
                tableCart.repaint();
            }
        });

        JScrollPane sp = new JScrollPane(tableCart);
        sp.setPreferredSize(new Dimension(0, 200));
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)
        ));

        return sp;
    }

    private JPanel createBottomPanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel totals = new JPanel(new GridLayout(2, 2, 4, 6));
        totals.setOpaque(false);
        totals.setBorder(new EmptyBorder(10, 8, 15, 8));

        lblTongMatHang = new JLabel("0", SwingConstants.RIGHT);
        lblTongTien = new JLabel("0đ", SwingConstants.RIGHT);

        lblTongMatHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setForeground(new Color(198, 40, 40));

        totals.add(new JLabel("Tổng số lượng:"));
        totals.add(lblTongMatHang);
        totals.add(new JLabel("<html><b style='font-size:14px'>TỔNG TIỀN:</b></html>"));
        totals.add(lblTongTien);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 4, 5, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnUpdate = new JButton("Sửa SL");
        JButton btnRemove = new JButton("Xóa SP");
        JButton btnClear = new JButton("Làm mới");
        JButton btnImport = new JButton("Nhập hàng");

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
        styleActionButton(btnImport, "success");

        btnUpdate.addActionListener(e -> updateQuantity());
        btnRemove.addActionListener(e -> removeProduct());
        btnClear.addActionListener(e -> clearImport());
        btnImport.addActionListener(e -> checkout());

        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnRemove);
        pnlButtons.add(btnClear);
        pnlButtons.add(btnImport);

        p.add(totals, BorderLayout.NORTH);
        p.add(pnlButtons, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createHistoryLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF");
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        p.add(createHistoryTopBar(), BorderLayout.NORTH);
        p.add(createHistoryTablePanel(), BorderLayout.CENTER);

        return p;
    }

    private JPanel createHistoryTopBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));

        txtSearchHistory = new JTextField();
        txtSearchHistory.setPreferredSize(new Dimension(0, 32));
        txtSearchHistory.putClientProperty("JTextField.placeholderText", "Nhập mã PN, mã NV, mã NCC để tìm kiếm...");

        txtSearchHistory.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadHistoryTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadHistoryTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadHistoryTable();
            }
        });

        cboFilterNgayHistory = new JComboBox<>(new String[]{
                "Tất cả thời gian", "Hôm nay", "Tháng này"
        });

        styleComboBox(cboFilterNgayHistory);
        cboFilterNgayHistory.setPreferredSize(new Dimension(150, 32));
        cboFilterNgayHistory.addActionListener(e -> loadHistoryTable());

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);
        pnlFilters.add(cboFilterNgayHistory);

        p.add(txtSearchHistory, BorderLayout.CENTER);
        p.add(pnlFilters, BorderLayout.EAST);

        return p;
    }

    private JPanel createHistoryTablePanel() {
        String[] cols = {
                "Mã PN", "Ngày nhập", "Mã NV", "Mã NCC", "Tổng tiền"
        };

        phieuNhapModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablePhieuNhap = new JTable(phieuNhapModel);

        tablePhieuNhap.getColumnModel().getColumn(0).setPreferredWidth(110);
        tablePhieuNhap.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablePhieuNhap.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablePhieuNhap.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablePhieuNhap.getColumnModel().getColumn(4).setPreferredWidth(100);

        tablePhieuNhap.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        styleHistoryTable(tablePhieuNhap, true);

        tablePhieuNhap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loadingHistoryTable) {
                loadSelectedImportRow();
            }
        });

        JScrollPane sp = new JScrollPane(tablePhieuNhap);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)
        ));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHistoryRightPanel() {
        JPanel formWrapper = new JPanel(new BorderLayout(0, 8));
        formWrapper.putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: #FFFFFF");
        formWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        formWrapper.setMinimumSize(new Dimension(400, 0));

        initHistoryFormComponents();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        int row = 0;

        addGridRow(contentPanel, gbc, row++, field("Mã phiếu nhập", txtMaPNHistory), field("Ngày nhập", txtNgayNhapHistory));
        addGridRow(contentPanel, gbc, row++, field("Mã nhân viên", txtNVHistory), field("Mã nhà cung cấp", txtNCCHistory));
        addGridRow(contentPanel, gbc, row++, field("Tổng tiền", txtTongTienHistory), new JLabel());

        JPanel alignTopPanel = new JPanel(new BorderLayout(0, 10));
        alignTopPanel.setOpaque(false);
        alignTopPanel.add(contentPanel, BorderLayout.NORTH);
        alignTopPanel.add(createHistoryDetailTable(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(alignTopPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlButtons.setPreferredSize(new Dimension(0, 40));
        pnlButtons.setOpaque(false);

        JButton btnRefresh = new JButton("Làm mới");
        JButton btnPrint = new JButton("In phiếu nhập");

        btnRefresh.setIcon(IconUtil.loadPng("/icons/refresh.png", 22));
        btnPrint.setIcon(IconUtil.loadPng("/icons/print.png", 22));

        btnRefresh.setIconTextGap(6);
        btnPrint.setIconTextGap(6);

        styleActionButton(btnRefresh, "default");
        styleActionButton(btnPrint, "success");

        btnRefresh.addActionListener(e -> refreshHistoryTab());
        btnPrint.addActionListener(e -> printSelectedImport());

        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnPrint);

        formWrapper.add(scroll, BorderLayout.CENTER);
        formWrapper.add(pnlButtons, BorderLayout.SOUTH);

        return formWrapper;
    }

    private void initHistoryFormComponents() {
        txtMaPNHistory = new JTextField();
        txtNgayNhapHistory = new JTextField();
        txtNVHistory = new JTextField();
        txtNCCHistory = new JTextField();
        txtTongTienHistory = new JTextField();

        JTextField[] arr = {txtMaPNHistory, txtNgayNhapHistory, txtNVHistory, txtNCCHistory, txtTongTienHistory};
        for (JTextField t : arr) {
            t.setEnabled(false);
        }
    }

    private JScrollPane createHistoryDetailTable() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "SL", "Giá nhập", "Thành tiền"};

        chiTietNhapModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableChiTietNhap = new JTable(chiTietNhapModel);

        tableChiTietNhap.getColumnModel().getColumn(0).setMinWidth(0);
        tableChiTietNhap.getColumnModel().getColumn(0).setMaxWidth(0);
        tableChiTietNhap.getColumnModel().getColumn(0).setPreferredWidth(0);

        tableChiTietNhap.getColumnModel().getColumn(1).setPreferredWidth(160);
        tableChiTietNhap.getColumnModel().getColumn(2).setPreferredWidth(40);
        tableChiTietNhap.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableChiTietNhap.getColumnModel().getColumn(4).setPreferredWidth(100);

        tableChiTietNhap.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        styleHistoryTable(tableChiTietNhap, false);

        JScrollPane sp = new JScrollPane(tableChiTietNhap);
        sp.setPreferredSize(new Dimension(0, 180));
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(2, 2, 2, 2)
        ));

        return sp;
    }

    private void initFormData() {
        txtNhanVien.setText(getNhanVienName());
        txtNgayNhap.setText(LocalDate.now().toString());
        txtMaPN.setText(generateImportId());
    }

    private void loadSupplierCombo() {
        DefaultComboBoxModel<NhaCungCapItem> model = new DefaultComboBoxModel<>();
        List<NhaCungCapDTO> list = nhaCungCapBUS.getAllActive();

        if (list != null) {
            for (NhaCungCapDTO ncc : list) {
                model.addElement(new NhaCungCapItem(ncc.getMaNCC(), ncc.getTenNCC()));
            }
        }

        cboNCC.setModel(model);
        if (model.getSize() > 0) {
            cboNCC.setSelectedIndex(0);
        }
    }

    private String getNhanVienName() {
        if (session == null || session.getNhanVien() == null) {
            return "";
        }
        String ho = session.getNhanVien().getHo() == null ? "" : session.getNhanVien().getHo().trim();
        String ten = session.getNhanVien().getTen() == null ? "" : session.getNhanVien().getTen().trim();
        return (ho + " " + ten).trim();
    }

    private String generateImportId() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "PN" + LocalDateTime.now().format(fmt);
    }

    private void loadProducts() {
        NhaCungCapItem nccItem = (NhaCungCapItem) cboNCC.getSelectedItem();
        if (nccItem == null || nccItem.maNCC == null || nccItem.maNCC.isBlank()) {
            productGridPanel.loadProducts(List.of());
            return;
        }

        String keyword = txtSearch.getText();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        List<SanPhamDTO> supplierProducts = sanPhamBUS.getBySupplier(nccItem.maNCC);
        List<SanPhamDTO> list = new ArrayList<>();

        for (SanPhamDTO sp : supplierProducts) {
            if (sp.getTrangThai() != 1) {
                continue;
            }

            if (normalizedKeyword.isEmpty()
                    || containsIgnoreCase(sp.getMaSP(), normalizedKeyword)
                    || containsIgnoreCase(sp.getTenSP(), normalizedKeyword)
                    || containsIgnoreCase(sp.getLoaiSP(), normalizedKeyword)
                    || containsIgnoreCase(sp.getThuongHieu(), normalizedKeyword)) {
                list.add(sp);
            }
        }

        productGridPanel.loadProducts(list);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void addProductToCart(SanPhamDTO sp) {
        if (sp == null) {
            return;
        }

        String soLuongText = (String) JOptionPane.showInputDialog(
                null,
                "Nhập số lượng cần nhập cho sản phẩm:\n" + sp.getTenSP(),
                "Thêm vào phiếu nhập",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "1"
        );
        if (soLuongText == null) {
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongText.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Số lượng phải là số nguyên!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (soLuong <= 0) {
            return;
        }

        String giaNhapText = (String) JOptionPane.showInputDialog(
                null,
                "Nhập giá nhập cho sản phẩm:\n" + sp.getTenSP(),
                "Giá nhập",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                sp.getGiaNhap() == null ? "" : formatInputMoney(sp.getGiaNhap())
        );
        if (giaNhapText == null) {
            return;
        }

        BigDecimal giaNhap;
        try {
            giaNhap = new BigDecimal(giaNhapText.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Giá nhập không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (giaNhap.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Giá nhập phải lớn hơn 0!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int rowIndex = findCartRow(sp.getMaSP());

        if (rowIndex != -1) {
            int currentQty = (int) cartModel.getValueAt(rowIndex, 2);
            BigDecimal currentPrice = (BigDecimal) cartModel.getValueAt(rowIndex, 3);
            int newQty = currentQty + soLuong;
            BigDecimal averagePrice = calculateWeightedAverageImportPrice(currentPrice, currentQty, giaNhap, soLuong);

            cartModel.setValueAt(sp.getTenSP(), rowIndex, 1);
            cartModel.setValueAt(newQty, rowIndex, 2);
            cartModel.setValueAt(averagePrice, rowIndex, 3);
            cartModel.setValueAt(averagePrice.multiply(BigDecimal.valueOf(newQty)), rowIndex, 4);
        } else {
            cartModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    soLuong,
                    giaNhap,
                    giaNhap.multiply(BigDecimal.valueOf(soLuong))
            });
        }

        calculateTotals();
    }

    private int findCartRow(String maSP) {
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (String.valueOf(cartModel.getValueAt(i, 0)).equals(maSP)) {
                return i;
            }
        }
        return -1;
    }

    private void updateQuantity() {
        int r = tableCart.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn sản phẩm cần sửa trong phiếu nhập!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String input = (String) JOptionPane.showInputDialog(
                null,
                "Cập nhật số lượng mới:",
                "Sửa số lượng",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                cartModel.getValueAt(r, 2).toString()
        );
        if (input == null) {
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(input.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Số lượng phải là số nguyên!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (soLuong <= 0) {
            removeProduct();
            return;
        }

        BigDecimal giaNhap = (BigDecimal) cartModel.getValueAt(r, 3);
        cartModel.setValueAt(soLuong, r, 2);
        cartModel.setValueAt(giaNhap.multiply(BigDecimal.valueOf(soLuong)), r, 4);

        calculateTotals();
    }

    private void removeProduct() {
        int r = tableCart.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn sản phẩm cần xóa khỏi phiếu nhập!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        cartModel.removeRow(r);
        calculateTotals();
    }

    private void calculateTotals() {
        int tongSoLuong = 0;
        BigDecimal tongTien = BigDecimal.ZERO;

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            tongSoLuong += (int) cartModel.getValueAt(i, 2);
            tongTien = tongTien.add((BigDecimal) cartModel.getValueAt(i, 4));
        }

        lblTongMatHang.setText(String.valueOf(tongSoLuong));
        lblTongTien.setText(formatMoney(tongTien));
    }

    private void checkout() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Phiếu nhập đang trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        NhaCungCapItem nccItem = (NhaCungCapItem) cboNCC.getSelectedItem();
        if (nccItem == null || nccItem.maNCC == null || nccItem.maNCC.isBlank()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn nhà cung cấp!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            cboNCC.requestFocus();
            return;
        }

        if (session == null || session.getNhanVien() == null || session.getNhanVien().getMaNV() == null || session.getNhanVien().getMaNV().isBlank()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Không xác định được nhân viên đăng nhập!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        PhieuNhapDTO pn = new PhieuNhapDTO();
        pn.setMaPN(txtMaPN.getText().trim());
        pn.setMaNV(session.getNhanVien().getMaNV().trim());
        pn.setMaNCC(nccItem.maNCC);
        pn.setNgayNhap(LocalDate.now());

        List<ChiTietPhieuNhapDTO> items = new ArrayList<>();

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            ChiTietPhieuNhapDTO ct = new ChiTietPhieuNhapDTO();
            ct.setMaPN(pn.getMaPN());
            ct.setMaSP(String.valueOf(cartModel.getValueAt(i, 0)));
            ct.setSoLuong((int) cartModel.getValueAt(i, 2));
            ct.setGiaNhap((BigDecimal) cartModel.getValueAt(i, 3));
            items.add(ct);
        }

        boolean ok = nhapHangBUS.createImport(pn, items);

        if (ok) {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Nhập hàng thành công!\nBạn có muốn in phiếu nhập kho không?",
                    "In Phiếu Nhập",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                inPhieuNhapPDF(pn, items, nccItem.tenNCC);
            }

            clearImport();
            loadProducts();
            loadHistoryTable();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Nhập hàng thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadHistoryTable() {
        loadingHistoryTable = true;
        try {
            String keyword = txtSearchHistory.getText();
            if (keyword == null) {
                keyword = "";
            }
            keyword = keyword.trim();

            String filterDate = String.valueOf(cboFilterNgayHistory.getSelectedItem());

            tablePhieuNhap.clearSelection();
            phieuNhapModel.setRowCount(0);
            chiTietNhapModel.setRowCount(0);

            txtMaPNHistory.setText("");
            txtNgayNhapHistory.setText("");
            txtNVHistory.setText("");
            txtNCCHistory.setText("");
            txtTongTienHistory.setText("");

            List<PhieuNhapDTO> list = keyword.isEmpty() ? nhapHangBUS.getAllImports() : nhapHangBUS.searchImports(keyword);

            if (list != null) {
                LocalDate today = LocalDate.now();
                for (PhieuNhapDTO pn : list) {
                    if (pn.getNgayNhap() != null) {
                        if ("Hôm nay".equals(filterDate) && !pn.getNgayNhap().equals(today)) {
                            continue;
                        }
                        if ("Tháng này".equals(filterDate)
                                && (pn.getNgayNhap().getMonthValue() != today.getMonthValue()
                                || pn.getNgayNhap().getYear() != today.getYear())) {
                            continue;
                        }
                    }

                    phieuNhapModel.addRow(new Object[]{
                            pn.getMaPN(),
                            pn.getNgayNhap(),
                            pn.getMaNV(),
                            pn.getMaNCC(),
                            pn.getTongTien()
                    });
                }
            }
        } finally {
            loadingHistoryTable = false;
        }
    }

    private void loadReturnData() {
        if (cboReturnImport == null) return;
        DefaultComboBoxModel<PhieuNhapItem> model = new DefaultComboBoxModel<>();
        for (PhieuNhapDTO pn : nhapHangBUS.getAllImports()) {
            model.addElement(new PhieuNhapItem(pn));
        }
        cboReturnImport.setModel(model);
        loadReturnSourceDetails();

        returnRequestModel.setRowCount(0);
        for (PhieuTraNccDTO p : traHangNccBUS.getAllRequests()) {
            returnRequestModel.addRow(new Object[]{p.getMaPT(), p.getMaPN(), p.getMaNCC(), p.getNgayTao(),
                    p.getTongSoMatHang(), p.getTongTien(), p.getTrangThai(), p.getLyDo()});
        }
        updateReturnButtons();
    }

    private void loadReturnSourceDetails() {
        if (returnSourceModel == null) return;
        returnSourceModel.setRowCount(0);
        PhieuNhapItem item = (PhieuNhapItem) cboReturnImport.getSelectedItem();
        if (item == null) return;
        for (ChiTietPhieuNhapDTO ct : nhapHangBUS.getImportDetails(item.phieuNhap.getMaPN())) {
            SanPhamDTO sp = sanPhamBUS.findById(ct.getMaSP());
            returnSourceModel.addRow(new Object[]{ct.getMaSP(), sp == null ? ct.getMaSP() : sp.getTenSP(),
                    ct.getSoLuong(), ct.getGiaNhap()});
        }
    }

    private void createReturnRequest() {
        if (session == null || session.getNhanVien() == null
                || session.getNhanVien().getMaNV() == null
                || session.getNhanVien().getMaNV().isBlank()) {
            JOptionPane.showMessageDialog(this, "Không xác định được nhân viên đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int row = tableReturnSource.getSelectedRow();
        PhieuNhapItem item = (PhieuNhapItem) cboReturnImport.getSelectedItem();
        if (row < 0 || item == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập và sản phẩm cần hoàn trả!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String quantity = JOptionPane.showInputDialog(this, "Nhập số lượng hoàn trả (tối đa " + returnSourceModel.getValueAt(row, 2) + "):", "1");
        if (quantity == null) return;
        int amount;
        try { amount = Integer.parseInt(quantity.trim()); } catch (NumberFormatException e) { amount = 0; }
        int max = (int) returnSourceModel.getValueAt(row, 2);
        if (amount <= 0 || amount > max) {
            JOptionPane.showMessageDialog(this, "Số lượng hoàn trả không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reason = JOptionPane.showInputDialog(this, "Lý do hoàn trả (ví dụ: sản phẩm lỗi):", "Sản phẩm lỗi");
        if (reason == null || reason.trim().isEmpty()) return;

        PhieuTraNccDTO request = new PhieuTraNccDTO();
        request.setMaPT("PT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")));
        request.setMaPN(item.phieuNhap.getMaPN()); request.setMaNV(session.getNhanVien().getMaNV());
        request.setMaNCC(item.phieuNhap.getMaNCC()); request.setLyDo(reason.trim());
        ChiTietTraNccDTO detail = new ChiTietTraNccDTO();
        detail.setMaSP(String.valueOf(returnSourceModel.getValueAt(row, 0))); detail.setSoLuong(amount);
        if (traHangNccBUS.createRequest(request, detail)) {
            JOptionPane.showMessageDialog(this, "Đã tạo yêu cầu, đang chờ duyệt.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadReturnData();
        } else {
            String error = traHangNccBUS.getLastError();
            if (error == null || error.isBlank()) error = "Không thể tạo yêu cầu hoàn trả.";
            JOptionPane.showMessageDialog(this, "Không thể tạo yêu cầu hoàn trả:\n" + error, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processReturn(boolean approve) {
        int row = tableReturnRequests.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một yêu cầu hoàn trả.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maPT = String.valueOf(returnRequestModel.getValueAt(row, 0));
        String status = String.valueOf(returnRequestModel.getValueAt(row, 6));
        if (!"Đang duyệt".equals(status)) {
            JOptionPane.showMessageDialog(this, "Yêu cầu này đã được xử lý.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maNV = session == null || session.getNhanVien() == null ? "" : session.getNhanVien().getMaNV();
        boolean ok = approve ? traHangNccBUS.approve(maPT, maNV) : traHangNccBUS.reject(maPT, maNV);
        if (ok) {
            JOptionPane.showMessageDialog(this, approve ? "Đã duyệt và trừ tồn kho." : "Đã từ chối yêu cầu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadReturnData(); loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, approve ? "Không đủ tồn kho hoặc yêu cầu không còn hiệu lực." : "Không thể từ chối yêu cầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReturnButtons() {
        if (tableReturnRequests == null) return;
        int row = tableReturnRequests.getSelectedRow();
        boolean pending = row >= 0 && "Đang duyệt".equals(returnRequestModel.getValueAt(row, 6));
        tableReturnRequests.setToolTipText(pending ? "Yêu cầu đang chờ xử lý" : "Chọn yêu cầu đang duyệt để xử lý");
    }

    private void loadSelectedImportRow() {
        if (loadingHistoryTable) {
            return;
        }

        int viewRow = tablePhieuNhap.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = viewRow;
        if (modelRow < 0 || modelRow >= phieuNhapModel.getRowCount()) {
            return;
        }

        String maPN = String.valueOf(phieuNhapModel.getValueAt(modelRow, 0));

        PhieuNhapDTO pn = nhapHangBUS.findImportById(maPN);
        if (pn == null) {
            return;
        }

        txtMaPNHistory.setText(pn.getMaPN());
        txtNgayNhapHistory.setText(String.valueOf(pn.getNgayNhap()));
        txtNVHistory.setText(pn.getMaNV());
        txtNCCHistory.setText(pn.getMaNCC());
        txtTongTienHistory.setText(formatMoney(pn.getTongTien()));

        loadImportDetailTable(maPN);
    }

    private void loadImportDetailTable(String maPN) {
        chiTietNhapModel.setRowCount(0);
        List<ChiTietPhieuNhapDTO> list = nhapHangBUS.getImportDetails(maPN);

        if (list != null) {
            for (ChiTietPhieuNhapDTO ct : list) {
                SanPhamDTO sp = sanPhamBUS.findById(ct.getMaSP());
                String tenSP = sp != null ? sp.getTenSP() : ct.getMaSP();
                BigDecimal thanhTien = ct.getGiaNhap().multiply(new BigDecimal(ct.getSoLuong()));

                chiTietNhapModel.addRow(new Object[]{
                        ct.getMaSP(),
                        tenSP,
                        ct.getSoLuong(),
                        ct.getGiaNhap(),
                        thanhTien
                });
            }
        }
    }

    private void printSelectedImport() {
        int viewRow = tablePhieuNhap.getSelectedRow();
        if (viewRow < 0 || viewRow >= tablePhieuNhap.getRowCount()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Vui lòng chọn 1 phiếu nhập để in!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = viewRow;
        if (modelRow < 0 || modelRow >= phieuNhapModel.getRowCount()) {
            return;
        }

        String maPN = String.valueOf(phieuNhapModel.getValueAt(modelRow, 0));

        try {
            PhieuNhapDTO pn = nhapHangBUS.findImportById(maPN);
            if (pn == null) {
                return;
            }

            List<ChiTietPhieuNhapDTO> items = nhapHangBUS.getImportDetails(maPN);
            String tenNCC = pn.getMaNCC();

            NhaCungCapDTO ncc = nhaCungCapBUS.findById(pn.getMaNCC());
            if (ncc != null && ncc.getTenNCC() != null) {
                tenNCC = ncc.getTenNCC();
            }

            inPhieuNhapPDF(pn, items, tenNCC);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi in phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inPhieuNhapPDF(PhieuNhapDTO pn, List<ChiTietPhieuNhapDTO> items, String tenNCC) {
        try {
            String path = "PhieuNhapKho_" + pn.getMaPN() + ".pdf";
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font fontBold = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);

            Paragraph title = new Paragraph("PHIEU NHAP KHO\n\n", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Ma PN: " + pn.getMaPN(), fontNormal));
            document.add(new Paragraph("Ngay nhap: " + pn.getNgayNhap(), fontNormal));
            document.add(new Paragraph("Nhan vien: " + safePdfText(getNhanVienNameById(pn.getMaNV())), fontNormal));
            document.add(new Paragraph("Nha cung cap: " + safePdfText(tenNCC), fontNormal));
            document.add(new Paragraph("---------------------------------------------------------------------------------------\n", fontNormal));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2.5f, 2.8f});

            PdfPCell h1 = new PdfPCell(new Phrase("Ten SP", fontBold));
            PdfPCell h2 = new PdfPCell(new Phrase("SL", fontBold));
            PdfPCell h3 = new PdfPCell(new Phrase("Gia nhap", fontBold));
            PdfPCell h4 = new PdfPCell(new Phrase("Tong", fontBold));

            h1.setHorizontalAlignment(Element.ALIGN_LEFT);
            h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            h4.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(h1);
            table.addCell(h2);
            table.addCell(h3);
            table.addCell(h4);

            DecimalFormat df = new DecimalFormat("#,###");
            int tongSoLuong = 0;
            BigDecimal tongTien = BigDecimal.ZERO;

            for (ChiTietPhieuNhapDTO ct : items) {
                SanPhamDTO sp = sanPhamBUS.findById(ct.getMaSP());
                String tenSP = sp != null ? safePdfText(sp.getTenSP()) : ct.getMaSP();
                BigDecimal tong = ct.getGiaNhap().multiply(new BigDecimal(ct.getSoLuong()));

                tongSoLuong += ct.getSoLuong();
                tongTien = tongTien.add(tong);

                PdfPCell c1 = new PdfPCell(new Phrase(tenSP, fontNormal));
                PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                PdfPCell c3 = new PdfPCell(new Phrase(df.format(ct.getGiaNhap()) + " VND", fontNormal));
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

            document.add(new Paragraph("---------------------------------------------------------------------------------------\n", fontNormal));
            document.add(new Paragraph("Tong so luong: " + tongSoLuong, fontNormal));
            document.add(new Paragraph("Tong tien: " + df.format(tongTien) + " VND", fontTitle));

            document.close();

            File file = new File(path);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi in Phiếu Nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNhanVienNameById(String maNV) {
        if (session != null && session.getNhanVien() != null && maNV != null && maNV.equals(session.getNhanVien().getMaNV())) {
            return getNhanVienName();
        }
        return maNV == null ? "" : maNV;
    }

    private void clearImport() {
        cartModel.setRowCount(0);
        txtMaPN.setText(generateImportId());
        txtNgayNhap.setText(LocalDate.now().toString());

        if (cboNCC.getItemCount() > 0) {
            cboNCC.setSelectedIndex(0);
        }

        hoverRowCart = -1;
        tableCart.clearSelection();
        tableCart.repaint();

        calculateTotals();
    }

    private void refreshHistoryTab() {
        txtSearchHistory.setText("");
        cboFilterNgayHistory.setSelectedIndex(0);

        txtMaPNHistory.setText("");
        txtNgayNhapHistory.setText("");
        txtNVHistory.setText("");
        txtNCCHistory.setText("");
        txtTongTienHistory.setText("");

        hoverRowPN = -1;
        hoverRowCT = -1;

        tablePhieuNhap.clearSelection();
        chiTietNhapModel.setRowCount(0);
        loadHistoryTable();
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
                "arc:8;focusWidth:0;innerFocusWidth:0"
        );
    }

    private void styleHistoryTable(JTable targetTable, boolean isPhieuNhapTable) {
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
                    int column
            ) {
                if (value instanceof BigDecimal) {
                    value = formatMoney((BigDecimal) value);
                }

                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                int currentHoverRow = isPhieuNhapTable ? hoverRowPN : hoverRowCT;

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

                if (isPhieuNhapTable) {
                    if (column == 0 || column == 1 || column == 2 || column == 3) {
                        setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        setHorizontalAlignment(SwingConstants.RIGHT);
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
                if (isPhieuNhapTable) {
                    if (row != hoverRowPN) {
                        hoverRowPN = row;
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
                if (isPhieuNhapTable) {
                    hoverRowPN = -1;
                } else {
                    hoverRowCT = -1;
                }
                targetTable.repaint();
            }
        });
    }

    static BigDecimal calculateWeightedAverageImportPrice(BigDecimal previousPrice, int previousQty, BigDecimal newPrice, int newQty) {
        if (previousPrice == null) {
            previousPrice = BigDecimal.ZERO;
        }
        if (newPrice == null) {
            newPrice = BigDecimal.ZERO;
        }

        int totalQty = Math.max(0, previousQty) + Math.max(0, newQty);
        if (totalQty <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = previousPrice.multiply(BigDecimal.valueOf(Math.max(0, previousQty)))
                .add(newPrice.multiply(BigDecimal.valueOf(Math.max(0, newQty))));

        return totalCost.divide(BigDecimal.valueOf(totalQty), 2, java.math.RoundingMode.HALF_UP);
    }

    private String safePdfText(String s) {
        if (s == null) {
            return "";
        }
        return s
                .replace("Đ", "D").replace("đ", "d")
                .replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                .replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
                .replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
                .replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                .replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
                .replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                .replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                .replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
                .replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
                .replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                .replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
                .replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                .replace("Á", "A").replace("À", "A").replace("Ả", "A").replace("Ã", "A").replace("Ạ", "A")
                .replace("Ă", "A").replace("Ắ", "A").replace("Ằ", "A").replace("Ẳ", "A").replace("Ẵ", "A").replace("Ặ", "A")
                .replace("Â", "A").replace("Ấ", "A").replace("Ầ", "A").replace("Ẩ", "A").replace("Ẫ", "A").replace("Ậ", "A")
                .replace("É", "E").replace("È", "E").replace("Ẻ", "E").replace("Ẽ", "E").replace("Ẹ", "E")
                .replace("Ê", "E").replace("Ế", "E").replace("Ề", "E").replace("Ể", "E").replace("Ễ", "E").replace("Ệ", "E")
                .replace("Í", "I").replace("Ì", "I").replace("Ỉ", "I").replace("Ĩ", "I").replace("Ị", "I")
                .replace("Ó", "O").replace("Ò", "O").replace("Ỏ", "O").replace("Õ", "O").replace("Ọ", "O")
                .replace("Ô", "O").replace("Ố", "O").replace("Ồ", "O").replace("Ổ", "O").replace("Ỗ", "O").replace("Ộ", "O")
                .replace("Ơ", "O").replace("Ớ", "O").replace("Ờ", "O").replace("Ở", "O").replace("Ỡ", "O").replace("Ợ", "O")
                .replace("Ú", "U").replace("Ù", "U").replace("Ủ", "U").replace("Ũ", "U").replace("Ụ", "U")
                .replace("Ư", "U").replace("Ứ", "U").replace("Ừ", "U").replace("Ử", "U").replace("Ữ", "U").replace("Ự", "U")
                .replace("Ý", "Y").replace("Ỳ", "Y").replace("Ỷ", "Y").replace("Ỹ", "Y").replace("Ỵ", "Y");
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "0đ";
        }
        return new DecimalFormat("#,###").format(value) + "đ";
    }

    private String formatInputMoney(BigDecimal value) {
        if (value == null) {
            return "";
        }
        value = value.stripTrailingZeros();
        if (value.scale() < 0) {
            value = value.setScale(0);
        }
        return value.toPlainString();
    }

    @Override
    public void refreshData() {
        loadProducts();
        clearImport();
        loadSupplierCombo();
        refreshHistoryTab();
        loadReturnData();
    }

    private static class NhaCungCapItem {
        private final String maNCC;
        private final String tenNCC;

        public NhaCungCapItem(String maNCC, String tenNCC) {
            this.maNCC = maNCC;
            this.tenNCC = tenNCC;
        }

        @Override
        public String toString() {
            return maNCC + " - " + tenNCC;
        }
    }

    private static class PhieuNhapItem {
        private final PhieuNhapDTO phieuNhap;

        private PhieuNhapItem(PhieuNhapDTO phieuNhap) {
            this.phieuNhap = phieuNhap;
        }

        @Override
        public String toString() {
            return phieuNhap.getMaPN() + " - " + phieuNhap.getNgayNhap() + " - NCC: " + phieuNhap.getMaNCC();
        }
    }
}