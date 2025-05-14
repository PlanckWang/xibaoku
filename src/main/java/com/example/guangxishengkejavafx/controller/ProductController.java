package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ProductController {

    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, String> productCodeColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, BigDecimal> unitPriceColumn;
    @FXML
    private TableColumn<Product, String> unitColumn;
    @FXML
    private TableColumn<Product, String> specificationsColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Product, LocalDateTime> updatedAtColumn;

    @FXML
    private TextField productNameField;
    @FXML
    private TextField productCodeField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField unitField;
    @FXML
    private TextField specificationsField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label statusLabel;

    private final ProductService productService;
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @FXML
    public void initialize() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCodeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        specificationsColumn.setCellValueFactory(new PropertyValueFactory<>("specifications"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        productTableView.setItems(productList);
        loadProducts();

        productTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );
    }

    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productList.setAll(products);
            statusLabel.setText("产品列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载产品列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Product product) {
        if (product != null) {
            productNameField.setText(product.getProductName());
            productCodeField.setText(product.getProductCode());
            categoryField.setText(product.getCategory());
            unitPriceField.setText(product.getUnitPrice() != null ? product.getUnitPrice().toPlainString() : "");
            unitField.setText(product.getUnit());
            specificationsField.setText(product.getSpecifications());
            descriptionArea.setText(product.getDescription());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        productNameField.clear();
        productCodeField.clear();
        categoryField.clear();
        unitPriceField.clear();
        unitField.clear();
        specificationsField.clear();
        descriptionArea.clear();
        productTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddProduct(ActionEvent event) {
        String name = productNameField.getText();
        if (name.isEmpty()) {
            statusLabel.setText("产品名称不能为空。");
            return;
        }

        Product newProduct = new Product();
        newProduct.setProductName(name);
        newProduct.setProductCode(productCodeField.getText());
        newProduct.setCategory(categoryField.getText());
        try {
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                newProduct.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("单价格式无效。");
            return;
        }
        newProduct.setUnit(unitField.getText());
        newProduct.setSpecifications(specificationsField.getText());
        newProduct.setDescription(descriptionArea.getText());

        try {
            productService.saveProduct(newProduct);
            statusLabel.setText("产品添加成功！");
            loadProducts();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加产品失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            statusLabel.setText("请先选择一个产品进行修改。");
            return;
        }

        String name = productNameField.getText();
        if (name.isEmpty()) {
            statusLabel.setText("产品名称不能为空。");
            return;
        }

        Product updatedDetails = new Product();
        updatedDetails.setProductName(name);
        updatedDetails.setProductCode(productCodeField.getText());
        updatedDetails.setCategory(categoryField.getText());
        try {
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                updatedDetails.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            } else {
                updatedDetails.setUnitPrice(null); // Allow clearing the price
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("单价格式无效。");
            return;
        }
        updatedDetails.setUnit(unitField.getText());
        updatedDetails.setSpecifications(specificationsField.getText());
        updatedDetails.setDescription(descriptionArea.getText());

        try {
            productService.updateProduct(selectedProduct.getProductId(), updatedDetails);
            statusLabel.setText("产品更新成功！");
            loadProducts();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新产品失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteProduct(ActionEvent event) {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            statusLabel.setText("请先选择一个产品进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除产品: " + selectedProduct.getProductName());
        alert.setContentText("您确定要删除这个产品吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productService.deleteProduct(selectedProduct.getProductId());
                statusLabel.setText("产品删除成功！");
                loadProducts();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除产品失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshProducts(ActionEvent event) {
        loadProducts();
        clearForm();
        statusLabel.setText("产品列表已刷新。");
    }
}

