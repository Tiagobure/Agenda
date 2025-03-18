package model.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DataBase;
import db.DbException;
import model.User;

public class UserDAO {

	public void userRegister(User register) {

		if (userExists(register.getName())) {
			throw new DbException("Usuário já cadastrado.");
		}

		String sql = "INSERT INTO usuarios (nome, senha) VALUES (?, ?)";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, register.getName());
			pstmt.setString(2, hashPass(register.getPass()));

			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
			throw new DbException("Erro ao cadastrar usuário: " + e.getMessage());
		}
	}

	public User toDoLogin(String name, String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new DbException("Senha não pode ser nula ou vazia.");
		}

		String sql = "SELECT id, nome, senha FROM usuarios WHERE nome = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String senhaHash = rs.getString("senha");
				if (hashPass(password).equals(senhaHash)) {

					User usuario = new User(rs.getInt("id"), rs.getString("nome"), password);
					return usuario;
				} else {
					throw new DbException("Senha incorreta.");
				}
			} else {
				throw new DbException("Usuário não encontrado.");
			}
		} catch (SQLException e) {
			System.err.println("Erro ao fazer login: " + e.getMessage());
			throw new DbException("Erro ao fazer login: " + e.getMessage());
		}
	}

	public boolean userExists(String name) {
		String sql = "SELECT id FROM usuarios WHERE nome = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			System.err.println("Erro ao verificar usuário existente: " + e.getMessage());
			throw new DbException("Erro ao verificar usuário existente: " + e.getMessage());
		}
	}

	private String hashPass(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Erro ao criptografar senha: " + e.getMessage());
			throw new RuntimeException("Erro ao criptografar senha.", e);
		}
	}

	public void deleteUser(int userId) {
		String sql = "DELETE FROM usuarios WHERE id = ?";
		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Erro ao excluir usuário: " + e.getMessage());
		}
	}

}