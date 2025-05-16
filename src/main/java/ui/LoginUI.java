package ui;

import dao.ManagerDAO;
import model.Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginUI extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JLabel statusLabel;

    public LoginUI() {
        setTitle("Đăng nhập hệ thống quản lý trang phục");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        JLabel userLabel = new JLabel("Tên đăng nhập:");
        userField = new JTextField(20);
        userField.addKeyListener(new EnterKeyListener());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(userField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Mật khẩu:");
        passField = new JPasswordField(20);
        passField.addKeyListener(new EnterKeyListener());

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passField, gbc);

        // Status label for error messages
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        loginBtn = new JButton("Đăng nhập");
        loginBtn.setPreferredSize(new Dimension(120, 30));
        loginBtn.addActionListener(this::loginAction);

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(120, 30));
        cancelBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Set focus to username field
        SwingUtilities.invokeLater(() -> userField.requestFocusInWindow());

        setVisible(true);
    }

    private void loginAction(ActionEvent e) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin đăng nhập!");
            return;
        }

        // Attempt login
        ManagerDAO managerDAO = new ManagerDAO();
        Manager manager = managerDAO.login(username, password);

        if (manager != null) {
            // Login successful
            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công! Xin chào " + manager.getName(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            // Open home UI and close login window
            new HomeUI();
            dispose();
        } else {
            // Login failed
            statusLabel.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
            passField.setText("");
            passField.requestFocusInWindow();
        }
    }

    // Inner class to handle Enter key press
    private class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                loginAction(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginUI());
    }
}