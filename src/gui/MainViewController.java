package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import application.Main;
import application.MainAppAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{

	private Main mainApp;
	@FXML
	private Button btSummary, btKeyWord, btSchedule;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		
	}

	

	@FXML
	private void openSumaryAction() {
		mainApp.loadView("/gui/ResumosView.fxml", "Resumos", null);
	}

	@FXML
	private void openKeyWordAction() {
		mainApp.loadView("/gui/PalavrasChaveView.fxml", "Palavras-Chave", null);
	}

	@FXML
	private void openScheduleAction() {
		mainApp.loadView("/gui/MostrarCronogramaView.fxml", "Cronograma", null);
	}

	@FXML
	private void openShearch() {
		mainApp.loadView("/gui/BuscaView.fxml", "Busca", null);
	}

	

}
