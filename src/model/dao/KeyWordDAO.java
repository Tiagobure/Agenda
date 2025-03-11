package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DataBase;
import db.DbException;
import model.KeyWord;

public class KeyWordDAO {

	public void inserir(KeyWord KeyWord, int userId) {
		String sql = "INSERT INTO palavras_chave (palavra, descricao, materia, assunto, usuario_id) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, KeyWord.getKeyword());
			pstmt.setString(2, KeyWord.getDescription());
			pstmt.setString(3, KeyWord.getSubject());
			pstmt.setString(4, KeyWord.getTalkAbout());
			pstmt.setInt(5, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<KeyWord> listBySubject(String subject, int userId) {
		List<KeyWord> key = new ArrayList<>();
		String sql = "SELECT * FROM palavras_chave WHERE materia = ? AND usuario_id = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, subject);
			pstmt.setInt(2, userId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				KeyWord pc = new KeyWord(rs.getString("palavra"), rs.getString("descricao"),
						rs.getString("materia"), rs.getString("assunto"), rs.getInt("usuario_id"));
				pc.setId(rs.getInt("id"));
				key.add(pc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return key;
	}

	public List<KeyWord> searchByTerm(String term, int userId) {
		List<KeyWord> palavrasChave = new ArrayList<>();
		String sql = "SELECT * FROM palavras_chave WHERE usuario_id = ? AND (" + "LOWER(palavra) LIKE LOWER(?) OR "
				+ "LOWER(descricao) LIKE LOWER(?) OR " + "LOWER(materia) LIKE LOWER(?) OR "
				+ "LOWER(assunto) LIKE LOWER(?))";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			String search = "%" + term + "%";
			pstmt.setInt(1, userId);
			pstmt.setString(2, search);
			pstmt.setString(3, search);
			pstmt.setString(4, search);
			pstmt.setString(5, search);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				KeyWord pc = new KeyWord(rs.getString("palavra"), rs.getString("descricao"),
						rs.getString("materia"), rs.getString("assunto"), rs.getInt("usuario_id"));
				pc.setId(rs.getInt("id"));
				palavrasChave.add(pc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return palavrasChave;
	}

	public void delete(KeyWord key) {
		String sql = "DELETE FROM palavras_chave WHERE id = ?";
		try (Connection conn = DataBase.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, key.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<KeyWord> findAllByUserId(int userId) {
	    String sql = "SELECT * FROM palavras_chave WHERE usuario_id = ?";
	    try (Connection conn = DataBase.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        ResultSet rs = stmt.executeQuery();

	        List<KeyWord> keyWords = new ArrayList<>();
	        while (rs.next()) {
	            KeyWord key = new KeyWord(rs.getString("palavra"), 
	            		rs.getString("descricao"),
	            		rs.getString("materia"),
	            		rs.getString("assunto"),
	            		rs.getInt("usuario_id"));
                key.setId(rs.getInt("id"));

	            keyWords.add(key);
	        }
	        return keyWords;
	    } catch (SQLException e) {
	        throw new DbException("Erro ao buscar palavras-chave do usuário: " + e.getMessage());
	    }
	}
}
