package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.DepositorService;
import com.example.guangxishengkejavafx.service.OrderService;
import com.example.guangxishengkejavafx.service.ProductService;
import com.example.guangxishengkejavafx.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    @FXML
    private TableView<Order> orderTableView;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, String> orderNumberColumn;
    @FXML
    private TableColumn<Order, Depositor> customerColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<Order, Product> productColumn;
    @FXML
    private TableColumn<Order, String> sampleTypeColumn;
    @FXML
    private TableColumn<Order, Integer> quantityColumn;
    @FXML
    private TableColumn<Order, BigDecimal> totalAmountColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, User> salespersonColumn;
    @FXML
    private TableColumn<Order, String> notesColumn;

    @FXML
    private TextField orderNumberField;
    @FXML
    private ComboBox<Depositor> customerComboBox;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField sampleTypeField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField totalAmountField;
    @FXML
    private ComboBox<String> orderStatusComboBox;
    @FXML
    private ComboBox<User> salespersonComboBox;
    @FXML
    private TextArea notesArea;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final OrderService orderService;
    private final DepositorService depositorService;
    private final ProductService productService;
    private final UserService userService;

    private ObservableList<Order> orderList = FXCollections.observableArrayList();
    private Order currentOrder = null;

    @Autowired
    public OrderController(OrderService orderService, DepositorService depositorService, ProductService productService, UserService userService) {
        this.orderService = orderService;
        this.depositorService = depositorService;
        this.productService = productService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadOrderData();

        orderTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        customerColumn.setCellFactory(column -> new TableCell<Order, Depositor>() {
            @Override
            protected void updateItem(Depositor item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getCustomerName());
                }
            }
        });
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        productColumn.setCellFactory(column -> new TableCell<Order, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getProductName());
                }
            }
        });
        sampleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sampleType"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        salespersonColumn.setCellValueFactory(new PropertyValueFactory<>("salesperson"));
        salespersonColumn.setCellFactory(column -> new TableCell<Order, User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getUsername()); // Or FullName if available and preferred
                }
            }
        });
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        orderTableView.setItems(orderList);
    }

    private void loadComboBoxData() {
        customerComboBox.setItems(FXCollections.observableArrayList(depositorService.findAllDepositors()));
        customerComboBox.setConverter(new javafx.util.StringConverter<Depositor>() {
            @Override
            public String toString(Depositor depositor) {
                return depositor == null ? null : depositor.getCustomerName();
            }
            @Override
            public Depositor fromString(String string) { return null; }
        });

        productComboBox.setItems(FXCollections.observableArrayList(productService.findAllProducts()));
        productComboBox.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getProductName();
            }
            @Override
            public Product fromString(String string) { return null; }
        });

        // Example order statuses - adjust as per your application's needs
        orderStatusComboBox.setItems(FXCollections.observableArrayList("待审核", "已审核", "进行中", "已完成", "已取消"));

        salespersonComboBox.setItems(FXCollections.observableArrayList(userService.findAllUsers())); // Assuming userService has findAllUsers()
        salespersonComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? null : user.getUsername(); // Or FullName
            }
            @Override
            public User fromString(String string) { return null; }
        });
    }

    private void loadOrderData() {
        List<Order> currentOrders = orderService.findAllOrders();
        orderList.setAll(currentOrders);
    }

    @FXML
    private void handleAddOrder() {
        currentOrder = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        orderNumberField.requestFocus();
    }

    @FXML
    private void handleEditOrder() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            currentOrder = selectedOrder;
            populateForm(currentOrder);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个订单进行编辑。");
        }
    }

    @FXML
    private void handleDeleteOrder() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除订单: " + selectedOrder.getOrderNumber() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    orderService.deleteOrderById(selectedOrder.getOrderId());
                    loadOrderData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "订单已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除订单时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个订单进行删除。");
        }
    }

    @FXML
    private void handleRefreshOrders() {
        loadOrderData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveOrder() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentOrder == null || currentOrder.getOrderId() == null);
        Order orderToSave = isNew ? new Order() : currentOrder;

        orderToSave.setOrderNumber(orderNumberField.getText());
        orderToSave.setCustomer(customerComboBox.getValue());
        if (orderDatePicker.getValue() != null) {
            orderToSave.setOrderDate(orderDatePicker.getValue().atStartOfDay());
        }
        orderToSave.setProduct(productComboBox.getValue());
        orderToSave.setSampleType(sampleTypeField.getText());
        try {
            orderToSave.setQuantity(Integer.parseInt(quantityField.getText()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "输入无效", "数量必须是有效的整数。");
            return;
        }
        try {
            if (totalAmountField.getText() != null && !totalAmountField.getText().isEmpty()) {
                orderToSave.setTotalAmount(new BigDecimal(totalAmountField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "输入无效", "总金额必须是有效的数字。");
            return;
        }
        orderToSave.setOrderStatus(orderStatusComboBox.getValue());
        orderToSave.setSalesperson(salespersonComboBox.getValue());
        orderToSave.setNotes(notesArea.getText());

        try {
            orderService.saveOrder(orderToSave);
            loadOrderData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            orderTableView.getSelectionModel().select(orderToSave); // Select the saved item
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "订单已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存订单时发生错误: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelOrder() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        orderTableView.getSelectionModel().clearSelection();
        currentOrder = null;
    }

    private void populateForm(Order order) {
        if (order != null) {
            orderNumberField.setText(order.getOrderNumber());
            customerComboBox.setValue(order.getCustomer());
            orderDatePicker.setValue(order.getOrderDate() != null ? order.getOrderDate().toLocalDate() : null);
            productComboBox.setValue(order.getProduct());
            sampleTypeField.setText(order.getSampleType());
            quantityField.setText(order.getQuantity() != null ? String.valueOf(order.getQuantity()) : "");
            totalAmountField.setText(order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "");
            orderStatusComboBox.setValue(order.getOrderStatus());
            salespersonComboBox.setValue(order.getSalesperson());
            notesArea.setText(order.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        orderNumberField.clear();
        customerComboBox.setValue(null);
        orderDatePicker.setValue(null);
        productComboBox.setValue(null);
        sampleTypeField.clear();
        quantityField.clear();
        totalAmountField.clear();
        orderStatusComboBox.setValue(null);
        salespersonComboBox.setValue(null);
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        orderNumberField.setEditable(editable);
        customerComboBox.setDisable(!editable);
        orderDatePicker.setDisable(!editable);
        productComboBox.setDisable(!editable);
        sampleTypeField.setEditable(editable);
        quantityField.setEditable(editable);
        totalAmountField.setEditable(editable);
        orderStatusComboBox.setDisable(!editable);
        salespersonComboBox.setDisable(!editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (orderNumberField.getText() == null || orderNumberField.getText().trim().isEmpty()) {
            errors.append("订单号不能为空。\n");
        }
        if (customerComboBox.getValue() == null) {
            errors.append("必须选择一个储户。\n");
        }
        if (orderDatePicker.getValue() == null) {
            errors.append("下单日期不能为空。\n");
        }
        if (quantityField.getText() != null && !quantityField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                errors.append("数量必须是有效的整数。\n");
            }
        } else {
             errors.append("数量不能为空。\n");
        }

        if (totalAmountField.getText() != null && !totalAmountField.getText().trim().isEmpty()) {
            try {
                new BigDecimal(totalAmountField.getText());
            } catch (NumberFormatException e) {
                errors.append("总金额必须是有效的数字。\n");
            }
        }
        if (orderStatusComboBox.getValue() == null) {
            errors.append("必须选择订单状态。\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "输入验证错误", errors.toString());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

