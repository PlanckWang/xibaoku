package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.entity.MaterialInboundRecord;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.MaterialInboundRecordService;
import com.example.guangxishengkejavafx.service.MaterialService;
import com.example.guangxishengkejavafx.service.UserService; // To fetch user details for operator
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MaterialInboundRecordController {

    @FXML
    private TableView<InboundRecordWrapper> inboundRecordTableView;
    @FXML
    private TableColumn<InboundRecordWrapper, Integer> recordIdColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> materialNameColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> materialCodeColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, Integer> inboundQuantityColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, BigDecimal> unitPriceColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, BigDecimal> totalAmountColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, LocalDateTime> inboundDateColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> operatorNameColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> supplierNameColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> batchNumberColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, String> notesColumn;
    @FXML
    private TableColumn<InboundRecordWrapper, LocalDateTime> createdAtColumn;

    @FXML
    private ComboBox<Material> materialComboBox;
    @FXML
    private TextField inboundQuantityField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField supplierNameField;
    @FXML
    private TextField batchNumberField;
    @FXML
    private DatePicker inboundDatePicker;
    @FXML
    private ComboBox<User> operatorUserComboBox;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label statusLabel;

    private final MaterialInboundRecordService inboundRecordService;
    private final MaterialService materialService; // For material ComboBox
    private final UserService userService; // For operator ComboBox

    private final ObservableList<InboundRecordWrapper> inboundRecordWrapperList = FXCollections.observableArrayList();
    private final ObservableList<Material> materialObservableList = FXCollections.observableArrayList();
    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @Autowired
    public MaterialInboundRecordController(MaterialInboundRecordService inboundRecordService, 
                                         MaterialService materialService, 
                                         UserService userService) {
        this.inboundRecordService = inboundRecordService;
        this.materialService = materialService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        materialCodeColumn.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        inboundQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("inboundQuantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        inboundDateColumn.setCellValueFactory(new PropertyValueFactory<>("inboundDate"));
        operatorNameColumn.setCellValueFactory(new PropertyValueFactory<>("operatorUserName"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        batchNumberColumn.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        inboundRecordTableView.setItems(inboundRecordWrapperList);
        setupComboBoxes();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        loadInboundRecords();

        // No form population on selection for now, as updates are complex/restricted for inbound records.
        // inboundRecordTableView.getSelectionModel().selectedItemProperty().addListener(
        // (observable, oldValue, newValue) -> populateForm(newValue != null ? newValue.getRecord() : null)
        // );
    }

    private void setupComboBoxes() {
        materialComboBox.setItems(materialObservableList);
        materialComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Material material) {
                return material == null ? null : material.getMaterialName() + " (" + material.getMaterialCode() + ")";
            }
            @Override
            public Material fromString(String string) {
                return materialObservableList.stream().filter(m -> (m.getMaterialName() + " (" + m.getMaterialCode() + ")").equals(string)).findFirst().orElse(null);
            }
        });

        operatorUserComboBox.setItems(userObservableList);
        operatorUserComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user == null ? null : user.getFullName() + " (" + user.getUsername() + ")";
            }
            @Override
            public User fromString(String string) {
                return userObservableList.stream().filter(u -> (u.getFullName() + " (" + u.getUsername() + ")").equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void loadMaterialsForComboBox() {
        try {
            materialObservableList.setAll(materialService.getAllMaterials());
        } catch (Exception e) {
            statusLabel.setText("加载物料列表失败: " + e.getMessage());
        }
    }

    private void loadUsersForComboBox() {
        try {
            // Assuming UserService has a method to get all relevant users (e.g., active users)
            userObservableList.setAll(userService.getAllActiveUsers()); // Or getAllUsers()
        } catch (Exception e) {
            statusLabel.setText("加载用户列表失败: " + e.getMessage());
        }
    }

    private void loadInboundRecords() {
        try {
            List<MaterialInboundRecord> records = inboundRecordService.getAllInboundRecords();
            List<InboundRecordWrapper> wrappers = records.stream()
                .map(r -> new InboundRecordWrapper(r, materialService, userService))
                .collect(Collectors.toList());
            inboundRecordWrapperList.setAll(wrappers);
            statusLabel.setText("入库记录已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载入库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Form population might not be needed if updates are restricted.
    // private void populateForm(MaterialInboundRecord record) { ... }

    private void clearForm() {
        materialComboBox.getSelectionModel().clearSelection();
        inboundQuantityField.clear();
        unitPriceField.clear();
        supplierNameField.clear();
        batchNumberField.clear();
        inboundDatePicker.setValue(null);
        operatorUserComboBox.getSelectionModel().clearSelection();
        notesArea.clear();
        inboundRecordTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddInboundRecord(ActionEvent event) {
        Material selectedMaterial = materialComboBox.getSelectionModel().getSelectedItem();
        if (selectedMaterial == null) {
            statusLabel.setText("请选择物料。");
            return;
        }
        if (inboundQuantityField.getText().isEmpty()) {
            statusLabel.setText("入库数量不能为空。");
            return;
        }

        MaterialInboundRecord newRecord = new MaterialInboundRecord();
        newRecord.setMaterialId(selectedMaterial.getMaterialId());
        try {
            newRecord.setInboundQuantity(Integer.parseInt(inboundQuantityField.getText()));
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                newRecord.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("数量或单价格式无效。");
            return;
        }
        newRecord.setSupplierName(supplierNameField.getText());
        newRecord.setBatchNumber(batchNumberField.getText());
        if (inboundDatePicker.getValue() != null) {
            newRecord.setInboundDate(inboundDatePicker.getValue().atStartOfDay()); // Or specific time
        } else {
            newRecord.setInboundDate(LocalDateTime.now()); // Default to now if not picked
        }
        User selectedOperator = operatorUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedOperator != null) {
            newRecord.setOperatorUserId(selectedOperator.getUserId());
        }
        newRecord.setNotes(notesArea.getText());

        try {
            inboundRecordService.saveInboundRecord(newRecord);
            statusLabel.setText("入库记录添加成功！物料库存已更新。");
            loadInboundRecords();
            loadMaterialsForComboBox(); // Refresh material stock in combobox if displayed
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加入库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleDeleteInboundRecord(ActionEvent event) {
        InboundRecordWrapper selectedWrapper = inboundRecordTableView.getSelectionModel().getSelectedItem();
        if (selectedWrapper == null) {
            statusLabel.setText("请先选择一条记录进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除入库记录 ID: " + selectedWrapper.getRecordId());
        alert.setContentText("您确定要删除此入库记录吗？此操作将尝试回退相应的物料库存，请谨慎操作！");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                inboundRecordService.deleteInboundRecord(selectedWrapper.getRecordId());
                statusLabel.setText("入库记录删除成功！物料库存已调整。");
                loadInboundRecords();
                loadMaterialsForComboBox(); // Refresh material stock
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除入库记录失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshInboundRecords(ActionEvent event) {
        loadInboundRecords();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        clearForm();
        statusLabel.setText("入库记录列表已刷新。");
    }

    // Inner class to wrap InboundRecord and related Material/User details for TableView
    public static class InboundRecordWrapper {
        private final MaterialInboundRecord record;
        private final Material material;
        private final User operatorUser;

        public InboundRecordWrapper(MaterialInboundRecord record, MaterialService materialService, UserService userService) {
            this.record = record;
            this.material = record.getMaterialId() != null ? materialService.getMaterialById(record.getMaterialId()).orElse(null) : null;
            this.operatorUser = record.getOperatorUserId() != null ? userService.getUserById(record.getOperatorUserId()).orElse(null) : null;
        }

        public MaterialInboundRecord getRecord() { return record; }

        public Integer getRecordId() { return record.getRecordId(); }
        public String getMaterialName() { return material != null ? material.getMaterialName() : "N/A"; }
        public String getMaterialCode() { return material != null ? material.getMaterialCode() : "N/A"; }
        public Integer getInboundQuantity() { return record.getInboundQuantity(); }
        public BigDecimal getUnitPrice() { return record.getUnitPrice(); }
        public BigDecimal getTotalAmount() { return record.getTotalAmount(); }
        public LocalDateTime getInboundDate() { return record.getInboundDate(); }
        public String getOperatorUserName() { return operatorUser != null ? operatorUser.getFullName() : (record.getOperatorUserId() != null ? "User:"+record.getOperatorUserId() : "N/A"); }
        public String getSupplierName() { return record.getSupplierName(); }
        public String getBatchNumber() { return record.getBatchNumber(); }
        public String getNotes() { return record.getNotes(); }
        public LocalDateTime getCreatedAt() { return record.getCreatedAt(); }
    }
}

