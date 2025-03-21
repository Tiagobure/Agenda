package gui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import gui.util.Notifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;

import java.io.IOException;
import model.Schedule;
import model.dao.ScheduleDAO;

public final class ScheduleViewController implements MainAppAware {

	private Main mainApp;

	@SuppressWarnings("exports")
	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		System.out.println("mainApp foi definido: " + (mainApp != null));
	}

	private ScheduleDAO scheduleDAO = new ScheduleDAO();
	private Integer userId;

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
	                setGraphic(null);
	            } else {
	                // Cria um layout personalizado
	                HBox hbox = new HBox(10); // Espaçamento de 10 entre os elementos
	                hbox.setAlignment(Pos.CENTER_LEFT);

	                Label dayLabel = new Label("Dia: " + item.getDayWeek());
	                Label hourLabel = new Label("Hora: " + item.getHour());
	                Label subjectLabel = new Label("Matéria: " + item.getSubject());
	                Label topicLabel = new Label("Tópico: " + item.getTalkAbout());

	                dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1565C0;"); // Azul
	                hourLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D32F2F;"); // Vermelho
	                subjectLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #388E3C;"); // Verde
	                topicLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFA000;"); // laranja

	                hbox.getChildren().addAll(dayLabel, hourLabel, subjectLabel, topicLabel);

	                setGraphic(hbox);
	            }
	        }
	    });
	    listSchedule.setItems(schedules);
	}

	public void loadSchedule() {

		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();
		
		int userIdexist = getUserId();
		System.out.println("Carregando cronogramas para o userId: " + userIdexist);

		try {
			List<Schedule> schedulesFromDAO = scheduleDAO.listAll(userId);
			System.out.println("Cronogramas carregados: " + schedulesFromDAO.size());
			 
			Notifier.startNotifier(schedulesFromDAO);
			schedules.setAll(schedulesFromDAO);
	       

		} catch (Exception e) {
			System.err.println("Erro ao carregar cronogramas:");
			Alerts.showAlert("Erro", null, "Falha ao carregar cronogramas.", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	public void openRegistrationAction() {
		if (userId == null) {
			Alerts.showAlert("Erro", null, "UserId não está definido.  Verifique o usuário logado.", AlertType.ERROR);
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/RegisterScheduleView.fxml"));
			Parent root = loader.load();

			ScheduleRegistrationViewController registrationController = loader.getController();
			registrationController.setSelectedSchedule(null);
			registrationController.setUserId(userId);
			Stage stage = new Stage();
			stage.setTitle("Novo Cronograma");
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(btNewSchedule.getScene().getWindow());

			stage.showAndWait();

			loadSchedule();
		} catch (IOException e) {
			handleViewError("abrir a tela de novo cronograma", e);
		}
	}

	@FXML
	public void editeScheduleAction() {

		if (validateSelection()) {
			try {
				if (mainApp == null) {
					throw new IllegalStateException("mainApp não foi inicializado!");
				}

				Schedule selected = listSchedule.getSelectionModel().getSelectedItem();
				scheduleDAO.editSchedule(selected);

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
			Alerts.showAlert("Atenção", null, "Selecione um cronograma da lista para continuar.", AlertType.WARNING);
			return false;
		}
		return true;
	}

	private boolean confirmDeletion() {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Tem certeza que deseja excluir este cronograma permanentemente?");
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	private void handleViewError(String operation, Exception e) {
		Alerts.showAlert("Erro", null, "Não foi possível " + operation + ".", AlertType.ERROR);
		e.printStackTrace();
	}

	public void addSchedule(Schedule schedule) {

		if (!schedules.contains(schedule)) {
			schedules.add(schedule);
		}
	}

	public void setUserId(Integer userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId não pode ser nulo. erifique o usuário logado.");
		}
		this.userId = userId;
		System.out.println("setUserId chamado com: " + userId);
		loadSchedule();
	}

	private int getUserId() {
		return userId;
	}
}