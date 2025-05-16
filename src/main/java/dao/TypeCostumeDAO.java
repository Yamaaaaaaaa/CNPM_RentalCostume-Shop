package dao;

import model.TypeCostume;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeCostumeDAO {
    public List<TypeCostume> getAllTypeCostumes() {
        List<TypeCostume> list = new ArrayList<>();
        String sql = "SELECT * FROM TypeCostume";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TypeCostume typeCostume = new TypeCostume(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                list.add(typeCostume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public TypeCostume getTypeCostumeById(int id) {
        String sql = "SELECT * FROM TypeCostume WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TypeCostume(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertTypeCostume(TypeCostume typeCostume) {
        String sql = "INSERT INTO TypeCostume (name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, typeCostume.getName());
            ps.setString(2, typeCostume.getDescription());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    typeCostume.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTypeCostume(TypeCostume typeCostume) {
        String sql = "UPDATE TypeCostume SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, typeCostume.getName());
            ps.setString(2, typeCostume.getDescription());
            ps.setInt(3, typeCostume.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTypeCostume(int id) {
        String sql = "DELETE FROM TypeCostume WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}