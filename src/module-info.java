module ProjetoA {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;

	exports gui to javafx.fxml;

	opens gui to javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;

}
