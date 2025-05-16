package dao;

import model.StyleCostume;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StyleCostumeDAO {
    public List<StyleCostume> getAllStyleCostumes() {
        List<StyleCostume> list = new ArrayList<>();
        String sql = "SELECT * FROM StyleCostume";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StyleCostume styleCostume = new StyleCostume(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(styleCostume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public StyleCostume getStyleCostumeById(int id) {
        String sql = "SELECT * FROM StyleCostume WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StyleCostume(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertStyleCostume(StyleCostume styleCostume) {
        String sql = "INSERT INTO StyleCostume (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, styleCostume.getName());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    styleCostume.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStyleCostume(StyleCostume styleCostume) {
        String sql = "UPDATE StyleCostume SET name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, styleCostume.getName());
            ps.setInt(2, styleCostume.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStyleCostume(int id) {
        String sql = "DELETE FROM StyleCostume WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}