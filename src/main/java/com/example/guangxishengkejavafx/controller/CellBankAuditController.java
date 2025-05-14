package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.CellBankAudit;
import com.example.guangxishengkejavafx.service.CellBankAuditService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CellBankAuditController {

    @Autowired
    private CellBankAuditService cellBankAuditService;

    @FXML
    private TextField searchCellBankIdField;
    @FXML
    private TableView<CellBankAudit> cellBankAuditTable;
    @FXML
    private TableColumn<CellBankAudit, Integer> auditIdColumn;
    @FXML
    private TableColumn<CellBankAudit, Integer> cellBankIdColumn;
    @FXML
    private TableColumn<CellBankAudit, Integer> auditorIdColumn;
    @FXML
    private TableColumn<CellBankAudit, String> auditDateColumn;
    @FXML
    private TableColumn<CellBankAudit, String> auditStatusColumn;
    @FXML
    private TableColumn<CellBankAudit, String> auditCommentsColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField auditIdField;
    @FXML
    private TextField formCellBankIdField; // Renamed to avoid conflict with TableColumn
    @FXML
    private TextField auditorIdField; // For form input
    @FXML
    private TextField formAuditDateField; // Renamed to avoid conflict
    @FXML
    private ComboBox<String> auditStatusComboBox;
    @FXML
    private TextArea auditCommentsArea;

    private ObservableList<CellBankAudit> cellBankAuditData = FXCollections.observableArrayList();
    private CellBankAudit currentAudit;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        auditIdColumn.setCellValueFactory(new PropertyValueFactory<>("auditId"));
        cellBankIdColumn.setCellValueFactory(new PropertyValueFactory<>("cellBankId"));
        auditorIdColumn.setCellValueFactory(new PropertyValueFactory<>("auditorId"));
        auditStatusColumn.setCellValueFactory(new PropertyValueFactory<>("auditStatus"));
        auditCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("auditComments"));

        auditDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getAuditDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateTimeFormatter) : "");
        });

        auditStatusComboBox.setItems(FXCollections.observableArrayList("待审核", "通过", "驳回"));

        loadAudits();
        cellBankAuditTable.setItems(cellBankAuditData);
        hideForm();
    }

    private void loadAudits() {
        cellBankAuditData.setAll(cellBankAuditService.findAll());
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchCellBankIdField.getText();
        if (searchTerm.isEmpty()) {
            loadAudits();
        } else {
            try {
                Integer searchId = Integer.parseInt(searchTerm);
                List<CellBankAudit> filteredList = cellBankAuditService.findByCellBankId(searchId);
                cellBankAuditData.setAll(filteredList);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "输入错误", "请输入有效的细胞库ID进行搜索。");
                cellBankAuditData.clear();
            }
        }
        cellBankAuditTable.setItems(cellBankAuditData);
    }

    @FXML
    private void handleRefresh() {
        searchCellBankIdField.clear();
        loadAudits();
        hideForm();
    }

    @FXML
    private void handleAddAudit() {
        currentAudit = null;
        clearForm();
        formTitleLabel.setText("新增细胞入库审核");
        showForm();
    }

    @FXML
    private void handleEditAudit() {
        CellBankAudit selectedAudit = cellBankAuditTable.getSelectionModel().getSelectedItem();
        if (selectedAudit != null) {
            currentAudit = selectedAudit;
            populateForm(selectedAudit);
            formTitleLabel.setText("编辑细胞入库审核");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的审核记录。");
        }
    }

    @FXML
    private void handleDeleteAudit() {
        CellBankAudit selectedAudit = cellBankAuditTable.getSelectionModel().getSelectedItem();
        if (selectedAudit != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的审核记录吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cellBankAuditService.deleteById(selectedAudit.getAuditId());
                        loadAudits();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "审核记录已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除审核记录: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的审核记录。");
        }
    }

    @FXML
    private void handleSaveAudit() {
        if (!validateForm()) {
            return;
        }

        CellBankAudit auditToSave = (currentAudit == null) ? new CellBankAudit() : currentAudit;

        try {
            auditToSave.setCellBankId(Integer.parseInt(formCellBankIdField.getText()));
            auditToSave.setAuditorId(Integer.parseInt(auditorIdField.getText())); // Assuming auditor ID is manually entered or selected
            auditToSave.setAuditStatus(auditStatusComboBox.getValue());
            auditToSave.setAuditComments(auditCommentsArea.getText());
            // AuditDate is set in service layer

            cellBankAuditService.save(auditToSave);
            loadAudits();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "审核信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "数据格式错误，请检查细胞库ID和审核员ID。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存审核信息: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        hideForm();
    }

    private void populateForm(CellBankAudit audit) {
        auditIdField.setText(audit.getAuditId() != null ? audit.getAuditId().toString() : "");
        formCellBankIdField.setText(audit.getCellBankId() != null ? audit.getCellBankId().toString() : "");
        auditorIdField.setText(audit.getAuditorId() != null ? audit.getAuditorId().toString() : "");
        formAuditDateField.setText(audit.getAuditDate() != null ? audit.getAuditDate().format(dateTimeFormatter) : "");
        auditStatusComboBox.setValue(audit.getAuditStatus());
        auditCommentsArea.setText(audit.getAuditComments());
    }

    private void clearForm() {
        auditIdField.clear();
        formCellBankIdField.clear();
        auditorIdField.clear();
        formAuditDateField.clear();
        auditStatusComboBox.getSelectionModel().clearSelection();
        auditStatusComboBox.setValue(null); // Clear selection
        auditCommentsArea.clear();
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentAudit = null;
    }

    private boolean validateForm() {
        if (formCellBankIdField.getText().isEmpty() || auditorIdField.getText().isEmpty() || auditStatusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID、审核员ID和审核状态不能为空。");
            return false;
        }
        try {
            Integer.parseInt(formCellBankIdField.getText());
            Integer.parseInt(auditorIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID和审核员ID必须是数字。");
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

