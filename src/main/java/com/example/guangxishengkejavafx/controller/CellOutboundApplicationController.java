package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.CellOutboundApplication;
import com.example.guangxishengkejavafx.service.CellOutboundApplicationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CellOutboundApplicationController {

    @Autowired
    private CellOutboundApplicationService cellOutboundApplicationService;

    @FXML
    private TextField searchCellBankIdField;
    @FXML
    private TextField searchProductionPlanIdField;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    @FXML
    private TableView<CellOutboundApplication> cellOutboundApplicationTable;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> applicationIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> productionPlanIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> cellBankIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> applicantIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> applicationDateColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> reasonColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> quantityColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> destinationColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> statusColumn;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> approverIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> approvalDateColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> approvalCommentsColumn;
    @FXML
    private TableColumn<CellOutboundApplication, Integer> outboundHandlerIdColumn;
    @FXML
    private TableColumn<CellOutboundApplication, String> outboundDateColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField applicationIdField;
    @FXML
    private TextField formProductionPlanIdField;
    @FXML
    private TextField formCellBankIdField;
    @FXML
    private TextField formApplicantIdField;
    @FXML
    private TextField formApplicationDateField;
    @FXML
    private TextArea formReasonArea;
    @FXML
    private TextField formQuantityField;
    @FXML
    private TextField formDestinationField;
    @FXML
    private ComboBox<String> formStatusComboBox;
    @FXML
    private VBox approvalFieldsContainer;
    @FXML
    private TextField formApproverIdField;
    @FXML
    private TextField formApprovalDateField;
    @FXML
    private TextArea formApprovalCommentsArea;
    @FXML
    private VBox outboundFieldsContainer;
    @FXML
    private TextField formOutboundHandlerIdField;
    @FXML
    private TextField formOutboundDateField;
    @FXML
    private Button saveButton;
    @FXML
    private Button submitApprovalButton;
    @FXML
    private Button submitOutboundButton;

    private ObservableList<CellOutboundApplication> applicationData = FXCollections.observableArrayList();
    private CellOutboundApplication currentApplication;
    private enum FormMode { ADD, EDIT, APPROVE, OUTBOUND }
    private FormMode currentFormMode;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> allStatusOptions = FXCollections.observableArrayList("待审批", "已批准", "已驳回", "已出库");
    private final ObservableList<String> searchStatusOptions = FXCollections.observableArrayList("全部", "待审批", "已批准", "已驳回", "已出库");

    @FXML
    public void initialize() {
        setupTableColumns();
        formStatusComboBox.setItems(allStatusOptions);
        searchStatusComboBox.setItems(searchStatusOptions);
        searchStatusComboBox.setValue("全部");
        loadApplications();
        cellOutboundApplicationTable.setItems(applicationData);
        hideForm();
    }

    private void setupTableColumns() {
        applicationIdColumn.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        productionPlanIdColumn.setCellValueFactory(new PropertyValueFactory<>("productionPlanId"));
        cellBankIdColumn.setCellValueFactory(new PropertyValueFactory<>("cellBankId"));
        applicantIdColumn.setCellValueFactory(new PropertyValueFactory<>("applicantId"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        approverIdColumn.setCellValueFactory(new PropertyValueFactory<>("approverId"));
        approvalCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("approvalComments"));
        outboundHandlerIdColumn.setCellValueFactory(new PropertyValueFactory<>("outboundHandlerId"));

        applicationDateColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getApplicationDate()));
        approvalDateColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getApprovalDate()));
        outboundDateColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getOutboundDate()));
    }

    private javafx.beans.property.SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new javafx.beans.property.SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadApplications() {
        applicationData.setAll(cellOutboundApplicationService.findAll());
    }

    @FXML
    private void handleSearch() {
        String cellBankIdSearch = searchCellBankIdField.getText().trim();
        String productionPlanIdSearch = searchProductionPlanIdField.getText().trim();
        String statusSearch = searchStatusComboBox.getValue();

        List<CellOutboundApplication> filteredList = cellOutboundApplicationService.findAll().stream()
            .filter(app -> {
                boolean matchesCellBank = cellBankIdSearch.isEmpty() || (app.getCellBankId() != null && app.getCellBankId().toString().contains(cellBankIdSearch));
                boolean matchesProdPlan = productionPlanIdSearch.isEmpty() || (app.getProductionPlanId() != null && app.getProductionPlanId().toString().contains(productionPlanIdSearch));
                boolean matchesStatus = "全部".equals(statusSearch) || (app.getStatus() != null && app.getStatus().equals(statusSearch));
                return matchesCellBank && matchesProdPlan && matchesStatus;
            })
            .collect(Collectors.toList());
        applicationData.setAll(filteredList);
    }

    @FXML
    private void handleRefresh() {
        searchCellBankIdField.clear();
        searchProductionPlanIdField.clear();
        searchStatusComboBox.setValue("全部");
        loadApplications();
        hideForm();
    }

    @FXML
    private void handleAddApplication() {
        currentApplication = null;
        currentFormMode = FormMode.ADD;
        clearForm();
        formTitleLabel.setText("新增出库申请");
        configureForm();
        showForm();
    }

    @FXML
    private void handleEditApplication() {
        CellOutboundApplication selected = cellOutboundApplicationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!"待审批".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "编辑受限", "只有状态为“待审批”的申请才能编辑。");
                return;
            }
            currentApplication = selected;
            currentFormMode = FormMode.EDIT;
            populateForm(selected);
            formTitleLabel.setText("编辑出库申请");
            configureForm();
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的申请。");
        }
    }

    @FXML
    private void handleApproveApplication() {
        CellOutboundApplication selected = cellOutboundApplicationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!"待审批".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "审批受限", "只有状态为“待审批”的申请才能审批。");
                return;
            }
            currentApplication = selected;
            currentFormMode = FormMode.APPROVE;
            populateForm(selected);
            formTitleLabel.setText("审批出库申请");
            configureForm();
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要审批的申请。");
        }
    }

    @FXML
    private void handleProcessOutbound() {
        CellOutboundApplication selected = cellOutboundApplicationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!"已批准".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "出库受限", "只有状态为“已批准”的申请才能处理出库。");
                return;
            }
            currentApplication = selected;
            currentFormMode = FormMode.OUTBOUND;
            populateForm(selected);
            formTitleLabel.setText("处理细胞出库");
            configureForm();
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要处理出库的申请。");
        }
    }

    @FXML
    private void handleDeleteApplication() {
        CellOutboundApplication selected = cellOutboundApplicationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的申请吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cellOutboundApplicationService.deleteById(selected.getApplicationId());
                        loadApplications();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "申请已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除申请: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的申请。");
        }
    }

    @FXML
    private void handleSaveApplication() { // For ADD and EDIT modes
        if (!validateForm()) return;
        CellOutboundApplication appToSave = (currentApplication == null) ? new CellOutboundApplication() : currentApplication;
        try {
            appToSave.setProductionPlanId(formProductionPlanIdField.getText().isEmpty() ? null : Integer.parseInt(formProductionPlanIdField.getText()));
            appToSave.setCellBankId(Integer.parseInt(formCellBankIdField.getText()));
            appToSave.setApplicantId(Integer.parseInt(formApplicantIdField.getText()));
            appToSave.setReason(formReasonArea.getText());
            appToSave.setQuantity(formQuantityField.getText());
            appToSave.setDestination(formDestinationField.getText());
            if (currentFormMode == FormMode.ADD) {
                appToSave.setApplicationDate(LocalDateTime.now());
                appToSave.setStatus("待审批");
            }
            // Status for EDIT is not changed here, it's fixed to "待审批"

            cellOutboundApplicationService.save(appToSave);
            loadApplications();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "申请信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "生产计划ID, 细胞库ID, 申请人ID必须是有效的数字。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存申请: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitApproval() { // For APPROVE mode
        if (!validateForm()) return;
        if (currentApplication == null) return;
        try {
            currentApplication.setApproverId(Integer.parseInt(formApproverIdField.getText()));
            currentApplication.setApprovalComments(formApprovalCommentsArea.getText());
            currentApplication.setStatus(formStatusComboBox.getValue()); // "已批准" or "已驳回"
            // ApprovalDate is set in service

            cellOutboundApplicationService.save(currentApplication);
            loadApplications();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "审批成功", "审批信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "审批人ID必须是有效的数字。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "审批失败", "无法保存审批信息: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitOutbound() { // For OUTBOUND mode
        if (!validateForm()) return;
        if (currentApplication == null) return;
        try {
            currentApplication.setOutboundHandlerId(Integer.parseInt(formOutboundHandlerIdField.getText()));
            currentApplication.setStatus("已出库");
            // OutboundDate is set in service

            cellOutboundApplicationService.save(currentApplication);
            loadApplications();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "出库成功", "出库信息已记录。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "出库处理人ID必须是有效的数字。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "出库失败", "无法记录出库信息: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(CellOutboundApplication app) {
        applicationIdField.setText(app.getApplicationId() != null ? app.getApplicationId().toString() : "");
        formProductionPlanIdField.setText(app.getProductionPlanId() != null ? app.getProductionPlanId().toString() : "");
        formCellBankIdField.setText(app.getCellBankId() != null ? app.getCellBankId().toString() : "");
        formApplicantIdField.setText(app.getApplicantId() != null ? app.getApplicantId().toString() : "");
        formApplicationDateField.setText(app.getApplicationDate() != null ? app.getApplicationDate().format(dateTimeFormatter) : "");
        formReasonArea.setText(app.getReason());
        formQuantityField.setText(app.getQuantity());
        formDestinationField.setText(app.getDestination());
        formStatusComboBox.setValue(app.getStatus());
        formApproverIdField.setText(app.getApproverId() != null ? app.getApproverId().toString() : "");
        formApprovalDateField.setText(app.getApprovalDate() != null ? app.getApprovalDate().format(dateTimeFormatter) : "");
        formApprovalCommentsArea.setText(app.getApprovalComments());
        formOutboundHandlerIdField.setText(app.getOutboundHandlerId() != null ? app.getOutboundHandlerId().toString() : "");
        formOutboundDateField.setText(app.getOutboundDate() != null ? app.getOutboundDate().format(dateTimeFormatter) : "");
    }

    private void clearForm() {
        applicationIdField.clear();
        formProductionPlanIdField.clear();
        formCellBankIdField.clear();
        formApplicantIdField.clear();
        formApplicationDateField.clear();
        formReasonArea.clear();
        formQuantityField.clear();
        formDestinationField.clear();
        formStatusComboBox.getSelectionModel().clearSelection();
        formApproverIdField.clear();
        formApprovalDateField.clear();
        formApprovalCommentsArea.clear();
        formOutboundHandlerIdField.clear();
        formOutboundDateField.clear();
    }

    private void configureForm() {
        // Common visibility
        saveButton.setVisible(currentFormMode == FormMode.ADD || currentFormMode == FormMode.EDIT);
        saveButton.setManaged(currentFormMode == FormMode.ADD || currentFormMode == FormMode.EDIT);
        submitApprovalButton.setVisible(currentFormMode == FormMode.APPROVE);
        submitApprovalButton.setManaged(currentFormMode == FormMode.APPROVE);
        submitOutboundButton.setVisible(currentFormMode == FormMode.OUTBOUND);
        submitOutboundButton.setManaged(currentFormMode == FormMode.OUTBOUND);

        approvalFieldsContainer.setVisible(currentFormMode == FormMode.APPROVE || currentFormMode == FormMode.OUTBOUND || (currentApplication != null && currentApplication.getApproverId() != null) );
        approvalFieldsContainer.setManaged(approvalFieldsContainer.isVisible());
        outboundFieldsContainer.setVisible(currentFormMode == FormMode.OUTBOUND || (currentApplication != null && currentApplication.getOutboundHandlerId() != null));
        outboundFieldsContainer.setManaged(outboundFieldsContainer.isVisible());

        // Field editability
        boolean isAddOrEdit = currentFormMode == FormMode.ADD || currentFormMode == FormMode.EDIT;
        formProductionPlanIdField.setDisable(!isAddOrEdit);
        formCellBankIdField.setDisable(!isAddOrEdit);
        formApplicantIdField.setDisable(!isAddOrEdit);
        formReasonArea.setDisable(!isAddOrEdit);
        formQuantityField.setDisable(!isAddOrEdit);
        formDestinationField.setDisable(!isAddOrEdit);
        
        formStatusComboBox.setDisable(true); // Generally disabled, set specifically if needed
        if (currentFormMode == FormMode.ADD) {
            formStatusComboBox.setValue("待审批");
        } else if (currentFormMode == FormMode.EDIT) {
            formStatusComboBox.setValue(currentApplication.getStatus()); // Should be "待审批"
        } else if (currentFormMode == FormMode.APPROVE) {
            formStatusComboBox.setDisable(false);
            formStatusComboBox.setItems(FXCollections.observableArrayList("已批准", "已驳回"));
            formStatusComboBox.setValue(currentApplication.getStatus()); // Pre-select current status if any, or let user choose
        } else if (currentFormMode == FormMode.OUTBOUND) {
            formStatusComboBox.setValue("已出库");
        }

        formApproverIdField.setDisable(currentFormMode != FormMode.APPROVE);
        formApprovalCommentsArea.setDisable(currentFormMode != FormMode.APPROVE);
        formOutboundHandlerIdField.setDisable(currentFormMode != FormMode.OUTBOUND);
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentApplication = null;
        formStatusComboBox.setItems(allStatusOptions); // Reset to all options
    }

    private boolean validateForm() {
        String prodPlanId = formProductionPlanIdField.getText();
        String cellBankId = formCellBankIdField.getText();
        String applicantId = formApplicantIdField.getText();
        String quantity = formQuantityField.getText();
        String approverId = formApproverIdField.getText();
        String outboundHandlerId = formOutboundHandlerIdField.getText();

        if (currentFormMode == FormMode.ADD || currentFormMode == FormMode.EDIT) {
            if (cellBankId.isEmpty() || applicantId.isEmpty() || formReasonArea.getText().isEmpty() || quantity.isEmpty() || formDestinationField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID, 申请人ID, 原因, 数量, 去向不能为空。");
                return false;
            }
            try {
                if (!prodPlanId.isEmpty()) Integer.parseInt(prodPlanId);
                Integer.parseInt(cellBankId);
                Integer.parseInt(applicantId);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "数据错误", "生产计划ID, 细胞库ID, 申请人ID必须是有效的数字。");
                return false;
            }
        } else if (currentFormMode == FormMode.APPROVE) {
            if (approverId.isEmpty() || formStatusComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "审批人ID和审批状态不能为空。");
                return false;
            }
            try {
                Integer.parseInt(approverId);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "数据错误", "审批人ID必须是有效的数字。");
                return false;
            }
        } else if (currentFormMode == FormMode.OUTBOUND) {
            if (outboundHandlerId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "出库处理人ID不能为空。");
                return false;
            }
            try {
                Integer.parseInt(outboundHandlerId);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "数据错误", "出库处理人ID必须是有效的数字。");
                return false;
            }
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

