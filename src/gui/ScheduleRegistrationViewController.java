package gui;

import java.time.LocalDateTime;

import db.DbException;
import gui.util.Alerts;
import gui.util.Notifier;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Schedule;
import model.dao.ScheduleDAO;

public class ScheduleRegistrationViewController {

	private Schedule selectedSchedule;
	private final ScheduleDAO scheduleDAO;
	private int userId;

	private ScheduleViewController scheduleViewController;

	@FXML
	private TextField txtDayWeek, txtHour, txtSubject, txtTalkAbout;
	@FXML
	private Button btSave;

	public ScheduleRegistrationViewController() {
		this.scheduleDAO = new ScheduleDAO();
	}

	public void setScheduleViewController(ScheduleViewController scheduleViewController) {
		this.scheduleViewController = scheduleViewController;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setSelectedSchedule(Schedule schedule) {
		this.selectedSchedule = schedule;
		populateFields();
	}

	private void populateFields() {
		if (selectedSchedule != null) {
			txtDayWeek.setText(selectedSchedule.getDayWeek());
			txtHour.setText(selectedSchedule.getHour());
			txtSubject.setText(selectedSchedule.getSubject());
			txtTalkAbout.setText(selectedSchedule.getTalkAbout());
		}
	}

	@FXML
	public void saveBlock() {
		String dayWeek = txtDayWeek.getText().trim();
		String hour = txtHour.getText().trim();
		String subject = txtSubject.getText().trim();
		String talkAbout = txtTalkAbout.getText().trim();

		if (!validateInputs(dayWeek, hour, subject, talkAbout)) {
			return;
		}

		try {
		if (selectedSchedule == null) {
			createNewSchedule(dayWeek, hour, subject, talkAbout);
		} else {
			updateExistingSchedule(dayWeek, hour, subject, talkAbout);
		}
		}catch (IllegalArgumentException e) {
	        Alerts.showAlert("Erro", null, e.getMessage(), AlertType.ERROR);
	    } catch (Exception e) {
	        Alerts.showAlert("Erro", null, "Ocorreu um erro ao salvar o cronograma.", AlertType.ERROR);
	        e.printStackTrace();
	    }
	}
	private boolean validateInputs(String dayWeek, String hour, String subject, String talkAbout) {
		if (dayWeek.isEmpty() || hour.isEmpty() || subject.isEmpty() || talkAbout.isEmpty()) {
			Alerts.showAlert("Erro", null, "Preencha todos os campos obrigatórios", AlertType.ERROR);
			return false;
		}
		  

		if (!isValidHour(hour)) {
			Alerts.showAlert("Erro", null, "Formato de horário inválido (use HH:mm)", AlertType.ERROR);
			return false;
		}

		return true;
	}

	private boolean isValidHour(String hour) {
		try {
			String[] parts = hour.split(":");
			int hours = Integer.parseInt(parts[0]);
			int minutes = Integer.parseInt(parts[1]);
			return hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60;
		} catch (Exception e) {
			return false;
		}
	}

	private void createNewSchedule(String dayWeek, String hour, String subject, String talkAbout) {
	    try {
	        // Converte o dia da semana e a hora para LocalDateTime
	        LocalDateTime dateTime = Notifier.convertToDateTime(dayWeek, hour);

	        // Cria um novo cronograma
	        Schedule newSchedule = new Schedule(dayWeek, hour, subject, talkAbout, userId);
	        newSchedule.setDateTime(dateTime); // Define a data/hora combinada

	        // Insere o cronograma no banco de dados
	        scheduleDAO.insert(newSchedule);

	        // Exibe uma mensagem de sucesso
	        showSuccessAlert("Bloco salvo com sucesso!");

	        // Notifica a ScheduleViewController para atualizar a lista
	        if (scheduleViewController != null) {
	            scheduleViewController.addSchedule(newSchedule); // Adiciona o novo cronograma à lista
	        }

	      
	        closeWindow();
	    } catch (IllegalArgumentException e) {
	        // Captura erros de conversão de data/hora
	        showErrorAlert("Erro: " + e.getMessage());
	    } catch (DbException e) {
	        showErrorAlert("Erro ao salvar no banco de dados: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        // Captura outros erros genéricos
	        showErrorAlert("Erro ao salvar bloco: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	private void updateExistingSchedule(String dayWeek, String hour, String subject, String talkAbout) {
		selectedSchedule.setDayWeek(dayWeek);
		selectedSchedule.setHour(hour);
		selectedSchedule.setSubject(subject);
		selectedSchedule.setTalkAbout(talkAbout);

		try {
			scheduleDAO.editSchedule(selectedSchedule);
			showSuccessAlert("Bloco atualizado com sucesso!");

			// Notifica a ScheduleViewController para atualizar a lista
			if (scheduleViewController != null) {
				scheduleViewController.loadSchedule();
			}

			closeWindow();
		} catch (Exception e) {
			showErrorAlert("Erro ao atualizar bloco");
		}
	}

	private void showSuccessAlert(String message) {
		Alerts.showAlert("Sucesso", null, message, AlertType.INFORMATION);
	}

	private void showErrorAlert(String errorMessage) {
		Alerts.showAlert("Erro", null, errorMessage, AlertType.ERROR);
	}


	private void closeWindow() {
		Stage stage = (Stage) btSave.getScene().getWindow();
		stage.close();
	}
}