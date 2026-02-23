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

public class EditProductView {

    public static void show(Stage stage, String adminName) {

        Label title = new Label("Edit Product Stock");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<ProductItem> productBox = new ComboBox<>();
        TextField stockField = new TextField();

        Button updateBtn = new Button("Update Stock");
        Button backBtn = new Button("Back");

        loadProducts(productBox);

        // When product is selected, show current stock
        productBox.setOnAction(e -> {
            ProductItem p = productBox.getValue();
            if (p != null) {
                stockField.setText(String.valueOf(p.stock));
            }
        });

        updateBtn.setOnAction(e -> {
            ProductItem selected = productBox.getValue();

            if (selected == null) {
                showError("Please select a product");
                return;
            }

            if (stockField.getText().isEmpty()) {
                showError("Stock must be filled");
                return;
            }

            int newStock;
            try {
                newStock = Integer.parseInt(stockField.getText());
            } catch (NumberFormatException ex) {
                showError("Stock must be numeric");
                return;
            }

            if (newStock < 0) {
                showError("Stock cannot be negative");
                return;
            }

            boolean success = updateStock(selected.id, newStock);

            if (success) {
                showInfo("Stock updated successfully");
                selected.stock = newStock;
            } else {
                showError("Failed to update stock");
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

        grid.add(new Label("Product"), 0, row);
        grid.add(productBox, 1, row++);

        grid.add(new Label("Stock"), 0, row);
        grid.add(stockField, 1, row++);

        grid.add(updateBtn, 0, row);
        grid.add(backBtn, 1, row);

        stage.setScene(new Scene(grid, 450, 300));
        stage.setTitle("Edit Product");
        stage.show();
    }

    private static void loadProducts(ComboBox<ProductItem> box) {

        String sql = "SELECT id, name, stock FROM products";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                box.getItems().add(
                        new ProductItem(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getInt("stock")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean updateStock(int productId, int stock) {

        String sql = "UPDATE products SET stock = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class ProductItem {
        int id;
        String name;
        int stock;

        ProductItem(int id, String name, int stock) {
            this.id = id;
            this.name = name;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name + " (Stock: " + stock + ")";
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
