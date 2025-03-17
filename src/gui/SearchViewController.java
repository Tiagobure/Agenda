package gui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import application.Main;
import application.MainAppAware;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.KeyWord;
import model.Summary;
import model.dao.KeyWordDAO;
import model.dao.SummaryDAO;

public class SearchViewController implements MainAppAware {

	@FXML
	private TextField txtSearch;
	@FXML
	private ComboBox<String> txtTypeSearch;
	@FXML
	private ListView<Object> listResult; // Suporta Resumo e PalavraChave
	@FXML
	private Label msgFeedback;
	@FXML
	private Button btBack, btAvance, btSearch, btSearchAll;

	private Main mainApp;

	@SuppressWarnings("exports")
	@Override
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		System.out.println("mainApp foi definido: " + (mainApp != null));
	}

	private SummaryDAO summaryDAO = new SummaryDAO();
	private KeyWordDAO keyWordDAO = new KeyWordDAO();

	private int userId; // ID do usuário logado

	private Stack<String> searchHistory = new Stack<>(); // Pilha para armazenar buscas passadas
	private Stack<String> futureResearch = new Stack<>(); // Pilha para armazenar pesquisas que podem ser avançadas

	public void setUsuarioId(int userId) {
		this.userId = userId;
	}

	@FXML
	public void initialize() {

		txtTypeSearch.getItems().addAll("Resumos", "Palavras-Chave");
		txtTypeSearch.getSelectionModel().selectFirst();

		listResult.setCellFactory(param -> new ListCell<Object>() {
			@Override
			protected void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					Label label = new Label();
					Button btnMore = new Button("++");

					// Estilo do botão "Ver Mais" (laranja)
					btnMore.setStyle("-fx-background-color: #ff6f00; -fx-text-fill: white; -fx-font-size: 12px; "
							+ "-fx-padding: 5px 10px; -fx-border-radius: 5px; "
							+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");

					// Efeito de hover para o botão "Ver Mais"
					btnMore.setOnMouseEntered(e -> btnMore
							.setStyle("-fx-background-color: #ff8f00; -fx-text-fill: white; -fx-font-size: 12px; "
									+ "-fx-padding: 5px 10px; -fx-border-radius: 5px; "
									+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);"));
					btnMore.setOnMouseExited(e -> btnMore
							.setStyle("-fx-background-color: #ff6f00; -fx-text-fill: white; -fx-font-size: 12px; "
									+ "-fx-padding: 5px 10px; -fx-border-radius: 5px; "
									+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"));

					if (item instanceof Summary) {
						Summary summary = (Summary) item;
						String shortSummary = summary.getText().substring(0, Math.min(100, summary.getText().length()))
								+ "...";
						label.setText("Título: " + summary.getTitle() + "\n" + "Matéria: " + summary.getSubject() + "\n"
								+ "Assunto: " + summary.getTalkAbout() + "\n" + "Resumo: " + shortSummary);

						btnMore.setOnAction(event -> showPopupSummary(summary));

					} else if (item instanceof KeyWord) {
						KeyWord keyWord = (KeyWord) item;
						String shortDescription = keyWord.getDescription().substring(0,
								Math.min(100, keyWord.getDescription().length())) + "...";
						label.setText("Palavra-Chave: " + keyWord.getKeyword() + "\n" + "Matéria: "
								+ keyWord.getSubject() + "\n" + "Assunto: " + keyWord.getTalkAbout() + "\n"
								+ "Descrição: " + shortDescription);
						btnMore.setOnAction(event -> showPopupKeyWord(keyWord));
					}

					// Botão de excluir (azul)
					Button btnDelete = new Button("🗑");
					btnDelete.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 14px; "
							+ "-fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px; "
							+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);");

					// Efeito de hover para o botão de excluir
					btnDelete.setOnMouseEntered(e -> btnDelete
							.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-size: 14px; "
									+ "-fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px; "
									+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);"));
					btnDelete.setOnMouseExited(e -> btnDelete
							.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 14px; "
									+ "-fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px; "
									+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"));

					btnDelete.setOnAction(event -> deleteItem(item));

					// Layout horizontal (Texto + Botões)
					if ((item instanceof Summary && ((Summary) item).getText().length() > 100)
							|| (item instanceof KeyWord && ((KeyWord) item).getDescription().length() > 10)) {
						HBox hbox = new HBox(10, label, btnMore, btnDelete);
						setGraphic(hbox);
					} else {
						HBox hbox = new HBox(10, label, btnDelete);
						setGraphic(hbox);
					}
				}
			}
		});
	}

	private void deleteItem(Object item) {
		if (item instanceof Summary) {
			summaryDAO.delete((Summary) item);
		} else if (item instanceof KeyWord) {
			keyWordDAO.delete((KeyWord) item);
		}
		listResult.getItems().remove(item);
		msgFeedback.setText("Item deletado com sucesso!");
	}

	@FXML
	public void search() {
		String term = txtSearch.getText().trim();
		String typeSearch = txtTypeSearch.getValue();
		listResult.getItems().clear();

		if (term.isEmpty()) {
			msgFeedback.setText("Digite um termo para buscar!");
			return;
		}

		// Adiciona a pesquisa atual ao histórico e limpa a pilha de pesquisas futuras
		if (!searchHistory.isEmpty()) {
			String lastSearch = searchHistory.peek();
			if (!lastSearch.equals(term)) {
				searchHistory.push(term);
				// Se fizer uma nova busca, limpa os "avanços"

				futureResearch.clear();
			}
		} else {
			searchHistory.push(term);
			futureResearch.clear();
		}

		if (typeSearch.equals("Resumos")) {
			searchSummary(term);
		} else if (typeSearch.equals("Palavras-Chave")) {
			searchKeyWord(term);
		}
	}

	private void searchSummary(String termo) {
		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();
		List<Summary> result = summaryDAO.searchByWord(termo, userId);

		if (result.isEmpty()) {
			msgFeedback.setText("Nenhum resumo encontrado para: " + termo);
		} else {
			listResult.getItems().clear();
			listResult.getItems().addAll(result);
			msgFeedback.setText(result.size() + " resumo(s) encontrado(s)" + termo);
		}
	}

	private void searchKeyWord(String termo) {
		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();
		List<KeyWord> result = keyWordDAO.searchByTerm(termo, userId);

		if (result.isEmpty()) {
			msgFeedback.setText("Nenhuma palavra-chave encontrada para: " + termo);
		} else {
			listResult.getItems().clear(); //
			listResult.getItems().addAll(result);
			msgFeedback.setText(result.size() + " palavra(s)-chave encontrada(s)" + termo);
		}
	}

	@FXML
	public void back() {
		if (searchHistory.size() > 1) {
			futureResearch.push(searchHistory.pop()); // Salva a busca atual na pilha de futuros
			String lastSearch = searchHistory.peek(); // Obtém a busca anterior
			txtSearch.setText(lastSearch); // Atualiza o campo de busca
			search(); // Refaz a busca
		} else {
			msgFeedback.setText("Não há pesquisas anteriores!");
		}
	}

	@FXML
	public void advance() {
		if (!futureResearch.isEmpty()) {
			String advanceBusca = futureResearch.pop(); // Recupera a busca futura
			searchHistory.push(advanceBusca); // Adiciona de volta ao histórico
			txtSearch.setText(advanceBusca); // Atualiza o campo de busca
			search(); // Refaz a busca
		} else {
			msgFeedback.setText("Não há pesquisas futuras para avançar!");
		}
	}

	private void showPopupSummary(Summary summary) {
		Stage popupStage = new Stage();
		popupStage.setTitle("Detalhes do Resumo");

		// Layout principal
		VBox vbox = new VBox(15); // Espaçamento entre os elementos
		vbox.setStyle("-fx-padding: 25; -fx-background-color: #f5f5f5;"); // Fundo claro

		// Título do resumo
		Label lblTitle = new Label("Título: " + summary.getTitle());
		lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 18px;"); // Texto escuro e
																									// maior

		// Matéria
		Label lblSubject = new Label("Matéria: " + summary.getSubject());
		lblSubject.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;"); // Texto cinza escuro

		// Assunto
		Label lblTalkAbout = new Label("Assunto: " + summary.getTalkAbout());
		lblTalkAbout.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;"); // Texto cinza escuro

		// Texto do resumo
		Label lblText = new Label(summary.getText());
		lblText.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;"); // Texto escuro
		lblText.setWrapText(true); // Quebra de linha

		// Botão para abrir o anexo (se existir)
		Button btnOpenAttachment = new Button("Abrir Anexo");
		btnOpenAttachment.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-size: 14px; "
				+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"); // Botão azul

		// Efeito de hover para o botão "Abrir Anexo"
		btnOpenAttachment.setOnMouseEntered(e -> btnOpenAttachment
				.setStyle("-fx-background-color: #42a5f5; -fx-text-fill: white; -fx-font-size: 14px; "
						+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);"));
		btnOpenAttachment.setOnMouseExited(e -> btnOpenAttachment
				.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-size: 14px; "
						+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"));

		// Verifica se há um anexo e adiciona o botão "Abrir Anexo"
		if (summary.getAttachment() != null && !summary.getAttachment().isEmpty()) {
			btnOpenAttachment.setOnAction(event -> {
				File file = new File(summary.getAttachment());
				if (file.exists()) {
					try {
						java.awt.Desktop.getDesktop().open(file);
					} catch (IOException e) {
						Alerts.showAlert("Erro", null, "Não foi possível abrir o arquivo: " + e.getMessage(),
								AlertType.ERROR);
					}
				} else {
					Alerts.showAlert("Erro", null, "Arquivo não encontrado!", AlertType.ERROR);
				}
			});
		} else {
			btnOpenAttachment.setDisable(true); // Desabilita o botão se não houver anexo
			btnOpenAttachment.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14px; "
					+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
					+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"); // Botão cinza
		}

		// Botão "Fechar"
		Button btnClose = new Button("Fechar");
		btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 14px; "
				+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"); // Botão vermelho

		// Efeito de hover para o botão "Fechar"
		btnClose.setOnMouseEntered(
				e -> btnClose.setStyle("-fx-background-color: #ff6e6e; -fx-text-fill: white; -fx-font-size: 14px; "
						+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);"));
		btnClose.setOnMouseExited(
				e -> btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 14px; "
						+ "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"));

		btnClose.setOnAction(event -> popupStage.close());

		// Layout dos botões
		HBox buttonBox = new HBox(10, btnOpenAttachment, btnClose);
		buttonBox.setStyle("-fx-alignment: center-right; -fx-padding: 10 0 0 0;"); // Alinhamento à direita

		// Adiciona os componentes ao VBox
		vbox.getChildren().addAll(lblTitle, lblSubject, lblTalkAbout, lblText, buttonBox);

		// Adicionando ScrollPane
		ScrollPane scrollPane = new ScrollPane(vbox);
		scrollPane.setFitToWidth(true); // Ajusta a largura do conteúdo ao ScrollPane
		scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;"); // Remove bordas desnecessárias

		Scene scene = new Scene(scrollPane, 600, 400); // Tamanho maior para a janela
		popupStage.setScene(scene);

		// Impedir que a janela seja maximizada
		popupStage.setResizable(false);

		popupStage.show();
	}

	private void showPopupKeyWord(KeyWord key) {
	    Stage popupStage = new Stage();
	    popupStage.setTitle("Detalhes da Palavra-Chave");

	    // Layout principal
	    VBox vbox = new VBox(15); // Espaçamento entre os elementos
	    vbox.setStyle("-fx-padding: 25; -fx-background-color: #f5f5f5;"); // Fundo claro

	    // Palavra-chave
	    Label lblKeyWord = new Label("Palavra-Chave: " + key.getKeyword());
	    lblKeyWord.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 18px;"); // Texto escuro e maior

	    // Matéria
	    Label lblSubject = new Label("Matéria: " + key.getSubject());
	    lblSubject.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;"); // Texto cinza escuro

	    // Assunto
	    Label lblTalkAbout = new Label("Assunto: " + key.getTalkAbout());
	    lblTalkAbout.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;"); // Texto cinza escuro

	    // Descrição
	    Label lblDescription = new Label(key.getDescription());
	    lblDescription.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;"); // Texto escuro
	    lblDescription.setWrapText(true); // Quebra de linha

	    // Botão "Fechar"
	    Button btnClose = new Button("Fechar");
	    btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 14px; "
	            + "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
	            + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"); // Botão vermelho

	    // Efeito de hover para o botão "Fechar"
	    btnClose.setOnMouseEntered(
	            e -> btnClose.setStyle("-fx-background-color: #ff6e6e; -fx-text-fill: white; -fx-font-size: 14px; "
	                    + "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
	                    + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 5, 0, 0, 1);"));
	    btnClose.setOnMouseExited(
	            e -> btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 14px; "
	                    + "-fx-padding: 8px 16px; -fx-border-radius: 4px; "
	                    + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"));

	    btnClose.setOnAction(event -> popupStage.close());

	    // Layout dos botões
	    HBox buttonBox = new HBox(10, btnClose);
	    buttonBox.setStyle("-fx-alignment: center-right; -fx-padding: 10 0 0 0;"); // Alinhamento à direita

	    // Adiciona os componentes ao VBox
	    vbox.getChildren().addAll(lblKeyWord, lblSubject, lblTalkAbout, lblDescription, buttonBox);

	    // Adicionando ScrollPane
	    ScrollPane scrollPane = new ScrollPane(vbox);
	    scrollPane.setFitToWidth(true); // Ajusta a largura do conteúdo ao ScrollPane
	    scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;"); // Remove bordas desnecessárias

	    Scene scene = new Scene(scrollPane, 600, 400); // Tamanho maior para a janela
	    popupStage.setScene(scene);

	    // Impedir que a janela seja maximizada
	    popupStage.setResizable(false);

	    popupStage.show();
	}
	@FXML
	public void listAll() {
		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();

		listResult.getItems().clear();

		List<Summary> summaries = summaryDAO.findAllByUserId(userId);
		if (!summaries.isEmpty()) {
			listResult.getItems().addAll(summaries);
		}

		List<KeyWord> keyWords = keyWordDAO.findAllByUserId(userId);
		if (!keyWords.isEmpty()) {
			listResult.getItems().addAll(keyWords);
		}

		// Exibe feedback ao usuário
		if (listResult.getItems().isEmpty()) {
			msgFeedback.setText("Nenhum resumo ou palavra-chave encontrado.");
		} else {
			msgFeedback.setText("Listando todos os resumos e palavras-chave.");
		}
	}

}
