package ui;

import dao.CostumeDAO;
import dao.StyleCostumeDAO;
import dao.TypeCostumeDAO;
import model.Costume;
import model.StyleCostume;
import model.TypeCostume;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CostumeUI extends JFrame {
    private JTextField idField, nameField, descriptionField, stockField, rentalPriceField, originalCostField;
    private JComboBox<StyleCostume> styleComboBox;
    private JComboBox<TypeCostume> typeComboBox;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, searchBtn;
    private JTextField searchField;
    private JTable costumeTable;
    private DefaultTableModel tableModel;

    private CostumeDAO costumeDAO;
    private StyleCostumeDAO styleCostumeDAO;
    private TypeCostumeDAO typeCostumeDAO;

    public CostumeUI() {
        // Initialize DAOs
        costumeDAO = new CostumeDAO();
        styleCostumeDAO = new StyleCostumeDAO();
        typeCostumeDAO = new TypeCostumeDAO();

        // Set up the frame
        setTitle("Quản lý trang phục");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Load initial data
        loadCostumeData();
        loadComboBoxData();

        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Thông tin trang phục"));

        // Input fields panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID field (disabled for new entries)
        JLabel idLabel = new JLabel("ID:");
        idField = new JTextField(10);
        idField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(idField, gbc);

        // Name field
        JLabel nameLabel = new JLabel("Tên trang phục:");
        nameField = new JTextField(20);

        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(nameLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        inputPanel.add(nameField, gbc);

        // Description field
        JLabel descLabel = new JLabel("Mô tả:");
        descriptionField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        inputPanel.add(descriptionField, gbc);

        // Reset gridwidth
        gbc.gridwidth = 1;

        // Stock quantity field
        JLabel stockLabel = new JLabel("Số lượng tồn kho:");
        stockField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(stockLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(stockField, gbc);

        // Rental price field
        JLabel rentalPriceLabel = new JLabel("Giá thuê:");
        rentalPriceField = new JTextField(10);

        gbc.gridx = 2;
        gbc.gridy = 2;
        inputPanel.add(rentalPriceLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        inputPanel.add(rentalPriceField, gbc);

        // Original cost field
        JLabel originalCostLabel = new JLabel("Giá gốc:");
        originalCostField = new JTextField(10);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(originalCostLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(originalCostField, gbc);

        // Style combo box
        JLabel styleLabel = new JLabel("Kiểu trang phục:");
        styleComboBox = new JComboBox<>();

        gbc.gridx = 2;
        gbc.gridy = 3;
        inputPanel.add(styleLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        inputPanel.add(styleComboBox, gbc);

        // Type combo box
        JLabel typeLabel = new JLabel("Loại trang phục:");
        typeComboBox = new JComboBox<>();

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        inputPanel.add(typeComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addBtn = new JButton("Thêm");
        updateBtn = new JButton("Cập nhật");
        deleteBtn = new JButton("Xóa");
        clearBtn = new JButton("Làm mới");

        addBtn.addActionListener(e -> addCostume());
        updateBtn.addActionListener(e -> updateCostume());
        deleteBtn.addActionListener(e -> deleteCostume());
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        // Add components to form panel
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new TitledBorder("Danh sách trang phục"));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchField = new JTextField(20);
        searchBtn = new JButton("Tìm");
        JButton refreshBtn = new JButton("Tải lại");

        searchBtn.addActionListener(e -> searchCostumes());
        refreshBtn.addActionListener(e -> loadCostumeData());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Table
        String[] columnNames = {"ID", "Tên trang phục", "Mô tả", "Số lượng", "Giá thuê", "Giá gốc", "Kiểu", "Loại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        costumeTable = new JTable(tableModel);
        costumeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        costumeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add mouse listener to table
        costumeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = costumeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    displayCostumeDetails(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(costumeTable);

        // Add components to panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadCostumeData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get all costumes
        List<Costume> costumes = costumeDAO.getAllCostumes();

        // Add costumes to table
        for (Costume costume : costumes) {
            tableModel.addRow(new Object[]{
                    costume.getId(),
                    costume.getName(),
                    costume.getDescription(),
                    costume.getStockQuantity(),
                    costume.getRentalPrice(),
                    costume.getOriginalCost(),
                    costume.getStyleCostumeName(),
                    costume.getTypeCostumeName()
            });
        }
    }

    private void loadComboBoxData() {
        // Clear existing items
        styleComboBox.removeAllItems();
        typeComboBox.removeAllItems();

        // Load style costumes
        List<StyleCostume> styles = styleCostumeDAO.getAllStyleCostumes();
        for (StyleCostume style : styles) {
            styleComboBox.addItem(style);
        }

        // Load type costumes
        List<TypeCostume> types = typeCostumeDAO.getAllTypeCostumes();
        for (TypeCostume type : types) {
            typeComboBox.addItem(type);
        }
    }

    private void displayCostumeDetails(int row) {
        // Get data from selected row
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String description = (String) tableModel.getValueAt(row, 2);
        int stockQuantity = (int) tableModel.getValueAt(row, 3);
        float rentalPrice = (float) tableModel.getValueAt(row, 4);
        float originalCost = (float) tableModel.getValueAt(row, 5);
        String styleName = (String) tableModel.getValueAt(row, 6);
        String typeName = (String) tableModel.getValueAt(row, 7);

        // Set values to form fields
        idField.setText(String.valueOf(id));
        nameField.setText(name);
        descriptionField.setText(description);
        stockField.setText(String.valueOf(stockQuantity));
        rentalPriceField.setText(String.valueOf(rentalPrice));
        originalCostField.setText(String.valueOf(originalCost));

        // Select style in combo box
        for (int i = 0; i < styleComboBox.getItemCount(); i++) {
            if (styleComboBox.getItemAt(i).toString().equals(styleName)) {
                styleComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Select type in combo box
        for (int i = 0; i < typeComboBox.getItemCount(); i++) {
            if (typeComboBox.getItemAt(i).toString().equals(typeName)) {
                typeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void addCostume() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Get values from form
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            int stockQuantity = Integer.parseInt(stockField.getText().trim());
            float rentalPrice = Float.parseFloat(rentalPriceField.getText().trim());
            float originalCost = Float.parseFloat(originalCostField.getText().trim());
            StyleCostume selectedStyle = (StyleCostume) styleComboBox.getSelectedItem();
            TypeCostume selectedType = (TypeCostume) typeComboBox.getSelectedItem();

            // Create costume object
            Costume costume = new Costume(
                    0, // ID will be generated by database
                    name,
                    description,
                    stockQuantity,
                    rentalPrice,
                    originalCost,
                    selectedStyle.getId(),
                    selectedType.getId()
            );

            // Add costume to database
            costumeDAO.insertCostume(costume);

            // Show success message
            JOptionPane.showMessageDialog(this, "Thêm trang phục thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Reload data and clear form
            loadCostumeData();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCostume() {
        // Check if a costume is selected
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trang phục để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Get values from form
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            int stockQuantity = Integer.parseInt(stockField.getText().trim());
            float rentalPrice = Float.parseFloat(rentalPriceField.getText().trim());
            float originalCost = Float.parseFloat(originalCostField.getText().trim());
            StyleCostume selectedStyle = (StyleCostume) styleComboBox.getSelectedItem();
            TypeCostume selectedType = (TypeCostume) typeComboBox.getSelectedItem();

            // Create costume object
            Costume costume = new Costume(
                    id,
                    name,
                    description,
                    stockQuantity,
                    rentalPrice,
                    originalCost,
                    selectedStyle.getId(),
                    selectedType.getId()
            );

            // Update costume in database
            costumeDAO.updateCostume(costume);

            // Show success message
            JOptionPane.showMessageDialog(this, "Cập nhật trang phục thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Reload data and clear form
            loadCostumeData();
            clearForm();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCostume() {
        // Check if a costume is selected
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trang phục để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa trang phục này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get costume ID
                int id = Integer.parseInt(idField.getText().trim());

                // Delete costume from database
                costumeDAO.deleteCostume(id);

                // Show success message
                JOptionPane.showMessageDialog(this, "Xóa trang phục thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Reload data and clear form
                loadCostumeData();
                clearForm();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCostumes() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadCostumeData();
            return;
        }

        // Clear existing data
        tableModel.setRowCount(0);

        // Get all costumes
        List<Costume> costumes = costumeDAO.getAllCostumes();

        // Filter costumes by search term
        for (Costume costume : costumes) {
            if (costume.getName().toLowerCase().contains(searchTerm) ||
                    costume.getDescription().toLowerCase().contains(searchTerm) ||
                    costume.getStyleCostumeName().toLowerCase().contains(searchTerm) ||
                    costume.getTypeCostumeName().toLowerCase().contains(searchTerm)) {

                tableModel.addRow(new Object[]{
                        costume.getId(),
                        costume.getName(),
                        costume.getDescription(),
                        costume.getStockQuantity(),
                        costume.getRentalPrice(),
                        costume.getOriginalCost(),
                        costume.getStyleCostumeName(),
                        costume.getTypeCostumeName()
                });
            }
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        stockField.setText("");
        rentalPriceField.setText("");
        originalCostField.setText("");

        if (styleComboBox.getItemCount() > 0) {
            styleComboBox.setSelectedIndex(0);
        }

        if (typeComboBox.getItemCount() > 0) {
            typeComboBox.setSelectedIndex(0);
        }

        costumeTable.clearSelection();
    }

    private boolean validateInput() {
        // Check for empty fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên trang phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (stockField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng tồn kho!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            stockField.requestFocus();
            return false;
        }

        if (rentalPriceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá thuê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            rentalPriceField.requestFocus();
            return false;
        }

        if (originalCostField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá gốc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            originalCostField.requestFocus();
            return false;
        }

        // Validate numeric fields
        try {
            int stockQuantity = Integer.parseInt(stockField.getText().trim());
            if (stockQuantity < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng tồn kho không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                stockField.requestFocus();
                return false;
            }

            float rentalPrice = Float.parseFloat(rentalPriceField.getText().trim());
            if (rentalPrice < 0) {
                JOptionPane.showMessageDialog(this, "Giá thuê không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                rentalPriceField.requestFocus();
                return false;
            }

            float originalCost = Float.parseFloat(originalCostField.getText().trim());
            if (originalCost < 0) {
                JOptionPane.showMessageDialog(this, "Giá gốc không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                originalCostField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if style and type are selected
        if (styleComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kiểu trang phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            styleComboBox.requestFocus();
            return false;
        }

        if (typeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại trang phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            typeComboBox.requestFocus();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new CostumeUI());
    }
}