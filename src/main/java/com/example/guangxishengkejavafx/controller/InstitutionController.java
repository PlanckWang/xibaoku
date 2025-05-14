package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Institution;
import com.example.guangxishengkejavafx.service.InstitutionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class InstitutionController {

    @FXML
    private TableView<Institution> institutionTableView;
    @FXML
    private TableColumn<Institution, Integer> idColumn;
    @FXML
    private TableColumn<Institution, String> nameColumn;
    @FXML
    private TableColumn<Institution, String> addressColumn;
    @FXML
    private TableColumn<Institution, String> contactPersonColumn;
    @FXML
    private TableColumn<Institution, String> contactPhoneColumn;
    @FXML
    private TableColumn<Institution, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Institution, LocalDateTime> updatedAtColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField contactPersonField;
    @FXML
    private TextField contactPhoneField;
    @FXML
    private Label statusLabel;

    private final InstitutionService institutionService;
    private final ObservableList<Institution> institutionList = FXCollections.observableArrayList();

    @Autowired
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("institutionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("institutionName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactPersonColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        contactPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("contactPhone"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        institutionTableView.setItems(institutionList);
        loadInstitutions();

        // Add listener for table selection to populate form fields
        institutionTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );
    }

    private void loadInstitutions() {
        try {
            List<Institution> institutions = institutionService.getAllInstitutions();
            institutionList.setAll(institutions);
            statusLabel.setText("机构列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载机构列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Institution institution) {
        if (institution != null) {
            nameField.setText(institution.getInstitutionName());
            addressField.setText(institution.getAddress());
            contactPersonField.setText(institution.getContactPerson());
            contactPhoneField.setText(institution.getContactPhone());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
        contactPersonField.clear();
        contactPhoneField.clear();
        institutionTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddInstitution(ActionEvent event) {
        String name = nameField.getText();
        String address = addressField.getText();
        String contactPerson = contactPersonField.getText();
        String contactPhone = contactPhoneField.getText();

        if (name.isEmpty()) {
            statusLabel.setText("机构名称不能为空。");
            return;
        }

        // Check if institution with the same name already exists
        Optional<Institution> existingInstitution = institutionService.getInstitutionByName(name);
        if (existingInstitution.isPresent()) {
            statusLabel.setText("错误：具有该名称的机构已存在。");
            return;
        }

        Institution newInstitution = new Institution();
        newInstitution.setInstitutionName(name);
        newInstitution.setAddress(address);
        newInstitution.setContactPerson(contactPerson);
        newInstitution.setContactPhone(contactPhone);

        try {
            institutionService.saveInstitution(newInstitution);
            statusLabel.setText("机构添加成功！");
            loadInstitutions();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加机构失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateInstitution(ActionEvent event) {
        Institution selectedInstitution = institutionTableView.getSelectionModel().getSelectedItem();
        if (selectedInstitution == null) {
            statusLabel.setText("请先选择一个机构进行修改。");
            return;
        }

        String name = nameField.getText();
        String address = addressField.getText();
        String contactPerson = contactPersonField.getText();
        String contactPhone = contactPhoneField.getText();

        if (name.isEmpty()) {
            statusLabel.setText("机构名称不能为空。");
            return;
        }

        // Check if another institution (not the selected one) has the same name
        Optional<Institution> existingInstitution = institutionService.getInstitutionByName(name);
        if (existingInstitution.isPresent() && !existingInstitution.get().getInstitutionId().equals(selectedInstitution.getInstitutionId())) {
            statusLabel.setText("错误：具有该名称的其他机构已存在。");
            return;
        }

        Institution updatedDetails = new Institution();
        updatedDetails.setInstitutionName(name);
        updatedDetails.setAddress(address);
        updatedDetails.setContactPerson(contactPerson);
        updatedDetails.setContactPhone(contactPhone);

        try {
            institutionService.updateInstitution(selectedInstitution.getInstitutionId(), updatedDetails);
            statusLabel.setText("机构更新成功！");
            loadInstitutions();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新机构失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteInstitution(ActionEvent event) {
        Institution selectedInstitution = institutionTableView.getSelectionModel().getSelectedItem();
        if (selectedInstitution == null) {
            statusLabel.setText("请先选择一个机构进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除机构: " + selectedInstitution.getInstitutionName());
        alert.setContentText("您确定要删除这个机构吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                institutionService.deleteInstitution(selectedInstitution.getInstitutionId());
                statusLabel.setText("机构删除成功！");
                loadInstitutions();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除机构失败: " + e.getMessage());
                // Potentially check for foreign key constraints or other dependencies
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshInstitutions(ActionEvent event) {
        loadInstitutions();
        clearForm();
        statusLabel.setText("机构列表已刷新。");
    }
}

