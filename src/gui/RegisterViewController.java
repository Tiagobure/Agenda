package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import model.dao.UserDAO;
import application.Main;
import gui.util.*;
public class RegisterViewController {

	private Main mainApp;

	@FXML
	private TextField txtName;
	@FXML
	private PasswordField passField;

	private UserDAO userDAO = new UserDAO();

	@FXML
	private Button btRegister;
	
	@FXML
	private void startRegistration() {
		String name = txtName.getText().trim();
		String password = passField.getText().trim();

		// Validação básica
		if (name.isEmpty() || password.isEmpty()) {
			Alerts.showAlert("Error", null, "Preencha todos os campos!", AlertType.ERROR);
			return;
		}

		if (password.length() < 6) {
			Alerts.showAlert("Error", null,"A senha deve ter pelo menos 6 caracteres!", AlertType.WARNING);
			return;
		}

		User usuario = new User(name, password);

		try {
			userDAO.userRegister(usuario);
			Alerts.showAlert("Sucesso", null,"Usuário cadastrado com sucesso!", AlertType.INFORMATION);
			clearLabel();
			Stage stage = (Stage) btRegister.getScene().getWindow();
			stage.close();
			
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Nome de usuário já cadastrado!", AlertType.ERROR);
		}
	}

	

	private void clearLabel() {
		txtName.clear();
		passField.clear();
		
	}
	
	@FXML
	private void hoverRegister() {
	    btRegister.setStyle("-fx-background-color: #0D47A1; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}

	@FXML
	private void normalRegister() {
		btRegister.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}
}
