package com.qlgiay.gui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.qlgiay.bus.KhachHangBUS;
import com.qlgiay.dto.KhachHangDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ThemKhachHangDialog extends JDialog {
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final Consumer<KhachHangDTO> onSaved;

    private JTextField txtTen;
    private JTextField txtSdt;

    public ThemKhachHangDialog(Window parent, Consumer<KhachHangDTO> onSaved) {
        super(parent, "Thêm khách hàng", ModalityType.APPLICATION_MODAL);
        this.onSaved = onSaved;

        setSize(390, 240);
        setResizable(false);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        mainPanel.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 5));
        form.setOpaque(false);

        txtTen = new JTextField();
        txtSdt = new JTextField();
        styleField(txtTen, "Nhập tên khách hàng...");
        styleField(txtSdt, "Nhập số điện thoại...");

        form.add(new JLabel("Tên khách hàng:"));
        form.add(txtTen);
        form.add(new JLabel("Số điện thoại:"));
        form.add(txtSdt);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, 38));

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        btnCancel.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;background:#F5F5F5;foreground:#333333;hoverBackground:#E0E0E0");
        btnSave.putClientProperty(FlatClientProperties.STYLE,
                "arc:8;background:#005A9E;foreground:#FFFFFF;hoverBackground:#004578");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveCustomer());
        getRootPane().setDefaultButton(btnSave);

        buttons.add(btnCancel);
        buttons.add(btnSave);

        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void styleField(JTextField field, String placeholder) {
        field.setPreferredSize(new Dimension(0, 34));
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc:8;focusWidth:0;innerFocusWidth:0");
    }

    private void saveCustomer() {
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty()) {
            showWarning("Vui lòng nhập đầy đủ tên và số điện thoại.");
            return;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            showWarning("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
            return;
        }

        KhachHangDTO customer = new KhachHangDTO(nextCustomerId(), ten, sdt, "", 0, 1);
        if (!khachHangBUS.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu khách hàng. Mã khách hàng có thể đã tồn tại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (onSaved != null)
            onSaved.accept(customer);
        dispose();
    }

    private String nextCustomerId() {
        int max = 0;
        List<KhachHangDTO> customers = khachHangBUS.getAll();
        if (customers != null) {
            for (KhachHangDTO customer : customers) {
                String id = customer.getMaKH();
                if (id == null || !id.toUpperCase().startsWith("KH"))
                    continue;

                try {
                    max = Math.max(max, Integer.parseInt(id.substring(2)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("KH%03d", max + 1);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }
}