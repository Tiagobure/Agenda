package application;

import java.io.IOException;
import java.util.Map;
import db.DataBase;
import gui.ScheduleRegistrationViewController;
import gui.LoginViewController;
import gui.util.Alerts;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Schedule;
import model.User;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Main extends Application {

	private static Scene mainScene;
	private static Main instance;

	private int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	private User loggedUser;

	public void setLoggedUser(User user) {
		this.loggedUser = user;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public Main() {
		if (instance == null) {
			instance = this;
		} else {
			throw new IllegalStateException("Main já foi inicializado!");
		}
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// tela de login
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginView.fxml"));
			VBox root = loader.load();
			LoginViewController controller = loader.getController();
			controller.setMainApp(this);

			mainScene = new Scene(root);
			primaryStage.setTitle("Login");
			primaryStage.setScene(mainScene);
			primaryStage.show();
		} catch (IOException e) {
			Alerts.showAlert("Erro", "Erro ao carregar a tela de login",
					"O arquivo da tela de login não pôde ser carregado.", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	public void loadView(String fxml, String titulo, Map<String, Object> params) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
			Parent root = loader.load();
			Object controller = loader.getController();

			if (controller instanceof MainAppAware) {
				((MainAppAware) controller).setMainApp(this);
			}

			if (controller instanceof ScheduleRegistrationViewController && params != null) {
				ScheduleRegistrationViewController scheduleController = (ScheduleRegistrationViewController) controller;

				if (params.containsKey("cronograma")) {
					scheduleController.setSelectedSchedule((Schedule) params.get("cronograma"));
				}

				if (params.containsKey("usuarioId")) {
					scheduleController.setUserId((int) params.get("usuarioId"));
				}
			}
			Stage stage = new Stage();
			stage.setScene(new Scene(root));

			stage.setTitle(titulo);
			stage.sizeToScene();
			stage.initModality(Modality.APPLICATION_MODAL);

			if (mainScene != null && mainScene.getWindow() != null) {
				stage.initOwner(mainScene.getWindow());
			}

			stage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("Erro", "Erro ao carregar a tela", "Não foi possível carregar a tela: " + fxml,
					AlertType.ERROR);
			e.printStackTrace();
		} catch (NullPointerException | ClassCastException e) {
			Alerts.showAlert("Erro", "Erro de parâmetro", "Parâmetros inválidos ao carregar a tela.", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		DataBase.init();
		launch(args);
	}
}