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

public class CourierHomepage {

    public static void show(Stage stage, String courierName) {

        Label title = new Label("Courier Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label welcome = new Label("Welcome, " + courierName);

        ComboBox<OrderItem> orderBox = new ComboBox<>();
        ComboBox<String> statusBox = new ComboBox<>();

        statusBox.getItems().addAll(
                "Pending",
                "In Progress",
                "Delivered"
        );

        Button updateBtn = new Button("Update Status");

        loadOrders(orderBox, courierName);

        updateBtn.setOnAction(e -> {

            OrderItem order = orderBox.getValue();
            String status = statusBox.getValue();

            if (order == null || status == null) {
                showError("Order and status must be selected");
                return;
            }

            boolean success = updateDeliveryStatus(order.id, status);

            if (success) {
                showInfo("Delivery status updated");
             // Refresh orders
             orderBox.getItems().clear();
             loadOrders(orderBox, courierName);

            } else {
                showError("Failed to update status");
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(title, 0, row++, 2, 1);
        grid.add(welcome, 0, row++, 2, 1);

        grid.add(new Label("Assigned Orders"), 0, row);
        grid.add(orderBox, 1, row++);

        grid.add(new Label("Delivery Status"), 0, row);
        grid.add(statusBox, 1, row++);

        grid.add(updateBtn, 0, row, 2, 1);

        stage.setScene(new Scene(grid, 500, 320));
        stage.setTitle("Courier Homepage");
        stage.show();
    }

    private static void loadOrders(
            ComboBox<OrderItem> box,
            String courierName
    ) {

        String sql = """
            SELECT o.id, o.status
            FROM orders o
            JOIN users u ON o.courier_id = u.id
            WHERE u.username = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courierName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                box.getItems().add(
                        new OrderItem(
                                rs.getInt("id"),
                                rs.getString("status")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean updateDeliveryStatus(int orderId, String status) {

        String sql =
                "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class OrderItem {
        int id;
        String deliveryStatus;

        OrderItem(int id, String deliveryStatus) {
            this.id = id;
            this.deliveryStatus = deliveryStatus;
        }

        @Override
        public String toString() {
            return "Order #" + id + " (" + deliveryStatus + ")";
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
