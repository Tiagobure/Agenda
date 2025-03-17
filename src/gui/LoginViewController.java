package gui;



import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import db.DbException;

public class LoginViewController implements MainAppAware{

	private Main mainApp;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private TextField txtname;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button btLogin;
	@FXML
	private Button btRegister;


	@FXML
	private void onLogin() {
		String nome = txtname.getText().trim();
		String senha = passwordField.getText().trim();

		if (nome.isEmpty() || senha.isEmpty()) {
			Alerts.showAlert("Erro", null, "Preencha todos os campos!", AlertType.ERROR);
			return;
		}

		try {
			
			openMainView();
			
		} catch (DbException e) {
			Alerts.showAlert("Erro", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void openMainView() {
		try {
			Stage stage = (Stage) btLogin.getScene().getWindow();
			stage.close();
			mainApp.loadView("/gui/MainView.fxml", "Agenda", null);
			
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Não foi possível abrir a tela principal!", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void RegistrationUser() {
		try {
			
			mainApp.loadView("/gui/RegisterView.fxml", "Cadastro", null);
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Não foi possível abrir a tela de cadastro!", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	
	@FXML
	private void hoverLogin() {
	    btLogin.setStyle("-fx-background-color: #0D47A1; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}

	@FXML
	private void normalLogin() {
	    btLogin.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}

	// Hover Cadastro
	@FXML
	private void hoverRegister() {
		btRegister.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}

	@FXML
	private void normalRegister() {
		btRegister.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-pref-width: 260px; -fx-padding: 10px; -fx-border-radius: 6px;");
	}
	
	
}