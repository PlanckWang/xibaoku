package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.OrderAudit;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.OrderAuditService;
import com.example.guangxishengkejavafx.service.OrderService;
import com.example.guangxishengkejavafx.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class OrderAuditController {

    @FXML
    private ComboBox<Order> orderComboBox;
    @FXML
    private ComboBox<User> auditorComboBox;
    @FXML
    private DatePicker auditDatePicker;
    @FXML
    private ComboBox<String> auditStatusComboBox;
    @FXML
    private TextArea auditCommentsArea;
    @FXML
    private Button saveAuditButton;
    @FXML
    private Button refreshAuditsButton;
    @FXML
    private Button clearFormButton;

    @FXML
    private TableView<OrderAudit> orderAuditTableView;
    @FXML
    private TableColumn<OrderAudit, Integer> auditIdColumn;
    @FXML
    private TableColumn<OrderAudit, Order> orderNumberAuditColumn; // Will use cell factory
    @FXML
    private TableColumn<OrderAudit, User> auditorColumn; // Will use cell factory
    @FXML
    private TableColumn<OrderAudit, LocalDateTime> auditDateColumn;
    @FXML
    private TableColumn<OrderAudit, String> auditStatusColumn;
    @FXML
    private TableColumn<OrderAudit, String> auditCommentsColumn;

    private final OrderAuditService orderAuditService;
    private final OrderService orderService;
    private final UserService userService;

    private ObservableList<OrderAudit> orderAuditList = FXCollections.observableArrayList();

    @Autowired
    public OrderAuditController(OrderAuditService orderAuditService, OrderService orderService, UserService userService) {
        this.orderAuditService = orderAuditService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadAuditData();

        // Listener to display selected audit details (optional, as audits are generally not edited)
        orderAuditTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Populate form if needed for viewing, but typically audit forms are for new entries
                        // For this example, we'll keep the form for new entries primarily.
                    }
                });
    }

    private void setupTableColumns() {
        auditIdColumn.setCellValueFactory(new PropertyValueFactory<>("auditId"));
        orderNumberAuditColumn.setCellValueFactory(new PropertyValueFactory<>("order"));
        orderNumberAuditColumn.setCellFactory(column -> new TableCell<OrderAudit, Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getOrderNumber());
                }
            }
        });

        auditorColumn.setCellValueFactory(new PropertyValueFactory<>("auditor"));
        auditorColumn.setCellFactory(column -> new TableCell<OrderAudit, User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getUsername()); // Or FullName if available
                }
            }
        });

        auditDateColumn.setCellValueFactory(new PropertyValueFactory<>("auditDate"));
        auditStatusColumn.setCellValueFactory(new PropertyValueFactory<>("auditStatus"));
        auditCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("auditComments"));

        orderAuditTableView.setItems(orderAuditList);
    }

    private void loadComboBoxData() {
        // Load orders - consider filtering for orders that need auditing (e.g., status '待审核')
        orderComboBox.setItems(FXCollections.observableArrayList(orderService.findAllOrders()));
        orderComboBox.setConverter(new javafx.util.StringConverter<Order>() {
            @Override
            public String toString(Order order) {
                return order == null ? null : order.getOrderNumber() + " (" + order.getCustomer().getCustomerName() + ")";
            }
            @Override
            public Order fromString(String string) { return null; }
        });

        // Load users who can be auditors (e.g., based on role)
        // For simplicity, loading all users. In a real app, filter by role.
        auditorComboBox.setItems(FXCollections.observableArrayList(userService.findAllUsers()));
        auditorComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? null : user.getUsername(); // Or FullName
            }
            @Override
            public User fromString(String string) { return null; }
        });

        // Populate audit statuses
        auditStatusComboBox.setItems(FXCollections.observableArrayList("通过", "驳回", "待审核")); // Add more as needed
    }

    private void loadAuditData() {
        List<OrderAudit> audits = orderAuditService.findAllOrderAudits();
        orderAuditList.setAll(audits);
    }

    @FXML
    private void handleSaveAudit() {
        if (!validateInputs()) {
            return;
        }

        OrderAudit newAudit = new OrderAudit();
        newAudit.setOrder(orderComboBox.getValue());
        newAudit.setAuditor(auditorComboBox.getValue());
        if (auditDatePicker.getValue() != null) {
            newAudit.setAuditDate(auditDatePicker.getValue().atStartOfDay());
        }
        newAudit.setAuditStatus(auditStatusComboBox.getValue());
        newAudit.setAuditComments(auditCommentsArea.getText());

        try {
            orderAuditService.saveOrderAudit(newAudit);
            loadAuditData(); // Refresh the list of audits
            // Also refresh the order list if the order status was changed by the audit service
            // This might require a mechanism to notify the OrderController or re-fetch orders.
            // For now, we assume the OrderAuditService handles updating the Order status.
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "审核成功", "订单审核记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "审核失败", "保存订单审核记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefreshAudits() {
        loadAuditData();
        loadComboBoxData(); // Refresh combo boxes as well, in case new orders/users were added
        clearForm();
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    private void clearForm() {
        orderComboBox.setValue(null);
        auditorComboBox.setValue(null);
        auditDatePicker.setValue(null);
        auditStatusComboBox.setValue(null);
        auditCommentsArea.clear();
        orderComboBox.requestFocus();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (orderComboBox.getValue() == null) {
            errors.append("必须选择一个订单进行审核。\n");
        }
        if (auditorComboBox.getValue() == null) {
            errors.append("必须选择审核员。\n");
        }
        if (auditDatePicker.getValue() == null) {
            errors.append("审核日期不能为空。\n");
        }
        if (auditStatusComboBox.getValue() == null) {
            errors.append("必须选择审核状态。\n");
        }
        // Audit comments can be optional depending on requirements

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

