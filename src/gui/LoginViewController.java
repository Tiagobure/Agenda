package gui;



import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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

		// Validação dos campos
		if (nome.isEmpty() || senha.isEmpty()) {
			Alerts.showAlert("Erro", null, "Preencha todos os campos!", AlertType.ERROR);
			return;
		}

		try {
			//Usuario usuario = userDAO.fazerLogin(nome, senha);
			openMainView();
			// Fecha a tela de login
			mainApp.closeScene(btLogin);
			
		} catch (DbException e) {
			// Exibe mensagem de erro em caso de falha no login
			Alerts.showAlert("Erro", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void openMainView() {
		try {
			mainApp.carregarTela("/gui/MainView.fxml", "Agenda", null);
		} catch (Exception e) {
			Alerts.showAlert("Erro", null, "Não foi possível abrir a tela principal!", AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@FXML
	private void RegistrationUser() {
		try {
			mainApp.carregarTela("/gui/RegisterView.fxml", "Cadastro", null);
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