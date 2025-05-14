package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.*;
import com.example.guangxishengkejavafx.service.*;
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
public class MaterialOutboundRecordController {

    @FXML
    private TableView<OutboundRecordWrapper> outboundRecordTableView;
    @FXML
    private TableColumn<OutboundRecordWrapper, Integer> recordIdColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> materialNameColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> materialCodeColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, Integer> outboundQuantityColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, BigDecimal> unitPriceColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, BigDecimal> totalAmountColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, LocalDateTime> outboundDateColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> recipientDepartmentNameColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> recipientPersonnelNameColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> purposeColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> operatorNameColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, Integer> relatedProjectIdColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, String> notesColumn;
    @FXML
    private TableColumn<OutboundRecordWrapper, LocalDateTime> createdAtColumn;

    @FXML
    private ComboBox<Material> materialComboBox;
    @FXML
    private TextField outboundQuantityField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private ComboBox<Department> recipientDepartmentComboBox;
    @FXML
    private ComboBox<Personnel> recipientPersonnelComboBox;
    @FXML
    private DatePicker outboundDatePicker;
    @FXML
    private TextField purposeField;
    @FXML
    private ComboBox<User> operatorUserComboBox;
    @FXML
    private TextField relatedProjectIdField;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label statusLabel;

    private final MaterialOutboundRecordService outboundRecordService;
    private final MaterialService materialService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final PersonnelService personnelService;

    private final ObservableList<OutboundRecordWrapper> outboundRecordWrapperList = FXCollections.observableArrayList();
    private final ObservableList<Material> materialObservableList = FXCollections.observableArrayList();
    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private final ObservableList<Department> departmentObservableList = FXCollections.observableArrayList();
    private final ObservableList<Personnel> personnelObservableList = FXCollections.observableArrayList();

    @Autowired
    public MaterialOutboundRecordController(MaterialOutboundRecordService outboundRecordService,
                                          MaterialService materialService,
                                          UserService userService,
                                          DepartmentService departmentService,
                                          PersonnelService personnelService) {
        this.outboundRecordService = outboundRecordService;
        this.materialService = materialService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        materialCodeColumn.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        outboundQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("outboundQuantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        outboundDateColumn.setCellValueFactory(new PropertyValueFactory<>("outboundDate"));
        recipientDepartmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("recipientDepartmentName"));
        recipientPersonnelNameColumn.setCellValueFactory(new PropertyValueFactory<>("recipientPersonnelName"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        operatorNameColumn.setCellValueFactory(new PropertyValueFactory<>("operatorUserName"));
        relatedProjectIdColumn.setCellValueFactory(new PropertyValueFactory<>("relatedProjectId"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        outboundRecordTableView.setItems(outboundRecordWrapperList);
        setupComboBoxes();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        loadDepartmentsForComboBox();
        loadPersonnelForComboBox();
        loadOutboundRecords();
    }

    private void setupComboBoxes() {
        materialComboBox.setItems(materialObservableList);
        materialComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Material m) { return m == null ? null : m.getMaterialName() + " (" + m.getMaterialCode() + ")"; }
            @Override public Material fromString(String s) { return materialObservableList.stream().filter(m -> (m.getMaterialName() + " (" + m.getMaterialCode() + ")").equals(s)).findFirst().orElse(null); }
        });

        operatorUserComboBox.setItems(userObservableList);
        operatorUserComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(User u) { return u == null ? null : u.getFullName() + " (" + u.getUsername() + ")"; }
            @Override public User fromString(String s) { return userObservableList.stream().filter(u -> (u.getFullName() + " (" + u.getUsername() + ")").equals(s)).findFirst().orElse(null); }
        });

        recipientDepartmentComboBox.setItems(departmentObservableList);
        recipientDepartmentComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Department d) { return d == null ? null : d.getDepartmentName(); }
            @Override public Department fromString(String s) { return departmentObservableList.stream().filter(d -> d.getDepartmentName().equals(s)).findFirst().orElse(null); }
        });

        recipientPersonnelComboBox.setItems(personnelObservableList);
        recipientPersonnelComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(Personnel p) { return p == null ? null : p.getName(); } // Assuming Personnel has getName()
            @Override public Personnel fromString(String s) { return personnelObservableList.stream().filter(p -> p.getName().equals(s)).findFirst().orElse(null); }
        });
    }

    private void loadMaterialsForComboBox() {
        try { materialObservableList.setAll(materialService.getAllMaterials()); } catch (Exception e) { statusLabel.setText("加载物料失败: " + e.getMessage()); }
    }

    private void loadUsersForComboBox() {
        try { userObservableList.setAll(userService.getAllActiveUsers()); } catch (Exception e) { statusLabel.setText("加载用户失败: " + e.getMessage()); }
    }

    private void loadDepartmentsForComboBox() {
        try { departmentObservableList.setAll(departmentService.getAllDepartments()); } catch (Exception e) { statusLabel.setText("加载部门失败: " + e.getMessage()); }
    }

    private void loadPersonnelForComboBox() {
        try { personnelObservableList.setAll(personnelService.getAllPersonnel()); } catch (Exception e) { statusLabel.setText("加载人员失败: " + e.getMessage()); }
    }

    private void loadOutboundRecords() {
        try {
            List<MaterialOutboundRecord> records = outboundRecordService.getAllOutboundRecords();
            List<OutboundRecordWrapper> wrappers = records.stream()
                .map(r -> new OutboundRecordWrapper(r, materialService, userService, departmentService, personnelService))
                .collect(Collectors.toList());
            outboundRecordWrapperList.setAll(wrappers);
            statusLabel.setText("出库记录已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载出库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        materialComboBox.getSelectionModel().clearSelection();
        outboundQuantityField.clear();
        unitPriceField.clear();
        recipientDepartmentComboBox.getSelectionModel().clearSelection();
        recipientPersonnelComboBox.getSelectionModel().clearSelection();
        outboundDatePicker.setValue(null);
        purposeField.clear();
        operatorUserComboBox.getSelectionModel().clearSelection();
        relatedProjectIdField.clear();
        notesArea.clear();
        outboundRecordTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddOutboundRecord(ActionEvent event) {
        Material selectedMaterial = materialComboBox.getSelectionModel().getSelectedItem();
        if (selectedMaterial == null) { statusLabel.setText("请选择物料。"); return; }
        if (outboundQuantityField.getText().isEmpty()) { statusLabel.setText("出库数量不能为空。"); return; }

        MaterialOutboundRecord newRecord = new MaterialOutboundRecord();
        newRecord.setMaterialId(selectedMaterial.getMaterialId());
        try {
            newRecord.setOutboundQuantity(Integer.parseInt(outboundQuantityField.getText()));
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                newRecord.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            } else {
                newRecord.setUnitPrice(selectedMaterial.getUnitPrice()); // Default to material's current price
            }
            if (relatedProjectIdField.getText() != null && !relatedProjectIdField.getText().isEmpty()) {
                newRecord.setRelatedProjectId(Integer.parseInt(relatedProjectIdField.getText()));
            }
        } catch (NumberFormatException e) { statusLabel.setText("数量、单价或项目ID格式无效。"); return; }
        
        Department selectedDept = recipientDepartmentComboBox.getSelectionModel().getSelectedItem();
        if (selectedDept != null) newRecord.setRecipientDepartmentId(selectedDept.getDepartmentId());
        
        Personnel selectedPersonnel = recipientPersonnelComboBox.getSelectionModel().getSelectedItem();
        if (selectedPersonnel != null) newRecord.setRecipientPersonnelId(selectedPersonnel.getPersonnelId());

        if (outboundDatePicker.getValue() != null) {
            newRecord.setOutboundDate(outboundDatePicker.getValue().atStartOfDay());
        } else {
            newRecord.setOutboundDate(LocalDateTime.now());
        }
        newRecord.setPurpose(purposeField.getText());
        User selectedOperator = operatorUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedOperator != null) newRecord.setOperatorUserId(selectedOperator.getUserId());
        newRecord.setNotes(notesArea.getText());

        try {
            outboundRecordService.saveOutboundRecord(newRecord);
            statusLabel.setText("出库记录添加成功！物料库存已更新。");
            loadOutboundRecords();
            loadMaterialsForComboBox(); // Refresh stock
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加出库记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteOutboundRecord(ActionEvent event) {
        OutboundRecordWrapper selectedWrapper = outboundRecordTableView.getSelectionModel().getSelectedItem();
        if (selectedWrapper == null) { statusLabel.setText("请先选择一条记录进行删除。"); return; }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除出库记录 ID: " + selectedWrapper.getRecordId());
        alert.setContentText("您确定要删除此出库记录吗？此操作将尝试回增相应的物料库存，请谨慎操作！");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                outboundRecordService.deleteOutboundRecord(selectedWrapper.getRecordId());
                statusLabel.setText("出库记录删除成功！物料库存已调整。");
                loadOutboundRecords();
                loadMaterialsForComboBox(); // Refresh stock
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除出库记录失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshOutboundRecords(ActionEvent event) {
        loadOutboundRecords();
        loadMaterialsForComboBox();
        loadUsersForComboBox();
        loadDepartmentsForComboBox();
        loadPersonnelForComboBox();
        clearForm();
        statusLabel.setText("出库记录列表已刷新。");
    }

    public static class OutboundRecordWrapper {
        private final MaterialOutboundRecord record;
        private final Material material;
        private final User operatorUser;
        private final Department recipientDepartment;
        private final Personnel recipientPersonnel;

        public OutboundRecordWrapper(MaterialOutboundRecord record, MaterialService materialService, 
                                     UserService userService, DepartmentService departmentService, 
                                     PersonnelService personnelService) {
            this.record = record;
            this.material = record.getMaterialId() != null ? materialService.getMaterialById(record.getMaterialId()).orElse(null) : null;
            this.operatorUser = record.getOperatorUserId() != null ? userService.getUserById(record.getOperatorUserId()).orElse(null) : null;
            this.recipientDepartment = record.getRecipientDepartmentId() != null ? departmentService.getDepartmentById(record.getRecipientDepartmentId()).orElse(null) : null;
            this.recipientPersonnel = record.getRecipientPersonnelId() != null ? personnelService.getPersonnelById(record.getRecipientPersonnelId()).orElse(null) : null;
        }

        public MaterialOutboundRecord getRecord() { return record; }
        public Integer getRecordId() { return record.getRecordId(); }
        public String getMaterialName() { return material != null ? material.getMaterialName() : "N/A"; }
        public String getMaterialCode() { return material != null ? material.getMaterialCode() : "N/A"; }
        public Integer getOutboundQuantity() { return record.getOutboundQuantity(); }
        public BigDecimal getUnitPrice() { return record.getUnitPrice(); }
        public BigDecimal getTotalAmount() { return record.getTotalAmount(); }
        public LocalDateTime getOutboundDate() { return record.getOutboundDate(); }
        public String getRecipientDepartmentName() { return recipientDepartment != null ? recipientDepartment.getDepartmentName() : "N/A"; }
        public String getRecipientPersonnelName() { return recipientPersonnel != null ? recipientPersonnel.getName() : "N/A"; }
        public String getPurpose() { return record.getPurpose(); }
        public String getOperatorUserName() { return operatorUser != null ? operatorUser.getFullName() : (record.getOperatorUserId() != null ? "User:"+record.getOperatorUserId() : "N/A"); }
        public Integer getRelatedProjectId() { return record.getRelatedProjectId(); }
        public String getNotes() { return record.getNotes(); }
        public LocalDateTime getCreatedAt() { return record.getCreatedAt(); }
    }
}

