package gui;

import java.util.List;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.KeyWord;
import model.Summary;
import model.dao.KeyWordDAO;
import model.dao.SummaryDAO;

public class SearchViewController {

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
					Button btnMore = new Button("Ver Mais");

					btnMore.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 12px;");
					if (item instanceof Summary) {
						Summary summary = (Summary) item;
						String shortSummary = summary.getText().substring(0, Math.min(100, summary.getText().length()))
								+ "...";
						label.setText("Título: " + summary.getTitle() + "\n" + "Matéria: " + summary.getSubject() + "\n"
								+ "Assunto: " + summary.getTalkAbout() + "\n" + "Resumo: " + shortSummary);

						btnMore.setOnAction(event -> showSummary(summary));

					} else if (item instanceof KeyWord) {

						KeyWord keyWord = (KeyWord) item;
						String shortDescription = keyWord.getDescription().substring(0,
								Math.min(100, keyWord.getDescription().length())) + "...";
						label.setText("Palavra-Chave: " + keyWord.getKeyword() + "\n" + "Matéria: "
								+ keyWord.getSubject() + "\n" + "Assunto: " + keyWord.getTalkAbout() + "\n"
								+ "Descrição: " + shortDescription);
						btnMore.setOnAction(event -> showPopupKeyWord(keyWord));
					}

					// Criando o botão com um ícone de lixeira
					Button btnDelete = new Button("🗑");
					btnDelete.setStyle(
							"-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;");

					// Efeito de sombra para destacar o botão
					DropShadow shadow = new DropShadow();
					shadow.setRadius(5.0);
					shadow.setOffsetX(2.0);
					shadow.setOffsetY(2.0);
					shadow.setColor(Color.rgb(0, 0, 0, 0.3));
					btnDelete.setEffect(shadow);

					// Efeito de hover (muda de cor ao passar o mouse)
					btnDelete.setOnMouseEntered(e -> btnDelete.setStyle(
							"-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;"));
					btnDelete.setOnMouseExited(e -> btnDelete.setStyle(
							"-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 30px; -fx-min-height: 30px; -fx-background-radius: 15px;"));

					btnDelete.setOnAction(event -> deleteItem(item));

					// Layout horizontal (Texto + Botão)
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

	// Método para deletar um item da lista e do banco de dados
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

	private void showSummary(Summary summary) {
		Stage popupStage = new Stage();
		popupStage.setTitle("Resumo Completo");

		VBox vbox = new VBox(10);
		vbox.setStyle("-fx-padding: 20; -fx-background-color: #ffebee;");

		Label lblTitle = new Label("Título: " + summary.getTitle());
		lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #d32f2f; -fx-font-size: 16px;");

		Label lblSubject = new Label("Matéria: " + summary.getSubject());
		Label lnlTalkAbout = new Label("Assunto: " + summary.getTalkAbout());

		Label lblText = new Label(summary.getText());
		lblText.setWrapText(true);

		Button btnClose = new Button("Fechar");
		btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white;");
		btnClose.setOnAction(event -> popupStage.close());

		vbox.getChildren().addAll(lblTitle, lblSubject, lnlTalkAbout, lblText, btnClose);

		Scene scene = new Scene(vbox, 400, 300);
		popupStage.setScene(scene);
		popupStage.show();
	}

	private void showPopupKeyWord(KeyWord key) {
		Stage popupStage = new Stage();
		popupStage.setTitle("Detalhes da Palavra-Chave");

		VBox vbox = new VBox(10);
		vbox.setStyle("-fx-padding: 20; -fx-background-color: #ffebee;");

		Label lblKeyWord = new Label("Palavra-Chave: " + key.getKeyword());
		lblKeyWord.setStyle("-fx-font-weight: bold; -fx-text-fill: #d32f2f; -fx-font-size: 16px;");

		Label lblSubject = new Label("Matéria: " + key.getSubject());
		lblSubject.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

		Label lblTalkAbout = new Label("Assunto: " + key.getTalkAbout());
		lblTalkAbout.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

		Label lblDescription = new Label(key.getDescription());
		lblDescription.setWrapText(true);

		Button btnClose = new Button("Fechar");
		btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white;");
		btnClose.setOnAction(event -> popupStage.close());

		vbox.getChildren().addAll(lblKeyWord, lblSubject, lblTalkAbout, lblDescription, btnClose);

		Scene scene = new Scene(vbox, 400, 250);
		popupStage.setScene(scene);
		popupStage.show();
	}
	
	@FXML
	public void listAll() {
	    // Limpa a lista atual
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
