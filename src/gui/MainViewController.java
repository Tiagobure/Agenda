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

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private LineChart<String, Number> graficoHoras;

	@FXML
	private VBox containerCalendario;

	@FXML
	private ComboBox<String> comboVisualizacao;

	@FXML
	private Label labelMesAtual;

	@FXML
	private VBox diasDoMes;

	private YearMonth mesAtual; // Para controle do calendário

	

	@FXML
	private void mudarVisualizacao() {
		String selecao = comboVisualizacao.getSelectionModel().getSelectedItem();
		if (selecao.equals("Gráfico de Horas")) {
			graficoHoras.setVisible(true);
			containerCalendario.setVisible(false);
		} else {
			graficoHoras.setVisible(false);
			containerCalendario.setVisible(true);
		}
	}

	private void carregarDadosGrafico() {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Horas Estudadas");
		series.getData().add(new XYChart.Data<>("Seg", 4));
		series.getData().add(new XYChart.Data<>("Ter", 3));
		series.getData().add(new XYChart.Data<>("Qua", 6));
		series.getData().add(new XYChart.Data<>("Qui", 2));
		series.getData().add(new XYChart.Data<>("Sex", 5));
		graficoHoras.getData().add(series);
	}

	private void atualizarCalendario() {
		diasDoMes.getChildren().clear(); // Limpa os dias anteriores
		labelMesAtual.setText(mesAtual.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

		LocalDate primeiroDia = mesAtual.atDay(1);
		int diasNoMes = mesAtual.lengthOfMonth();
		int diaSemana = (primeiroDia.getDayOfWeek().getValue() == 7) ? 0 : primeiroDia.getDayOfWeek().getValue();

		HBox semanaAtual = new HBox(5);
		semanaAtual.setAlignment(Pos.CENTER);

		for (int i = 0; i < diaSemana; i++) {
			semanaAtual.getChildren().add(new Label(""));
		}

		for (int dia = 1; dia <= diasNoMes; dia++) {
			adicionarBotaoDia(semanaAtual, dia);

			if ((diaSemana + dia) % 7 == 0) {
				diasDoMes.getChildren().add(semanaAtual);
				semanaAtual = new HBox(5);
				semanaAtual.setAlignment(Pos.CENTER);
			}
		}

		if (!semanaAtual.getChildren().isEmpty()) {
			diasDoMes.getChildren().add(semanaAtual);
		}
	}

	private void adicionarBotaoDia(HBox semanaAtual, int dia) {
		Button botaoDia = new Button(String.valueOf(dia));
		botaoDia.setOnAction(this::selecionarDia);
		semanaAtual.getChildren().add(botaoDia);
	}

	@FXML
	private void mesAnterior() {
		mesAtual = mesAtual.minusMonths(1);
		atualizarCalendario();
	}

	@FXML
	private void proximoMes() {
		mesAtual = mesAtual.plusMonths(1);
		atualizarCalendario();
	}


	@FXML
	private void abrirResumos() {
		mainApp.carregarTela("/gui/ResumosView.fxml", "Resumos", null);
	}

	@FXML
	private void abrirPalavrasChave() {
		mainApp.carregarTela("/gui/PalavrasChaveView.fxml", "Palavras-Chave", null);
	}

	@FXML
	private void abrirCronograma() {
		mainApp.carregarTela("/gui/MostrarCronogramaView.fxml", "Cronograma", null);
	}

	@FXML
	private void abrirBusca() {
		mainApp.carregarTela("/gui/BuscaView.fxml", "Busca", null);
	}

	@FXML
	private void selecionarDia(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof Button) {
			Button botaoClicado = (Button) source;
			String diaSelecionado = botaoClicado.getText();
			System.out.println("Dia selecionado: " + diaSelecionado);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		
	}
}
