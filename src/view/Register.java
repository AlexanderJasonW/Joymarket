package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.UserDAO;

public class Register {

    public static void show(Stage stage) {

        Label title = new Label("Register");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");
        genderBox.setValue("Male");

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back");

        Label message = new Label();

        registerBtn.setOnAction(e -> {

            if (usernameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() ||
                phoneField.getText().isEmpty() ||
                addressField.getText().isEmpty()) {

                message.setText("All fields must be filled");
                return;
            }

            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                message.setText("Passwords do not match");
                return;
            }

            boolean success = UserDAO.register(
                    usernameField.getText(),
                    passwordField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    genderBox.getValue(),
                    "CUSTOMER"
            );

            message.setText(success ?
                    "Registration successful" :
                    "Registration failed");
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

        grid.add(new Label("Username"), 0, row);
        grid.add(usernameField, 1, row++);

        grid.add(new Label("Email"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Password"), 0, row);
        grid.add(passwordField, 1, row++);

        grid.add(new Label("Confirm Password"), 0, row);
        grid.add(confirmPasswordField, 1, row++);

        grid.add(new Label("Phone"), 0, row);
        grid.add(phoneField, 1, row++);

        grid.add(new Label("Address"), 0, row);
        grid.add(addressField, 1, row++);

        grid.add(new Label("Gender"), 0, row);
        grid.add(genderBox, 1, row++);

        grid.add(registerBtn, 0, row);
        grid.add(backBtn, 1, row++);

        grid.add(message, 0, row, 2, 1);

        stage.setScene(new Scene(grid, 450, 480));
        stage.setTitle("Register");
        stage.show();
    }
}
