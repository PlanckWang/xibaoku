package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Project;
import com.example.guangxishengkejavafx.model.entity.ResearchProtocol;
import com.example.guangxishengkejavafx.service.ProjectService;
import com.example.guangxishengkejavafx.service.ResearchProtocolService;
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
public class ResearchProtocolController {

    @Autowired
    private ResearchProtocolService researchProtocolService;
    @Autowired
    private ProjectService projectService; // For populating Project ComboBox

    @FXML
    private TextField searchProtocolTitleField;
    @FXML
    private TextField searchProtocolCodeField;
    @FXML
    private TextField searchProjectIdField;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    @FXML
    private TableView<ResearchProtocol> researchProtocolTable;
    @FXML
    private TableColumn<ResearchProtocol, Integer> protocolIdColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> protocolTitleColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> protocolCodeColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> projectNameColumn; // For displaying associated project name
    @FXML
    private TableColumn<ResearchProtocol, String> versionColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> statusColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> approvalDateColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> effectiveDateColumn;
    @FXML
    private TableColumn<ResearchProtocol, Integer> principalInvestigatorIdColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> sponsorColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> createdAtColumn;
    @FXML
    private TableColumn<ResearchProtocol, String> updatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField protocolIdField;
    @FXML
    private TextField formProtocolTitleField;
    @FXML
    private TextField formProtocolCodeField;
    @FXML
    private ComboBox<Project> formProjectComboBox;
    @FXML
    private TextField formVersionField;
    @FXML
    private ComboBox<String> formStatusComboBox;
    @FXML
    private DatePicker formApprovalDatePicker;
    @FXML
    private DatePicker formEffectiveDatePicker;
    @FXML
    private DatePicker formExpiryDatePicker;
    @FXML
    private TextField formPrincipalInvestigatorIdField;
    @FXML
    private TextField formSponsorField;
    @FXML
    private TextArea formObjectivesArea;
    @FXML
    private TextArea formMethodologyArea;
    @FXML
    private TextArea formInclusionCriteriaArea;
    @FXML
    private TextArea formExclusionCriteriaArea;
    @FXML
    private TextArea formEthicalConsiderationsArea;
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

    private ObservableList<ResearchProtocol> researchProtocolData = FXCollections.observableArrayList();
    private ResearchProtocol currentResearchProtocol;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("草稿", "待审批", "已批准", "执行中", "已完成", "已终止");
    private final ObservableList<String> searchStatusOptions = FXCollections.observableArrayList("全部", "草稿", "待审批", "已批准", "执行中", "已完成", "已终止");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProjectsIntoComboBox();
        formStatusComboBox.setItems(statusOptions);
        searchStatusComboBox.setItems(searchStatusOptions);
        searchStatusComboBox.setValue("全部");
        loadResearchProtocols();
        researchProtocolTable.setItems(researchProtocolData);
        hideForm();
    }

    private void setupTableColumns() {
        protocolIdColumn.setCellValueFactory(new PropertyValueFactory<>("protocolId"));
        protocolTitleColumn.setCellValueFactory(new PropertyValueFactory<>("protocolTitle"));
        protocolCodeColumn.setCellValueFactory(new PropertyValueFactory<>("protocolCode"));
        projectNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProject() != null ? cellData.getValue().getProject().getProjectName() : "N/A"));
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        approvalDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getApprovalDate()));
        effectiveDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getEffectiveDate()));
        principalInvestigatorIdColumn.setCellValueFactory(new PropertyValueFactory<>("principalInvestigatorId"));
        sponsorColumn.setCellValueFactory(new PropertyValueFactory<>("sponsor"));
        createdAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getUpdatedAt()));
    }

    private void loadProjectsIntoComboBox() {
        List<Project> projects = projectService.findAll();
        ObservableList<Project> projectObservableList = FXCollections.observableArrayList(projects);
        formProjectComboBox.setItems(projectObservableList);
        // Converter for displaying names in ComboBox
        javafx.util.StringConverter<Project> converter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(Project object) {
                return object == null ? "" : object.getProjectName() + " (" + object.getProjectCode() + ")";
            }
            @Override
            public Project fromString(String string) {
                return formProjectComboBox.getItems().stream().filter(p -> (p.getProjectName() + " (" + p.getProjectCode() + ")").equals(string)).findFirst().orElse(null);
            }
        };
        formProjectComboBox.setConverter(converter);
    }

    private SimpleStringProperty formatDate(LocalDate date) {
        return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadResearchProtocols() {
        researchProtocolData.setAll(researchProtocolService.findAll());
    }

    @FXML
    private void handleSearch() {
        String titleSearch = searchProtocolTitleField.getText().trim();
        String codeSearch = searchProtocolCodeField.getText().trim();
        String projectIdSearch = searchProjectIdField.getText().trim();
        String statusSearch = searchStatusComboBox.getValue();

        Specification<ResearchProtocol> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!titleSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("protocolTitle")), "%" + titleSearch.toLowerCase() + "%"));
            }
            if (!codeSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("protocolCode")), "%" + codeSearch.toLowerCase() + "%"));
            }
            if (!projectIdSearch.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("projectId"), Integer.parseInt(projectIdSearch)));
                } catch (NumberFormatException e) { /* Ignore */ }
            }
            if (statusSearch != null && !"全部".equals(statusSearch)) {
                predicates.add(cb.equal(root.get("status"), statusSearch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        researchProtocolData.setAll(researchProtocolService.findAll(spec));
    }

    @FXML
    private void handleRefresh() {
        searchProtocolTitleField.clear();
        searchProtocolCodeField.clear();
        searchProjectIdField.clear();
        searchStatusComboBox.setValue("全部");
        loadResearchProtocols();
        hideForm();
    }

    @FXML
    private void handleAddProtocol() {
        currentResearchProtocol = null;
        clearForm();
        formTitleLabel.setText("新增研究方案");
        auditFieldsContainer.setVisible(false);
        auditFieldsContainer.setManaged(false);
        auditFieldsContainer2.setVisible(false);
        auditFieldsContainer2.setManaged(false);
        showForm();
    }

    @FXML
    private void handleEditProtocol() {
        ResearchProtocol selected = researchProtocolTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentResearchProtocol = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑研究方案");
            auditFieldsContainer.setVisible(true);
            auditFieldsContainer.setManaged(true);
            auditFieldsContainer2.setVisible(true);
            auditFieldsContainer2.setManaged(true);
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的研究方案。");
        }
    }

    @FXML
    private void handleDeleteProtocol() {
        ResearchProtocol selected = researchProtocolTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的研究方案吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        researchProtocolService.deleteById(selected.getProtocolId());
                        loadResearchProtocols();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "研究方案已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除研究方案: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的研究方案。");
        }
    }

    @FXML
    private void handleSaveProtocol() {
        if (!validateForm()) return;

        ResearchProtocol protocolToSave = (currentResearchProtocol == null) ? new ResearchProtocol() : currentResearchProtocol;
        protocolToSave.setProtocolTitle(formProtocolTitleField.getText());
        protocolToSave.setProtocolCode(formProtocolCodeField.getText());
        Project selectedProject = formProjectComboBox.getValue();
        if (selectedProject != null) {
            protocolToSave.setProjectId(selectedProject.getProjectId());
            protocolToSave.setProject(selectedProject);
        } else {
            protocolToSave.setProjectId(null);
            protocolToSave.setProject(null);
        }
        protocolToSave.setVersion(formVersionField.getText());
        protocolToSave.setStatus(formStatusComboBox.getValue());
        protocolToSave.setApprovalDate(formApprovalDatePicker.getValue());
        protocolToSave.setEffectiveDate(formEffectiveDatePicker.getValue());
        protocolToSave.setExpiryDate(formExpiryDatePicker.getValue());
        try {
            protocolToSave.setPrincipalInvestigatorId(formPrincipalInvestigatorIdField.getText().isEmpty() ? null : Integer.parseInt(formPrincipalInvestigatorIdField.getText()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "主要研究者ID必须是有效的数字。");
            return;
        }
        protocolToSave.setSponsor(formSponsorField.getText());
        protocolToSave.setObjectives(formObjectivesArea.getText());
        protocolToSave.setMethodology(formMethodologyArea.getText());
        protocolToSave.setInclusionCriteria(formInclusionCriteriaArea.getText());
        protocolToSave.setExclusionCriteria(formExclusionCriteriaArea.getText());
        protocolToSave.setEthicalConsiderations(formEthicalConsiderationsArea.getText());

        try {
            researchProtocolService.save(protocolToSave);
            loadResearchProtocols();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "研究方案信息已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存研究方案: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(ResearchProtocol protocol) {
        protocolIdField.setText(protocol.getProtocolId() != null ? protocol.getProtocolId().toString() : "");
        formProtocolTitleField.setText(protocol.getProtocolTitle());
        formProtocolCodeField.setText(protocol.getProtocolCode());
        formProjectComboBox.setValue(protocol.getProject());
        formVersionField.setText(protocol.getVersion());
        formStatusComboBox.setValue(protocol.getStatus());
        formApprovalDatePicker.setValue(protocol.getApprovalDate());
        formEffectiveDatePicker.setValue(protocol.getEffectiveDate());
        formExpiryDatePicker.setValue(protocol.getExpiryDate());
        formPrincipalInvestigatorIdField.setText(protocol.getPrincipalInvestigatorId() != null ? protocol.getPrincipalInvestigatorId().toString() : "");
        formSponsorField.setText(protocol.getSponsor());
        formObjectivesArea.setText(protocol.getObjectives());
        formMethodologyArea.setText(protocol.getMethodology());
        formInclusionCriteriaArea.setText(protocol.getInclusionCriteria());
        formExclusionCriteriaArea.setText(protocol.getExclusionCriteria());
        formEthicalConsiderationsArea.setText(protocol.getEthicalConsiderations());
        formCreatedByField.setText(protocol.getCreatedBy() != null ? protocol.getCreatedBy().toString() : "");
        formCreatedAtField.setText(protocol.getCreatedAt() != null ? protocol.getCreatedAt().format(dateTimeFormatter) : "");
        formUpdatedByField.setText(protocol.getUpdatedBy() != null ? protocol.getUpdatedBy().toString() : "");
        formUpdatedAtField.setText(protocol.getUpdatedAt() != null ? protocol.getUpdatedAt().format(dateTimeFormatter) : "");
    }

    private void clearForm() {
        protocolIdField.clear();
        formProtocolTitleField.clear();
        formProtocolCodeField.clear();
        formProjectComboBox.getSelectionModel().clearSelection();
        formVersionField.clear();
        formStatusComboBox.getSelectionModel().clearSelection();
        formApprovalDatePicker.setValue(null);
        formEffectiveDatePicker.setValue(null);
        formExpiryDatePicker.setValue(null);
        formPrincipalInvestigatorIdField.clear();
        formSponsorField.clear();
        formObjectivesArea.clear();
        formMethodologyArea.clear();
        formInclusionCriteriaArea.clear();
        formExclusionCriteriaArea.clear();
        formEthicalConsiderationsArea.clear();
        formCreatedByField.clear();
        formCreatedAtField.clear();
        formUpdatedByField.clear();
        formUpdatedAtField.clear();
    }

    private boolean validateForm() {
        if (formProtocolTitleField.getText().isEmpty() || formProtocolCodeField.getText().isEmpty() || formProjectComboBox.getValue() == null || formStatusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "方案标题、方案编号、关联项目和状态不能为空。");
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
        currentResearchProtocol = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

