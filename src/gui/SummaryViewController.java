package gui;

import java.io.File;
import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import gui.util.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Summary;
import model.dao.SummaryDAO;

public final class SummaryViewController implements MainAppAware {
    @FXML
    private TextField txtFieldTitle, txtFieldSubject, txtFieldTalkAbout, txtFieldAttachment;
    @FXML
    private TextArea txtArea;
    @FXML
    private Button btSave, btClear, btFile;

    private Main mainApp;

	@SuppressWarnings("exports")
	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		System.out.println("mainApp foi definido: " + (mainApp != null));
	}
    private SummaryDAO summaryDAO = new SummaryDAO();
    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() {
        Constraints.setTextAreaMaxLength(txtArea, 5000);
        Constraints.setTextFieldMaxLength(txtFieldTitle, 70);
        Constraints.setTextFieldMaxLength(txtFieldSubject, 60);
        Constraints.setTextFieldMaxLength(txtFieldTalkAbout, 100);
        Constraints.setTextFieldMaxLength(txtFieldAttachment, 200);
    }

    @FXML
    public void saveSummaryAction() {
    	if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
	    userId = mainApp.getLoggedUser().getId();
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

        System.out.println("Resumo salvo com sucesso!");
        clearLabelsAction();
        
    }

    @FXML
    public void clearLabelsAction() {
        txtFieldTitle.clear();
        txtArea.clear();
        txtFieldSubject.clear();
        txtFieldTalkAbout.clear();
        txtFieldAttachment.clear();
    }

    @FXML
    private void openFileExplorerAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo");
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt", "*.pdf");
        fileChooser.getExtensionFilters().addAll(textFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            System.out.println("Arquivo selecionado: " + filePath);
            txtFieldAttachment.setText(filePath); 
        }
    }

  
}