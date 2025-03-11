package application;

import java.io.IOException;
import java.util.List;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;

public class Main extends Application {

	private static Scene mainScene;

	public void closeScene(Button bt) {
		Stage stage = (Stage) bt.getScene().getWindow();
		stage.close();
	}
	@Override
	public void start(Stage primaryStage) {
		try {

			// tela de login->cadastro

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginView.fxml"));
			VBox root = loader.load();
			LoginViewController controller = loader.getController();
			controller.setMainApp(this);
			mainScene = new Scene(root);
			primaryStage.setTitle("Login");
			primaryStage.setScene(mainScene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();

			Alerts.showAlert("Erro", "Erro ao carregar a tela de login",
					"O arquivo a tela de login não pôde ser carregado.", AlertType.ERROR);
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
				ScheduleRegistrationViewController cronogramaController = (ScheduleRegistrationViewController) controller;

				if (params.containsKey("cronograma")) {
					cronogramaController.setCronogramaSelecionado((Schedule) params.get("cronograma"));
				}

				if (params.containsKey("usuarioId")) {
					cronogramaController.setUsuarioId((int) params.get("usuarioId"));
				}
			}

			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle(titulo);
//	        stage.setMaximized(true);
//			stage.sizeToScene();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao carregar a tela", null, "Não foi possível carregar a tela: " + fxml,
					AlertType.INFORMATION);

		}
	}

	public static void main(String[] args) {
		DataBase.init();
		launch(args);
	}
}
