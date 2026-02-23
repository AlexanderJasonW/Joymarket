package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.UserDAO;

public class Login {

    public static void show(Stage stage) {

        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button loginBtn = new Button("Login");
        Button backBtn = new Button("Back");

        Label message = new Label();

        loginBtn.setOnAction(e -> {

            LoginResult result = UserDAO.loginByEmail(
                    emailField.getText(),
                    passwordField.getText()
            );

            if (result == null) {
                message.setText("Invalid email or password");
                return;
            }

            switch (result.role) {
                case "CUSTOMER":
                    CustomerHomepage.show(stage, result.username);
                    break;
                case "ADMIN":
                    AdminHomepage.show(stage, result.username);
                    break;
                case "COURIER":
                    CourierHomepage.show(stage, result.username);
                    break;
            }
        });

        backBtn.setOnAction(e ->
                HomepageView.show(stage)
        );

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(title, 0, row++, 2, 1);

        grid.add(new Label("Email"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Password"), 0, row);
        grid.add(passwordField, 1, row++);

        grid.add(loginBtn, 0, row);
        grid.add(backBtn, 1, row++);

        grid.add(message, 0, row, 2, 1);

        stage.setScene(new Scene(grid, 400, 260));
        stage.setTitle("Login");
        stage.show();
    }

    public static class LoginResult {
        public int id;
        public String username;
        public String role;

        public LoginResult(int id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
    }

}
