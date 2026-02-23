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

public class AssignCourierView {

    public static void show(Stage stage, String adminName) {

        Label title = new Label("Assign Courier to Order");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<OrderItem> orderBox = new ComboBox<>();
        ComboBox<CourierItem> courierBox = new ComboBox<>();

        Button assignBtn = new Button("Assign Courier");
        Button backBtn = new Button("Back");

        loadOrders(orderBox);
        loadCouriers(courierBox);

        assignBtn.setOnAction(e -> {

            OrderItem order = orderBox.getValue();
            CourierItem courier = courierBox.getValue();

            if (order == null || courier == null) {
                showError("Order and Courier must be selected");
                return;
            }

            boolean success = assignCourier(order.id, courier.id);

            if (success) {
                showInfo("Courier assigned successfully");
                orderBox.getItems().remove(order);
            } else {
                showError("Failed to assign courier");
            }
        });

        backBtn.setOnAction(e ->
                AdminHomepage.show(stage, adminName)
        );

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(title, 0, row++, 2, 1);

        grid.add(new Label("Order"), 0, row);
        grid.add(orderBox, 1, row++);

        grid.add(new Label("Courier"), 0, row);
        grid.add(courierBox, 1, row++);

        grid.add(assignBtn, 0, row);
        grid.add(backBtn, 1, row);

        stage.setScene(new Scene(grid, 500, 300));
        stage.setTitle("Assign Courier");
        stage.show();
    }

    private static void loadOrders(ComboBox<OrderItem> box) {

        String sql = """
            SELECT id, total_amount
            FROM orders
            WHERE status = 'PAID' AND courier_id IS NULL
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                box.getItems().add(
                        new OrderItem(
                                rs.getInt("id"),
                                rs.getDouble("total_amount")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadCouriers(ComboBox<CourierItem> box) {

        String sql = "SELECT id, username FROM users WHERE role = 'COURIER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                box.getItems().add(
                        new CourierItem(
                                rs.getInt("id"),
                                rs.getString("username")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean assignCourier(int orderId, int courierId) {

        String sql =
                "UPDATE orders SET courier_id = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courierId);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class OrderItem {
        int id;
        double total;

        OrderItem(int id, double total) {
            this.id = id;
            this.total = total;
        }

        @Override
        public String toString() {
            return "Order #" + id + " - Total: " + total;
        }
    }

    private static class CourierItem {
        int id;
        String username;

        CourierItem(int id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public String toString() {
            return username;
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
