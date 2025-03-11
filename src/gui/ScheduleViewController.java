package gui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import model.Schedule;
import model.dao.ScheduleDAO;

public class ScheduleViewController implements MainAppAware {

	private Main mainApp;
	private ScheduleDAO scheduleDAO = new ScheduleDAO();
	private int userId;

	@FXML
	private ListView<Schedule> listSchedule;
	@FXML
	private Button btNewSchedule, btDelete, btEdit;

	private ObservableList<Schedule> schedules = FXCollections.observableArrayList();

	@FXML
	public void initialize() {
		configureListView();
	}

	@FXML
	private void configureListView() {
		listSchedule.setCellFactory(lv -> new ListCell<Schedule>() {
			@Override
			protected void updateItem(Schedule item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.toString()); // Usa o método toString() para exibir o item
				}
			}
		});
		listSchedule.setItems(schedules);
	}

	// Carrega os cronogramas do usuário
	public void loadSchedule() {
	    System.out.println("Carregando cronogramas para o userId: " + userId);
		try {
			List<Schedule> schedulesFromDAO = scheduleDAO.listAll(userId);
	        System.out.println("Cronogramas carregados: " + schedulesFromDAO.size());
			schedules.setAll(schedulesFromDAO);
		} catch (Exception e) {
	        System.err.println("Erro ao carregar cronogramas:");
			Alerts.showAlert("Erro", null, "Falha ao carregar cronogramas.", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	// Ações dos botões
	@FXML
	public void openRegistrationAction() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/RegisterScheduleView.fxml"));
			Parent root = loader.load();

			// Obtém o controlador da tela de registro
			ScheduleRegistrationViewController registrationController = loader.getController();
			registrationController.setSelectedSchedule(null); // Passa a referência
			registrationController.setUserId(userId); // Passa o ID do usuário

			// Abre a nova janela
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Novo Cronograma");
			stage.show();
		} catch (IOException e) {
			handleViewError("abrir a tela de novo cronograma", e);
		}
	}

	@FXML
	public void editeScheduleAction() {
		if (validateSelection()) {
			try {
				Schedule selected = listSchedule.getSelectionModel().getSelectedItem();
				mainApp.loadView("/gui/RegisterScheduleView.fxml", "Editar Cronograma",
						Map.of("cronograma", selected, "usuarioId", userId));
			} catch (Exception e) {
				handleViewError("abrir a tela de edição", e);
			}
		}
	}

	@FXML
	public void deleteScheduleAction() {
		if (validateSelection() && confirmDeletion()) {
			try {
				Schedule selected = listSchedule.getSelectionModel().getSelectedItem();
				scheduleDAO.delete(selected.getId(), userId);
				schedules.remove(selected);
				Alerts.showAlert("Sucesso", null, "Cronograma excluído com sucesso!", AlertType.INFORMATION);
			} catch (Exception e) {
				Alerts.showAlert("Erro", null, "Falha na exclusão do cronograma.", AlertType.ERROR);
				e.printStackTrace();
			}
		}
	}

	// Validações
	private boolean validateSelection() {
		if (listSchedule.getSelectionModel().isEmpty()) {
			Alerts.showAlert("Atenção", null, "Selecione um cronograma da lista.", AlertType.WARNING);
			return false;
		}
		return true;
	}

	private boolean confirmDeletion() {
		Optional<ButtonType> result = Alerts.showConfirmation(
				"Confirmação",
				"Tem certeza que deseja excluir este cronograma permanentemente?");
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	private void handleViewError(String operation, Exception e) {
		Alerts.showAlert("Erro", null, "Não foi possível " + operation + ".", AlertType.ERROR);
		e.printStackTrace();
	}

	// Método para adicionar um novo cronograma à lista
	public void addSchedule(Schedule schedule) {

		schedules.add(schedule); 
	}

	// Método para atualizar a lista de cronogramas
	public void refreshSchedules() {
		loadSchedule(); 
		
	}

	public void setUserId(int userId) {
	    System.out.println("UserId definido: " + userId);
		this.userId = userId;
		loadSchedule();
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
}