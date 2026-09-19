package com.qlgiay.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.NhanVienBUS;
import com.qlgiay.dto.AuthSession;
import com.qlgiay.dto.NhanVienDTO;

import javax.swing.*;
import java.awt.*;

public class ThongTinTaiKhoanDialog extends JDialog {
    private final AuthSession session;
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();

    private JPasswordField txtPassCu;
    private JPasswordField txtPassMoi;
    private JPasswordField txtXacNhan;

    public ThongTinTaiKhoanDialog(Window parent, AuthSession session) {
        super(parent, "Thông tin tài khoản & Đổi mật khẩu", ModalityType.APPLICATION_MODAL);
        this.session = session;

        setSize(400, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        NhanVienDTO nv = session != null ? session.getNhanVien() : null;

        String hoTen = "Không xác định";
        String taiKhoan = "Chưa có";
        if (nv != null) {
            hoTen = (nv.getHo() != null ? nv.getHo() : "") + " " + (nv.getTen() != null ? nv.getTen() : "");
            if (hoTen.trim().isEmpty()) hoTen = "Nhân viên";
            taiKhoan = nv.getTaiKhoan() != null ? nv.getTaiKhoan() : "Chưa có";
        }

        String chucVu = "Chưa cấp quyền";
        if (session != null && session.getQuyen() != null && session.getQuyen().getTenQuyen() != null) {
            chucVu = session.getQuyen().getTenQuyen();
        }

        JLabel lblName = new JLabel(hoTen.trim());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setForeground(new Color(0, 90, 158));

        JLabel lblRole = new JLabel(chucVu + " - Tài khoản: " + taiKhoan);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRole.setForeground(Color.DARK_GRAY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlInfo.add(lblName);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblRole);

        JPanel pnlForm = new JPanel(new GridLayout(6, 1, 0, 5));
        pnlForm.setOpaque(false);

        txtPassCu = new JPasswordField();
        txtPassMoi = new JPasswordField();
        txtXacNhan = new JPasswordField();

        stylePasswordField(txtPassCu, "Nhập mật khẩu hiện tại...");
        stylePasswordField(txtPassMoi, "Nhập mật khẩu mới...");
        stylePasswordField(txtXacNhan, "Nhập lại mật khẩu mới...");

        pnlForm.add(new JLabel("Mật khẩu hiện tại:"));
        pnlForm.add(txtPassCu);
        pnlForm.add(new JLabel("Mật khẩu mới (tối thiểu 6 ký tự):"));
        pnlForm.add(txtPassMoi);
        pnlForm.add(new JLabel("Xác nhận mật khẩu mới:"));
        pnlForm.add(txtXacNhan);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlButtons.setOpaque(false);
        pnlButtons.setPreferredSize(new Dimension(0, 40));

        JButton btnSave = new JButton("Lưu thay đổi");
        JButton btnCancel = new JButton("Hủy");

        btnSave.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;background:#005A9E;foreground:#FFFFFF;hoverBackground:#004578");

        btnCancel.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;background:#F5F5F5;foreground:#333333;hoverBackground:#E0E0E0");

        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> savePassword());
        btnCancel.addActionListener(e -> dispose());

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnSave);

        mainPanel.add(pnlInfo, BorderLayout.NORTH);
        mainPanel.add(pnlForm, BorderLayout.CENTER);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void stylePasswordField(JPasswordField pf, String placeholder) {
        pf.setPreferredSize(new Dimension(0, 35));
        pf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        pf.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;focusWidth:0;innerFocusWidth:0;showClearButton:true");
    }

    private void savePassword() {
        String passCu = new String(txtPassCu.getPassword());
        String passMoi = new String(txtPassMoi.getPassword());
        String xacNhan = new String(txtXacNhan.getPassword());

        if (passCu.isEmpty() || passMoi.isEmpty() || xacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (session == null || session.getNhanVien() == null) {
            JOptionPane.showMessageDialog(this, "Lỗi phiên đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NhanVienDTO dbNV = nhanVienBUS.findById(session.getNhanVien().getMaNV());

        if (dbNV == null || !dbNV.getMatKhau().equals(passCu)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (passMoi.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!passMoi.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (passCu.equals(passMoi)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới không được trùng với mật khẩu cũ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dbNV.setMatKhau(passMoi);

        if (nhanVienBUS.updateNhanVien(dbNV)) {
            session.getNhanVien().setMatKhau(passMoi);
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}