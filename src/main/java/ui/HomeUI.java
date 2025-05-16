// File: ui/HomeUI.java
package ui;

import javax.swing.*;
import java.awt.*;

public class HomeUI extends JFrame {
    public HomeUI() {
        setTitle("Trang chủ");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton btnCostume = new JButton("Quản lý trang phục");
        JButton btnInvoice = new JButton("Quản lý hóa đơn");
        JButton btnStats = new JButton("Thống kê thuê nhiều");
        JButton btnLogout = new JButton("Đăng xuất");

        btnCostume.addActionListener(e -> new CostumeUI());
        btnInvoice.addActionListener(e -> new InvoiceUI());
        btnStats.addActionListener(e -> new StatisticsUI());
        btnLogout.addActionListener(e -> {
            new LoginUI();
            dispose();
        });

        setLayout(new GridLayout(4, 1));
        add(btnCostume);
        add(btnInvoice);
        add(btnStats);
        add(btnLogout);

        setVisible(true);
    }
}