package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditProfileView {

    public static void show(Stage stage, String currentUsername) {

        Label title = new Label("Edit Profile");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();

        Button saveBtn = new Button("Save");
        Button backBtn = new Button("Back");

        loadUserData(currentUsername, usernameField, phoneField, addressField);

        saveBtn.setOnAction(e -> {

            if (usernameField.getText().isEmpty() ||
                phoneField.getText().isEmpty() ||
                addressField.getText().isEmpty()) {

                showError("All fields must be filled");
                return;
            }

            boolean success = updateProfile(
                    currentUsername,
                    usernameField.getText(),
                    phoneField.getText(),
                    addressField.getText()
            );

            if (success) {
                showInfo("Profile updated successfully");
                CustomerHomepage.show(stage, usernameField.getText());
            } else {
                showError("Failed to update profile");
            }
        });

        backBtn.setOnAction(e ->
                CustomerHomepage.show(stage, currentUsername)
        );

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(title, 0, row++, 2, 1);

        grid.add(new Label("Username"), 0, row);
        grid.add(usernameField, 1, row++);

        grid.add(new Label("Phone"), 0, row);
        grid.add(phoneField, 1, row++);

        grid.add(new Label("Address"), 0, row);
        grid.add(addressField, 1, row++);

        grid.add(saveBtn, 0, row);
        grid.add(backBtn, 1, row);

        stage.setScene(new Scene(grid, 420, 300));
        stage.setTitle("Edit Profile");
        stage.show();
    }

    private static void loadUserData(
            String username,
            TextField usernameField,
            TextField phoneField,
            TextField addressField
    ) {

        String sql =
                "SELECT username, phone, address FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                phoneField.setText(rs.getString("phone"));
                addressField.setText(rs.getString("address"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean updateProfile(
            String oldUsername,
            String newUsername,
            String phone,
            String address
    ) {

        String sql =
                "UPDATE users SET username=?, phone=?, address=? WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, oldUsername);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helpers for alert

    private static void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
