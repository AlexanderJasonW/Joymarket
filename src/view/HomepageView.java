package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomepageView {

    public static void show(Stage stage) {

        Label title = new Label("Welcome to JoyMarket");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        loginBtn.setOnAction(e ->
                Login.show(stage)
        );

        registerBtn.setOnAction(e ->
        		Register.show(stage)
        );

        VBox root = new VBox(20,
                title,
                loginBtn,
                registerBtn
        );

        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Homepage");
        stage.show();
    }

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
