package ui;

import dao.InvoiceDAO;
import dao.InvoiceCostumeDAO;
import dao.ManagerDAO;
import dao.CustomerDAO;
import dao.CostumeDAO;
import model.Invoice;
import model.InvoiceCostume;
import model.Manager;
import model.Customer;
import model.Costume;

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
import java.util.ArrayList;

public class InvoiceUI extends JFrame {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private InvoiceDAO invoiceDAO;
    private InvoiceCostumeDAO invoiceCostumeDAO;
    private ManagerDAO managerDAO;
    private CustomerDAO customerDAO;
    private CostumeDAO costumeDAO;
    private JButton createButton;

    public InvoiceUI() {
        // Khởi tạo DAO
        invoiceDAO = new InvoiceDAO();
        invoiceCostumeDAO = new InvoiceCostumeDAO();
        managerDAO = new ManagerDAO();
        customerDAO = new CustomerDAO();
        costumeDAO = new CostumeDAO();

        // Thiết lập frame
        setTitle("Quản lý hóa đơn");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tạo panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createButton = new JButton("Tạo hóa đơn mới");
        createButton.addActionListener(e -> showCreateInvoiceDialog());
        buttonPanel.add(createButton);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Tạo bảng
        createTable();
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm panel chính vào frame
        add(mainPanel);

        // Hiển thị frame
        setVisible(true);

        // Tải dữ liệu
        loadInvoiceData();
    }

    private void createTable() {
        // Tạo model cho bảng
        String[] columnNames = {"ID", "Tiền đặt cọc", "Tổng tiền", "Ngày thuê", "Ngày trả", "Quản lý", "Loại hóa đơn", "Khách hàng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tạo bảng
        invoiceTable = new JTable(tableModel);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        invoiceTable.getTableHeader().setReorderingAllowed(false);

        // Thiết lập kích thước cột
        invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        invoiceTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Tiền đặt cọc
        invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Tổng tiền
        invoiceTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Ngày thuê
        invoiceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Ngày trả
        invoiceTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Quản lý
        invoiceTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Loại hóa đơn
        invoiceTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Khách hàng

        // Thêm mouse listener để hiển thị chi tiết khi click vào hóa đơn
        invoiceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = invoiceTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int invoiceId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                        showInvoiceDetails(invoiceId);
                    }
                }
            }
        });
    }

    private void loadInvoiceData() {
        try {
            // Xóa dữ liệu hiện tại
            tableModel.setRowCount(0);

            // Lấy tất cả hóa đơn
            List<Invoice> invoices = invoiceDAO.getAllInvoices();

            // Kiểm tra xem có dữ liệu không
            if (invoices == null || invoices.isEmpty()) {
                System.out.println("Không có dữ liệu hóa đơn");
                return;
            }

            System.out.println("Đã tải " + invoices.size() + " hóa đơn");

            // Thêm hóa đơn vào bảng
            for (Invoice invoice : invoices) {
                Object[] rowData = new Object[]{
                        invoice.getId(),
                        invoice.getDeposit(),
                        invoice.getTotalAmount(),
                        dateFormat.format(invoice.getRentalDate()),
                        dateFormat.format(invoice.getReturnDate()),
                        invoice.getManagerName(),
                        invoice.getTypeInvoice(),
                        invoice.getCustomerName()
                };

                tableModel.addRow(rowData);
            }

            // Đảm bảo bảng được cập nhật
            invoiceTable.repaint();

            // Kiểm tra số hàng trong bảng
            System.out.println("Số hàng trong bảng sau khi tải: " + tableModel.getRowCount());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu hóa đơn: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInvoiceDetails(int invoiceId) {
        try {
            // Lấy thông tin hóa đơn
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
            if (invoice == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với ID: " + invoiceId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy danh sách chi tiết hóa đơn
            List<InvoiceCostume> details = invoiceCostumeDAO.getInvoiceCostumesByInvoiceId(invoiceId);
            if (details == null || details.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này không có chi tiết nào", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Tạo dialog để hiển thị chi tiết
            JDialog detailDialog = new JDialog(this, "Chi tiết hóa đơn #" + invoiceId, true);
            detailDialog.setSize(800, 400);
            detailDialog.setLocationRelativeTo(this);

            // Tạo panel chính
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Tạo panel thông tin hóa đơn
            JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 0));
            infoPanel.setBorder(new TitledBorder("Thông tin hóa đơn"));

            infoPanel.add(new JLabel("Khách hàng: " + invoice.getCustomerName()));
            infoPanel.add(new JLabel("Ngày thuê: " + dateFormat.format(invoice.getRentalDate())));
            infoPanel.add(new JLabel("Ngày trả: " + dateFormat.format(invoice.getReturnDate())));
            infoPanel.add(new JLabel("Tổng tiền: " + invoice.getTotalAmount()));

            mainPanel.add(infoPanel, BorderLayout.NORTH);

            // Tạo bảng chi tiết
            String[] columnNames = {"ID", "Trang phục", "Số lượng", "Đơn giá", "Trạng thái", "Phí phạt", "Thành tiền"};
            DefaultTableModel detailModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable detailTable = new JTable(detailModel);
            detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            detailTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            detailTable.getTableHeader().setReorderingAllowed(false);

            // Thêm dữ liệu vào bảng chi tiết
            for (InvoiceCostume detail : details) {
                float total = detail.getQuantity() * detail.getPricePerUnit() + detail.getPenaltyFee();
                detailModel.addRow(new Object[]{
                        detail.getId(),
                        detail.getCostumeName(),
                        detail.getQuantity(),
                        detail.getPricePerUnit(),
                        detail.getStatus(),
                        detail.getPenaltyFee(),
                        total
                });
            }

            JScrollPane scrollPane = new JScrollPane(detailTable);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Thêm nút đóng
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> detailDialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Thêm panel vào dialog
            detailDialog.add(mainPanel);

            // Hiển thị dialog
            detailDialog.setVisible(true);

        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCreateInvoiceDialog() {
        try {
            // Tạo dialog để nhập thông tin hóa đơn mới
            JDialog createDialog = new JDialog(this, "Tạo hóa đơn mới", true);
            createDialog.setSize(800, 600);
            createDialog.setLocationRelativeTo(this);

            // Tạo panel chính
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Tạo tabbed pane để chia thành 2 tab: Thông tin hóa đơn và Chọn trang phục
            JTabbedPane tabbedPane = new JTabbedPane();

            // Tab 1: Thông tin hóa đơn
            JPanel invoiceInfoPanel = new JPanel(new BorderLayout(10, 10));

            // Tạo form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Tiền đặt cọc
            JLabel depositLabel = new JLabel("Tiền đặt cọc:");
            JTextField depositField = new JTextField(15);

            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(depositLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            formPanel.add(depositField, gbc);

            // Tổng tiền (sẽ được tính tự động dựa trên các trang phục đã chọn)
            JLabel totalLabel = new JLabel("Tổng tiền:");
            JTextField totalField = new JTextField(15);
            totalField.setEditable(false);
            totalField.setText("0");

            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(totalLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            formPanel.add(totalField, gbc);

            // Ngày thuê
            JLabel rentalDateLabel = new JLabel("Ngày thuê:");
            SpinnerDateModel rentalDateModel = new SpinnerDateModel();
            JSpinner rentalDateSpinner = new JSpinner(rentalDateModel);
            JSpinner.DateEditor rentalDateEditor = new JSpinner.DateEditor(rentalDateSpinner, "yyyy-MM-dd");
            rentalDateSpinner.setEditor(rentalDateEditor);
            rentalDateSpinner.setValue(new Date());

            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(rentalDateLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            formPanel.add(rentalDateSpinner, gbc);

            // Ngày trả
            JLabel returnDateLabel = new JLabel("Ngày trả:");
            SpinnerDateModel returnDateModel = new SpinnerDateModel();
            JSpinner returnDateSpinner = new JSpinner(returnDateModel);
            JSpinner.DateEditor returnDateEditor = new JSpinner.DateEditor(returnDateSpinner, "yyyy-MM-dd");
            returnDateSpinner.setEditor(returnDateEditor);
            returnDateSpinner.setValue(new Date());

            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(returnDateLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            formPanel.add(returnDateSpinner, gbc);

            // Quản lý
            JLabel managerLabel = new JLabel("Quản lý:");
            JComboBox<Manager> managerComboBox = new JComboBox<>();

            // Lấy danh sách quản lý
            List<Manager> managers = managerDAO.getAllManagers();
            if (managers != null && !managers.isEmpty()) {
                for (Manager manager : managers) {
                    managerComboBox.addItem(manager);
                }
            }

            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(managerLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 4;
            formPanel.add(managerComboBox, gbc);

            // Loại hóa đơn
            JLabel typeLabel = new JLabel("Loại hóa đơn:");
            JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"rent", "import"});

            gbc.gridx = 0;
            gbc.gridy = 5;
            formPanel.add(typeLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 5;
            formPanel.add(typeComboBox, gbc);

            // Khách hàng
            JLabel customerLabel = new JLabel("Khách hàng:");
            JComboBox<Customer> customerComboBox = new JComboBox<>();

            // Lấy danh sách khách hàng
            List<Customer> customers = customerDAO.getAllCustomers();
            if (customers != null && !customers.isEmpty()) {
                for (Customer customer : customers) {
                    customerComboBox.addItem(customer);
                }
            }

            gbc.gridx = 0;
            gbc.gridy = 6;
            formPanel.add(customerLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 6;
            formPanel.add(customerComboBox, gbc);

            invoiceInfoPanel.add(formPanel, BorderLayout.NORTH);

            // Tab 2: Chọn trang phục
            JPanel costumePanel = new JPanel(new BorderLayout(10, 10));

            // Panel chọn trang phục
            JPanel selectCostumePanel = new JPanel(new GridBagLayout());
            selectCostumePanel.setBorder(new TitledBorder("Chọn trang phục"));

            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Trang phục
            JLabel costumeLabel = new JLabel("Trang phục:");
            JComboBox<Costume> costumeComboBox = new JComboBox<>();

            // Lấy danh sách trang phục
            List<Costume> costumes = costumeDAO.getAllCostumes();
            if (costumes != null && !costumes.isEmpty()) {
                for (Costume costume : costumes) {
                    costumeComboBox.addItem(costume);
                }
            }

            gbc.gridx = 0;
            gbc.gridy = 0;
            selectCostumePanel.add(costumeLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            selectCostumePanel.add(costumeComboBox, gbc);

            // Số lượng
            JLabel quantityLabel = new JLabel("Số lượng:");
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            gbc.gridx = 0;
            gbc.gridy = 1;
            selectCostumePanel.add(quantityLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            selectCostumePanel.add(quantitySpinner, gbc);

            // Đơn giá
            JLabel priceLabel = new JLabel("Đơn giá:");
            JTextField priceField = new JTextField(10);
            priceField.setEditable(false);

            // Cập nhật đơn giá khi chọn trang phục
            costumeComboBox.addActionListener(e -> {
                Costume selectedCostume = (Costume) costumeComboBox.getSelectedItem();
                if (selectedCostume != null) {
                    priceField.setText(String.valueOf(selectedCostume.getRentalPrice()));
                }
            });

            // Thiết lập giá trị ban đầu
            if (costumeComboBox.getItemCount() > 0) {
                Costume firstCostume = (Costume) costumeComboBox.getItemAt(0);
                priceField.setText(String.valueOf(firstCostume.getRentalPrice()));
            }

            gbc.gridx = 0;
            gbc.gridy = 2;
            selectCostumePanel.add(priceLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            selectCostumePanel.add(priceField, gbc);

            // Nút thêm trang phục
            JButton addCostumeButton = new JButton("Thêm trang phục");

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            selectCostumePanel.add(addCostumeButton, gbc);

            costumePanel.add(selectCostumePanel, BorderLayout.NORTH);

            // Bảng trang phục đã chọn
            String[] costumeColumnNames = {"Trang phục", "Số lượng", "Đơn giá", "Thành tiền"};
            DefaultTableModel costumeModel = new DefaultTableModel(costumeColumnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable costumeTable = new JTable(costumeModel);
            costumeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane costumeScrollPane = new JScrollPane(costumeTable);
            costumePanel.add(costumeScrollPane, BorderLayout.CENTER);

            // Panel nút cho trang phục
            JPanel costumeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton removeCostumeButton = new JButton("Xóa trang phục đã chọn");
            costumeButtonPanel.add(removeCostumeButton);
            costumePanel.add(costumeButtonPanel, BorderLayout.SOUTH);

            // Danh sách lưu trữ trang phục đã chọn
            List<CostumeSelection> selectedCostumes = new ArrayList<>();

            // Xử lý sự kiện thêm trang phục
            addCostumeButton.addActionListener(e -> {
                Costume selectedCostume = (Costume) costumeComboBox.getSelectedItem();
                if (selectedCostume == null) {
                    JOptionPane.showMessageDialog(createDialog, "Vui lòng chọn trang phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity = (int) quantitySpinner.getValue();

                // Kiểm tra số lượng tồn kho
                if (quantity > selectedCostume.getStockQuantity()) {
                    JOptionPane.showMessageDialog(createDialog,
                            "Số lượng yêu cầu (" + quantity + ") vượt quá số lượng tồn kho (" + selectedCostume.getStockQuantity() + ")!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra xem trang phục đã được chọn chưa
                boolean alreadySelected = false;
                for (CostumeSelection cs : selectedCostumes) {
                    if (cs.getCostume().getId() == selectedCostume.getId()) {
                        // Cập nhật số lượng nếu trang phục đã được chọn
                        int newQuantity = cs.getQuantity() + quantity;
                        if (newQuantity > selectedCostume.getStockQuantity()) {
                            JOptionPane.showMessageDialog(createDialog,
                                    "Tổng số lượng yêu cầu (" + newQuantity + ") vượt quá số lượng tồn kho (" + selectedCostume.getStockQuantity() + ")!",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        cs.setQuantity(newQuantity);
                        alreadySelected = true;
                        break;
                    }
                }

                if (!alreadySelected) {
                    // Thêm trang phục mới vào danh sách
                    selectedCostumes.add(new CostumeSelection(selectedCostume, quantity));
                }

                // Cập nhật bảng
                updateCostumeTable(costumeModel, selectedCostumes);

                // Cập nhật tổng tiền
                updateTotalAmount(totalField, selectedCostumes);
            });

            // Xử lý sự kiện xóa trang phục
            removeCostumeButton.addActionListener(e -> {
                int selectedRow = costumeTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < selectedCostumes.size()) {
                    selectedCostumes.remove(selectedRow);

                    // Cập nhật bảng
                    updateCostumeTable(costumeModel, selectedCostumes);

                    // Cập nhật tổng tiền
                    updateTotalAmount(totalField, selectedCostumes);
                } else {
                    JOptionPane.showMessageDialog(createDialog, "Vui lòng chọn trang phục để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Thêm các tab vào tabbed pane
            tabbedPane.addTab("Thông tin hóa đơn", invoiceInfoPanel);
            tabbedPane.addTab("Chọn trang phục", costumePanel);

            mainPanel.add(tabbedPane, BorderLayout.CENTER);

            // Tạo panel nút
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton saveButton = new JButton("Lưu");
            JButton cancelButton = new JButton("Hủy");

            saveButton.addActionListener(e -> {
                try {
                    // Kiểm tra dữ liệu đầu vào
                    if (depositField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(createDialog, "Vui lòng nhập tiền đặt cọc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tabbedPane.setSelectedIndex(0); // Chuyển về tab thông tin hóa đơn
                        return;
                    }

                    if (managerComboBox.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(createDialog, "Vui lòng chọn quản lý!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tabbedPane.setSelectedIndex(0);
                        return;
                    }

                    if (customerComboBox.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(createDialog, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tabbedPane.setSelectedIndex(0);
                        return;
                    }

                    if (selectedCostumes.isEmpty()) {
                        JOptionPane.showMessageDialog(createDialog, "Vui lòng chọn ít nhất một trang phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tabbedPane.setSelectedIndex(1); // Chuyển đến tab chọn trang phục
                        return;
                    }

                    // Lấy dữ liệu từ form
                    float deposit = Float.parseFloat(depositField.getText().trim());
                    float totalAmount = Float.parseFloat(totalField.getText().trim());
                    Date rentalDate = (Date) rentalDateSpinner.getValue();
                    Date returnDate = (Date) returnDateSpinner.getValue();
                    Manager selectedManager = (Manager) managerComboBox.getSelectedItem();
                    String typeInvoice = (String) typeComboBox.getSelectedItem();
                    Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();

                    // Kiểm tra ngày
                    if (returnDate.before(rentalDate)) {
                        JOptionPane.showMessageDialog(createDialog, "Ngày trả phải sau ngày thuê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        tabbedPane.setSelectedIndex(0);
                        return;
                    }

                    // Tạo đối tượng hóa đơn mới
                    Invoice newInvoice = new Invoice(
                            0, // ID sẽ được tạo bởi database
                            deposit,
                            totalAmount,
                            rentalDate,
                            returnDate,
                            selectedManager.getId(),
                            typeInvoice,
                            selectedCustomer.getId()
                    );

                    // Lưu hóa đơn vào database
                    int invoiceId = invoiceDAO.insertInvoice(newInvoice);

                    if (invoiceId <= 0) {
                        JOptionPane.showMessageDialog(createDialog, "Lỗi khi tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Lưu chi tiết hóa đơn
                    for (CostumeSelection cs : selectedCostumes) {
                        InvoiceCostume invoiceCostume = new InvoiceCostume(
                                0, // ID sẽ được tạo bởi database
                                cs.getQuantity(),
                                cs.getCostume().getRentalPrice(),
                                "pending", // Trạng thái mặc định
                                0, // Phí phạt mặc định
                                invoiceId,
                                cs.getCostume().getId()
                        );

                        invoiceCostumeDAO.insertInvoiceCostume(invoiceCostume);

                        // Cập nhật số lượng tồn kho nếu là hóa đơn thuê
                        if ("rent".equals(typeInvoice)) {
                            costumeDAO.updateStock(cs.getCostume().getId(), -cs.getQuantity());
                        }
                    }

                    // Hiển thị thông báo thành công
                    JOptionPane.showMessageDialog(createDialog, "Tạo hóa đơn mới thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    // Đóng dialog
                    createDialog.dispose();

                    // Tải lại dữ liệu
                    loadInvoiceData();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(createDialog, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Lỗi khi tạo hóa đơn mới: " + ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(createDialog, "Lỗi khi tạo hóa đơn mới: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> createDialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Thêm panel vào dialog
            createDialog.add(mainPanel);

            // Hiển thị dialog
            createDialog.setVisible(true);

        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị dialog tạo hóa đơn: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị dialog tạo hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cập nhật bảng trang phục đã chọn
    private void updateCostumeTable(DefaultTableModel model, List<CostumeSelection> costumes) {
        // Xóa dữ liệu hiện tại
        model.setRowCount(0);

        // Thêm dữ liệu mới
        for (CostumeSelection cs : costumes) {
            float total = cs.getQuantity() * cs.getCostume().getRentalPrice();
            model.addRow(new Object[]{
                    cs.getCostume().getName(),
                    cs.getQuantity(),
                    cs.getCostume().getRentalPrice(),
                    total
            });
        }
    }

    // Cập nhật tổng tiền
    private void updateTotalAmount(JTextField totalField, List<CostumeSelection> costumes) {
        float total = 0;
        for (CostumeSelection cs : costumes) {
            total += cs.getQuantity() * cs.getCostume().getRentalPrice();
        }
        totalField.setText(String.valueOf(total));
    }

    // Lớp lưu trữ thông tin trang phục đã chọn
    private class CostumeSelection {
        private Costume costume;
        private int quantity;

        public CostumeSelection(Costume costume, int quantity) {
            this.costume = costume;
            this.quantity = quantity;
        }

        public Costume getCostume() {
            return costume;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
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
                new InvoiceUI();
            } catch (Exception e) {
                System.err.println("Lỗi khi khởi tạo InvoiceUI: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi khởi tạo giao diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}