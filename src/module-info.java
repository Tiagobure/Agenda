module ProjetoA {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;

	exports gui to javafx.fxml;

	opens gui to javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;

}
