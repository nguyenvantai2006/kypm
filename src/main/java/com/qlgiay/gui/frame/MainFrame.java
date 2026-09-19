package com.qlgiay.gui.frame;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.NhanVienDTO;
import com.qlgiay.dto.QuyenDTO;
import com.qlgiay.gui.dialog.ThongTinTaiKhoanDialog;
import com.qlgiay.gui.panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final AuthSession session;

    private JPanel mainContent;
    private CardLayout cardLayout;
    private JPanel pnlNav;

    private JLabel lblUser;
    private JLabel lblRole;

    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private final ButtonGroup navGroup = new ButtonGroup();
    private final Map<String, JPanel> viewMap = new LinkedHashMap<>();
    private final List<HeaderItem> headers = new ArrayList<>();
    private HeaderItem currentHeader = null;

    public MainFrame(AuthSession session) {
        this.session = session;
        initUI();
        buildNav();
        applyPermission();
    }

    private void initUI() {
        setTitle("Quản Lý Cửa Hàng Giày Thể Thao Và Phụ Kiện");
        setIconImage(new ImageIcon(getClass().getResource("/icons/app.png")).getImage());
        setSize(1400, 800);
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        setContentPane(root);

        root.add(createSidebar(), BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        root.add(center, BorderLayout.CENTER);

        center.add(createTopbar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBorder(new EmptyBorder(12, 12, 12, 12));
        center.add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));

        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(225, 225, 225)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel brandBox = new JPanel();
        brandBox.setOpaque(false);
        brandBox.setLayout(new BoxLayout(brandBox, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("QLGIAY");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel sub = new JLabel("Giày thể thao & Phụ kiện");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        brandBox.add(brand);
        brandBox.add(Box.createVerticalStrut(2));
        brandBox.add(sub);
        brandBox.add(Box.createVerticalStrut(8));

        sidebar.add(brandBox, BorderLayout.NORTH);

        pnlNav = new JPanel();
        pnlNav.setOpaque(false);
        pnlNav.setLayout(new BoxLayout(pnlNav, BoxLayout.Y_AXIS));

        JScrollPane sp = new JScrollPane(pnlNav);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);

        sidebar.add(sp, BorderLayout.CENTER);

        JButton btnAccount = new JButton("Tài khoản");
        btnAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAccount.setFocusable(false);
        btnAccount.setIcon(loadIcon("/icons/account.png"));
        btnAccount.setIconTextGap(10);
        btnAccount.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAccount.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;" +
                        "focusWidth:0;" +
                        "margin:8,14,8,14;" +
                        "background:null;" +
                        "hoverBackground:#EAEAEC;" +
                        "pressedBackground:#DCE8FC;"
        );
        btnAccount.addActionListener(e -> new ThongTinTaiKhoanDialog(this, session).setVisible(true));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogout.setFocusable(false);
        btnLogout.setIcon(loadIcon("/icons/logout.png"));
        btnLogout.setIconTextGap(10);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;" +
                        "focusWidth:0;" +
                        "margin:8,14,8,14;" +
                        "background:null;" +
                        "hoverBackground:#FF0000;" +
                        "hoverForeground:#FFFFFF;" +
                        "pressedBackground:#FCD7D7;"
        );
        btnLogout.addActionListener(e -> logout());

        JPanel bottomActions = new JPanel();
        bottomActions.setOpaque(false);
        bottomActions.setLayout(new BoxLayout(bottomActions, BoxLayout.Y_AXIS));

        btnAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomActions.add(btnAccount);
        bottomActions.add(Box.createVerticalStrut(4));
        bottomActions.add(btnLogout);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottom.add(bottomActions, BorderLayout.SOUTH);

        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBorder(new EmptyBorder(14, 16, 10, 16));

        JLabel title = new JLabel("Hệ thống quản lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        topbar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        lblUser = new JLabel();
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblRole = new JLabel();
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        right.add(lblUser);
        right.add(lblRole);

        topbar.add(right, BorderLayout.EAST);

        bindUserInfo();

        return topbar;
    }

    private void bindUserInfo() {
        NhanVienDTO nv = session == null ? null : session.getNhanVien();
        QuyenDTO q = session == null ? null : session.getQuyen();

        String ten = "";
        if (nv != null) {
            ten = (nv.getHo() == null ? "" : nv.getHo().trim()) + " " +
                    (nv.getTen() == null ? "" : nv.getTen().trim());
        }

        String role = q == null ? "" : q.getTenQuyen();

        lblUser.setText(ten.isBlank() ? "Nhân viên" : ten);
        lblRole.setText(role.isBlank() ? "Quyền: ?" : "Quyền: " + role);
    }

    static Permission permissionForKey(String key) {
        if (key == null) return Permission.ALWAYS;

        return switch (key) {
            case "HOME" -> Permission.ALWAYS;
            case "BANHANG", "HOADON", "DOITRA", "BAOHANH" -> Permission.QL_BANHANG;
            case "NHAPHANG" -> Permission.QL_NHAPHANG;
            case "SANPHAM" -> Permission.QL_SANPHAM;
            case "KHACHHANG", "VOUCHER" -> Permission.QL_KHACHHANG;
            case "NHACUNGCAP" -> Permission.QL_NHAPHANG;
            case "NHANVIEN", "PHANQUYEN" -> Permission.QL_NHANVIEN;
            case "THONGKE" -> Permission.QL_THONGKE;
            default -> Permission.ALWAYS;
        };
    }

    private void buildNav() {
        if (pnlNav == null) return;

        navItems.clear();
        navGroup.clearSelection();
        pnlNav.removeAll();
        mainContent.removeAll();
        viewMap.clear();
        headers.clear();
        currentHeader = null;

        addView("HOME", "Trang chủ", permissionForKey("HOME"));

        addGroupHeader("GIAO DỊCH");
        addView("BANHANG", "Bán hàng", permissionForKey("BANHANG"));
        addView("HOADON", "Hóa đơn", permissionForKey("HOADON"));
        addView("NHAPHANG", "Nhập hàng", permissionForKey("NHAPHANG"));
        addView("DOITRA", "Đổi trả", permissionForKey("DOITRA"));
        addView("BAOHANH", "Bảo hành", permissionForKey("BAOHANH"));

        addGroupHeader("DANH MỤC");
        addView("SANPHAM", "Sản phẩm", permissionForKey("SANPHAM"));
        addView("KHACHHANG", "Khách hàng", permissionForKey("KHACHHANG"));
        addView("NHACUNGCAP", "Nhà cung cấp", permissionForKey("NHACUNGCAP"));
        addView("VOUCHER", "Voucher", permissionForKey("VOUCHER"));

        addGroupHeader("HỆ THỐNG");
        addView("NHANVIEN", "Nhân viên", permissionForKey("NHANVIEN"));
        addView("PHANQUYEN", "Phân quyền", permissionForKey("PHANQUYEN"));
        addView("THONGKE", "Thống kê", permissionForKey("THONGKE"));

        pnlNav.add(Box.createVerticalGlue());

        pnlNav.revalidate();
        pnlNav.repaint();
    }

    private JSeparator createLine() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, sep.getPreferredSize().height));
        sep.setForeground(new Color(215, 215, 215));
        return sep;
    }

    private void addGroupHeader(String title) {
        Component strut1 = Box.createVerticalStrut(4);
        pnlNav.add(strut1);

        JSeparator line = createLine();
        pnlNav.add(line);

        Component strut2 = Box.createVerticalStrut(6);
        pnlNav.add(strut2);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(130, 130, 130));
        lblTitle.setBorder(new EmptyBorder(0, 12, 6, 0));

        pnlNav.add(lblTitle);

        currentHeader = new HeaderItem();
        currentHeader.label = lblTitle;
        currentHeader.strut1 = strut1;
        currentHeader.line = line;
        currentHeader.strut2 = strut2;
        headers.add(currentHeader);
    }

    private void addView(String key, String title, Permission permission) {
        JToggleButton btn = new JToggleButton(title);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setIcon(loadIcon(iconPathByKey(key)));
        btn.setIconTextGap(10);

        btn.setFocusable(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;" +
                        "margin:8,14,8,14;" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "background:null;" +
                        "hoverBackground:#EAEAEC;" +
                        "selectedBackground:#E8F0FE;" +
                        "selectedForeground:#005A9E"
        );

        btn.addActionListener(e -> showView(key));

        navGroup.add(btn);
        pnlNav.add(btn);
        pnlNav.add(Box.createVerticalStrut(4));

        NavItem item = new NavItem(btn, permission);
        navItems.put(key, item);

        if (currentHeader != null) {
            currentHeader.children.add(item);
        }
    }

    private JPanel createPanelByKey(String key) {
        return switch (key) {
            case "HOME" -> new HomePanel(session);
            case "BANHANG" -> new BanHangPanel(session);
            case "HOADON" -> new HoaDonPanel();
            case "NHAPHANG" -> new NhapHangPanel(session);
            case "DOITRA" -> new DoiTraPanel(session);
            case "BAOHANH" -> new BaoHanhPanel();
            case "SANPHAM" -> new SanPhamPanel();
            case "KHACHHANG" -> new KhachHangPanel();
            case "NHACUNGCAP" -> new NhaCungCapPanel();
            case "VOUCHER" -> new VoucherPanel();
            case "NHANVIEN" -> new NhanVienPanel();
            case "PHANQUYEN" -> new PhanQuyenPanel();
            case "THONGKE" -> new ThongKePanel();
            default -> createPlaceholderPanel("Tính năng " + key);
        };
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel sub = new JLabel("Panel này sẽ làm sau.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        box.add(lbl);
        box.add(Box.createVerticalStrut(8));
        box.add(sub);

        p.add(box, BorderLayout.NORTH);

        return p;
    }

    public void setPanel(String key, JPanel panel) {
        if (key == null || key.trim().isEmpty()) return;
        if (panel == null) return;

        String k = key.trim().toUpperCase();

        JPanel old = viewMap.get(k);
        if (old != null) {
            mainContent.remove(old);
        }

        viewMap.put(k, panel);
        mainContent.add(panel, k);

        mainContent.revalidate();
        mainContent.repaint();

        showView(k);
    }

    private void showView(String key) {
        JPanel panel = viewMap.get(key);

        if (panel == null) {
            panel = createPanelByKey(key);
            viewMap.put(key, panel);
            mainContent.add(panel, key);
        }

        cardLayout.show(mainContent, key);

        NavItem item = navItems.get(key);
        if (item != null) {
            item.button.setSelected(true);
        } else {
            navGroup.clearSelection();
        }

        if (panel instanceof IRefreshable refreshable) {
            refreshable.refreshData();
        }
    }

    private void applyPermission() {
        QuyenDTO q = session == null ? null : session.getQuyen();

        String first = null;

        for (Map.Entry<String, NavItem> e : navItems.entrySet()) {
            String key = e.getKey();
            boolean allow = hasPermission(q, e.getValue().permission);

            if (key.equals("PHANQUYEN")) {
                if (q != null && (q.getMaQuyen().equalsIgnoreCase("ADMIN") || q.getMaQuyen().equalsIgnoreCase("Q01"))) {
                    allow = true;
                } else {
                    allow = false;
                }
            }

            e.getValue().button.setVisible(allow);
            e.getValue().button.setEnabled(allow);

            if (first == null && allow) {
                first = e.getKey();
            }
        }

        for (HeaderItem h : headers) {
            boolean hasVisibleChild = false;
            for (NavItem child : h.children) {
                if (child.button.isVisible()) {
                    hasVisibleChild = true;
                    break;
                }
            }
            h.label.setVisible(hasVisibleChild);
            h.strut1.setVisible(hasVisibleChild);
            h.line.setVisible(hasVisibleChild);
            h.strut2.setVisible(hasVisibleChild);
        }

        if (first != null) {
            showView(first);
        } else {
            showView("HOME");
        }
    }

    private boolean hasPermission(QuyenDTO q, Permission p) {
        if (p == Permission.ALWAYS) return true;
        if (q == null) return false;

        return switch (p) {
            case QL_BANHANG -> q.getQlBanHang() == 1;
            case QL_KHACHHANG -> q.getQlKhachHang() == 1;
            case QL_SANPHAM -> q.getQlSanPham() == 1;
            case QL_NHAPHANG -> q.getQlNhapHang() == 1;
            case QL_NHANVIEN -> q.getQlNhanVien() == 1;
            case QL_THONGKE -> q.getQlThongKe() == 1;
            default -> false;
        };
    }

    private ImageIcon loadIcon(String path) {
        if (path == null) return null;

        URL url = getClass().getResource(path);
        if (url == null) return null;

        return new ImageIcon(url);
    }

    private String iconPathByKey(String key) {
        return switch (key) {
            case "HOME" -> "/icons/home.png";
            case "BANHANG" -> "/icons/cart.png";
            case "HOADON" -> "/icons/bill.png";
            case "NHAPHANG" -> "/icons/import.png";
            case "DOITRA" -> "/icons/return.png";
            case "BAOHANH" -> "/icons/warranty.png";
            case "SANPHAM" -> "/icons/product.png";
            case "KHACHHANG" -> "/icons/customer.png";
            case "NHACUNGCAP" -> "/icons/supplier.png";
            case "VOUCHER" -> "/icons/voucher.png";
            case "NHANVIEN" -> "/icons/staff.png";
            case "PHANQUYEN" -> "/icons/role.png";
            case "THONGKE" -> "/icons/stats.png";
            default -> null;
        };
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private static class NavItem {
        final JToggleButton button;
        final Permission permission;

        NavItem(JToggleButton button, Permission permission) {
            this.button = button;
            this.permission = permission;
        }
    }

    private static class HeaderItem {
        JLabel label;
        Component strut1;
        JSeparator line;
        Component strut2;
        List<NavItem> children = new ArrayList<>();
    }

    public enum Permission {
        ALWAYS,
        QL_BANHANG,
        QL_KHACHHANG,
        QL_SANPHAM,
        QL_NHAPHANG,
        QL_NHANVIEN,
        QL_THONGKE
    }
}