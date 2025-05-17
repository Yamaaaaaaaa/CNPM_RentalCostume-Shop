package dao;

import model.Invoice;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class InvoiceDAO {
    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, m.name as manager_name, c.name as customer_name " +
                "FROM Invoice i " +
                "LEFT JOIN Manager m ON i.manager_id = m.id " +
                "LEFT JOIN Customer c ON i.Customerid = c.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getInt("id"),
                        rs.getFloat("deposit"),
                        rs.getFloat("total_amount"),
                        rs.getDate("rental_date"),
                        rs.getDate("return_date"),
                        rs.getInt("manager_id"),
                        rs.getString("type_invoice"),
                        rs.getInt("Customerid")
                );
                invoice.setManagerName(rs.getString("manager_name"));
                invoice.setCustomerName(rs.getString("customer_name"));
                list.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Invoice getInvoiceById(int id) {
        String sql = "SELECT i.*, m.name as manager_name, c.name as customer_name " +
                "FROM Invoice i " +
                "LEFT JOIN Manager m ON i.manager_id = m.id " +
                "LEFT JOIN Customer c ON i.Customerid = c.id " +
                "WHERE i.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice(
                            rs.getInt("id"),
                            rs.getFloat("deposit"),
                            rs.getFloat("total_amount"),
                            rs.getDate("rental_date"),
                            rs.getDate("return_date"),
                            rs.getInt("manager_id"),
                            rs.getString("type_invoice"),
                            rs.getInt("Customerid")
                    );
                    invoice.setManagerName(rs.getString("manager_name"));
                    invoice.setCustomerName(rs.getString("customer_name"));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoice (deposit, total_amount, rental_date, return_date, manager_id, type_invoice, Customerid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setFloat(1, invoice.getDeposit());
            ps.setFloat(2, invoice.getTotalAmount());
            ps.setDate(3, new java.sql.Date(invoice.getRentalDate().getTime()));
            ps.setDate(4, new java.sql.Date(invoice.getReturnDate().getTime()));
            ps.setInt(5, invoice.getManagerId());
            ps.setString(6, invoice.getTypeInvoice());
            ps.setInt(7, invoice.getCustomerId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return -1; // Không có hàng nào được thêm vào
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    invoice.setId(id);
                    return id;
                } else {
                    return -1; // Không lấy được ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Lỗi SQL
        }
    }
    public void updateInvoice(Invoice invoice) {
        String sql = "UPDATE Invoice SET deposit = ?, total_amount = ?, rental_date = ?, return_date = ?, " +
                "manager_id = ?, type_invoice = ?, Customerid = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setFloat(1, invoice.getDeposit());
            ps.setFloat(2, invoice.getTotalAmount());
            ps.setDate(3, new java.sql.Date(invoice.getRentalDate().getTime()));
            ps.setDate(4, new java.sql.Date(invoice.getReturnDate().getTime()));
            ps.setInt(5, invoice.getManagerId());
            ps.setString(6, invoice.getTypeInvoice());
            ps.setInt(7, invoice.getCustomerId());
            ps.setInt(8, invoice.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInvoice(int id) {
        // Đầu tiên xóa các chi tiết hóa đơn liên quan
        String deleteDetailsSql = "DELETE FROM Invoice_Costume WHERE Invoiceid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteDetailsSql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return; // Nếu không xóa được chi tiết, không xóa hóa đơn
        }

        // Sau đó xóa hóa đơn
        String deleteInvoiceSql = "DELETE FROM Invoice WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteInvoiceSql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Invoice> getInvoiceRentedCostume(int costumeId, Date startDate, Date endDate) {
        List<Invoice> result = new ArrayList<>();

        String sql = "SELECT DISTINCT i.*, m.name as manager_name, c.name as customer_name " +
                "FROM Invoice i " +
                "LEFT JOIN Manager m ON i.manager_id = m.id " +
                "LEFT JOIN Customer c ON i.Customerid = c.id " +
                "JOIN Invoice_Costume ic ON i.id = ic.Invoiceid " +
                "WHERE ic.Costumeid = ? " +
                "AND i.rental_date BETWEEN ? AND ? " +
                "AND i.type_invoice = 'rent' " +
                "ORDER BY i.rental_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, costumeId);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice(
                            rs.getInt("id"),
                            rs.getFloat("deposit"),
                            rs.getFloat("total_amount"),
                            rs.getDate("rental_date"),
                            rs.getDate("return_date"),
                            rs.getInt("manager_id"),
                            rs.getString("type_invoice"),
                            rs.getInt("Customerid")
                    );
                    invoice.setManagerName(rs.getString("manager_name"));
                    invoice.setCustomerName(rs.getString("customer_name"));

                    result.add(invoice);
                }
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn thuê trang phục: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}