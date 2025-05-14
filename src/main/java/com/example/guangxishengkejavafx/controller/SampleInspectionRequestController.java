package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.SampleInspectionRequestService;
import com.example.guangxishengkejavafx.service.SampleRegistrationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class SampleInspectionRequestController {

    @FXML
    private TableView<SampleInspectionRequest> requestTableView;
    @FXML
    private TableColumn<SampleInspectionRequest, Integer> requestIdColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, SampleRegistration> sampleRegistrationColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, Personnel> requesterColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, LocalDateTime> requestDateColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, String> inspectionItemsColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, String> statusColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<SampleInspectionRequest, String> notesColumn;

    @FXML
    private ComboBox<SampleRegistration> sampleRegistrationComboBox;
    @FXML
    private ComboBox<Personnel> requesterComboBox;
    @FXML
    private DatePicker requestDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea inspectionItemsArea;
    @FXML
    private TextArea notesArea;

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

    private final SampleInspectionRequestService sampleInspectionRequestService;
    private final SampleRegistrationService sampleRegistrationService;
    private final PersonnelService personnelService;

    private ObservableList<SampleInspectionRequest> requestList = FXCollections.observableArrayList();
    private SampleInspectionRequest currentRequest = null;

    @Autowired
    public SampleInspectionRequestController(SampleInspectionRequestService sampleInspectionRequestService,
                                           SampleRegistrationService sampleRegistrationService,
                                           PersonnelService personnelService) {
        this.sampleInspectionRequestService = sampleInspectionRequestService;
        this.sampleRegistrationService = sampleRegistrationService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadRequestData();

        requestTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        sampleRegistrationColumn.setCellValueFactory(new PropertyValueFactory<>("sampleRegistration"));
        sampleRegistrationColumn.setCellFactory(column -> new TableCell<SampleInspectionRequest, SampleRegistration>() {
            @Override
            protected void updateItem(SampleRegistration item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSampleCode());
            }
        });
        requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requester"));
        requesterColumn.setCellFactory(column -> new TableCell<SampleInspectionRequest, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        inspectionItemsColumn.setCellValueFactory(new PropertyValueFactory<>("inspectionItems"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        requestTableView.setItems(requestList);
    }

    private void loadComboBoxData() {
        sampleRegistrationComboBox.setItems(FXCollections.observableArrayList(sampleRegistrationService.findAllSampleRegistrations()));
        sampleRegistrationComboBox.setConverter(new javafx.util.StringConverter<SampleRegistration>() {
            @Override
            public String toString(SampleRegistration registration) {
                return registration == null ? null : registration.getSampleCode() + " (" + registration.getCustomer().getCustomerName() + ")";
            }
            @Override
            public SampleRegistration fromString(String string) { return null; }
        });

        requesterComboBox.setItems(FXCollections.observableArrayList(personnelService.findAllPersonnel()));
        requesterComboBox.setConverter(new javafx.util.StringConverter<Personnel>() {
            @Override
            public String toString(Personnel personnel) {
                return personnel == null ? null : personnel.getName();
            }
            @Override
            public Personnel fromString(String string) { return null; }
        });
        requesterComboBox.getItems().add(0, null); // Allow null selection for requester

        statusComboBox.setItems(FXCollections.observableArrayList("待审批", "审批通过", "审批拒绝", "检验中", "检验完成"));
    }

    private void loadRequestData() {
        List<SampleInspectionRequest> currentRequests = sampleInspectionRequestService.findAllSampleInspectionRequests();
        requestList.setAll(currentRequests);
    }

    @FXML
    private void handleAddRequest() {
        currentRequest = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        sampleRegistrationComboBox.requestFocus();
    }

    @FXML
    private void handleEditRequest() {
        SampleInspectionRequest selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            currentRequest = selectedRequest;
            populateForm(currentRequest);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个请检记录进行编辑。");
        }
    }

    @FXML
    private void handleDeleteRequest() {
        SampleInspectionRequest selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除请检记录 ID: " + selectedRequest.getRequestId() + " (样本: " + selectedRequest.getSampleRegistration().getSampleCode() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    sampleInspectionRequestService.deleteSampleInspectionRequestById(selectedRequest.getRequestId());
                    loadRequestData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "请检记录已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除请检记录时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个请检记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshRequests() {
        loadRequestData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveRequest() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentRequest == null || currentRequest.getRequestId() == null);
        SampleInspectionRequest requestToSave = isNew ? new SampleInspectionRequest() : currentRequest;

        requestToSave.setSampleRegistration(sampleRegistrationComboBox.getValue());
        requestToSave.setRequester(requesterComboBox.getValue()); // Can be null
        if (requestDatePicker.getValue() != null) {
            requestToSave.setRequestDate(requestDatePicker.getValue().atStartOfDay());
        } else {
            requestToSave.setRequestDate(LocalDateTime.now()); // Default to now if not set
        }
        requestToSave.setInspectionItems(inspectionItemsArea.getText());
        requestToSave.setStatus(statusComboBox.getValue());
        requestToSave.setNotes(notesArea.getText());

        try {
            sampleInspectionRequestService.saveSampleInspectionRequest(requestToSave);
            loadRequestData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            requestTableView.getSelectionModel().select(requestToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "请检记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存请检记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelRequest() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        requestTableView.getSelectionModel().clearSelection();
        currentRequest = null;
    }

    private void populateForm(SampleInspectionRequest request) {
        if (request != null) {
            sampleRegistrationComboBox.setValue(request.getSampleRegistration());
            requesterComboBox.setValue(request.getRequester());
            requestDatePicker.setValue(request.getRequestDate() != null ? request.getRequestDate().toLocalDate() : null);
            inspectionItemsArea.setText(request.getInspectionItems());
            statusComboBox.setValue(request.getStatus());
            notesArea.setText(request.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        sampleRegistrationComboBox.setValue(null);
        requesterComboBox.setValue(null);
        requestDatePicker.setValue(null);
        inspectionItemsArea.clear();
        statusComboBox.setValue(null);
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        sampleRegistrationComboBox.setDisable(!editable);
        requesterComboBox.setDisable(!editable);
        requestDatePicker.setDisable(!editable);
        inspectionItemsArea.setEditable(editable);
        statusComboBox.setDisable(!editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (sampleRegistrationComboBox.getValue() == null) {
            errors.append("必须选择一个关联样本。\n");
        }
        if (inspectionItemsArea.getText() == null || inspectionItemsArea.getText().trim().isEmpty()) {
            errors.append("检验项目不能为空。\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("必须选择状态。\n");
        }
        // Add more specific validations as needed

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

