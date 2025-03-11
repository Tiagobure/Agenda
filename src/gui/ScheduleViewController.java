package gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.Schedule;
import model.dao.ScheduleDAO;

public class ScheduleViewController implements MainAppAware {

	private Main mainApp;

	// Lista de cronogramas (strings para exibição)
	private ObservableList<String> schedules = FXCollections.observableArrayList();

	// Lista de objetos Cronograma
	private List<Schedule> listScheduleObject;

	// FXML
	@FXML
	private ListView<String> listSchedule;

	@FXML
	private Button btNewSchedule, btDelete, btEdit;

	private ScheduleDAO scheduleDAO = new ScheduleDAO();

	private int userId;

	public void setUsuarioId(int userId) {
		this.userId = userId;
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		loadSchedule(); 
	}

	@FXML
	public void initialize() {
		listSchedule.setItems(schedules); 
		// Vincula a lista ao ListView

		// Adiciona ações para os botões
		btNewSchedule.setOnAction(event -> openRegistration());
		btEdit.setOnAction(event -> editeSchedule());
		btDelete.setOnAction(event -> deleteSchedule());
	}

	
	private void loadSchedule() {
		//int userId = obtainUserId(); 
		// Obtém o ID do usuário logado
		listScheduleObject = scheduleDAO.listAll(userId);

		// Limpa a lista atual e adiciona os novos cronogramas
		schedules.clear();
		for (Schedule c : listScheduleObject) {
			schedules.add(c.toString()); // Converte Cronograma para String
		}
	}

	// Abre a janela de cadastro de um novo cronograma
	@FXML
	private void openRegistration() {
		try {
			mainApp.loadView("/gui/CronogramaView.fxml", "Novo Cronograma", null);
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Não foi possível abrir a tela principal!", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void editeSchedule() {
		if (checkSelection()) {
			int selectedIndex = listSchedule.getSelectionModel().getSelectedIndex();
			Schedule selectSchedule = listScheduleObject.get(selectedIndex);

			Map<String, Object> params = new HashMap<>();
			params.put("cronograma", selectSchedule);
			params.put("usuarioId", userId);
			try {
				mainApp.loadView("/gui/CronogramaView.fxml", "Editar Cronograma", params);
			} catch (Exception e) {
				Alerts.showAlert("Erro", null, "não foi possivel abrir ", AlertType.ERROR);
				e.printStackTrace();
			}
		}

	}

	@FXML
	private void deleteSchedule() {
		if (checkSelection()) {
			int selectedIndex = listSchedule.getSelectionModel().getSelectedIndex();
			Schedule selectSchedule = listScheduleObject.get(selectedIndex);

			// Remove do banco de dados
			scheduleDAO.delete(selectSchedule.getId(), userId);

			// Remove da lista de objetos e da lista de strings
			listScheduleObject.remove(selectedIndex);
			schedules.remove(selectedIndex);
		}
	}

	private boolean checkSelection() {
		if (listSchedule.getSelectionModel().getSelectedItem() == null) {
			Alerts.showAlert("Selecione um cronograma", null, "Por favor, selecione um cronograma.",
					AlertType.INFORMATION);
			return false;
		}
		return true;
	}

}