package com.qlgiay.gui.frame;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.AuthBUS;
import com.qlgiay.dto.AuthSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private final AuthBUS authBUS = new AuthBUS();

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JPanel pnlLogin;
    private JLabel lblLogin;

    private JLabel lblForgot;

    private final Color hoverColor = new Color(96, 125, 139);

    public LoginFrame() {
        initComponent();
    }

    private void initComponent() {
        setTitle("Đăng nhập");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        add(createImagePanel(), BorderLayout.WEST);
        add(createFormPanel(), BorderLayout.EAST);

        bindEnterToLogin();
    }

    private void bindEnterToLogin() {
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "DO_LOGIN");
        root.getActionMap().put("DO_LOGIN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
    }

    private JPanel createImagePanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.setPreferredSize(new Dimension(500, 500));
        left.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = loadImage("/images/login/login.png");
        if (icon == null) {
            lblImage.setText("<html><div style='text-align:center;'>Thiếu ảnh<br/>/images/login.png</div></html>");
            lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            left.add(lblImage, BorderLayout.CENTER);
            return left;
        }

        Image original = icon.getImage();

        left.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = left.getWidth() - 40;
                int h = left.getHeight() - 40;
                if (w <= 0 || h <= 0) return;
                int size = Math.min(w, h);
                Image scaled = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaled));
            }
        });

        SwingUtilities.invokeLater(() -> {
            int w = left.getWidth() - 40;
            int h = left.getHeight() - 40;
            int size = Math.min(w, h);
            Image scaled = original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        });

        left.add(lblImage, BorderLayout.CENTER);
        return left;
    }

    private JPanel createFormPanel() {
        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        right.setBackground(Color.WHITE);
        right.setBorder(new EmptyBorder(20, 0, 0, 0));
        right.setPreferredSize(new Dimension(500, 500));

        JLabel title = new JLabel("ĐĂNG NHẬP VÀO HỆ THỐNG");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setPreferredSize(new Dimension(420, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        right.add(title);

        right.add(Box.createVerticalStrut(10));

        JPanel formBox = new JPanel();
        formBox.setBackground(Color.WHITE);
        formBox.setPreferredSize(new Dimension(420, 220));
        formBox.setLayout(new GridLayout(4, 1, 0, 10));

        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formBox.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(420, 40));
        formBox.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formBox.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(420, 40));
        formBox.add(txtPassword);

        right.add(formBox);

        lblForgot = new JLabel("Quên mật khẩu", SwingConstants.RIGHT);
        lblForgot.setPreferredSize(new Dimension(420, 50));
        lblForgot.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showForgotDialog();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblForgot.setForeground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblForgot.setForeground(UIManager.getColor("Label.foreground"));
            }
        });
        right.add(lblForgot);

        lblLogin = new JLabel("ĐĂNG NHẬP");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogin.setForeground(Color.WHITE);

        pnlLogin = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        pnlLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 99");
        pnlLogin.setBackground(Color.BLACK);
        pnlLogin.setPreferredSize(new Dimension(400, 45));
        pnlLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlLogin.add(lblLogin);

        pnlLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!txtUsername.isEnabled()) return;
                pnlLogin.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pnlLogin.setBackground(Color.BLACK);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!txtUsername.isEnabled()) return;
                doLogin();
            }
        });

        right.add(pnlLogin);

        return right;
    }

    private void doLogin() {
        String taiKhoan = txtUsername.getText() == null ? "" : txtUsername.getText().trim();
        String matKhau = new String(txtPassword.getPassword());

        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setEnabledForm(false);

        SwingWorker<AuthSession, Void> worker = new SwingWorker<>() {
            @Override
            protected AuthSession doInBackground() {
                return authBUS.login(taiKhoan, matKhau);
            }

            @Override
            protected void done() {
                try {
                    AuthSession session = get();
                    if (session == null) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Sai tài khoản hoặc mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        setEnabledForm(true);
                        txtPassword.selectAll();
                        txtPassword.requestFocus();
                        return;
                    }

                    dispose();
                    new MainFrame(session).setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Lỗi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    setEnabledForm(true);
                }
            }
        };

        worker.execute();
    }

    private void setEnabledForm(boolean enabled) {
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);

        lblLogin.setText(enabled ? "ĐĂNG NHẬP" : "ĐANG XỬ LÝ...");
        pnlLogin.setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        pnlLogin.setBackground(Color.BLACK);
    }

    private void showForgotDialog() {
        JOptionPane.showMessageDialog(
                LoginFrame.this,
                "Vui lòng liên hệ Quản trị viên (Admin) để được cấp lại mật khẩu!",
                "Quên mật khẩu",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private ImageIcon loadImage(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url == null) return null;
            return new ImageIcon(url);
        } catch (Exception e) {
            return null;
        }
    }
}