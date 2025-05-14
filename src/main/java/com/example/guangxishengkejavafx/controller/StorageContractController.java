package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.StorageContract;
import com.example.guangxishengkejavafx.service.DepositorService;
import com.example.guangxishengkejavafx.service.OrderService;
import com.example.guangxishengkejavafx.service.StorageContractService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class StorageContractController {

    @FXML
    private TableView<StorageContract> contractTableView;
    @FXML
    private TableColumn<StorageContract, Integer> contractIdColumn;
    @FXML
    private TableColumn<StorageContract, String> contractNumberColumn;
    @FXML
    private TableColumn<StorageContract, Depositor> customerColumn;
    @FXML
    private TableColumn<StorageContract, Order> orderColumn;
    @FXML
    private TableColumn<StorageContract, LocalDate> contractSignDateColumn;
    @FXML
    private TableColumn<StorageContract, LocalDate> contractStartDateColumn;
    @FXML
    private TableColumn<StorageContract, LocalDate> contractEndDateColumn;
    @FXML
    private TableColumn<StorageContract, BigDecimal> storageFeeColumn;
    @FXML
    private TableColumn<StorageContract, String> paymentStatusColumn;
    @FXML
    private TableColumn<StorageContract, String> filePathColumn;
    @FXML
    private TableColumn<StorageContract, LocalDateTime> createdAtColumn;

    @FXML
    private TextField contractNumberField;
    @FXML
    private ComboBox<Depositor> customerComboBox;
    @FXML
    private ComboBox<Order> orderComboBox;
    @FXML
    private DatePicker contractSignDatePicker;
    @FXML
    private DatePicker contractStartDatePicker;
    @FXML
    private DatePicker contractEndDatePicker;
    @FXML
    private TextField storageFeeField;
    @FXML
    private ComboBox<String> paymentStatusComboBox;
    @FXML
    private TextField filePathField;
    @FXML
    private TextArea contractTermsArea;

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

    private final StorageContractService storageContractService;
    private final DepositorService depositorService;
    private final OrderService orderService;

    private ObservableList<StorageContract> contractList = FXCollections.observableArrayList();
    private StorageContract currentContract = null;

    @Autowired
    public StorageContractController(StorageContractService storageContractService, DepositorService depositorService, OrderService orderService) {
        this.storageContractService = storageContractService;
        this.depositorService = depositorService;
        this.orderService = orderService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadContractData();

        contractTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        contractIdColumn.setCellValueFactory(new PropertyValueFactory<>("contractId"));
        contractNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        customerColumn.setCellFactory(column -> new TableCell<StorageContract, Depositor>() {
            @Override
            protected void updateItem(Depositor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCustomerName());
            }
        });
        orderColumn.setCellValueFactory(new PropertyValueFactory<>("order"));
        orderColumn.setCellFactory(column -> new TableCell<StorageContract, Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getOrderNumber());
            }
        });
        contractSignDateColumn.setCellValueFactory(new PropertyValueFactory<>("contractSignDate"));
        contractStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("contractStartDate"));
        contractEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("contractEndDate"));
        storageFeeColumn.setCellValueFactory(new PropertyValueFactory<>("storageFee"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        contractTableView.setItems(contractList);
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

        orderComboBox.setItems(FXCollections.observableArrayList(orderService.findAllOrders()));
        orderComboBox.setConverter(new javafx.util.StringConverter<Order>() {
            @Override
            public String toString(Order order) {
                return order == null ? null : order.getOrderNumber();
            }
            @Override
            public Order fromString(String string) { return null; }
        });
        // Allow null selection for orderComboBox
        orderComboBox.getItems().add(0, null); 

        paymentStatusComboBox.setItems(FXCollections.observableArrayList("未支付", "已支付", "部分支付", "逾期"));
    }

    private void loadContractData() {
        List<StorageContract> currentContracts = storageContractService.findAllStorageContracts();
        contractList.setAll(currentContracts);
    }

    @FXML
    private void handleAddContract() {
        currentContract = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        contractNumberField.requestFocus();
    }

    @FXML
    private void handleEditContract() {
        StorageContract selectedContract = contractTableView.getSelectionModel().getSelectedItem();
        if (selectedContract != null) {
            currentContract = selectedContract;
            populateForm(currentContract);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个合同进行编辑。");
        }
    }

    @FXML
    private void handleDeleteContract() {
        StorageContract selectedContract = contractTableView.getSelectionModel().getSelectedItem();
        if (selectedContract != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除合同: " + selectedContract.getContractNumber() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    storageContractService.deleteStorageContractById(selectedContract.getContractId());
                    loadContractData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "合同已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除合同时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个合同进行删除。");
        }
    }

    @FXML
    private void handleRefreshContracts() {
        loadContractData();
        loadComboBoxData(); // Refresh combo boxes in case of new customers/orders
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveContract() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentContract == null || currentContract.getContractId() == null);
        StorageContract contractToSave = isNew ? new StorageContract() : currentContract;

        contractToSave.setContractNumber(contractNumberField.getText());
        contractToSave.setCustomer(customerComboBox.getValue());
        contractToSave.setOrder(orderComboBox.getValue()); // Can be null
        contractToSave.setContractSignDate(contractSignDatePicker.getValue());
        contractToSave.setContractStartDate(contractStartDatePicker.getValue());
        contractToSave.setContractEndDate(contractEndDatePicker.getValue());
        try {
            if (storageFeeField.getText() != null && !storageFeeField.getText().isEmpty()) {
                contractToSave.setStorageFee(new BigDecimal(storageFeeField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "输入无效", "储存费用必须是有效的数字。");
            return;
        }
        contractToSave.setPaymentStatus(paymentStatusComboBox.getValue());
        contractToSave.setFilePath(filePathField.getText());
        contractToSave.setContractTerms(contractTermsArea.getText());

        try {
            storageContractService.saveStorageContract(contractToSave);
            loadContractData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            contractTableView.getSelectionModel().select(contractToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "合同已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存合同时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelContract() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        contractTableView.getSelectionModel().clearSelection();
        currentContract = null;
    }

    private void populateForm(StorageContract contract) {
        if (contract != null) {
            contractNumberField.setText(contract.getContractNumber());
            customerComboBox.setValue(contract.getCustomer());
            orderComboBox.setValue(contract.getOrder());
            contractSignDatePicker.setValue(contract.getContractSignDate());
            contractStartDatePicker.setValue(contract.getContractStartDate());
            contractEndDatePicker.setValue(contract.getContractEndDate());
            storageFeeField.setText(contract.getStorageFee() != null ? contract.getStorageFee().toPlainString() : "");
            paymentStatusComboBox.setValue(contract.getPaymentStatus());
            filePathField.setText(contract.getFilePath());
            contractTermsArea.setText(contract.getContractTerms());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        contractNumberField.clear();
        customerComboBox.setValue(null);
        orderComboBox.setValue(null);
        contractSignDatePicker.setValue(null);
        contractStartDatePicker.setValue(null);
        contractEndDatePicker.setValue(null);
        storageFeeField.clear();
        paymentStatusComboBox.setValue(null);
        filePathField.clear();
        contractTermsArea.clear();
    }

    private void setFormEditable(boolean editable) {
        contractNumberField.setEditable(editable);
        customerComboBox.setDisable(!editable);
        orderComboBox.setDisable(!editable);
        contractSignDatePicker.setDisable(!editable);
        contractStartDatePicker.setDisable(!editable);
        contractEndDatePicker.setDisable(!editable);
        storageFeeField.setEditable(editable);
        paymentStatusComboBox.setDisable(!editable);
        filePathField.setEditable(editable);
        contractTermsArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (contractNumberField.getText() == null || contractNumberField.getText().trim().isEmpty()) {
            errors.append("合同号不能为空。\n");
        }
        if (customerComboBox.getValue() == null) {
            errors.append("必须选择一个储户。\n");
        }
        // Other fields like dates, fee, status can be optional depending on requirements
        // Add more specific validations as needed
        if (storageFeeField.getText() != null && !storageFeeField.getText().trim().isEmpty()) {
            try {
                new BigDecimal(storageFeeField.getText());
            } catch (NumberFormatException e) {
                errors.append("储存费用必须是有效的数字。\n");
            }
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

