package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.DBConnection;

import java.sql.*;

public class AddProductView {

    public static void show(Stage stage, String username) {

        Label title = new Label("Add Product to Cart");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<ProductItem> productBox = new ComboBox<>();
        Button addBtn = new Button("Add to Cart");
        Button backBtn = new Button("Back");

        loadProducts(productBox);

        addBtn.setOnAction(e -> {
            ProductItem selected = productBox.getValue();
            if (selected == null) {
                showError("Please select a product");
                return;
            }

            addToCart(username, selected);
        });

        backBtn.setOnAction(e ->
                CustomerHomepage.show(stage, username)
        );

        VBox root = new VBox(15,
                title,
                productBox,
                addBtn,
                backBtn
        );

        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 450, 300));
        stage.setTitle("Add Product");
        stage.show();
    }

    // ================= LOAD PRODUCTS =================

    private static void loadProducts(ComboBox<ProductItem> box) {

        String sql = "SELECT id, name, price FROM products WHERE stock > 0";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                box.getItems().add(
                        new ProductItem(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("price")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CART LOGIC =================

    private static void addToCart(String username, ProductItem product) {

        try (Connection con = DBConnection.getConnection()) {

            int userId = getUserId(con, username);
            int orderId = getOrCreateCart(con, userId);

            // Insert order detail
            String detailSql = """
                INSERT INTO order_details
                (order_id, product_id, quantity, subtotal)
                VALUES (?, ?, 1, ?)
            """;

            try (PreparedStatement ps = con.prepareStatement(detailSql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, product.id);
                ps.setDouble(3, product.price);
                ps.executeUpdate();
            }

            // Update order total
            String updateTotal =
                    "UPDATE orders SET total_amount = total_amount + ? WHERE id = ?";

            try (PreparedStatement ps = con.prepareStatement(updateTotal)) {
                ps.setDouble(1, product.price);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            showInfo("Product added to cart");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to add product");
        }
    }

    private static int getUserId(Connection con, String username) throws Exception {

        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        throw new Exception("User not found");
    }

    private static int getOrCreateCart(Connection con, int userId) throws Exception {

        String checkSql =
                "SELECT id FROM orders WHERE user_id = ? AND status = 'CART'";

        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }

        String createSql =
                "INSERT INTO orders(user_id, total_amount, status) VALUES (?, 0, 'CART')";

        try (PreparedStatement ps =
                     con.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }

        throw new Exception("Failed to create cart");
    }

    private static class ProductItem {
        int id;
        String name;
        double price;

        ProductItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " - " + price;
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
