package gui;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Summary;
import model.dao.SummaryDAO;

public class SummaryViewController {
	@FXML
	private TextField campoTitulo;
	@FXML
	private TextArea campoTexto;
	@FXML
	private TextField campoMateria;
	@FXML
	private TextField campoAssunto;
	@FXML
	private TextField campoAnexo;
	@FXML
	private Button btSave;

	private Main mainApp;

	private SummaryDAO resumoDAO = new SummaryDAO();
	private int usuarioId; // ID do usuário logado

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	@FXML
	public void initialize() {
		// Limitar o tamanho do texto
		campoTexto.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > 5000) { // Limite de 5000 caracteres
				campoTexto.setText(oldValue);
			}
		});
	}

	@FXML
	public void saveSummary() {

		String titulo = campoTitulo.getText();
		String texto = campoTexto.getText();
		String materia = campoMateria.getText();
		String assunto = campoAssunto.getText();
		String anexo = campoAnexo.getText();

		if (titulo.isEmpty() || texto.isEmpty() || materia.isEmpty() || assunto.isEmpty()) {
			Alerts.showAlert("Aviso", null, "Campos em branco", AlertType.WARNING);
			return;
		}

		Summary resumo = new Summary(titulo, texto, materia, assunto);
		resumo.setAttachment(anexo);
		resumoDAO.inserir(resumo, usuarioId);

		// Limpar campos após salvar
		System.out.println("Resumo salvo com sucesso!");
		clearLabels();
		mainApp.closeScene(btSave);
		
	}

	private void clearLabels() {
		campoTitulo.clear();
		campoTexto.clear();
		campoMateria.clear();
		campoAssunto.clear();
		campoAnexo.clear();
	}

	
}