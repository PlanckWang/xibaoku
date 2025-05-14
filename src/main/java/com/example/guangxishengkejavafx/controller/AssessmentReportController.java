package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.AssessmentReport;
import com.example.guangxishengkejavafx.model.entity.Depositor; // Assuming Depositor is used as Patient
import com.example.guangxishengkejavafx.model.entity.ResearchProtocol;
import com.example.guangxishengkejavafx.service.AssessmentReportService;
import com.example.guangxishengkejavafx.service.DepositorService; // For Patient ComboBox
import com.example.guangxishengkejavafx.service.ResearchProtocolService; // For Protocol ComboBox
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
public class AssessmentReportController {

    @Autowired
    private AssessmentReportService assessmentReportService;
    @Autowired
    private ResearchProtocolService researchProtocolService;
    @Autowired
    private DepositorService depositorService; // Assuming Depositor is Patient

    @FXML
    private TextField searchReportTitleField;
    @FXML
    private TextField searchProtocolIdField;
    @FXML
    private TextField searchPatientIdField;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    @FXML
    private TextField searchReportTypeField;
    @FXML
    private TableView<AssessmentReport> assessmentReportTable;
    @FXML
    private TableColumn<AssessmentReport, Integer> reportIdColumn;
    @FXML
    private TableColumn<AssessmentReport, String> reportTitleColumn;
    @FXML
    private TableColumn<AssessmentReport, String> protocolTitleColumn;
    @FXML
    private TableColumn<AssessmentReport, String> patientNameColumn;
    @FXML
    private TableColumn<AssessmentReport, String> assessmentDateColumn;
    @FXML
    private TableColumn<AssessmentReport, Integer> assessorIdColumn;
    @FXML
    private TableColumn<AssessmentReport, String> reportTypeColumn;
    @FXML
    private TableColumn<AssessmentReport, String> statusColumn;
    @FXML
    private TableColumn<AssessmentReport, String> versionColumn;
    @FXML
    private TableColumn<AssessmentReport, String> createdAtColumn;
    @FXML
    private TableColumn<AssessmentReport, String> updatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField reportIdField;
    @FXML
    private TextField formReportTitleField;
    @FXML
    private ComboBox<ResearchProtocol> formProtocolComboBox;
    @FXML
    private ComboBox<Depositor> formPatientComboBox; // Assuming Depositor is Patient
    @FXML
    private DatePicker formAssessmentDatePicker;
    @FXML
    private TextField formAssessorIdField;
    @FXML
    private ComboBox<String> formReportTypeComboBox;
    @FXML
    private ComboBox<String> formStatusComboBox;
    @FXML
    private TextField formVersionField;
    @FXML
    private TextArea formSummaryArea;
    @FXML
    private TextArea formFindingsArea;
    @FXML
    private TextArea formConclusionsArea;
    @FXML
    private TextArea formRecommendationsArea;
    @FXML
    private HBox auditFieldsContainer;
    @FXML
    private TextField formCreatedByField;
    @FXML
    private TextField formCreatedAtField;
    @FXML
    private HBox auditFieldsContainer2;
    @FXML
    private TextField formUpdatedByField;
    @FXML
    private TextField formUpdatedAtField;

    private ObservableList<AssessmentReport> assessmentReportData = FXCollections.observableArrayList();
    private AssessmentReport currentAssessmentReport;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("草稿", "待审核", "已审核", "已发布");
    private final ObservableList<String> searchStatusOptions = FXCollections.observableArrayList("全部", "草稿", "待审核", "已审核", "已发布");
    private final ObservableList<String> reportTypeOptions = FXCollections.observableArrayList("中期评估", "最终评估", "安全性评估", "有效性评估", "其他");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProtocolsIntoComboBox();
        loadPatientsIntoComboBox();
        formStatusComboBox.setItems(statusOptions);
        searchStatusComboBox.setItems(searchStatusOptions);
        searchStatusComboBox.setValue("全部");
        formReportTypeComboBox.setItems(reportTypeOptions);
        loadAssessmentReports();
        assessmentReportTable.setItems(assessmentReportData);
        hideForm();
    }

    private void setupTableColumns() {
        reportIdColumn.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        reportTitleColumn.setCellValueFactory(new PropertyValueFactory<>("reportTitle"));
        protocolTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getResearchProtocol() != null ? cellData.getValue().getResearchProtocol().getProtocolTitle() : "N/A"));
        patientNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient() != null ? cellData.getValue().getPatient().getName() : "N/A")); // Assuming Depositor has getName()
        assessmentDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getAssessmentDate()));
        assessorIdColumn.setCellValueFactory(new PropertyValueFactory<>("assessorId"));
        reportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        createdAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getUpdatedAt()));
    }

    private void loadProtocolsIntoComboBox() {
        List<ResearchProtocol> protocols = researchProtocolService.findAll();
        ObservableList<ResearchProtocol> protocolObservableList = FXCollections.observableArrayList(protocols);
        formProtocolComboBox.setItems(protocolObservableList);
        formProtocolComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ResearchProtocol object) {
                return object == null ? "" : object.getProtocolTitle() + " (" + object.getProtocolCode() + ")";
            }
            @Override
            public ResearchProtocol fromString(String string) {
                return formProtocolComboBox.getItems().stream().filter(p -> (p.getProtocolTitle() + " (" + p.getProtocolCode() + ")").equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void loadPatientsIntoComboBox() {
        List<Depositor> patients = depositorService.findAll(); // Assuming Depositor is Patient
        ObservableList<Depositor> patientObservableList = FXCollections.observableArrayList(patients);
        formPatientComboBox.setItems(patientObservableList);
        formPatientComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Depositor object) {
                return object == null ? "" : object.getName() + " (ID: " + object.getDepositorId() + ")"; // Assuming Depositor has getName() and getDepositorId()
            }
            @Override
            public Depositor fromString(String string) {
                return formPatientComboBox.getItems().stream().filter(p -> (p.getName() + " (ID: " + p.getDepositorId() + ")").equals(string)).findFirst().orElse(null);
            }
        });
    }

    private SimpleStringProperty formatDate(LocalDate date) {
        return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadAssessmentReports() {
        assessmentReportData.setAll(assessmentReportService.findAll());
    }

    @FXML
    private void handleSearch() {
        String titleSearch = searchReportTitleField.getText().trim();
        String protocolIdSearch = searchProtocolIdField.getText().trim();
        String patientIdSearch = searchPatientIdField.getText().trim();
        String statusSearch = searchStatusComboBox.getValue();
        String typeSearch = searchReportTypeField.getText().trim();

        Specification<AssessmentReport> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!titleSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("reportTitle")), "%" + titleSearch.toLowerCase() + "%"));
            }
            if (!protocolIdSearch.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("protocolId"), Integer.parseInt(protocolIdSearch)));
                } catch (NumberFormatException e) { /* Ignore */ }
            }
            if (!patientIdSearch.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("patientId"), Integer.parseInt(patientIdSearch)));
                } catch (NumberFormatException e) { /* Ignore */ }
            }
            if (statusSearch != null && !"全部".equals(statusSearch)) {
                predicates.add(cb.equal(root.get("status"), statusSearch));
            }
            if (!typeSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("reportType")), "%" + typeSearch.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        assessmentReportData.setAll(assessmentReportService.findAll(spec));
    }

    @FXML
    private void handleRefresh() {
        searchReportTitleField.clear();
        searchProtocolIdField.clear();
        searchPatientIdField.clear();
        searchStatusComboBox.setValue("全部");
        searchReportTypeField.clear();
        loadAssessmentReports();
        hideForm();
    }

    @FXML
    private void handleAddReport() {
        currentAssessmentReport = null;
        clearForm();
        formTitleLabel.setText("新增评估报告");
        auditFieldsContainer.setVisible(false);
        auditFieldsContainer.setManaged(false);
        auditFieldsContainer2.setVisible(false);
        auditFieldsContainer2.setManaged(false);
        showForm();
    }

    @FXML
    private void handleEditReport() {
        AssessmentReport selected = assessmentReportTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentAssessmentReport = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑评估报告");
            auditFieldsContainer.setVisible(true);
            auditFieldsContainer.setManaged(true);
            auditFieldsContainer2.setVisible(true);
            auditFieldsContainer2.setManaged(true);
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的评估报告。");
        }
    }

    @FXML
    private void handleDeleteReport() {
        AssessmentReport selected = assessmentReportTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的评估报告吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        assessmentReportService.deleteById(selected.getReportId());
                        loadAssessmentReports();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "评估报告已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除评估报告: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的评估报告。");
        }
    }

    @FXML
    private void handleSaveReport() {
        if (!validateForm()) return;

        AssessmentReport reportToSave = (currentAssessmentReport == null) ? new AssessmentReport() : currentAssessmentReport;
        reportToSave.setReportTitle(formReportTitleField.getText());
        ResearchProtocol selectedProtocol = formProtocolComboBox.getValue();
        if (selectedProtocol != null) {
            reportToSave.setProtocolId(selectedProtocol.getProtocolId());
            reportToSave.setResearchProtocol(selectedProtocol);
        } else {
            reportToSave.setProtocolId(null);
            reportToSave.setResearchProtocol(null);
        }
        Depositor selectedPatient = formPatientComboBox.getValue(); // Assuming Depositor is Patient
        if (selectedPatient != null) {
            reportToSave.setPatientId(selectedPatient.getDepositorId()); // Assuming Depositor has getDepositorId()
            reportToSave.setPatient(selectedPatient);
        } else {
            reportToSave.setPatientId(null);
            reportToSave.setPatient(null);
        }
        reportToSave.setAssessmentDate(formAssessmentDatePicker.getValue());
        try {
            reportToSave.setAssessorId(formAssessorIdField.getText().isEmpty() ? null : Integer.parseInt(formAssessorIdField.getText()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "评估人ID必须是有效的数字。");
            return;
        }
        reportToSave.setReportType(formReportTypeComboBox.getValue());
        reportToSave.setStatus(formStatusComboBox.getValue());
        reportToSave.setVersion(formVersionField.getText());
        reportToSave.setSummary(formSummaryArea.getText());
        reportToSave.setFindings(formFindingsArea.getText());
        reportToSave.setConclusions(formConclusionsArea.getText());
        reportToSave.setRecommendations(formRecommendationsArea.getText());

        try {
            assessmentReportService.save(reportToSave);
            loadAssessmentReports();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "评估报告信息已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存评估报告: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(AssessmentReport report) {
        reportIdField.setText(report.getReportId() != null ? report.getReportId().toString() : "");
        formReportTitleField.setText(report.getReportTitle());
        formProtocolComboBox.setValue(report.getResearchProtocol());
        formPatientComboBox.setValue(report.getPatient()); // Assuming Depositor is Patient
        formAssessmentDatePicker.setValue(report.getAssessmentDate());
        formAssessorIdField.setText(report.getAssessorId() != null ? report.getAssessorId().toString() : "");
        formReportTypeComboBox.setValue(report.getReportType());
        formStatusComboBox.setValue(report.getStatus());
        formVersionField.setText(report.getVersion());
        formSummaryArea.setText(report.getSummary());
        formFindingsArea.setText(report.getFindings());
        formConclusionsArea.setText(report.getConclusions());
        formRecommendationsArea.setText(report.getRecommendations());
        formCreatedByField.setText(report.getCreatedBy() != null ? report.getCreatedBy().toString() : "");
        formCreatedAtField.setText(report.getCreatedAt() != null ? report.getCreatedAt().format(dateTimeFormatter) : "");
        formUpdatedByField.setText(report.getUpdatedBy() != null ? report.getUpdatedBy().toString() : "");
        formUpdatedAtField.setText(report.getUpdatedAt() != null ? report.getUpdatedAt().format(dateTimeFormatter) : "");
    }

    private void clearForm() {
        reportIdField.clear();
        formReportTitleField.clear();
        formProtocolComboBox.getSelectionModel().clearSelection();
        formPatientComboBox.getSelectionModel().clearSelection();
        formAssessmentDatePicker.setValue(null);
        formAssessorIdField.clear();
        formReportTypeComboBox.getSelectionModel().clearSelection();
        formStatusComboBox.getSelectionModel().clearSelection();
        formVersionField.clear();
        formSummaryArea.clear();
        formFindingsArea.clear();
        formConclusionsArea.clear();
        formRecommendationsArea.clear();
        formCreatedByField.clear();
        formCreatedAtField.clear();
        formUpdatedByField.clear();
        formUpdatedAtField.clear();
    }

    private boolean validateForm() {
        if (formReportTitleField.getText().isEmpty() || formProtocolComboBox.getValue() == null || formPatientComboBox.getValue() == null || formAssessmentDatePicker.getValue() == null || formStatusComboBox.getValue() == null || formReportTypeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "报告标题、研究方案、患者、评估日期、状态和报告类型不能为空。");
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
        currentAssessmentReport = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

