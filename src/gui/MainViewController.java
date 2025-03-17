package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable, MainAppAware {

	private Main mainApp;

	@FXML
	private Button btSummary, btKeyWord, btSchedule, btSearch;

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
				controller.setUserId(userId); // Passa o userId para o controlador
			}

		} catch (IOException e) {
			Alerts.showAlert("Erro ao carregar a tela", null, "Não foi possível carregar a tela: " + fxmlPath,
					AlertType.INFORMATION);
		}
	}

	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
}
