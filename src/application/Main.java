package application;

import java.io.IOException;
import java.util.Map;
import db.DataBase;
import gui.ScheduleRegistrationViewController;
import gui.LoginViewController;
import gui.util.Alerts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import model.Schedule;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Main extends Application {

	private static Scene mainScene;
	private static Main instance; // Singleton instance

	private int userId; // ID do usuário logado

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

//	public Integer getUserId() {
//		if (userId == null) {
//			throw new IllegalStateException("UserId não está definido.");
//		}
//		return userId;
//	}

	public void setUserId(Integer userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId não pode ser nulo.");
		}
		this.userId = userId;
	}

	public Main() {
		instance = this;
	}

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// Carrega a tela de login
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

	public void closeScene(Button bt) {
		if (bt != null && bt.getScene() != null) {
			Stage stage = (Stage) bt.getScene().getWindow();
			if (stage != null) {
				stage.close();
			}
		}
	}

	public void loadView(String fxml, String titulo, Map<String, Object> params) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
			Parent root = loader.load();
			Object controller = loader.getController();

			// Injeção da aplicação principal
			if (controller instanceof MainAppAware) {
				((MainAppAware) controller).setMainApp(this);
			}

			// Configuração de parâmetros específicos
			if (controller instanceof ScheduleRegistrationViewController && params != null) {
				ScheduleRegistrationViewController scheduleController = (ScheduleRegistrationViewController) controller;

				if (params.containsKey("cronograma")) {
					scheduleController.setSelectedSchedule((Schedule) params.get("cronograma"));
				}

				if (params.containsKey("usuarioId")) {
					scheduleController.setUserId((int) params.get("usuarioId"));
					}
				}
			

			// Configuração da nova janela
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle(titulo);
			stage.sizeToScene();
			stage.show();
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