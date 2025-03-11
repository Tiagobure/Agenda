package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DataBase;
import model.Summary;

public class SummaryDAO {

    public void inserir(Summary summary, int userId) {
        String sql = "INSERT INTO resumos (titulo, texto, materia, assunto, anexo, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, summary.getTitle());
            pstmt.setString(2, summary.getText());
            pstmt.setString(3, summary.getSubject());
            pstmt.setString(4, summary.getTalkAbout());
            pstmt.setString(5, summary.getAttachment());
            pstmt.setInt(6, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Summary> listBySubject(String subject, int userId) {
        List<Summary> resumos = new ArrayList<>();
        String sql = "SELECT * FROM resumos WHERE materia = ? AND usuario_id = ?";

        try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Summary r = new Summary(rs.getString("titulo"), rs.getString("texto"), rs.getString("materia"),
                        rs.getString("assunto"), rs.getInt("usuario_id"));
                r.setId(rs.getInt("id"));
                r.setAttachment(rs.getString("anexo"));
                resumos.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resumos;
    }

    public List<Summary> searchByWord(String term, int userId) {
        List<Summary> resumos = new ArrayList<>();
        String sql = "SELECT * FROM resumos WHERE usuario_id = ? AND (titulo LIKE ? OR texto LIKE ? OR materia LIKE ? OR assunto LIKE ?)";

        try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            String parametro = "%" + term + "%";
            pstmt.setString(2, parametro);
            pstmt.setString(3, parametro);
            pstmt.setString(4, parametro);
            pstmt.setString(5, parametro);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Summary r = new Summary(rs.getString("titulo"), rs.getString("texto"), rs.getString("materia"),
                        rs.getString("assunto"), rs.getInt("usuario_id"));
                r.setId(rs.getInt("id"));
                r.setAttachment(rs.getString("anexo"));
                resumos.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resumos;
    }
    
    public void deletar(Summary summary) {
        String sql = "DELETE FROM resumos WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, summary.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}