package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.CellProductionPlan;
import com.example.guangxishengkejavafx.service.CellProductionPlanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CellProductionPlanController {

    @Autowired
    private CellProductionPlanService cellProductionPlanService;

    @FXML
    private TextField searchPlanNameField;
    @FXML
    private TableView<CellProductionPlan> cellProductionPlanTable;
    @FXML
    private TableColumn<CellProductionPlan, Integer> planIdColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> planNameColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> targetProductColumn;
    @FXML
    private TableColumn<CellProductionPlan, Integer> requiredCellBankIdColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> plannedQuantityColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> startDateColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> endDateColumn;
    @FXML
    private TableColumn<CellProductionPlan, String> statusColumn;
    @FXML
    private TableColumn<CellProductionPlan, Integer> creatorIdColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField planIdField;
    @FXML
    private TextField planNameField;
    @FXML
    private TextField targetProductField;
    @FXML
    private TextField requiredCellBankIdField;
    @FXML
    private TextField plannedQuantityField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField creatorIdField;

    private ObservableList<CellProductionPlan> cellProductionPlanData = FXCollections.observableArrayList();
    private CellProductionPlan currentPlan;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        planIdColumn.setCellValueFactory(new PropertyValueFactory<>("planId"));
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("planName"));
        targetProductColumn.setCellValueFactory(new PropertyValueFactory<>("targetProduct"));
        requiredCellBankIdColumn.setCellValueFactory(new PropertyValueFactory<>("requiredCellBankId"));
        plannedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("plannedQuantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        creatorIdColumn.setCellValueFactory(new PropertyValueFactory<>("creatorId"));

        startDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getStartDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });
        endDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getEndDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
        });

        statusComboBox.setItems(FXCollections.observableArrayList("计划中", "进行中", "已完成", "已取消"));

        loadPlans();
        cellProductionPlanTable.setItems(cellProductionPlanData);
        hideForm();
    }

    private void loadPlans() {
        cellProductionPlanData.setAll(cellProductionPlanService.findAll());
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchPlanNameField.getText();
        if (searchTerm.isEmpty()) {
            loadPlans();
        } else {
            List<CellProductionPlan> filteredList = cellProductionPlanService.findByPlanNameContaining(searchTerm);
            cellProductionPlanData.setAll(filteredList);
        }
        cellProductionPlanTable.setItems(cellProductionPlanData);
    }

    @FXML
    private void handleRefresh() {
        searchPlanNameField.clear();
        loadPlans();
        hideForm();
    }

    @FXML
    private void handleAddPlan() {
        currentPlan = null;
        clearForm();
        formTitleLabel.setText("新增细胞生产计划");
        showForm();
    }

    @FXML
    private void handleEditPlan() {
        CellProductionPlan selectedPlan = cellProductionPlanTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            currentPlan = selectedPlan;
            populateForm(selectedPlan);
            formTitleLabel.setText("编辑细胞生产计划");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的生产计划。");
        }
    }

    @FXML
    private void handleDeletePlan() {
        CellProductionPlan selectedPlan = cellProductionPlanTable.getSelectionModel().getSelectedItem();
        if (selectedPlan != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的生产计划吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cellProductionPlanService.deleteById(selectedPlan.getPlanId());
                        loadPlans();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "生产计划已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除生产计划: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的生产计划。");
        }
    }

    @FXML
    private void handleSavePlan() {
        if (!validateForm()) {
            return;
        }

        CellProductionPlan planToSave = (currentPlan == null) ? new CellProductionPlan() : currentPlan;

        try {
            planToSave.setPlanName(planNameField.getText());
            planToSave.setTargetProduct(targetProductField.getText());
            if (!requiredCellBankIdField.getText().isEmpty()) planToSave.setRequiredCellBankId(Integer.parseInt(requiredCellBankIdField.getText()));
            else planToSave.setRequiredCellBankId(null);
            planToSave.setPlannedQuantity(plannedQuantityField.getText());
            planToSave.setStartDate(startDatePicker.getValue());
            planToSave.setEndDate(endDatePicker.getValue());
            planToSave.setStatus(statusComboBox.getValue());
            planToSave.setCreatorId(Integer.parseInt(creatorIdField.getText()));

            cellProductionPlanService.save(planToSave);
            loadPlans();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "生产计划信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "数据格式错误，请检查细胞库ID和创建人ID。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存生产计划: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        hideForm();
    }

    private void populateForm(CellProductionPlan plan) {
        planIdField.setText(plan.getPlanId() != null ? plan.getPlanId().toString() : "");
        planNameField.setText(plan.getPlanName());
        targetProductField.setText(plan.getTargetProduct());
        requiredCellBankIdField.setText(plan.getRequiredCellBankId() != null ? plan.getRequiredCellBankId().toString() : "");
        plannedQuantityField.setText(plan.getPlannedQuantity());
        startDatePicker.setValue(plan.getStartDate());
        endDatePicker.setValue(plan.getEndDate());
        statusComboBox.setValue(plan.getStatus());
        creatorIdField.setText(plan.getCreatorId() != null ? plan.getCreatorId().toString() : "");
    }

    private void clearForm() {
        planIdField.clear();
        planNameField.clear();
        targetProductField.clear();
        requiredCellBankIdField.clear();
        plannedQuantityField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        statusComboBox.getSelectionModel().clearSelection();
        statusComboBox.setValue(null);
        creatorIdField.clear();
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentPlan = null;
    }

    private boolean validateForm() {
        if (planNameField.getText().isEmpty() || targetProductField.getText().isEmpty() || 
            plannedQuantityField.getText().isEmpty() || startDatePicker.getValue() == null || 
            statusComboBox.getValue() == null || creatorIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "计划名称、目标产品、计划数量、开始日期、状态和创建人ID不能为空。");
            return false;
        }
        try {
            if (!requiredCellBankIdField.getText().isEmpty()) Integer.parseInt(requiredCellBankIdField.getText());
            Integer.parseInt(creatorIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID和创建人ID必须是数字。");
            return false;
        }
        if (endDatePicker.getValue() != null && startDatePicker.getValue() != null && endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "结束日期不能早于开始日期。");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

