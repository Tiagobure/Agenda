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

	private int usuarioId;

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		loadSchedule(); // Carrega cronogramas ao iniciar
	}

	@FXML
	public void initialize() {
		listSchedule.setItems(schedules); // Vincula a lista ao ListView

		// Adiciona ações para os botões
		btNewSchedule.setOnAction(event -> openRegistration());
		btEdit.setOnAction(event -> editarCronograma());
		btDelete.setOnAction(event -> deleteSchedule());
	}

	// Carrega os cronogramas
	private void loadSchedule() {
		int usuarioId = obterUsuarioId(); // Obtém o ID do usuário logado
		listScheduleObject = scheduleDAO.listAll(usuarioId);

		// Limpa a lista atual e adiciona os novos cronogramas
		schedules.clear();
		for (Schedule c : listScheduleObject) {
			schedules.add(c.toString()); // Converte Cronograma para String
		}
	}

	// Obtém o ID do usuário logado
	private int obterUsuarioId() {
		// Implemente a lógica para obter o ID do usuário logado
		return usuarioId;
	}

	// Abre a janela de cadastro de um novo cronograma
	@FXML
	private void openRegistration() {
		try {
			mainApp.carregarTela("/gui/CronogramaView.fxml", "Novo Cronograma", null);
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Não foi possível abrir a tela principal!", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	// Edita o cronograma selecionado
	@FXML
	private void editarCronograma() {
		if (checkSelection()) {
			int indiceSelecionado = listSchedule.getSelectionModel().getSelectedIndex();
			Schedule cronogramaSelecionado = listScheduleObject.get(indiceSelecionado);

			Map<String, Object> params = new HashMap<>();
			params.put("cronograma", cronogramaSelecionado);
			params.put("usuarioId", usuarioId);
			try {
				mainApp.carregarTela("/gui/CronogramaView.fxml", "Editar Cronograma", params);
			} catch (Exception e) {
				Alerts.showAlert("Erro", null, "não foi possivel abrir ", AlertType.ERROR);
				e.printStackTrace();
			}
		}

	}

	// Deleta o cronograma selecionado
	@FXML
	private void deleteSchedule() {
		if (checkSelection()) {
			int indiceSelecionado = listSchedule.getSelectionModel().getSelectedIndex();
			Schedule cronogramaSelecionado = listScheduleObject.get(indiceSelecionado);

			// Remove do banco de dados
			scheduleDAO.delete(cronogramaSelecionado.getId(), obterUsuarioId());

			// Remove da lista de objetos e da lista de strings
			listScheduleObject.remove(indiceSelecionado);
			schedules.remove(indiceSelecionado);
		}
	}

	// Verifica se um cronograma foi selecionado
	private boolean checkSelection() {
		if (listSchedule.getSelectionModel().getSelectedItem() == null) {
			Alerts.showAlert("Selecione um cronograma", null, "Por favor, selecione um cronograma.",
					AlertType.INFORMATION);
			return false;
		}
		return true;
	}

}