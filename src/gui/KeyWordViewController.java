package gui;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.KeyWord;
import model.dao.KeyWordDAO;

public final class  KeyWordViewController implements MainAppAware  {
	
	private Main mainApp;

	@SuppressWarnings("exports")
	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		System.out.println("mainApp foi definido: " + (mainApp != null));
	}

	@FXML
	private TextField txtKeyWord,txtSubject,txtTalkAbout;
	@FXML
	private TextArea txtdescription;
	@FXML
	private Button btSave;
	

	private KeyWordDAO keyWordDAO = new KeyWordDAO();
	
	private int userId; 

	public void setUsuarioId(int userId) {
		this.userId = userId;
	}

	@FXML
	public void initialize() {
		
		Constraints.setTextAreaMaxLength(txtdescription, 1000);
		Constraints.setTextFieldMaxLength(txtKeyWord, 60);
		Constraints.setTextFieldMaxLength(txtSubject, 50);
		Constraints.setTextFieldMaxLength(txtTalkAbout, 50);
	}
	@FXML
	public void saveKeyWordAction() {
		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
	    userId = mainApp.getLoggedUser().getId();
	    
		String word = txtKeyWord.getText();
		String description = txtdescription.getText();
		String subject = txtSubject.getText();
		String talkAbout = txtTalkAbout.getText();

		if (word.isEmpty() || description.isEmpty() || subject.isEmpty() || talkAbout.isEmpty()) {
			Alerts.showAlert("Aviso", null, "Campos em Branco", AlertType.WARNING);
			return;
		}

		KeyWord keyWord = new KeyWord(word, description, subject, talkAbout);
		keyWordDAO.inserir(keyWord, userId);

		Alerts.showAlert("Aviso", null, "Palavra-chave salva com sucesso!", AlertType.INFORMATION);

		ClearFilds();
	}

	private void ClearFilds() {
		txtKeyWord.clear();
		txtdescription.clear();
		txtSubject.clear();
		txtTalkAbout.clear();
	}
}
