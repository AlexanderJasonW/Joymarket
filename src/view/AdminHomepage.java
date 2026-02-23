package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminHomepage {

    public static void show(Stage stage, String adminName) {

        Label welcomeLabel = new Label("Welcome, administrator " + adminName);
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button editProductBtn = new Button("Edit Product");
        Button assignCourierBtn = new Button("Assign Courier");

        // Navigation
        editProductBtn.setOnAction(e ->
        	EditProductView.show(stage, adminName)
        );

        assignCourierBtn.setOnAction(e ->
        	AssignCourierView.show(stage, adminName)
        );

        VBox root = new VBox(20,
                welcomeLabel,
                editProductBtn,
                assignCourierBtn
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 500, 300));
        stage.setTitle("Admin Homepage");
        stage.show();
    }

    // Helpers for alert

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
