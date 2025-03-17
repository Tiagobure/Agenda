package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import gui.util.Notifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Schedule;

public class MainViewController implements Initializable, MainAppAware {

	private Main mainApp;
	@SuppressWarnings("exports")
	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private Button btSummary, btKeyWord, btSchedule, btSearch, btLogout;

	@FXML
	private StackPane contentArea;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	@FXML
	private void openSumaryAction() {
		loadViewStack("/gui/SummaryView.fxml", null);
	}

	@FXML
	private void openKeyWordAction() {
		loadViewStack("/gui/KeyWordView.fxml", null);
	}

	@FXML
	private void openScheduleAction() {
		Integer userId = mainApp.getUserId(); // Certifique-se de que mainApp não é null
		loadViewStack("/gui/ScheduleView.fxml", userId);
	}

	@FXML
	private void openSearchAction() {
		mainApp.loadView("/gui/SearchView.fxml", "Busca", null);
	}

	@FXML
	private void openDeveloperLink(ActionEvent event) {
	    try {
	        java.awt.Desktop.getDesktop().browse(new java.net.URI("https://github.com/Tiagobure"));
	    } catch (Exception e) {
	        Alerts.showAlert("Erro", null, "Não foi possível abrir o link.", AlertType.ERROR);
	    }
	}
	private void loadViewStack(String fxmlPath, Integer userId) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Node view = loader.load();
			contentArea.getChildren().setAll(view);

			
		      if (loader.getController() instanceof MainAppAware) {
		            ((MainAppAware) loader.getController()).setMainApp(mainApp);
		        }

			if (loader.getController() instanceof ScheduleViewController) {
				ScheduleViewController controller = (ScheduleViewController) loader.getController();
				if (userId == null) {
					throw new IllegalArgumentException("UserId não pode ser nulo.");
				}
				controller.setUserId(userId); 
			}

		} catch (IOException e) {
			Alerts.showAlert("Erro ao carregar a tela", null, "Não foi possível carregar a tela: " + fxmlPath,
					AlertType.INFORMATION);
		}
	}
	
	@FXML
	private void logoutAction() {
	    try {
	        Stage currentStage = (Stage) contentArea.getScene().getWindow();
	        currentStage.close();

	        Stage loginStage = new Stage();
	        mainApp.start(loginStage);
	    } catch (Exception e) {
	        Alerts.showAlert("Erro", null, "Não foi possível fazer logout.", AlertType.ERROR);
	        e.printStackTrace();
	    }
	}
	
	

}
