package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.model.entity.MaterialReturnRecord;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.MaterialReturnRecordService;
import com.example.guangxishengkejavafx.service.MaterialService;
import com.example.guangxishengkejavafx.service.UserService;
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
public class MaterialReturnRecordController {

    @FXML
    private TableView<ReturnRecordWrapper> returnRecordTableView;
    @FXML
    private TableColumn<ReturnRecordWrapper, Integer> recordIdColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, String> materialNameColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, String> materialCodeColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, Integer> returnQuantityColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, BigDecimal> unitPriceColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, BigDecimal> totalAmountColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, LocalDateTime> returnDateColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, String> reasonColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, String> operatorNameColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, Integer> relatedInboundRecordIdColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, String> notesColumn;
    @FXML
    private TableColumn<ReturnRecordWrapper, LocalDateTime> createdAtColumn;

    @FXML
    private ComboBox<Material> materialComboBox;
    @FXML
    private TextField returnQuantityField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField reasonField;
    @FXML
    private TextField relatedInboundRecordIdField;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private ComboBox<User> operatorUserComboBox;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label statusLabel;

    private final MaterialReturnRecordService returnRecordService;
    private final MaterialService materialService;
    private final UserService userService;

    private final ObservableList<ReturnRecordWrapper> returnRecordWrapperList = FXCollections.observableArrayList();
    private final ObservableList<Material> materialObservableList = FXCollections.observableArrayList();
    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @Autowired
    public MaterialReturnRecordController(MaterialReturnRecordService returnRecordService,
                                        MaterialService materialService,
                                        UserService userService) {
        this.returnRecordService = returnRecordService;
        this.materialService = materialService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        materialCodeColumn.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        returnQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("returnQuantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        operatorNameColumn.setCellValueFactory(new PropertyValueFactory<>("operatorUserName"));
        relatedInboundRecordIdColumn.setCellValueFactory(new PropertyValueFactory<>("relatedInboundRecordId"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        returnRecordTableView.setItems(returnRecordWrapperList);
        setupComboBoxes();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        loadReturnRecords();
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
            userObservableList.setAll(userService.getAllActiveUsers());
        } catch (Exception e) {
            statusLabel.setText("加载用户列表失败: " + e.getMessage());
        }
    }

    private void loadReturnRecords() {
        try {
            List<MaterialReturnRecord> records = returnRecordService.getAllReturnRecords();
            List<ReturnRecordWrapper> wrappers = records.stream()
                .map(r -> new ReturnRecordWrapper(r, materialService, userService))
                .collect(Collectors.toList());
            returnRecordWrapperList.setAll(wrappers);
            statusLabel.setText("退库记录已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载退库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        materialComboBox.getSelectionModel().clearSelection();
        returnQuantityField.clear();
        unitPriceField.clear();
        reasonField.clear();
        relatedInboundRecordIdField.clear();
        returnDatePicker.setValue(null);
        operatorUserComboBox.getSelectionModel().clearSelection();
        notesArea.clear();
        returnRecordTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddReturnRecord(ActionEvent event) {
        Material selectedMaterial = materialComboBox.getSelectionModel().getSelectedItem();
        if (selectedMaterial == null) {
            statusLabel.setText("请选择物料。");
            return;
        }
        if (returnQuantityField.getText().isEmpty()) {
            statusLabel.setText("退库数量不能为空。");
            return;
        }

        MaterialReturnRecord newRecord = new MaterialReturnRecord();
        newRecord.setMaterialId(selectedMaterial.getMaterialId());
        try {
            newRecord.setReturnQuantity(Integer.parseInt(returnQuantityField.getText()));
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                newRecord.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            } else {
                // Optionally fetch current material price if not provided
                newRecord.setUnitPrice(selectedMaterial.getUnitPrice()); 
            }
            if (relatedInboundRecordIdField.getText() != null && !relatedInboundRecordIdField.getText().isEmpty()) {
                newRecord.setRelatedInboundRecordId(Integer.parseInt(relatedInboundRecordIdField.getText()));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("数量、单价或关联入库ID格式无效。");
            return;
        }
        newRecord.setReason(reasonField.getText());
        if (returnDatePicker.getValue() != null) {
            newRecord.setReturnDate(returnDatePicker.getValue().atStartOfDay());
        } else {
            newRecord.setReturnDate(LocalDateTime.now());
        }
        User selectedOperator = operatorUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedOperator != null) {
            newRecord.setOperatorUserId(selectedOperator.getUserId());
        }
        newRecord.setNotes(notesArea.getText());

        try {
            returnRecordService.saveReturnRecord(newRecord);
            statusLabel.setText("退库记录添加成功！物料库存已更新。");
            loadReturnRecords();
            loadMaterialsForComboBox(); // Refresh material stock
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加退库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteReturnRecord(ActionEvent event) {
        ReturnRecordWrapper selectedWrapper = returnRecordTableView.getSelectionModel().getSelectedItem();
        if (selectedWrapper == null) {
            statusLabel.setText("请先选择一条记录进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除退库记录 ID: " + selectedWrapper.getRecordId());
        alert.setContentText("您确定要删除此退库记录吗？此操作将尝试回增相应的物料库存，请谨慎操作！");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                returnRecordService.deleteReturnRecord(selectedWrapper.getRecordId());
                statusLabel.setText("退库记录删除成功！物料库存已调整。");
                loadReturnRecords();
                loadMaterialsForComboBox(); // Refresh material stock
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除退库记录失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshReturnRecords(ActionEvent event) {
        loadReturnRecords();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        clearForm();
        statusLabel.setText("退库记录列表已刷新。");
    }

    public static class ReturnRecordWrapper {
        private final MaterialReturnRecord record;
        private final Material material;
        private final User operatorUser;

        public ReturnRecordWrapper(MaterialReturnRecord record, MaterialService materialService, UserService userService) {
            this.record = record;
            this.material = record.getMaterialId() != null ? materialService.getMaterialById(record.getMaterialId()).orElse(null) : null;
            this.operatorUser = record.getOperatorUserId() != null ? userService.getUserById(record.getOperatorUserId()).orElse(null) : null;
        }

        public MaterialReturnRecord getRecord() { return record; }

        public Integer getRecordId() { return record.getRecordId(); }
        public String getMaterialName() { return material != null ? material.getMaterialName() : "N/A"; }
        public String getMaterialCode() { return material != null ? material.getMaterialCode() : "N/A"; }
        public Integer getReturnQuantity() { return record.getReturnQuantity(); }
        public BigDecimal getUnitPrice() { return record.getUnitPrice(); }
        public BigDecimal getTotalAmount() { return record.getTotalAmount(); }
        public LocalDateTime getReturnDate() { return record.getReturnDate(); }
        public String getReason() { return record.getReason(); }
        public String getOperatorUserName() { return operatorUser != null ? operatorUser.getFullName() : (record.getOperatorUserId() != null ? "User:"+record.getOperatorUserId() : "N/A"); }
        public Integer getRelatedInboundRecordId() { return record.getRelatedInboundRecordId(); }
        public String getNotes() { return record.getNotes(); }
        public LocalDateTime getCreatedAt() { return record.getCreatedAt(); }
    }
}

