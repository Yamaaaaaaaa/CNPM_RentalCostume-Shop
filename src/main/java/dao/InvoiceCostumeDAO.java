package dao;

import model.InvoiceCostume;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceCostumeDAO {
    public List<InvoiceCostume> getAllInvoiceCostumes() {
        List<InvoiceCostume> list = new ArrayList<>();
        String sql = "SELECT ic.*, c.name as costume_name " +
                "FROM Invoice_Costume ic " +
                "LEFT JOIN Costume c ON ic.Costumeid = c.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InvoiceCostume invoiceCostume = new InvoiceCostume(
                        rs.getInt("id"),
                        rs.getInt("quantity"),
                        rs.getFloat("price_per_unit"),
                        rs.getString("status"),
                        rs.getFloat("penalty_fee"),
                        rs.getInt("Invoiceid"),
                        rs.getInt("Costumeid")
                );
                invoiceCostume.setCostumeName(rs.getString("costume_name"));
                list.add(invoiceCostume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<InvoiceCostume> getInvoiceCostumesByInvoiceId(int invoiceId) {
        List<InvoiceCostume> list = new ArrayList<>();
        String sql = "SELECT ic.*, c.name as costume_name " +
                "FROM Invoice_Costume ic " +
                "LEFT JOIN Costume c ON ic.Costumeid = c.id " +
                "WHERE ic.Invoiceid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceCostume invoiceCostume = new InvoiceCostume(
                            rs.getInt("id"),
                            rs.getInt("quantity"),
                            rs.getFloat("price_per_unit"),
                            rs.getString("status"),
                            rs.getFloat("penalty_fee"),
                            rs.getInt("Invoiceid"),
                            rs.getInt("Costumeid")
                    );
                    invoiceCostume.setCostumeName(rs.getString("costume_name"));
                    list.add(invoiceCostume);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public InvoiceCostume getInvoiceCostumeById(int id) {
        String sql = "SELECT ic.*, c.name as costume_name " +
                "FROM Invoice_Costume ic " +
                "LEFT JOIN Costume c ON ic.Costumeid = c.id " +
                "WHERE ic.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoiceCostume invoiceCostume = new InvoiceCostume(
                            rs.getInt("id"),
                            rs.getInt("quantity"),
                            rs.getFloat("price_per_unit"),
                            rs.getString("status"),
                            rs.getFloat("penalty_fee"),
                            rs.getInt("Invoiceid"),
                            rs.getInt("Costumeid")
                    );
                    invoiceCostume.setCostumeName(rs.getString("costume_name"));
                    return invoiceCostume;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertInvoiceCostume(InvoiceCostume invoiceCostume) {
        String sql = "INSERT INTO Invoice_Costume (quantity, price_per_unit, status, penalty_fee, Invoiceid, Costumeid) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoiceCostume.getQuantity());
            ps.setFloat(2, invoiceCostume.getPricePerUnit());
            ps.setString(3, invoiceCostume.getStatus());
            ps.setFloat(4, invoiceCostume.getPenaltyFee());
            ps.setInt(5, invoiceCostume.getInvoiceId());
            ps.setInt(6, invoiceCostume.getCostumeId());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoiceCostume.setId(generatedKeys.getInt(1));
                }
            }

            // Cập nhật số lượng tồn kho
            if ("rent".equals(getInvoiceType(invoiceCostume.getInvoiceId()))) {
                // Nếu là hóa đơn thuê, giảm số lượng tồn kho
                new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), -invoiceCostume.getQuantity());
            } else if ("import".equals(getInvoiceType(invoiceCostume.getInvoiceId()))) {
                // Nếu là hóa đơn nhập, tăng số lượng tồn kho
                new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), invoiceCostume.getQuantity());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInvoiceCostume(InvoiceCostume invoiceCostume) {
        // Lấy thông tin cũ để tính toán sự thay đổi số lượng
        InvoiceCostume oldInvoiceCostume = getInvoiceCostumeById(invoiceCostume.getId());
        int quantityDifference = invoiceCostume.getQuantity() - oldInvoiceCostume.getQuantity();

        String sql = "UPDATE Invoice_Costume SET quantity = ?, price_per_unit = ?, status = ?, penalty_fee = ?, " +
                "Invoiceid = ?, Costumeid = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceCostume.getQuantity());
            ps.setFloat(2, invoiceCostume.getPricePerUnit());
            ps.setString(3, invoiceCostume.getStatus());
            ps.setFloat(4, invoiceCostume.getPenaltyFee());
            ps.setInt(5, invoiceCostume.getInvoiceId());
            ps.setInt(6, invoiceCostume.getCostumeId());
            ps.setInt(7, invoiceCostume.getId());
            ps.executeUpdate();

            // Cập nhật số lượng tồn kho nếu có sự thay đổi
            if (quantityDifference != 0) {
                String invoiceType = getInvoiceType(invoiceCostume.getInvoiceId());
                if ("rent".equals(invoiceType)) {
                    // Nếu là hóa đơn thuê, giảm số lượng tồn kho
                    new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), -quantityDifference);
                } else if ("import".equals(invoiceType)) {
                    // Nếu là hóa đơn nhập, tăng số lượng tồn kho
                    new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), quantityDifference);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInvoiceCostume(int id) {
        // Lấy thông tin trước khi xóa để cập nhật số lượng tồn kho
        InvoiceCostume invoiceCostume = getInvoiceCostumeById(id);
        if (invoiceCostume != null) {
            String sql = "DELETE FROM Invoice_Costume WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.executeUpdate();

                // Cập nhật số lượng tồn kho
                String invoiceType = getInvoiceType(invoiceCostume.getInvoiceId());
                if ("rent".equals(invoiceType)) {
                    // Nếu là hóa đơn thuê, tăng số lượng tồn kho (hoàn trả)
                    new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), invoiceCostume.getQuantity());
                } else if ("import".equals(invoiceType)) {
                    // Nếu là hóa đơn nhập, giảm số lượng tồn kho (hủy nhập)
                    new CostumeDAO().updateStock(invoiceCostume.getCostumeId(), -invoiceCostume.getQuantity());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getInvoiceType(int invoiceId) {
        String sql = "SELECT type_invoice FROM Invoice WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("type_invoice");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


}