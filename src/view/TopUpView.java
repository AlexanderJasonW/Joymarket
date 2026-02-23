package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TopUpView {

    public static void show(Stage stage, String username) {

        Label title = new Label("Top Up Balance");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount (min 10000)");

        Button confirmBtn = new Button("Confirm Top Up");
        Button backBtn = new Button("Back");

        confirmBtn.setOnAction(e -> {
            String input = amountField.getText();

            // Validation
            if (input == null || input.trim().isEmpty()) {
                showError("Amount must be filled");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                showError("Amount must be numeric");
                return;
            }

            if (amount < 10000) {
                showError("Minimum top up is 10000");
                return;
            }

            String sql = "UPDATE users SET balance = balance + ? WHERE username = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setDouble(1, amount);
                ps.setString(2, username);
                ps.executeUpdate();

                showInfo("Top up successful!\nAmount: " + amount);
                CustomerHomepage.show(stage, username);

            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Top up failed");
            }
        });

        backBtn.setOnAction(e ->
                CustomerHomepage.show(stage, username)
        );

        VBox root = new VBox(15,
                title,
                amountField,
                confirmBtn,
                backBtn
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 250));
        stage.setTitle("Top Up Balance");
        stage.show();
    }

    // Helpers for alert

    private static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
