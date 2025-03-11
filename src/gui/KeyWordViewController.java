package gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.KeyWord;
import model.dao.KeyWordDAO;

public class KeyWordViewController {
	@FXML
	private TextField campoPalavra;
	@FXML
	private TextArea campoDescricao;
	@FXML
	private TextField campoMateria;
	@FXML
	private TextField campoAssunto;

	private KeyWordDAO palavraChaveDAO = new KeyWordDAO();
	
	private int usuarioId; // ID do usuário logado

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	@FXML
	public void initialize() {
		// Limitar o tamanho do texto
		campoDescricao.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > 1000) { // Limite de 5000 caracteres
				campoDescricao.setText(oldValue);
			}
		});
	}
	@FXML
	public void salvarPalavraChaveAction() {
		String palavra = campoPalavra.getText();
		String descricao = campoDescricao.getText();
		String materia = campoMateria.getText();
		String assunto = campoAssunto.getText();

		if (palavra.isEmpty() || descricao.isEmpty() || materia.isEmpty() || assunto.isEmpty()) {
			System.out.println("Preencha todos os campos!");
			return;
		}

		KeyWord palavraChave = new KeyWord(palavra, descricao, materia, assunto);
		palavraChaveDAO.inserir(palavraChave, usuarioId);

		System.out.println("Palavra-chave salva com sucesso!");
		limparCampos();
	}

	private void limparCampos() {
		campoPalavra.clear();
		campoDescricao.clear();
		campoMateria.clear();
		campoAssunto.clear();
	}
}
