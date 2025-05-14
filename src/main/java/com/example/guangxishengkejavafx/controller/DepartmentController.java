package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Department;
import com.example.guangxishengkejavafx.model.entity.Institution;
import com.example.guangxishengkejavafx.service.DepartmentService;
import com.example.guangxishengkejavafx.service.InstitutionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DepartmentController {

    @FXML
    private ComboBox<Institution> institutionComboBox;
    @FXML
    private TableView<Department> departmentTableView;
    @FXML
    private TableColumn<Department, Integer> idColumn;
    @FXML
    private TableColumn<Department, String> nameColumn;
    @FXML
    private TableColumn<Department, String> descriptionColumn;
    @FXML
    private TableColumn<Department, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Department, LocalDateTime> updatedAtColumn;

    @FXML
    private TextField departmentNameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label statusLabel;

    private final DepartmentService departmentService;
    private final InstitutionService institutionService;
    private final ObservableList<Department> departmentList = FXCollections.observableArrayList();
    private final ObservableList<Institution> institutionObservableList = FXCollections.observableArrayList();

    @Autowired
    public DepartmentController(DepartmentService departmentService, InstitutionService institutionService) {
        this.departmentService = departmentService;
        this.institutionService = institutionService;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        departmentTableView.setItems(departmentList);
        setupInstitutionComboBox();
        loadInstitutionsForComboBox();

        // Add listener for table selection to populate form fields
        departmentTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );
    }

    private void setupInstitutionComboBox() {
        institutionComboBox.setItems(institutionObservableList);
        institutionComboBox.setConverter(new StringConverter<Institution>() {
            @Override
            public String toString(Institution institution) {
                return institution == null ? null : institution.getInstitutionName();
            }

            @Override
            public Institution fromString(String string) {
                return institutionObservableList.stream()
                        .filter(institution -> institution.getInstitutionName().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void loadInstitutionsForComboBox() {
        try {
            List<Institution> institutions = institutionService.getAllInstitutions();
            institutionObservableList.setAll(institutions);
            if (!institutions.isEmpty()) {
                institutionComboBox.getSelectionModel().selectFirst();
                handleInstitutionSelection(null); // Load departments for the first institution
            }
        } catch (Exception e) {
            statusLabel.setText("加载机构列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleInstitutionSelection(ActionEvent event) {
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
        if (selectedInstitution != null) {
            loadDepartments(selectedInstitution.getInstitutionId());
        } else {
            departmentList.clear();
        }
        clearForm();
    }

    private void loadDepartments(Integer institutionId) {
        try {
            List<Department> departments = departmentService.getDepartmentsByInstitutionId(institutionId);
            departmentList.setAll(departments);
            statusLabel.setText("部门列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载部门列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Department department) {
        if (department != null) {
            departmentNameField.setText(department.getDepartmentName());
            descriptionField.setText(department.getDescription());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        departmentNameField.clear();
        descriptionField.clear();
        departmentTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddDepartment(ActionEvent event) {
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
        if (selectedInstitution == null) {
            statusLabel.setText("请先选择一个机构。");
            return;
        }

        String name = departmentNameField.getText();
        String description = descriptionField.getText();

        if (name.isEmpty()) {
            statusLabel.setText("部门名称不能为空。");
            return;
        }

        Department newDepartment = new Department();
        newDepartment.setDepartmentName(name);
        newDepartment.setDescription(description);
        newDepartment.setInstitutionId(selectedInstitution.getInstitutionId());

        try {
            departmentService.saveDepartment(newDepartment);
            statusLabel.setText("部门添加成功！");
            loadDepartments(selectedInstitution.getInstitutionId());
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加部门失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateDepartment(ActionEvent event) {
        Department selectedDepartment = departmentTableView.getSelectionModel().getSelectedItem();
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();

        if (selectedDepartment == null) {
            statusLabel.setText("请先选择一个部门进行修改。");
            return;
        }
        if (selectedInstitution == null) {
            statusLabel.setText("请选择部门所属的机构。"); // Should not happen if table is populated
            return;
        }

        String name = departmentNameField.getText();
        String description = descriptionField.getText();

        if (name.isEmpty()) {
            statusLabel.setText("部门名称不能为空。");
            return;
        }

        Department updatedDetails = new Department();
        updatedDetails.setDepartmentName(name);
        updatedDetails.setDescription(description);
        updatedDetails.setInstitutionId(selectedInstitution.getInstitutionId()); // Institution ID should not change typically

        try {
            departmentService.updateDepartment(selectedDepartment.getDepartmentId(), updatedDetails);
            statusLabel.setText("部门更新成功！");
            loadDepartments(selectedInstitution.getInstitutionId());
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新部门失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteDepartment(ActionEvent event) {
        Department selectedDepartment = departmentTableView.getSelectionModel().getSelectedItem();
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();

        if (selectedDepartment == null) {
            statusLabel.setText("请先选择一个部门进行删除。");
            return;
        }
        if (selectedInstitution == null) {
             statusLabel.setText("请选择部门所属的机构。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除部门: " + selectedDepartment.getDepartmentName());
        alert.setContentText("您确定要删除这个部门吗？此操作无法撤销，且可能影响关联的人员。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                departmentService.deleteDepartment(selectedDepartment.getDepartmentId());
                statusLabel.setText("部门删除成功！");
                loadDepartments(selectedInstitution.getInstitutionId());
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除部门失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshDepartments(ActionEvent event) {
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
        if (selectedInstitution != null) {
            loadDepartments(selectedInstitution.getInstitutionId());
        }
        clearForm();
        statusLabel.setText("部门列表已刷新。");
    }
}

