package dao;

import model.Costume;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
public class CostumeDAO {
    public List<Costume> getAllCostumes() {
        List<Costume> list = new ArrayList<>();
        String sql = "SELECT c.*, tc.name as type_name, sc.name as style_name " +
                "FROM Costume c " +
                "LEFT JOIN TypeCostume tc ON c.TypeCostumeid = tc.id " +
                "LEFT JOIN StyleCostume sc ON c.StyleCostumeid = sc.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Costume costume = new Costume(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity"),
                        rs.getFloat("rental_price"),
                        rs.getFloat("original_cost"),
                        rs.getInt("StyleCostumeid"),
                        rs.getInt("TypeCostumeid")
                );
                costume.setTypeCostumeName(rs.getString("type_name"));
                costume.setStyleCostumeName(rs.getString("style_name"));
                list.add(costume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Costume> searchCostumes(String searchTerm) {
        List<Costume> list = new ArrayList<>();
        String sql = "SELECT c.*, tc.name as type_name, sc.name as style_name " +
                "FROM Costume c " +
                "LEFT JOIN TypeCostume tc ON c.TypeCostumeid = tc.id " +
                "LEFT JOIN StyleCostume sc ON c.StyleCostumeid = sc.id " +
                "WHERE c.name LIKE ? OR c.description LIKE ? OR tc.name LIKE ? OR sc.name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Costume costume = new Costume(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("stock_quantity"),
                            rs.getFloat("rental_price"),
                            rs.getFloat("original_cost"),
                            rs.getInt("StyleCostumeid"),
                            rs.getInt("TypeCostumeid")
                    );
                    costume.setTypeCostumeName(rs.getString("type_name"));
                    costume.setStyleCostumeName(rs.getString("style_name"));
                    list.add(costume);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Costume getCostumeById(int id) {
        String sql = "SELECT c.*, tc.name as type_name, sc.name as style_name " +
                "FROM Costume c " +
                "LEFT JOIN TypeCostume tc ON c.TypeCostumeid = tc.id " +
                "LEFT JOIN StyleCostume sc ON c.StyleCostumeid = sc.id " +
                "WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Costume costume = new Costume(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("stock_quantity"),
                            rs.getFloat("rental_price"),
                            rs.getFloat("original_cost"),
                            rs.getInt("StyleCostumeid"),
                            rs.getInt("TypeCostumeid")
                    );
                    costume.setTypeCostumeName(rs.getString("type_name"));
                    costume.setStyleCostumeName(rs.getString("style_name"));
                    return costume;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertCostume(Costume costume) {
        String sql = "INSERT INTO Costume (name, description, stock_quantity, rental_price, original_cost, StyleCostumeid, TypeCostumeid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, costume.getName());
            ps.setString(2, costume.getDescription());
            ps.setInt(3, costume.getStockQuantity());
            ps.setFloat(4, costume.getRentalPrice());
            ps.setFloat(5, costume.getOriginalCost());
            ps.setInt(6, costume.getStyleCostumeId());
            ps.setInt(7, costume.getTypeCostumeId());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    costume.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCostume(Costume costume) {
        String sql = "UPDATE Costume SET name = ?, description = ?, stock_quantity = ?, rental_price = ?, " +
                "original_cost = ?, StyleCostumeid = ?, TypeCostumeid = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, costume.getName());
            ps.setString(2, costume.getDescription());
            ps.setInt(3, costume.getStockQuantity());
            ps.setFloat(4, costume.getRentalPrice());
            ps.setFloat(5, costume.getOriginalCost());
            ps.setInt(6, costume.getStyleCostumeId());
            ps.setInt(7, costume.getTypeCostumeId());
            ps.setInt(8, costume.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCostume(int id) {
        String sql = "DELETE FROM Costume WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(int costumeId, int quantity) {
        String sql = "UPDATE Costume SET stock_quantity = stock_quantity + ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, costumeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Costume, Integer> getPopularCostumeInRange(Date startDate, Date endDate) {
        Map<Costume, Integer> result = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự
        String sql = "SELECT c.*, tc.name as type_name, sc.name as style_name, COUNT(ic.Costumeid) as rent_count " +
                "FROM Costume c " +
                "LEFT JOIN TypeCostume tc ON c.TypeCostumeid = tc.id " +
                "LEFT JOIN StyleCostume sc ON c.StyleCostumeid = sc.id " +
                "JOIN Invoice_Costume ic ON c.id = ic.Costumeid " +
                "JOIN Invoice i ON ic.Invoiceid = i.id " +
                "WHERE i.rental_date BETWEEN ? AND ? " +
                "AND i.type_invoice = 'rent' " +
                "GROUP BY c.id " +
                "ORDER BY rent_count DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Costume costume = new Costume(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("stock_quantity"),
                            rs.getFloat("rental_price"),
                            rs.getFloat("original_cost"),
                            rs.getInt("StyleCostumeid"),
                            rs.getInt("TypeCostumeid")
                    );
                    costume.setStyleCostumeName(rs.getString("style_name"));
                    costume.setTypeCostumeName(rs.getString("type_name"));
                    int rentCount = rs.getInt("rent_count");
                    result.put(costume, rentCount);
                }
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách trang phục phổ biến: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}