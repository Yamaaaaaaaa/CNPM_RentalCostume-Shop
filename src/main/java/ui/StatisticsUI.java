package ui;

import dao.CostumeDAO;
import dao.InvoiceDAO;
import model.Costume;
import model.Invoice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsUI extends JFrame {
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTable costumeTable;
    private JTable invoiceTable;
    private DefaultTableModel costumeTableModel;
    private DefaultTableModel invoiceTableModel;
    private CostumeDAO costumeDAO;
    private InvoiceDAO invoiceDAO;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private JLabel selectedCostumeLabel;

    public StatisticsUI() {
        // Khởi tạo DAO
        costumeDAO = new CostumeDAO();
        invoiceDAO = new InvoiceDAO();

        // Thiết lập frame
        setTitle("Thống kê trang phục mượn nhiều");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tạo header panel cho input khoảng thời gian
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tạo split pane để chia đôi phần giữa
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Chia đều không gian

        // Panel hiển thị trang phục (nửa trên)
        JPanel costumePanel = createCostumePanel();
        splitPane.setTopComponent(costumePanel);

        // Panel hiển thị hóa đơn (nửa dưới)
        JPanel invoicePanel = createInvoicePanel();
        splitPane.setBottomComponent(invoicePanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Thêm panel chính vào frame
        add(mainPanel);

        // Hiển thị frame
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBorder(new TitledBorder("Chọn khoảng thời gian"));

        // Panel chứa các control input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Ngày bắt đầu
        JLabel startDateLabel = new JLabel("Ngày bắt đầu:");
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setPreferredSize(new Dimension(150, 25));

        // Ngày kết thúc
        JLabel endDateLabel = new JLabel("Ngày kết thúc:");
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setPreferredSize(new Dimension(150, 25));

        // Nút tìm kiếm
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> handleSearch());

        // Thêm các control vào panel
        inputPanel.add(startDateLabel);
        inputPanel.add(startDateSpinner);
        inputPanel.add(endDateLabel);
        inputPanel.add(endDateSpinner);
        inputPanel.add(searchButton);

        headerPanel.add(inputPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createCostumePanel() {
        JPanel costumePanel = new JPanel(new BorderLayout(10, 10));
        costumePanel.setBorder(new TitledBorder("Danh sách trang phục mượn nhiều"));

        // Tạo model cho bảng trang phục
        String[] columnNames = {"ID", "Tên trang phục", "Loại", "Kiểu", "Giá thuê", "Số lần được thuê"};
        costumeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tạo bảng trang phục
        costumeTable = new JTable(costumeTableModel);
        costumeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        costumeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        costumeTable.getTableHeader().setReorderingAllowed(false);

        // Thêm mouse listener để hiển thị hóa đơn khi click vào trang phục
        costumeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = costumeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int costumeId = Integer.parseInt(costumeTableModel.getValueAt(selectedRow, 0).toString());
                    String costumeName = costumeTableModel.getValueAt(selectedRow, 1).toString();
                    loadInvoicesForCostume(costumeId, costumeName);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(costumeTable);
        costumePanel.add(scrollPane, BorderLayout.CENTER);

        return costumePanel;
    }

    private JPanel createInvoicePanel() {
        JPanel invoicePanel = new JPanel(new BorderLayout(10, 10));
        invoicePanel.setBorder(new TitledBorder("Danh sách hóa đơn"));

        // Panel thông tin trang phục đã chọn
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedCostumeLabel = new JLabel("Chưa chọn trang phục");
        infoPanel.add(selectedCostumeLabel);
        invoicePanel.add(infoPanel, BorderLayout.NORTH);

        // Tạo model cho bảng hóa đơn
        String[] columnNames = {"ID", "Tiền đặt cọc", "Tổng tiền", "Ngày thuê", "Ngày trả", "Quản lý", "Khách hàng"};
        invoiceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tạo bảng hóa đơn
        invoiceTable = new JTable(invoiceTableModel);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        invoiceTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        invoicePanel.add(scrollPane, BorderLayout.CENTER);

        return invoicePanel;
    }

    private void handleSearch() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();

        // Kiểm tra ngày
        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật tiêu đề
        setTitle("Thống kê trang phục mượn nhiều từ " + dateFormat.format(startDate) + " đến " + dateFormat.format(endDate));

        // Tải dữ liệu trang phục phổ biến
        loadPopularCostumes(startDate, endDate);

        // Xóa dữ liệu hóa đơn và cập nhật label
        invoiceTableModel.setRowCount(0);
        selectedCostumeLabel.setText("Chưa chọn trang phục");
    }

    private void loadPopularCostumes(Date startDate, Date endDate) {
        try {
            // Xóa dữ liệu hiện tại
            costumeTableModel.setRowCount(0);

            // Lấy danh sách trang phục phổ biến trong khoảng thời gian
            Map<Costume, Integer> popularCostumes = costumeDAO.getPopularCostumeInRange(startDate, endDate);

            if (popularCostumes == null || popularCostumes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trang phục trong khoảng thời gian này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Thêm trang phục vào bảng
            for (Map.Entry<Costume, Integer> entry : popularCostumes.entrySet()) {
                Costume costume = entry.getKey();
                Integer rentCount = entry.getValue();

                Object[] rowData = new Object[]{
                        costume.getId(),
                        costume.getName(),
                        costume.getTypeCostumeName(),
                        costume.getStyleCostumeName(),
                        costume.getRentalPrice(),
                        rentCount
                };

                costumeTableModel.addRow(rowData);
            }

            // Đảm bảo bảng được cập nhật
            costumeTable.repaint();

        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu trang phục phổ biến: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInvoicesForCostume(int costumeId, String costumeName) {
        try {
            // Cập nhật label trang phục đã chọn
            selectedCostumeLabel.setText("Trang phục đã chọn: " + costumeName + " (ID: " + costumeId + ")");

            // Xóa dữ liệu hiện tại
            invoiceTableModel.setRowCount(0);

            // Lấy khoảng thời gian đã chọn
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();

            // Lấy danh sách hóa đơn đã thuê trang phục này
            List<Invoice> invoices = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);

            if (invoices == null || invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có hóa đơn nào thuê trang phục này trong khoảng thời gian đã chọn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Thêm hóa đơn vào bảng
            for (Invoice invoice : invoices) {
                Object[] rowData = new Object[]{
                        invoice.getId(),
                        invoice.getDeposit(),
                        invoice.getTotalAmount(),
                        dateFormat.format(invoice.getRentalDate()),
                        dateFormat.format(invoice.getReturnDate()),
                        invoice.getManagerName(),
                        invoice.getCustomerName()
                };

                invoiceTableModel.addRow(rowData);
            }

            // Đảm bảo bảng được cập nhật
            invoiceTable.repaint();

        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new StatisticsUI();
            } catch (Exception e) {
                System.err.println("Lỗi khi khởi tạo StatisticsUI: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi khởi tạo giao diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}