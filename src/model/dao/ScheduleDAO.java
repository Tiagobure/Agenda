package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import db.DataBase;
import gui.util.Notifier;
import model.Schedule;

public class ScheduleDAO {

	
	public void insert(Schedule schedule) {
		String sql = "INSERT INTO cronograma (diaSemana, horario, materia, assunto, usuario_id) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, schedule.getDayWeek());
			pstmt.setString(2, schedule.getHour());
			pstmt.setString(3, schedule.getSubject());
			pstmt.setString(4, schedule.getTalkAbout());
			pstmt.setInt(5, schedule.getUserId()); 
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Schedule> listAll(int userId) {
		List<Schedule> schedule = new ArrayList<>();
		String sql = "SELECT * FROM cronograma WHERE usuario_id = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, userId); // Filtro por usuario_id
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Schedule c = new Schedule(rs.getString("diaSemana"), rs.getString("horario"),
						rs.getString("materia"), rs.getString("assunto"), rs.getInt("usuario_id"));
				c.setId(rs.getInt("id"));
				  LocalDateTime dateTime = Notifier.convertToDateTime(c.getDayWeek(), c.getHour());
		          c.setDateTime(dateTime);
				
				schedule.add(c);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar cronogramas: " + e.getMessage());
			e.printStackTrace();
		}

		return schedule;
	}

	public void editSchedule(Schedule schedule) {
	    String sql = "UPDATE cronograma SET diaSemana = ?, horario = ?, materia = ?, assunto = ? WHERE id = ? AND usuario_id = ?";

	    try (Connection conn = DataBase.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, schedule.getDayWeek());
	        pstmt.setString(2, schedule.getHour());
	        pstmt.setString(3, schedule.getSubject());
	        pstmt.setString(4, schedule.getTalkAbout());
	        pstmt.setInt(5, schedule.getId());
	        pstmt.setInt(6, schedule.getUserId()); 
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        System.err.println("Erro ao editar cronograma: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	public List<Schedule> listByDay(String dayWeek, int userId) {
		List<Schedule> schedules = new ArrayList<>();
		String sql = "SELECT * FROM cronograma WHERE diaSemana = ? AND usuario_id = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, dayWeek);
			pstmt.setInt(2, userId); 
			
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Schedule c = new Schedule(rs.getString("diaSemana"), rs.getString("horario"),
						rs.getString("materia"), rs.getString("assunto"), rs.getInt("usuario_id"));
				c.setId(rs.getInt("id"));
				schedules.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return schedules;
	}

	public void delete(int id, int userId) {
		String sql = "DELETE FROM cronograma WHERE id = ? AND usuario_id = ?";

		try (Connection conn = DataBase.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.setInt(2, userId); // Garantir que apenas o usuário correto possa deletar
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
