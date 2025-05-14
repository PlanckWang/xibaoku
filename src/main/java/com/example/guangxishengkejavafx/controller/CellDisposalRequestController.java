package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.CellDisposalRequest;
import com.example.guangxishengkejavafx.service.CellDisposalRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CellDisposalRequestController {

    @Autowired
    private CellDisposalRequestService cellDisposalRequestService;

    @FXML
    private TextField searchCellBankIdField;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    @FXML
    private TableView<CellDisposalRequest> cellDisposalRequestTable;
    @FXML
    private TableColumn<CellDisposalRequest, Integer> disposalRequestIdColumn;
    @FXML
    private TableColumn<CellDisposalRequest, Integer> cellBankIdColumn;
    @FXML
    private TableColumn<CellDisposalRequest, String> requestDateColumn;
    @FXML
    private TableColumn<CellDisposalRequest, Integer> requesterIdColumn;
    @FXML
    private TableColumn<CellDisposalRequest, String> reasonColumn;
    @FXML
    private TableColumn<CellDisposalRequest, String> statusColumn;
    @FXML
    private TableColumn<CellDisposalRequest, Integer> approverIdColumn;
    @FXML
    private TableColumn<CellDisposalRequest, String> approvalDateColumn;
    @FXML
    private TableColumn<CellDisposalRequest, String> approvalCommentsColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField disposalRequestIdField;
    @FXML
    private TextField formCellBankIdField;
    @FXML
    private TextField requestDateField;
    @FXML
    private TextField requesterIdField;
    @FXML
    private TextArea reasonArea;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private VBox approvalFieldsContainer; // Changed from HBox to VBox to match FXML
    @FXML
    private TextField approverIdField;
    @FXML
    private TextField approvalDateField;
    @FXML
    private TextArea approvalCommentsArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button approveButton;

    private ObservableList<CellDisposalRequest> cellDisposalRequestData = FXCollections.observableArrayList();
    private CellDisposalRequest currentRequest;
    private boolean approvalMode = false; // Approval mode flag

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("待审批", "已批准", "已驳回");
    private final ObservableList<String> searchStatusOptions = FXCollections.observableArrayList("全部", "待审批", "已批准", "已驳回");


    @FXML
    public void initialize() {
        disposalRequestIdColumn.setCellValueFactory(new PropertyValueFactory<>("disposalRequestId"));
        cellBankIdColumn.setCellValueFactory(new PropertyValueFactory<>("cellBankId"));
        requesterIdColumn.setCellValueFactory(new PropertyValueFactory<>("requesterId"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        approverIdColumn.setCellValueFactory(new PropertyValueFactory<>("approverId"));
        approvalCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("approvalComments"));

        requestDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getRequestDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateTimeFormatter) : "");
        });
        approvalDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getApprovalDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateTimeFormatter) : "");
        });

        statusComboBox.setItems(statusOptions);
        searchStatusComboBox.setItems(searchStatusOptions);
        searchStatusComboBox.setValue("全部");

        loadRequests();
        cellDisposalRequestTable.setItems(cellDisposalRequestData);
        hideForm();
    }

    private void loadRequests() {
        cellDisposalRequestData.setAll(cellDisposalRequestService.findAll());
    }

    @FXML
    private void handleSearch() {
        String cellBankIdSearch = searchCellBankIdField.getText().trim();
        String statusSearch = searchStatusComboBox.getValue();

        List<CellDisposalRequest> allRequests = cellDisposalRequestService.findAll();
        List<CellDisposalRequest> filteredRequests = allRequests.stream()
            .filter(req -> {
                boolean matchesCellBankId = true;
                if (!cellBankIdSearch.isEmpty()) {
                    try {
                        matchesCellBankId = req.getCellBankId() != null && req.getCellBankId().equals(Integer.parseInt(cellBankIdSearch));
                    } catch (NumberFormatException e) {
                        matchesCellBankId = false; // Invalid number, so no match
                    }
                }
                boolean matchesStatus = true;
                if (statusSearch != null && !"全部".equals(statusSearch)) {
                    matchesStatus = req.getStatus() != null && req.getStatus().equals(statusSearch);
                }
                return matchesCellBankId && matchesStatus;
            })
            .collect(Collectors.toList());
        cellDisposalRequestData.setAll(filteredRequests);
        cellDisposalRequestTable.setItems(cellDisposalRequestData);
    }

    @FXML
    private void handleRefresh() {
        searchCellBankIdField.clear();
        searchStatusComboBox.setValue("全部");
        loadRequests();
        hideForm();
    }

    @FXML
    private void handleAddRequest() {
        currentRequest = null;
        approvalMode = false;
        clearForm();
        formTitleLabel.setText("新增细胞废弃申请");
        configureFormForAddEdit();
        showForm();
    }

    @FXML
    private void handleEditRequest() {
        CellDisposalRequest selectedRequest = cellDisposalRequestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            if (!"待审批".equals(selectedRequest.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "编辑受限", "只有状态为'待审批'的申请才能编辑。");
                return;
            }
            currentRequest = selectedRequest;
            approvalMode = false;
            populateForm(selectedRequest);
            formTitleLabel.setText("编辑细胞废弃申请");
            configureFormForAddEdit();
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的申请记录。");
        }
    }
    
    @FXML
    private void handleApproveRequest() {
        CellDisposalRequest selectedRequest = cellDisposalRequestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            if (!"待审批".equals(selectedRequest.getStatus())) {
                showAlert(Alert.AlertType.WARNING, "审批受限", "只有状态为'待审批'的申请才能进行审批操作。");
                return;
            }
            currentRequest = selectedRequest;
            approvalMode = true;
            populateForm(selectedRequest);
            formTitleLabel.setText("审批细胞废弃申请");
            configureFormForApproval();
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要审批的申请记录。");
        }
    }

    @FXML
    private void handleDeleteRequest() {
        CellDisposalRequest selectedRequest = cellDisposalRequestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的申请记录吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cellDisposalRequestService.deleteById(selectedRequest.getDisposalRequestId());
                        loadRequests();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "申请记录已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除申请记录: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的申请记录。");
        }
    }

    @FXML
    private void handleSaveRequest() { // For Add/Edit
        if (!validateForm(false)) {
            return;
        }
        CellDisposalRequest requestToSave = (currentRequest == null) ? new CellDisposalRequest() : currentRequest;
        try {
            requestToSave.setCellBankId(Integer.parseInt(formCellBankIdField.getText()));
            requestToSave.setRequesterId(Integer.parseInt(requesterIdField.getText()));
            requestToSave.setReason(reasonArea.getText());
            if (currentRequest == null) { // New request
                 requestToSave.setStatus("待审批");
                 requestToSave.setRequestDate(LocalDateTime.now());
            } else { // Editing existing, status might be changed by user if allowed, or fixed
                 requestToSave.setStatus(statusComboBox.getValue() != null ? statusComboBox.getValue() : "待审批");
            }

            cellDisposalRequestService.save(requestToSave);
            loadRequests();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "申请信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "数据格式错误，请检查细胞库ID和申请人ID。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存申请信息: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveApproval() { // For Approval action
        if (!validateForm(true)) {
            return;
        }
        if (currentRequest == null) return;

        try {
            currentRequest.setApproverId(Integer.parseInt(approverIdField.getText()));
            currentRequest.setApprovalComments(approvalCommentsArea.getText());
            currentRequest.setStatus(statusComboBox.getValue()); // Status is set by approver
            // ApprovalDate is set in service layer if status is Approved/Rejected
            
            cellDisposalRequestService.save(currentRequest);
            loadRequests();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "审批成功", "审批信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "审批失败", "审批人ID必须是数字。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "审批失败", "无法保存审批信息: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        hideForm();
    }

    private void populateForm(CellDisposalRequest request) {
        disposalRequestIdField.setText(request.getDisposalRequestId() != null ? request.getDisposalRequestId().toString() : "");
        formCellBankIdField.setText(request.getCellBankId() != null ? request.getCellBankId().toString() : "");
        requestDateField.setText(request.getRequestDate() != null ? request.getRequestDate().format(dateTimeFormatter) : "");
        requesterIdField.setText(request.getRequesterId() != null ? request.getRequesterId().toString() : "");
        reasonArea.setText(request.getReason());
        statusComboBox.setValue(request.getStatus());
        approverIdField.setText(request.getApproverId() != null ? request.getApproverId().toString() : "");
        approvalDateField.setText(request.getApprovalDate() != null ? request.getApprovalDate().format(dateTimeFormatter) : "");
        approvalCommentsArea.setText(request.getApprovalComments());
    }

    private void clearForm() {
        disposalRequestIdField.clear();
        formCellBankIdField.clear();
        requestDateField.clear();
        requesterIdField.clear();
        reasonArea.clear();
        statusComboBox.getSelectionModel().clearSelection();
        statusComboBox.setValue(null);
        approverIdField.clear();
        approvalDateField.clear();
        approvalCommentsArea.clear();
    }

    private void configureFormForAddEdit() {
        formCellBankIdField.setDisable(false);
        requesterIdField.setDisable(false);
        reasonArea.setDisable(false);
        statusComboBox.setDisable(true); // Status is '待审批' for new/edit, not user-settable here
        statusComboBox.setValue("待审批");
        approvalFieldsContainer.setVisible(false);
        approvalFieldsContainer.setManaged(false);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        approveButton.setVisible(false);
        approveButton.setManaged(false);
    }

    private void configureFormForApproval() {
        formCellBankIdField.setDisable(true);
        requesterIdField.setDisable(true);
        reasonArea.setDisable(true);
        statusComboBox.setDisable(false); // Approver sets the status
        statusComboBox.setItems(FXCollections.observableArrayList("已批准", "已驳回")); // Only approval/rejection options
        approvalFieldsContainer.setVisible(true);
        approvalFieldsContainer.setManaged(true);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        approveButton.setVisible(true);
        approveButton.setManaged(true);
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentRequest = null;
        approvalMode = false;
        statusComboBox.setItems(statusOptions); // Reset status options for add/edit
    }

    private boolean validateForm(boolean isApproval) {
        if (!isApproval) {
            if (formCellBankIdField.getText().isEmpty() || requesterIdField.getText().isEmpty() || reasonArea.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID、申请人ID和废弃原因不能为空。");
                return false;
            }
            try {
                Integer.parseInt(formCellBankIdField.getText());
                Integer.parseInt(requesterIdField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "细胞库ID和申请人ID必须是数字。");
                return false;
            }
        } else { // Approval mode validation
            if (approverIdField.getText().isEmpty() || statusComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "审批人ID和审批状态不能为空。");
                return false;
            }
            try {
                Integer.parseInt(approverIdField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "审批人ID必须是数字。");
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

