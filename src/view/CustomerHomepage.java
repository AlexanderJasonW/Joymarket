package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerHomepage {

    public static void show(Stage stage, String username) {

        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button topUpBtn = new Button("Top Up Balance");
        Button addProductBtn = new Button("Add Product to Cart");
        Button editProfileBtn = new Button("Edit Profile");
        Button checkoutBtn = new Button("Checkout");

        // Navigation
        topUpBtn.setOnAction(e ->
        	TopUpView.show(stage, username)
        );

        addProductBtn.setOnAction(e ->
        	AddProductView.show(stage, username)
        );

        editProfileBtn.setOnAction(e ->
        	EditProfileView.show(stage, username)
        );

        checkoutBtn.setOnAction(e ->
                handleCheckout(username)
        );

        VBox root = new VBox(15,
                welcomeLabel,
                topUpBtn,
                addProductBtn,
                editProfileBtn,
                checkoutBtn
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Customer Homepage");
        stage.show();
    }

    private static void handleCheckout(String username) {

        String userSql =
                "SELECT id, balance FROM users WHERE username = ?";

        String orderSql =
                "SELECT id, total_amount FROM orders WHERE user_id = ? AND status = 'CART'";

        String deductBalanceSql =
                "UPDATE users SET balance = balance - ? WHERE id = ?";

        String updateOrderSql =
                "UPDATE orders SET status = 'PAID' WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {

            // ===== Start Transaction =====
            con.setAutoCommit(false);

            int userId;
            double balance;

            // Get user
            try (PreparedStatement ps = con.prepareStatement(userSql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    showError("User not found");
                    con.rollback();
                    return;
                }

                userId = rs.getInt("id");
                balance = rs.getDouble("balance");
            }

            int orderId;
            double orderTotal;

            // Get cart
            try (PreparedStatement ps = con.prepareStatement(orderSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    showError("No active cart found");
                    con.rollback();
                    return;
                }

                orderId = rs.getInt("id");
                orderTotal = rs.getDouble("total_amount");
            }

            // Check balance
            if (balance < orderTotal) {
                showError("Insufficient balance\nBalance: " + balance +
                        "\nOrder Total: " + orderTotal);
                con.rollback();
                return;
            }

            // Balance deduction
            try (PreparedStatement ps = con.prepareStatement(deductBalanceSql)) {
                ps.setDouble(1, orderTotal);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Updating order status
            try (PreparedStatement ps = con.prepareStatement(updateOrderSql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            // Commit transact
            con.commit();

            showInfo("Checkout successful!\nTotal Paid: " + orderTotal);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Checkout failed");

            try {
                DBConnection.getConnection().rollback();
            } catch (Exception ignored) {}
        }
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
