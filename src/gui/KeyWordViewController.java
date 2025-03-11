package gui;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.KeyWord;
import model.dao.KeyWordDAO;

public class KeyWordViewController {
	@FXML
	private TextField txtKeyWord,txtSubject,txtTalkAbout;
	@FXML
	private TextArea txtdescription;
	@FXML
	private Button btSave;
	

	private KeyWordDAO keyWordDAO = new KeyWordDAO();
	
	private int userId; // ID do usuário logado

	public void setUsuarioId(int userId) {
		this.userId = userId;
	}

	@FXML
	public void initialize() {
		Constraints.setTextAreaMaxLength(txtdescription, 1000);
		Constraints.setTextFieldMaxLength(txtKeyWord, 70);
		Constraints.setTextFieldMaxLength(txtSubject, 50);
		Constraints.setTextFieldMaxLength(txtTalkAbout, 50);
	}
	@FXML
	public void saveKeyWordAction() {
		String word = txtKeyWord.getText();
		String description = txtdescription.getText();
		String subject = txtSubject.getText();
		String talkAbout = txtTalkAbout.getText();

		if (word.isEmpty() || description.isEmpty() || subject.isEmpty() || talkAbout.isEmpty()) {
			System.out.println("Preencha todos os campos!");
			return;
		}

		KeyWord keyWord = new KeyWord(word, description, subject, talkAbout);
		keyWordDAO.inserir(keyWord, userId);

		System.out.println("Palavra-chave salva com sucesso!");
		ClearFilds();
	}

	private void ClearFilds() {
		txtKeyWord.clear();
		txtdescription.clear();
		txtSubject.clear();
		txtTalkAbout.clear();
	}
}
