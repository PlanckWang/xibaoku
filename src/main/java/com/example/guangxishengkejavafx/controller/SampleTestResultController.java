package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleInspectionRequest;
import com.example.guangxishengkejavafx.model.entity.SampleTestResult;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.SampleInspectionRequestService;
import com.example.guangxishengkejavafx.service.SampleTestResultService;
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
public class SampleTestResultController {

    @FXML
    private TableView<SampleTestResult> resultTableView;
    @FXML
    private TableColumn<SampleTestResult, Integer> resultIdColumn;
    @FXML
    private TableColumn<SampleTestResult, SampleInspectionRequest> inspectionRequestColumn;
    @FXML
    private TableColumn<SampleTestResult, Personnel> testerColumn;
    @FXML
    private TableColumn<SampleTestResult, LocalDateTime> testDateColumn;
    @FXML
    private TableColumn<SampleTestResult, String> testResultsColumn;
    @FXML
    private TableColumn<SampleTestResult, String> conclusionColumn;
    @FXML
    private TableColumn<SampleTestResult, String> statusColumn;
    @FXML
    private TableColumn<SampleTestResult, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<SampleTestResult, String> notesColumn;

    @FXML
    private ComboBox<SampleInspectionRequest> inspectionRequestComboBox;
    @FXML
    private ComboBox<Personnel> testerComboBox;
    @FXML
    private DatePicker testDatePicker;
    @FXML
    private TextField conclusionField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea testResultsArea;
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

    private final SampleTestResultService sampleTestResultService;
    private final SampleInspectionRequestService sampleInspectionRequestService;
    private final PersonnelService personnelService;

    private ObservableList<SampleTestResult> resultList = FXCollections.observableArrayList();
    private SampleTestResult currentResult = null;

    @Autowired
    public SampleTestResultController(SampleTestResultService sampleTestResultService,
                                      SampleInspectionRequestService sampleInspectionRequestService,
                                      PersonnelService personnelService) {
        this.sampleTestResultService = sampleTestResultService;
        this.sampleInspectionRequestService = sampleInspectionRequestService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadResultData();

        resultTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        resultIdColumn.setCellValueFactory(new PropertyValueFactory<>("resultId"));
        inspectionRequestColumn.setCellValueFactory(new PropertyValueFactory<>("sampleInspectionRequest"));
        inspectionRequestColumn.setCellFactory(column -> new TableCell<SampleTestResult, SampleInspectionRequest>() {
            @Override
            protected void updateItem(SampleInspectionRequest item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getSampleRegistration() == null) {
                    setText(null);
                } else {
                    setText(item.getRequestId() + " (" + item.getSampleRegistration().getSampleCode() + ")");
                }
            }
        });
        testerColumn.setCellValueFactory(new PropertyValueFactory<>("tester"));
        testerColumn.setCellFactory(column -> new TableCell<SampleTestResult, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        testDateColumn.setCellValueFactory(new PropertyValueFactory<>("testDate"));
        testResultsColumn.setCellValueFactory(new PropertyValueFactory<>("testResults"));
        conclusionColumn.setCellValueFactory(new PropertyValueFactory<>("conclusion"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        resultTableView.setItems(resultList);
    }

    private void loadComboBoxData() {
        inspectionRequestComboBox.setItems(FXCollections.observableArrayList(sampleInspectionRequestService.findAllSampleInspectionRequests()));
        inspectionRequestComboBox.setConverter(new javafx.util.StringConverter<SampleInspectionRequest>() {
            @Override
            public String toString(SampleInspectionRequest request) {
                if (request == null) return null;
                String sampleCode = (request.getSampleRegistration() != null) ? request.getSampleRegistration().getSampleCode() : "N/A";
                return request.getRequestId() + " (样本: " + sampleCode + ")";
            }
            @Override
            public SampleInspectionRequest fromString(String string) { return null; }
        });

        testerComboBox.setItems(FXCollections.observableArrayList(personnelService.findAllPersonnel()));
        testerComboBox.setConverter(new javafx.util.StringConverter<Personnel>() {
            @Override
            public String toString(Personnel personnel) {
                return personnel == null ? null : personnel.getName();
            }
            @Override
            public Personnel fromString(String string) { return null; }
        });
        testerComboBox.getItems().add(0, null); // Allow null selection

        statusComboBox.setItems(FXCollections.observableArrayList("待审核", "合格", "不合格", "需复检"));
    }

    private void loadResultData() {
        List<SampleTestResult> currentResults = sampleTestResultService.findAllSampleTestResults();
        resultList.setAll(currentResults);
    }

    @FXML
    private void handleAddResult() {
        currentResult = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        inspectionRequestComboBox.requestFocus();
    }

    @FXML
    private void handleEditResult() {
        SampleTestResult selectedResult = resultTableView.getSelectionModel().getSelectedItem();
        if (selectedResult != null) {
            currentResult = selectedResult;
            populateForm(currentResult);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个检验结果进行编辑。");
        }
    }

    @FXML
    private void handleDeleteResult() {
        SampleTestResult selectedResult = resultTableView.getSelectionModel().getSelectedItem();
        if (selectedResult != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除检验结果 ID: " + selectedResult.getResultId() + " (请检单: " + selectedResult.getSampleInspectionRequest().getRequestId() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    sampleTestResultService.deleteSampleTestResultById(selectedResult.getResultId());
                    loadResultData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "检验结果已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除检验结果时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个检验结果进行删除。");
        }
    }

    @FXML
    private void handleRefreshResults() {
        loadResultData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveResult() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentResult == null || currentResult.getResultId() == null);
        SampleTestResult resultToSave = isNew ? new SampleTestResult() : currentResult;

        resultToSave.setSampleInspectionRequest(inspectionRequestComboBox.getValue());
        resultToSave.setTester(testerComboBox.getValue()); // Can be null
        if (testDatePicker.getValue() != null) {
            resultToSave.setTestDate(testDatePicker.getValue().atStartOfDay());
        } else {
            resultToSave.setTestDate(LocalDateTime.now()); // Default to now if not set
        }
        resultToSave.setTestResults(testResultsArea.getText());
        resultToSave.setConclusion(conclusionField.getText());
        resultToSave.setStatus(statusComboBox.getValue());
        resultToSave.setNotes(notesArea.getText());

        try {
            sampleTestResultService.saveSampleTestResult(resultToSave);
            loadResultData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            resultTableView.getSelectionModel().select(resultToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "检验结果已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存检验结果时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelResult() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        resultTableView.getSelectionModel().clearSelection();
        currentResult = null;
    }

    private void populateForm(SampleTestResult result) {
        if (result != null) {
            inspectionRequestComboBox.setValue(result.getSampleInspectionRequest());
            testerComboBox.setValue(result.getTester());
            testDatePicker.setValue(result.getTestDate() != null ? result.getTestDate().toLocalDate() : null);
            testResultsArea.setText(result.getTestResults());
            conclusionField.setText(result.getConclusion());
            statusComboBox.setValue(result.getStatus());
            notesArea.setText(result.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        inspectionRequestComboBox.setValue(null);
        testerComboBox.setValue(null);
        testDatePicker.setValue(null);
        testResultsArea.clear();
        conclusionField.clear();
        statusComboBox.setValue(null);
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        inspectionRequestComboBox.setDisable(!editable);
        testerComboBox.setDisable(!editable);
        testDatePicker.setDisable(!editable);
        testResultsArea.setEditable(editable);
        conclusionField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (inspectionRequestComboBox.getValue() == null) {
            errors.append("必须选择一个关联请检单。\n");
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

