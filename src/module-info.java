module ProjetoA {
	requires javafx.controls;
	requires java.sql;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.desktop;

	exports gui to javafx.fxml;

	opens gui to javafx.fxml;
	opens application to javafx.graphics, javafx.fxml;

}
