package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.ProjectType;
import com.example.guangxishengkejavafx.service.ProjectTypeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class ProjectTypeController {

    @Autowired
    private ProjectTypeService projectTypeService;

    @FXML
    private TextField searchProjectTypeNameField;
    @FXML
    private TableView<ProjectType> projectTypeTable;
    @FXML
    private TableColumn<ProjectType, Integer> projectTypeIdColumn;
    @FXML
    private TableColumn<ProjectType, String> projectTypeNameColumn;
    @FXML
    private TableColumn<ProjectType, String> descriptionColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField projectTypeIdField;
    @FXML
    private TextField projectTypeNameField;
    @FXML
    private TextArea descriptionArea;

    private ObservableList<ProjectType> projectTypeData = FXCollections.observableArrayList();
    private ProjectType currentProjectType;

    @FXML
    public void initialize() {
        projectTypeIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectTypeId"));
        projectTypeNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectTypeName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadProjectTypes();
        projectTypeTable.setItems(projectTypeData);
        hideForm();
    }

    private void loadProjectTypes() {
        projectTypeData.setAll(projectTypeService.findAll());
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchProjectTypeNameField.getText();
        if (searchTerm.isEmpty()) {
            loadProjectTypes();
        } else {
            List<ProjectType> filteredList = projectTypeService.findByProjectTypeNameContaining(searchTerm);
            projectTypeData.setAll(filteredList);
        }
        projectTypeTable.setItems(projectTypeData);
    }

    @FXML
    private void handleRefresh() {
        searchProjectTypeNameField.clear();
        loadProjectTypes();
        hideForm();
    }

    @FXML
    private void handleAddProjectType() {
        currentProjectType = null;
        clearForm();
        formTitleLabel.setText("新增项目类型");
        showForm();
    }

    @FXML
    private void handleEditProjectType() {
        ProjectType selectedType = projectTypeTable.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            currentProjectType = selectedType;
            populateForm(selectedType);
            formTitleLabel.setText("编辑项目类型");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的项目类型。");
        }
    }

    @FXML
    private void handleDeleteProjectType() {
        ProjectType selectedType = projectTypeTable.getSelectionModel().getSelectedItem();
        if (selectedType != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的项目类型吗？\n此操作可能会影响关联的项目。", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        projectTypeService.deleteById(selectedType.getProjectTypeId());
                        loadProjectTypes();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "项目类型已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除项目类型: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的项目类型。");
        }
    }

    @FXML
    private void handleSaveProjectType() {
        if (projectTypeNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "项目类型名称不能为空。");
            return;
        }

        ProjectType typeToSave = (currentProjectType == null) ? new ProjectType() : currentProjectType;
        typeToSave.setProjectTypeName(projectTypeNameField.getText());
        typeToSave.setDescription(descriptionArea.getText());

        try {
            projectTypeService.save(typeToSave);
            loadProjectTypes();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "项目类型信息已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存项目类型: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        hideForm();
    }

    private void populateForm(ProjectType projectType) {
        projectTypeIdField.setText(projectType.getProjectTypeId() != null ? projectType.getProjectTypeId().toString() : "");
        projectTypeNameField.setText(projectType.getProjectTypeName());
        descriptionArea.setText(projectType.getDescription());
    }

    private void clearForm() {
        projectTypeIdField.clear();
        projectTypeNameField.clear();
        descriptionArea.clear();
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentProjectType = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

