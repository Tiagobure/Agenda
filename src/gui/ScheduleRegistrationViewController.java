package gui;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Schedule;
import model.dao.ScheduleDAO;

public class ScheduleRegistrationViewController {
	private Main mainApp;

	private Schedule scheduleSelected;

	private ScheduleDAO scheduleDAO;

	@FXML
	private TextField txtDayWeek, txtHour, txtSubject, txtTalkAbout;

	@FXML
	private Button btSave;

	private int usuarioId; // ID do usuário logado

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public ScheduleRegistrationViewController() {
		// Injeção de dependência
		this.scheduleDAO = new ScheduleDAO();
	}

	// Método para definir o cronograma selecionado
	public void setCronogramaSelecionado(Schedule cronograma) {
		this.scheduleSelected = cronograma;
		fillInFields(); // Preenche os campos com os dados do cronograma
	}

	// Preenche os campos da tela com os dados do cronograma
	private void fillInFields() {
		if (scheduleSelected != null) {
			txtDayWeek.setText(scheduleSelected.getDayWeek());
			txtHour.setText(scheduleSelected.getHour());
			txtSubject.setText(scheduleSelected.getSubject());
			txtTalkAbout.setText(scheduleSelected.getTalkAbout());
		}
	}

	@FXML
	public void saveBlock() {
		// Obtém os valores dos campos
		String dayWeek = txtDayWeek.getText();
		String hour = txtHour.getText();
		String subject = txtSubject.getText();
		String talkAbout = txtTalkAbout.getText();

		// Valida se todos os campos estão preenchidos
		if (dayWeek.isEmpty() || hour.isEmpty() || subject.isEmpty() || talkAbout.isEmpty()) {
			Alerts.showAlert("Erro", null, "Preencha todos os campos", AlertType.ERROR);
			return;
		}

		// Validação de formato de horário (opcional, exemplo simples)
		if (!hour.matches("\\d{2}:\\d{2}")) { // Verifica se o horário está no formato HH:mm
			Alerts.showAlert("Erro", null, "Formato de horário inválido. Use o formato HH:mm.", AlertType.ERROR);
			return;
		}

		if (scheduleSelected == null) {
			// Cria um novo objeto Cronograma
			Schedule cronograma = new Schedule(dayWeek, hour, subject, talkAbout);
			cronograma.setUserId(usuarioId); // Define o ID do usuário

			try {
				// Insere o cronograma no banco de dados
				scheduleDAO.insert(cronograma);

				// Mensagem de sucesso
				Alerts.showAlert("Sucesso", null, "Bloco salvo com sucesso!", AlertType.INFORMATION);

				// Limpa os campos após salvar
				clearLabeltimeline();
				mainApp.closeScene(btSave);

			} catch (Exception e) {
				// Caso ocorra um erro ao salvar no banco de dados
				Alerts.showAlert("Erro ao salvar", "Erro ao tentar salvar o cronograma no banco de dados.",
						e.getMessage(), AlertType.ERROR);
			}
		} else {
			// Atualiza os dados do cronograma selecionado
			scheduleSelected.setDayWeek(dayWeek);
			scheduleSelected.setHour(hour);
			scheduleSelected.setSubject(subject);
			scheduleSelected.setTalkAbout(talkAbout);

			try {
				// Atualiza o cronograma no banco de dados
				scheduleDAO.editSchedule(scheduleSelected);
				Alerts.showAlert("Sucesso", null, "Bloco atualizado com sucesso!", AlertType.INFORMATION);

				mainApp.closeScene(btSave);

			} catch (Exception e) {
				// Caso ocorra um erro ao atualizar no banco de dados
				Alerts.showAlert("Erro ao atualizar", "Erro ao tentar atualizar o cronograma no banco de dados.",
						e.getMessage(), AlertType.ERROR);
			}
		}
	}

	private void clearLabeltimeline() {
		txtDayWeek.clear();
		txtHour.clear();
		txtSubject.clear();
		txtTalkAbout.clear();
	}

}