package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.HealthRecord;
import com.example.guangxishengkejavafx.service.HealthRecordService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @FXML
    private TextField searchPatientIdField;
    @FXML
    private DatePicker searchRecordDatePicker;
    @FXML
    private TextField searchRecordTypeField;
    @FXML
    private TableView<HealthRecord> healthRecordTable;
    @FXML
    private TableColumn<HealthRecord, Integer> recordIdColumn;
    @FXML
    private TableColumn<HealthRecord, Integer> patientIdColumn;
    @FXML
    private TableColumn<HealthRecord, String> recordDateColumn;
    @FXML
    private TableColumn<HealthRecord, String> recordTypeColumn;
    @FXML
    private TableColumn<HealthRecord, String> descriptionColumn;
    @FXML
    private TableColumn<HealthRecord, Double> heightColumn;
    @FXML
    private TableColumn<HealthRecord, Double> weightColumn;
    @FXML
    private TableColumn<HealthRecord, String> bloodPressureColumn;
    @FXML
    private TableColumn<HealthRecord, Integer> heartRateColumn;
    @FXML
    private TableColumn<HealthRecord, Double> bloodSugarColumn;
    @FXML
    private TableColumn<HealthRecord, Integer> recordedByColumn;
    @FXML
    private TableColumn<HealthRecord, String> createdAtColumn;
    @FXML
    private TableColumn<HealthRecord, String> updatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField recordIdField;
    @FXML
    private TextField formPatientIdField;
    @FXML
    private DatePicker formRecordDatePicker;
    @FXML
    private TextField formRecordTypeField;
    @FXML
    private TextArea formDescriptionArea;
    @FXML
    private TextField formHeightField;
    @FXML
    private TextField formWeightField;
    @FXML
    private TextField formBloodPressureField;
    @FXML
    private TextField formHeartRateField;
    @FXML
    private TextField formBloodSugarField;
    @FXML
    private TextArea formSymptomsArea;
    @FXML
    private TextArea formDiagnosisArea;
    @FXML
    private TextArea formTreatmentPlanArea;
    @FXML
    private TextArea formMedicationArea;
    @FXML
    private TextArea formNotesArea;
    @FXML
    private HBox auditFieldsContainer;
    @FXML
    private TextField formRecordedByField;
    @FXML
    private TextField formCreatedAtField;
    @FXML
    private HBox auditFieldsContainer2;
    @FXML
    private TextField formUpdatedAtField;

    private ObservableList<HealthRecord> healthRecordData = FXCollections.observableArrayList();
    private HealthRecord currentHealthRecord;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadHealthRecords();
        healthRecordTable.setItems(healthRecordData);
        hideForm();
    }

    private void setupTableColumns() {
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        recordDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getRecordDate()));
        recordTypeColumn.setCellValueFactory(new PropertyValueFactory<>("recordType"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        bloodPressureColumn.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
        heartRateColumn.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        bloodSugarColumn.setCellValueFactory(new PropertyValueFactory<>("bloodSugar"));
        recordedByColumn.setCellValueFactory(new PropertyValueFactory<>("recordedBy"));
        createdAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getUpdatedAt()));
    }

    private SimpleStringProperty formatDate(LocalDate date) {
        return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadHealthRecords() {
        healthRecordData.setAll(healthRecordService.findAll());
    }

    @FXML
    private void handleSearch() {
        String patientIdSearch = searchPatientIdField.getText().trim();
        LocalDate recordDateSearch = searchRecordDatePicker.getValue();
        String recordTypeSearch = searchRecordTypeField.getText().trim();

        Specification<HealthRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!patientIdSearch.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("patientId"), Integer.parseInt(patientIdSearch)));
                } catch (NumberFormatException e) {
                    // Ignore if not a valid number, or show an error
                }
            }
            if (recordDateSearch != null) {
                predicates.add(cb.equal(root.get("recordDate"), recordDateSearch));
            }
            if (!recordTypeSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("recordType")), "%" + recordTypeSearch.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        healthRecordData.setAll(healthRecordService.findAll(spec));
    }

    @FXML
    private void handleRefresh() {
        searchPatientIdField.clear();
        searchRecordDatePicker.setValue(null);
        searchRecordTypeField.clear();
        loadHealthRecords();
        hideForm();
    }

    @FXML
    private void handleAddRecord() {
        currentHealthRecord = null;
        clearForm();
        formTitleLabel.setText("新增健康记录");
        auditFieldsContainer.setVisible(true); // Show recordedBy for new record
        auditFieldsContainer.setManaged(true);
        formRecordedByField.setDisable(false); // Allow setting recordedBy for new record
        auditFieldsContainer2.setVisible(false);
        auditFieldsContainer2.setManaged(false);
        showForm();
    }

    @FXML
    private void handleEditRecord() {
        HealthRecord selected = healthRecordTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentHealthRecord = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑健康记录");
            auditFieldsContainer.setVisible(true);
            auditFieldsContainer.setManaged(true);
            formRecordedByField.setDisable(true); // Don't allow changing recordedBy on edit
            auditFieldsContainer2.setVisible(true);
            auditFieldsContainer2.setManaged(true);
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的健康记录。");
        }
    }

    @FXML
    private void handleDeleteRecord() {
        HealthRecord selected = healthRecordTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的健康记录吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        healthRecordService.deleteById(selected.getRecordId());
                        loadHealthRecords();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "健康记录已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除健康记录: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的健康记录。");
        }
    }

    @FXML
    private void handleSaveRecord() {
        if (!validateForm()) return;

        HealthRecord recordToSave = (currentHealthRecord == null) ? new HealthRecord() : currentHealthRecord;
        try {
            recordToSave.setPatientId(Integer.parseInt(formPatientIdField.getText()));
            recordToSave.setRecordDate(formRecordDatePicker.getValue());
            recordToSave.setRecordType(formRecordTypeField.getText());
            recordToSave.setDescription(formDescriptionArea.getText());
            recordToSave.setHeight(formHeightField.getText().isEmpty() ? null : Double.parseDouble(formHeightField.getText()));
            recordToSave.setWeight(formWeightField.getText().isEmpty() ? null : Double.parseDouble(formWeightField.getText()));
            recordToSave.setBloodPressure(formBloodPressureField.getText());
            recordToSave.setHeartRate(formHeartRateField.getText().isEmpty() ? null : Integer.parseInt(formHeartRateField.getText()));
            recordToSave.setBloodSugar(formBloodSugarField.getText().isEmpty() ? null : Double.parseDouble(formBloodSugarField.getText()));
            recordToSave.setSymptoms(formSymptomsArea.getText());
            recordToSave.setDiagnosis(formDiagnosisArea.getText());
            recordToSave.setTreatmentPlan(formTreatmentPlanArea.getText());
            recordToSave.setMedication(formMedicationArea.getText());
            recordToSave.setNotes(formNotesArea.getText());

            if (currentHealthRecord == null) { // New record
                recordToSave.setRecordedBy(formRecordedByField.getText().isEmpty() ? null : Integer.parseInt(formRecordedByField.getText()));
            } // For existing, recordedBy is not changed here, set in service or kept from original

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "患者ID、身高、体重、心率、血糖和记录人ID必须是有效的数字。");
            return;
        }

        try {
            healthRecordService.save(recordToSave);
            loadHealthRecords();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "健康记录信息已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存健康记录: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(HealthRecord record) {
        recordIdField.setText(record.getRecordId() != null ? record.getRecordId().toString() : "");
        formPatientIdField.setText(record.getPatientId() != null ? record.getPatientId().toString() : "");
        formRecordDatePicker.setValue(record.getRecordDate());
        formRecordTypeField.setText(record.getRecordType());
        formDescriptionArea.setText(record.getDescription());
        formHeightField.setText(record.getHeight() != null ? record.getHeight().toString() : "");
        formWeightField.setText(record.getWeight() != null ? record.getWeight().toString() : "");
        formBloodPressureField.setText(record.getBloodPressure());
        formHeartRateField.setText(record.getHeartRate() != null ? record.getHeartRate().toString() : "");
        formBloodSugarField.setText(record.getBloodSugar() != null ? record.getBloodSugar().toString() : "");
        formSymptomsArea.setText(record.getSymptoms());
        formDiagnosisArea.setText(record.getDiagnosis());
        formTreatmentPlanArea.setText(record.getTreatmentPlan());
        formMedicationArea.setText(record.getMedication());
        formNotesArea.setText(record.getNotes());
        formRecordedByField.setText(record.getRecordedBy() != null ? record.getRecordedBy().toString() : "");
        formCreatedAtField.setText(record.getCreatedAt() != null ? record.getCreatedAt().format(dateTimeFormatter) : "");
        formUpdatedAtField.setText(record.getUpdatedAt() != null ? record.getUpdatedAt().format(dateTimeFormatter) : "");
    }

    private void clearForm() {
        recordIdField.clear();
        formPatientIdField.clear();
        formRecordDatePicker.setValue(null);
        formRecordTypeField.clear();
        formDescriptionArea.clear();
        formHeightField.clear();
        formWeightField.clear();
        formBloodPressureField.clear();
        formHeartRateField.clear();
        formBloodSugarField.clear();
        formSymptomsArea.clear();
        formDiagnosisArea.clear();
        formTreatmentPlanArea.clear();
        formMedicationArea.clear();
        formNotesArea.clear();
        formRecordedByField.clear();
        formCreatedAtField.clear();
        formUpdatedAtField.clear();
    }

    private boolean validateForm() {
        if (formPatientIdField.getText().isEmpty() || formRecordDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "患者ID和记录日期不能为空。");
            return false;
        }
        // Add more specific validations as needed
        return true;
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentHealthRecord = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

