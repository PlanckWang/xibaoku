package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Material;
import com.example.guangxishengkejavafx.service.MaterialService;
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
public class MaterialController {

    @FXML
    private TableView<Material> materialTableView;
    @FXML
    private TableColumn<Material, Integer> materialIdColumn;
    @FXML
    private TableColumn<Material, String> materialNameColumn;
    @FXML
    private TableColumn<Material, String> materialCodeColumn;
    @FXML
    private TableColumn<Material, String> categoryColumn;
    @FXML
    private TableColumn<Material, String> specificationsColumn;
    @FXML
    private TableColumn<Material, String> unitColumn;
    @FXML
    private TableColumn<Material, BigDecimal> unitPriceColumn;
    @FXML
    private TableColumn<Material, String> supplierColumn;
    @FXML
    private TableColumn<Material, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Material, Integer> warningStockLevelColumn;
    @FXML
    private TableColumn<Material, String> notesColumn;
    @FXML
    private TableColumn<Material, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Material, LocalDateTime> updatedAtColumn;

    @FXML
    private TextField materialNameField;
    @FXML
    private TextField materialCodeField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField specificationsField;
    @FXML
    private TextField unitField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField supplierField;
    @FXML
    private TextField stockQuantityField;
    @FXML
    private TextField warningStockLevelField;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label statusLabel;

    private final MaterialService materialService;
    private final ObservableList<Material> materialList = FXCollections.observableArrayList();

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @FXML
    public void initialize() {
        materialIdColumn.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        materialNameColumn.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        materialCodeColumn.setCellValueFactory(new PropertyValueFactory<>("materialCode"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        specificationsColumn.setCellValueFactory(new PropertyValueFactory<>("specifications"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        warningStockLevelColumn.setCellValueFactory(new PropertyValueFactory<>("warningStockLevel"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        materialTableView.setItems(materialList);
        loadMaterials();

        materialTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );
    }

    private void loadMaterials() {
        try {
            List<Material> materials = materialService.getAllMaterials();
            materialList.setAll(materials);
            statusLabel.setText("物料列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载物料列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Material material) {
        if (material != null) {
            materialNameField.setText(material.getMaterialName());
            materialCodeField.setText(material.getMaterialCode());
            categoryField.setText(material.getCategory());
            specificationsField.setText(material.getSpecifications());
            unitField.setText(material.getUnit());
            unitPriceField.setText(material.getUnitPrice() != null ? material.getUnitPrice().toPlainString() : "");
            supplierField.setText(material.getSupplier());
            stockQuantityField.setText(material.getStockQuantity() != null ? String.valueOf(material.getStockQuantity()) : "");
            warningStockLevelField.setText(material.getWarningStockLevel() != null ? String.valueOf(material.getWarningStockLevel()) : "");
            notesArea.setText(material.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        materialNameField.clear();
        materialCodeField.clear();
        categoryField.clear();
        specificationsField.clear();
        unitField.clear();
        unitPriceField.clear();
        supplierField.clear();
        stockQuantityField.clear();
        warningStockLevelField.clear();
        notesArea.clear();
        materialTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddMaterial(ActionEvent event) {
        String name = materialNameField.getText();
        String code = materialCodeField.getText();
        if (name.isEmpty() || code.isEmpty()) {
            statusLabel.setText("物料名称和物料代码不能为空。");
            return;
        }

        Material newMaterial = new Material();
        newMaterial.setMaterialName(name);
        newMaterial.setMaterialCode(code);
        newMaterial.setCategory(categoryField.getText());
        newMaterial.setSpecifications(specificationsField.getText());
        newMaterial.setUnit(unitField.getText());
        try {
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                newMaterial.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            }
            if (stockQuantityField.getText() != null && !stockQuantityField.getText().isEmpty()) {
                newMaterial.setStockQuantity(Integer.parseInt(stockQuantityField.getText()));
            }
            if (warningStockLevelField.getText() != null && !warningStockLevelField.getText().isEmpty()) {
                newMaterial.setWarningStockLevel(Integer.parseInt(warningStockLevelField.getText()));
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("单价、库存数量或预警库存格式无效。");
            return;
        }
        newMaterial.setSupplier(supplierField.getText());
        newMaterial.setNotes(notesArea.getText());

        try {
            materialService.saveMaterial(newMaterial);
            statusLabel.setText("物料添加成功！");
            loadMaterials();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加物料失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateMaterial(ActionEvent event) {
        Material selectedMaterial = materialTableView.getSelectionModel().getSelectedItem();
        if (selectedMaterial == null) {
            statusLabel.setText("请先选择一个物料进行修改。");
            return;
        }

        String name = materialNameField.getText();
        String code = materialCodeField.getText();
        if (name.isEmpty() || code.isEmpty()) {
            statusLabel.setText("物料名称和物料代码不能为空。");
            return;
        }

        Material updatedDetails = new Material();
        updatedDetails.setMaterialName(name);
        updatedDetails.setMaterialCode(code);
        updatedDetails.setCategory(categoryField.getText());
        updatedDetails.setSpecifications(specificationsField.getText());
        updatedDetails.setUnit(unitField.getText());
        try {
            if (unitPriceField.getText() != null && !unitPriceField.getText().isEmpty()) {
                updatedDetails.setUnitPrice(new BigDecimal(unitPriceField.getText()));
            } else {
                updatedDetails.setUnitPrice(null);
            }
            if (stockQuantityField.getText() != null && !stockQuantityField.getText().isEmpty()) {
                updatedDetails.setStockQuantity(Integer.parseInt(stockQuantityField.getText()));
            } else {
                updatedDetails.setStockQuantity(null);
            }
            if (warningStockLevelField.getText() != null && !warningStockLevelField.getText().isEmpty()) {
                updatedDetails.setWarningStockLevel(Integer.parseInt(warningStockLevelField.getText()));
            } else {
                updatedDetails.setWarningStockLevel(null);
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("单价、库存数量或预警库存格式无效。");
            return;
        }
        updatedDetails.setSupplier(supplierField.getText());
        updatedDetails.setNotes(notesArea.getText());

        try {
            materialService.updateMaterial(selectedMaterial.getMaterialId(), updatedDetails);
            statusLabel.setText("物料更新成功！");
            loadMaterials();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新物料失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteMaterial(ActionEvent event) {
        Material selectedMaterial = materialTableView.getSelectionModel().getSelectedItem();
        if (selectedMaterial == null) {
            statusLabel.setText("请先选择一个物料进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除物料: " + selectedMaterial.getMaterialName());
        alert.setContentText("您确定要删除这个物料吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                materialService.deleteMaterial(selectedMaterial.getMaterialId());
                statusLabel.setText("物料删除成功！");
                loadMaterials();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除物料失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshMaterials(ActionEvent event) {
        loadMaterials();
        clearForm();
        statusLabel.setText("物料列表已刷新。");
    }
}

