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
    private InputDateRangeForm dateRangeForm;
    private SortedCostumeView costumeView;
    private SortedInvoiceView invoiceView;
    private CostumeDAO costumeDAO;
    private InvoiceDAO invoiceDAO;

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

        // Khởi tạo các component
        dateRangeForm = new InputDateRangeForm();
        costumeView = new SortedCostumeView();
        invoiceView = new SortedInvoiceView();

        // Thêm form date range vào phần trên
        mainPanel.add(dateRangeForm, BorderLayout.NORTH);

        // Tạo split pane để chia đôi phần giữa
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Chia đều không gian

        // Thêm view costume vào phần trên của split pane
        splitPane.setTopComponent(costumeView);

        // Thêm view invoice vào phần dưới của split pane
        splitPane.setBottomComponent(invoiceView);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Thêm panel chính vào frame
        add(mainPanel);

        // Hiển thị frame
        setVisible(true);
    }

    // Class quản lý phần nhập khoảng thời gian
    class InputDateRangeForm extends JPanel {
        private JSpinner startDateSpinner;
        private JSpinner endDateSpinner;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        public InputDateRangeForm() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new TitledBorder("Chọn khoảng thời gian"));

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

            add(inputPanel, BorderLayout.CENTER);
        }

        public Date getStartDate() {
            return (Date) startDateSpinner.getValue();
        }

        public Date getEndDate() {
            return (Date) endDateSpinner.getValue();
        }

        public String formatDate(Date date) {
            return dateFormat.format(date);
        }
    }

    // Class quản lý phần hiển thị trang phục
    class SortedCostumeView extends JPanel {
        private JTable costumeTable;
        private DefaultTableModel costumeTableModel;
        public SortedCostumeView() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new TitledBorder("Danh sách trang phục mượn nhiều"));
            // Tạo model cho bảng trang phục
            String[] columnNames = {"ID", "Tên trang phục", "Loại", "Kiểu", "Giá thuê", "Số lần được thuê", "Tổng tiền thu được"};
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
            add(scrollPane, BorderLayout.CENTER);
        }

        public void updateCostumeData(Map<Costume, Integer> popularCostumes) {
            // Xóa dữ liệu hiện tại
            costumeTableModel.setRowCount(0);

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
                        rentCount,
                        costume.getRentalPrice() * rentCount
                };

                costumeTableModel.addRow(rowData);
            }

            // Đảm bảo bảng được cập nhật
            costumeTable.repaint();
        }
    }

    // Class quản lý phần hiển thị hóa đơn
    class SortedInvoiceView extends JPanel {
        private JTable invoiceTable;
        private DefaultTableModel invoiceTableModel;
        private JLabel selectedCostumeLabel;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        public SortedInvoiceView() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new TitledBorder("Danh sách hóa đơn"));
            // Panel thông tin trang phục đã chọn
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectedCostumeLabel = new JLabel("Chưa chọn trang phục");
            infoPanel.add(selectedCostumeLabel);
            add(infoPanel, BorderLayout.NORTH);

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
            add(scrollPane, BorderLayout.CENTER);
        }

        public void loadInvoices(int costumeId, String costumeName, Date startDate, Date endDate) {
            // Cập nhật label trang phục đã chọn
            selectedCostumeLabel.setText("Trang phục đã chọn: " + costumeName + " (ID: " + costumeId + ")");

            // Xóa dữ liệu hiện tại
            invoiceTableModel.setRowCount(0);

            // Lấy danh sách hóa đơn đã thuê trang phục này
            List<Invoice> invoices = invoiceDAO.getInvoiceRentedCostume(costumeId, startDate, endDate);

            if (invoices == null || invoices.isEmpty()) {
                JOptionPane.showMessageDialog(StatisticsUI.this, "Không có hóa đơn nào thuê trang phục này trong khoảng thời gian đã chọn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        }

        public void clearInvoices() {
            invoiceTableModel.setRowCount(0);
            selectedCostumeLabel.setText("Chưa chọn trang phục");
        }
    }

    // Xử lý sự kiện tìm kiếm
    private void handleSearch() {
        Date startDate = dateRangeForm.getStartDate();
        Date endDate = dateRangeForm.getEndDate();

        // Kiểm tra ngày
        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật tiêu đề
        setTitle("Thống kê trang phục mượn nhiều từ " + dateRangeForm.formatDate(startDate) + " đến " + dateRangeForm.formatDate(endDate));

        // Tải dữ liệu trang phục phổ biến
        loadPopularCostumes(startDate, endDate);

        // Xóa dữ liệu hóa đơn và cập nhật label
        invoiceView.clearInvoices();
    }

    // Tải dữ liệu trang phục phổ biến
    private void loadPopularCostumes(Date startDate, Date endDate) {
        try {
            // Lấy danh sách trang phục phổ biến trong khoảng thời gian
            Map<Costume, Integer> popularCostumes = costumeDAO.getPopularCostumeInRange(startDate, endDate);

            if (popularCostumes == null || popularCostumes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trang phục trong khoảng thời gian này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Cập nhật view costume
            costumeView.updateCostumeData(popularCostumes);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu trang phục phổ biến: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Tải dữ liệu hóa đơn cho trang phục đã chọn
    private void loadInvoicesForCostume(int costumeId, String costumeName) {
        try {
            // Lấy khoảng thời gian đã chọn
            Date startDate = dateRangeForm.getStartDate();
            Date endDate = dateRangeForm.getEndDate();

            // Cập nhật view invoice
            invoiceView.loadInvoices(costumeId, costumeName, startDate, endDate);

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