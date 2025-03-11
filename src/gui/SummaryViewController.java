package gui;

import java.util.List;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Summary;
import model.dao.SummaryDAO;

public class SummaryViewController {
	@FXML
	private TextField txtFieldTitle, txtFieldSubject,txtFieldTalkAbout, txtFieldAttachment;
	@FXML
	private TextArea txtArea;
	@FXML
	private Button btSave, btClear, btFile;
	
	@FXML
	private ListView<Object> listView;
	
	private Main mainApp;

	private SummaryDAO summaryDAO = new SummaryDAO();
	private int userId; // ID do usuário logado

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@FXML
	public void initialize() {
		
		Constraints.setTextAreaMaxLength(txtArea, 5000);
		Constraints.setTextFieldMaxLength(txtFieldTitle, 70);
		Constraints.setTextFieldMaxLength(txtFieldSubject, 60);
		Constraints.setTextFieldMaxLength(txtFieldTalkAbout,100 );
		Constraints.setTextFieldMaxLength(txtFieldAttachment, 200);

		
	}

	@FXML
	public void saveSummary() {

		String title = txtFieldTitle.getText();
		String text = txtArea.getText();
		String subject = txtFieldSubject.getText();
		String talkAbout = txtFieldTalkAbout.getText();
		String attachment = txtFieldAttachment.getText();

		if (title.isEmpty() || text.isEmpty() || subject.isEmpty() || talkAbout.isEmpty()) {
			Alerts.showAlert("Aviso", null, "Campos em branco", AlertType.WARNING);
			return;
		}

		Summary summary = new Summary(title, text, subject, talkAbout);
		summary.setAttachment(attachment);
		summaryDAO.inserir(summary, userId);

		// Limpar campos após salvar
		System.out.println("Resumo salvo com sucesso!");
		clearLabels();
		mainApp.closeScene(btSave);
		
	}

	private void clearLabels() {
		txtFieldTitle.clear();
		txtArea.clear();
		txtFieldSubject.clear();
		txtFieldTalkAbout.clear();
		txtFieldAttachment.clear();
	}

	 
		
		 public void listSummary() {
	      List<Summary> list =  summaryDAO.listLastSummary(userId, 5);
	      listView.getItems().addAll(list);

		 }
	
	
}