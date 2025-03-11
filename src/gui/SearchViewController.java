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
	private TextField campoBusca;
	@FXML
	private ComboBox<String> comboTipoBusca;
	@FXML
	private ListView<Object> listaResultados; // Suporta Resumo e PalavraChave
	@FXML
	private Label mensagemFeedback;

	private SummaryDAO resumoDAO = new SummaryDAO();
	private KeyWordDAO palavraChaveDAO = new KeyWordDAO();

	private int usuarioId; // ID do usuário logado

	private Stack<String> historicoPesquisas = new Stack<>(); // Pilha para armazenar buscas passadas
	private Stack<String> pesquisasFuturas = new Stack<>(); // Pilha para armazenar pesquisas que podem ser avançadas

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	@FXML
	public void initialize() {
		comboTipoBusca.getItems().addAll("Resumos", "Palavras-Chave");
		comboTipoBusca.getSelectionModel().selectFirst();

		listaResultados.setCellFactory(param -> new ListCell<Object>() {
			@Override
			protected void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					Label label = new Label();
					Button btnVerMais = new Button("Ver Mais");

					btnVerMais.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white; -fx-font-size: 12px;");
					if (item instanceof Summary) {
						Summary summary = (Summary) item;
						String resumoCurto = summary.getText().substring(0, Math.min(100, summary.getText().length()))
								+ "...";
						label.setText("Título: " + summary.getTitle() + "\n" + "Matéria: " + summary.getSubject() + "\n"
								+ "Assunto: " + summary.getTalkAbout() + "\n" + "Resumo: " + resumoCurto);

						btnVerMais.setOnAction(event -> showSummary(summary));

					} else if (item instanceof KeyWord) {

						KeyWord palavraChave = (KeyWord) item;
						String descricaoCurta = palavraChave.getDescription().substring(0,
								Math.min(100, palavraChave.getDescription().length())) + "...";
						label.setText("Palavra-Chave: " + palavraChave.getKeyword() + "\n" + "Matéria: "
								+ palavraChave.getSubject() + "\n" + "Assunto: " + palavraChave.getTalkAbout() + "\n"
								+ "Descrição: " + descricaoCurta);
						btnVerMais.setOnAction(event -> mostrarPopupPalavraChave(palavraChave));
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
						HBox hbox = new HBox(10, label, btnVerMais, btnDelete);
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
			resumoDAO.deletar((Summary) item);
		} else if (item instanceof KeyWord) {
			palavraChaveDAO.delete((KeyWord) item);
		}
		listaResultados.getItems().remove(item);
		mensagemFeedback.setText("Item deletado com sucesso!");
	}

	@FXML
	private void search() {
		String termo = campoBusca.getText().trim();
		String tipoBusca = comboTipoBusca.getValue();
		listaResultados.getItems().clear();

		if (termo.isEmpty()) {
			mensagemFeedback.setText("Digite um termo para buscar!");
			return;
		}

		// Adiciona a pesquisa atual ao histórico e limpa a pilha de pesquisas futuras
		if (!historicoPesquisas.isEmpty()) {
			String ultimaBusca = historicoPesquisas.peek();
			if (!ultimaBusca.equals(termo)) { // Evita salvar buscas duplicadas seguidas
				historicoPesquisas.push(termo);
				pesquisasFuturas.clear(); // Se fizer uma nova busca, limpa os "avanços"
			}
		} else {
			historicoPesquisas.push(termo);
			pesquisasFuturas.clear();
		}

		if (tipoBusca.equals("Resumos")) {
			searchSummary(termo);
		} else if (tipoBusca.equals("Palavras-Chave")) {
			searchKeyWord(termo);
		}
	}

	private void searchSummary(String termo) {
		List<Summary> resultados = resumoDAO.searchByWord(termo, usuarioId);

		if (resultados.isEmpty()) {
			mensagemFeedback.setText("Nenhum resumo encontrado para: " + termo);
		} else {
			listaResultados.getItems().addAll(resultados);
			mensagemFeedback.setText(resultados.size() + " resumo(s) encontrado(s)");
		}
	}

	private void searchKeyWord(String termo) {
		List<KeyWord> resultados = palavraChaveDAO.searchByTerm(termo, usuarioId);

		if (resultados.isEmpty()) {
			mensagemFeedback.setText("Nenhuma palavra-chave encontrada para: " + termo);
		} else {
			listaResultados.getItems().addAll(resultados);
			mensagemFeedback.setText(resultados.size() + " palavra(s)-chave encontrada(s)");
		}
	}

	@FXML
	private void back() {
		if (historicoPesquisas.size() > 1) {
			pesquisasFuturas.push(historicoPesquisas.pop()); // Salva a busca atual na pilha de futuros
			String ultimaBusca = historicoPesquisas.peek(); // Obtém a busca anterior
			campoBusca.setText(ultimaBusca); // Atualiza o campo de busca
			search(); // Refaz a busca
		} else {
			mensagemFeedback.setText("Não há pesquisas anteriores!");
		}
	}

	@FXML
	private void advance() {
		if (!pesquisasFuturas.isEmpty()) {
			String proximaBusca = pesquisasFuturas.pop(); // Recupera a busca futura
			historicoPesquisas.push(proximaBusca); // Adiciona de volta ao histórico
			campoBusca.setText(proximaBusca); // Atualiza o campo de busca
			search(); // Refaz a busca
		} else {
			mensagemFeedback.setText("Não há pesquisas futuras para avançar!");
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

	private void mostrarPopupPalavraChave(KeyWord palavraChave) {
		Stage popupStage = new Stage();
		popupStage.setTitle("Detalhes da Palavra-Chave");

		VBox vbox = new VBox(10);
		vbox.setStyle("-fx-padding: 20; -fx-background-color: #ffebee;");

		Label lblKeyWord = new Label("Palavra-Chave: " + palavraChave.getKeyword());
		lblKeyWord.setStyle("-fx-font-weight: bold; -fx-text-fill: #d32f2f; -fx-font-size: 16px;");

		Label lblSubject = new Label("Matéria: " + palavraChave.getSubject());
		lblSubject.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

		Label lblTalkAbout = new Label("Assunto: " + palavraChave.getTalkAbout());
		lblTalkAbout.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

		Label lblDescription = new Label(palavraChave.getDescription());
		lblDescription.setWrapText(true);

		Button btnClose = new Button("Fechar");
		btnClose.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white;");
		btnClose.setOnAction(event -> popupStage.close());

		vbox.getChildren().addAll(lblKeyWord, lblSubject, lblTalkAbout, lblDescription, btnClose);

		Scene scene = new Scene(vbox, 400, 250);
		popupStage.setScene(scene);
		popupStage.show();
	}

}
