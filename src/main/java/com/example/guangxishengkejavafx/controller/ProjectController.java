package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Project;
import com.example.guangxishengkejavafx.model.entity.ProjectType;
import com.example.guangxishengkejavafx.service.ProjectService;
import com.example.guangxishengkejavafx.service.ProjectTypeService;
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
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectTypeService projectTypeService;

    @FXML
    private TextField searchProjectNameField;
    @FXML
    private TextField searchProjectCodeField;
    @FXML
    private ComboBox<ProjectType> searchProjectTypeComboBox;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, Integer> projectIdColumn;
    @FXML
    private TableColumn<Project, String> projectNameColumn;
    @FXML
    private TableColumn<Project, String> projectCodeColumn;
    @FXML
    private TableColumn<Project, String> projectTypeNameColumn;
    @FXML
    private TableColumn<Project, Integer> principalInvestigatorIdColumn;
    @FXML
    private TableColumn<Project, String> startDateColumn;
    @FXML
    private TableColumn<Project, String> endDateColumn;
    @FXML
    private TableColumn<Project, String> statusColumn;
    @FXML
    private TableColumn<Project, Double> budgetColumn;
    @FXML
    private TableColumn<Project, String> createdAtColumn;
    @FXML
    private TableColumn<Project, String> updatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField projectIdField;
    @FXML
    private TextField formProjectNameField;
    @FXML
    private TextField formProjectCodeField;
    @FXML
    private ComboBox<ProjectType> formProjectTypeComboBox;
    @FXML
    private TextField formPrincipalInvestigatorIdField;
    @FXML
    private DatePicker formStartDatePicker;
    @FXML
    private DatePicker formEndDatePicker;
    @FXML
    private ComboBox<String> formStatusComboBox;
    @FXML
    private TextField formBudgetField;
    @FXML
    private TextArea formDescriptionArea;
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

    private ObservableList<Project> projectData = FXCollections.observableArrayList();
    private Project currentProject;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList("进行中", "已完成", "已暂停", "已取消", "待启动");
    private final ObservableList<String> searchStatusOptions = FXCollections.observableArrayList("全部", "进行中", "已完成", "已暂停", "已取消", "待启动");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProjectTypesIntoComboBoxes();
        formStatusComboBox.setItems(statusOptions);
        searchStatusComboBox.setItems(searchStatusOptions);
        searchStatusComboBox.setValue("全部");
        loadProjects();
        projectTable.setItems(projectData);
        hideForm();
    }

    private void setupTableColumns() {
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectCodeColumn.setCellValueFactory(new PropertyValueFactory<>("projectCode"));
        projectTypeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProjectType() != null ? cellData.getValue().getProjectType().getProjectTypeName() : "N/A"));
        principalInvestigatorIdColumn.setCellValueFactory(new PropertyValueFactory<>("principalInvestigatorId"));
        startDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cellData -> formatDate(cellData.getValue().getEndDate()));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        budgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));
        createdAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getUpdatedAt()));
    }

    private void loadProjectTypesIntoComboBoxes() {
        List<ProjectType> types = projectTypeService.findAll();
        ObservableList<ProjectType> projectTypeObservableList = FXCollections.observableArrayList(types);
        searchProjectTypeComboBox.setItems(projectTypeObservableList);
        formProjectTypeComboBox.setItems(projectTypeObservableList);

        // Add a "null" or "All Types" option for search
        ProjectType allTypesOption = new ProjectType();
        allTypesOption.setProjectTypeName("所有类型");
        allTypesOption.setProjectTypeId(null); // Representing no filter
        searchProjectTypeComboBox.getItems().add(0, allTypesOption);
        searchProjectTypeComboBox.setValue(allTypesOption);

        // Converter for displaying names in ComboBox
        javafx.util.StringConverter<ProjectType> converter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(ProjectType object) {
                return object == null ? "" : object.getProjectTypeName();
            }
            @Override
            public ProjectType fromString(String string) {
                return searchProjectTypeComboBox.getItems().stream().filter(pt -> pt.getProjectTypeName().equals(string)).findFirst().orElse(null);
            }
        };
        searchProjectTypeComboBox.setConverter(converter);
        formProjectTypeComboBox.setConverter(converter);
    }

    private SimpleStringProperty formatDate(LocalDate date) {
        return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadProjects() {
        projectData.setAll(projectService.findAll());
    }

    @FXML
    private void handleSearch() {
        String nameSearch = searchProjectNameField.getText().trim();
        String codeSearch = searchProjectCodeField.getText().trim();
        ProjectType typeSearch = searchProjectTypeComboBox.getValue();
        String statusSearch = searchStatusComboBox.getValue();

        Specification<Project> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!nameSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("projectName")), "%" + nameSearch.toLowerCase() + "%"));
            }
            if (!codeSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("projectCode")), "%" + codeSearch.toLowerCase() + "%"));
            }
            if (typeSearch != null && typeSearch.getProjectTypeId() != null) { // Check if not "All Types"
                predicates.add(cb.equal(root.get("projectTypeId"), typeSearch.getProjectTypeId()));
            }
            if (statusSearch != null && !"全部".equals(statusSearch)) {
                predicates.add(cb.equal(root.get("status"), statusSearch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        projectData.setAll(projectService.findAll(spec));
    }

    @FXML
    private void handleRefresh() {
        searchProjectNameField.clear();
        searchProjectCodeField.clear();
        searchProjectTypeComboBox.getSelectionModel().selectFirst(); // Select "All Types"
        searchStatusComboBox.setValue("全部");
        loadProjects();
        hideForm();
    }

    @FXML
    private void handleAddProject() {
        currentProject = null;
        clearForm();
        formTitleLabel.setText("新增项目");
        auditFieldsContainer.setVisible(false);
        auditFieldsContainer.setManaged(false);
        auditFieldsContainer2.setVisible(false);
        auditFieldsContainer2.setManaged(false);
        showForm();
    }

    @FXML
    private void handleEditProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentProject = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑项目");
            auditFieldsContainer.setVisible(true);
            auditFieldsContainer.setManaged(true);
            auditFieldsContainer2.setVisible(true);
            auditFieldsContainer2.setManaged(true);
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的项目。");
        }
    }

    @FXML
    private void handleDeleteProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的项目吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        projectService.deleteById(selected.getProjectId());
                        loadProjects();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "项目已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除项目: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的项目。");
        }
    }

    @FXML
    private void handleSaveProject() {
        if (!validateForm()) return;

        Project projectToSave = (currentProject == null) ? new Project() : currentProject;
        projectToSave.setProjectName(formProjectNameField.getText());
        projectToSave.setProjectCode(formProjectCodeField.getText());
        projectToSave.setProjectType(formProjectTypeComboBox.getValue());
        projectToSave.setProjectTypeId(formProjectTypeComboBox.getValue() != null ? formProjectTypeComboBox.getValue().getProjectTypeId() : null);
        try {
            projectToSave.setPrincipalInvestigatorId(formPrincipalInvestigatorIdField.getText().isEmpty() ? null : Integer.parseInt(formPrincipalInvestigatorIdField.getText()));
            projectToSave.setBudget(formBudgetField.getText().isEmpty() ? null : Double.parseDouble(formBudgetField.getText()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "数据错误", "负责人ID和预算必须是有效的数字。");
            return;
        }
        projectToSave.setStartDate(formStartDatePicker.getValue());
        projectToSave.setEndDate(formEndDatePicker.getValue());
        projectToSave.setStatus(formStatusComboBox.getValue());
        projectToSave.setDescription(formDescriptionArea.getText());

        // CreatedBy/UpdatedBy would typically be set based on logged-in user
        // For now, we'll leave them as potentially null or handle in service if needed

        try {
            projectService.save(projectToSave);
            loadProjects();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "项目信息已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存项目: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(Project project) {
        projectIdField.setText(project.getProjectId() != null ? project.getProjectId().toString() : "");
        formProjectNameField.setText(project.getProjectName());
        formProjectCodeField.setText(project.getProjectCode());
        formProjectTypeComboBox.setValue(project.getProjectType());
        formPrincipalInvestigatorIdField.setText(project.getPrincipalInvestigatorId() != null ? project.getPrincipalInvestigatorId().toString() : "");
        formStartDatePicker.setValue(project.getStartDate());
        formEndDatePicker.setValue(project.getEndDate());
        formStatusComboBox.setValue(project.getStatus());
        formBudgetField.setText(project.getBudget() != null ? project.getBudget().toString() : "");
        formDescriptionArea.setText(project.getDescription());
        formCreatedByField.setText(project.getCreatedBy() != null ? project.getCreatedBy().toString() : "");
        formCreatedAtField.setText(project.getCreatedAt() != null ? project.getCreatedAt().format(dateTimeFormatter) : "");
        formUpdatedByField.setText(project.getUpdatedBy() != null ? project.getUpdatedBy().toString() : "");
        formUpdatedAtField.setText(project.getUpdatedAt() != null ? project.getUpdatedAt().format(dateTimeFormatter) : "");
    }

    private void clearForm() {
        projectIdField.clear();
        formProjectNameField.clear();
        formProjectCodeField.clear();
        formProjectTypeComboBox.getSelectionModel().clearSelection();
        formPrincipalInvestigatorIdField.clear();
        formStartDatePicker.setValue(null);
        formEndDatePicker.setValue(null);
        formStatusComboBox.getSelectionModel().clearSelection();
        formBudgetField.clear();
        formDescriptionArea.clear();
        formCreatedByField.clear();
        formCreatedAtField.clear();
        formUpdatedByField.clear();
        formUpdatedAtField.clear();
    }

    private boolean validateForm() {
        if (formProjectNameField.getText().isEmpty() || formProjectCodeField.getText().isEmpty() || formProjectTypeComboBox.getValue() == null || formStatusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "项目名称、项目编号、项目类型和状态不能为空。");
            return false;
        }
        // Add more specific validations as needed (e.g., date logic, budget format)
        return true;
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentProject = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

