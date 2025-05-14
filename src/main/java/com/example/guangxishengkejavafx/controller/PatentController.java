package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Patent;
import com.example.guangxishengkejavafx.service.PatentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PatentController {

    @Autowired
    private PatentService patentService;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Patent> patentTableView;
    @FXML
    private TableColumn<Patent, Long> patentIdColumn;
    @FXML
    private TableColumn<Patent, String> patentNameColumn;
    @FXML
    private TableColumn<Patent, String> patentNumberColumn;
    @FXML
    private TableColumn<Patent, String> applicantsColumn;
    @FXML
    private TableColumn<Patent, String> inventorsColumn;
    @FXML
    private TableColumn<Patent, LocalDate> applicationDateColumn;
    @FXML
    private TableColumn<Patent, LocalDate> authorizationDateColumn;
    @FXML
    private TableColumn<Patent, String> patentTypeColumn;
    @FXML
    private TableColumn<Patent, String> legalStatusColumn;
    @FXML
    private TableColumn<Patent, Long> projectIdColumn;
    @FXML
    private TableColumn<Patent, String> attachmentPathColumn;

    @FXML
    private TextField patentNameField;
    @FXML
    private TextField patentNumberField;
    @FXML
    private TextField applicantsField;
    @FXML
    private TextField inventorsField;
    @FXML
    private DatePicker applicationDateField;
    @FXML
    private DatePicker authorizationDateField;
    @FXML
    private TextField patentTypeField;
    @FXML
    private ComboBox<String> legalStatusComboBox;
    @FXML
    private TextArea abstractContentArea;
    @FXML
    private TextField keywordsField;
    @FXML
    private TextField projectIdField;
    @FXML
    private TextArea remarksArea;
    @FXML
    private TextField attachmentPathField;
    @FXML
    private Button downloadAttachmentButton;

    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;

    private ObservableList<Patent> patentList = FXCollections.observableArrayList();
    private Patent currentPatent;
    private File selectedAttachmentFile;

    private static final String ATTACHMENT_DIR = "/home/ubuntu/attachments/patents/";

    @FXML
    public void initialize() {
        patentIdColumn.setCellValueFactory(new PropertyValueFactory<>("patentId"));
        patentNameColumn.setCellValueFactory(new PropertyValueFactory<>("patentName"));
        patentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("patentNumber"));
        applicantsColumn.setCellValueFactory(new PropertyValueFactory<>("applicants"));
        inventorsColumn.setCellValueFactory(new PropertyValueFactory<>("inventors"));
        applicationDateColumn.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));
        authorizationDateColumn.setCellValueFactory(new PropertyValueFactory<>("authorizationDate"));
        patentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("patentType"));
        legalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("legalStatus"));
        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        attachmentPathColumn.setCellValueFactory(new PropertyValueFactory<>("attachmentPath"));

        // Populate legal status ComboBox (example values)
        legalStatusComboBox.setItems(FXCollections.observableArrayList("申请中", "已授权", "审查中", "已失效", "已放弃"));

        loadPatents();

        patentTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
                currentPatent = newSelection;
                setFormEditable(false);
                saveButton.setDisable(true);
                deleteButton.setDisable(false);
                cancelButton.setDisable(true);
                downloadAttachmentButton.setDisable(currentPatent.getAttachmentPath() == null || currentPatent.getAttachmentPath().isEmpty());
            }
        });
        
        // Create attachment directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(ATTACHMENT_DIR));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "无法创建附件目录: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPatents() {
        patentList.setAll(patentService.findAllPatents());
        patentTableView.setItems(patentList);
    }

    private void populateForm(Patent patent) {
        patentNameField.setText(patent.getPatentName());
        patentNumberField.setText(patent.getPatentNumber());
        applicantsField.setText(patent.getApplicants());
        inventorsField.setText(patent.getInventors());
        applicationDateField.setValue(patent.getApplicationDate());
        authorizationDateField.setValue(patent.getAuthorizationDate());
        patentTypeField.setText(patent.getPatentType());
        legalStatusComboBox.setValue(patent.getLegalStatus());
        abstractContentArea.setText(patent.getAbstractContent());
        keywordsField.setText(patent.getKeywords());
        projectIdField.setText(patent.getProjectId() != null ? patent.getProjectId().toString() : "");
        remarksArea.setText(patent.getRemarks());
        attachmentPathField.setText(patent.getAttachmentPath());
        selectedAttachmentFile = null; // Reset selected file when populating from existing record
        downloadAttachmentButton.setDisable(patent.getAttachmentPath() == null || patent.getAttachmentPath().isEmpty());
    }

    private void clearForm() {
        patentNameField.clear();
        patentNumberField.clear();
        applicantsField.clear();
        inventorsField.clear();
        applicationDateField.setValue(null);
        authorizationDateField.setValue(null);
        patentTypeField.clear();
        legalStatusComboBox.getSelectionModel().clearSelection();
        abstractContentArea.clear();
        keywordsField.clear();
        projectIdField.clear();
        remarksArea.clear();
        attachmentPathField.clear();
        currentPatent = null;
        selectedAttachmentFile = null;
        setFormEditable(true);
        saveButton.setDisable(false);
        deleteButton.setDisable(true);
        cancelButton.setDisable(false);
        downloadAttachmentButton.setDisable(true);
    }

    private void setFormEditable(boolean editable) {
        patentNameField.setEditable(editable);
        patentNumberField.setEditable(editable);
        applicantsField.setEditable(editable);
        inventorsField.setEditable(editable);
        applicationDateField.setDisable(!editable);
        authorizationDateField.setDisable(!editable);
        patentTypeField.setEditable(editable);
        legalStatusComboBox.setDisable(!editable);
        abstractContentArea.setEditable(editable);
        keywordsField.setEditable(editable);
        projectIdField.setEditable(editable);
        remarksArea.setEditable(editable);
        // Attachment path field is never directly editable by user
    }

    @FXML
    private void handleNewPatent() {
        clearForm();
        patentTableView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSavePatent() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = currentPatent == null || currentPatent.getPatentId() == null;
        Patent patentToSave = isNew ? new Patent() : currentPatent;

        patentToSave.setPatentName(patentNameField.getText());
        patentToSave.setPatentNumber(patentNumberField.getText());
        patentToSave.setApplicants(applicantsField.getText());
        patentToSave.setInventors(inventorsField.getText());
        patentToSave.setApplicationDate(applicationDateField.getValue());
        patentToSave.setAuthorizationDate(authorizationDateField.getValue());
        patentToSave.setPatentType(patentTypeField.getText());
        patentToSave.setLegalStatus(legalStatusComboBox.getValue());
        patentToSave.setAbstractContent(abstractContentArea.getText());
        patentToSave.setKeywords(keywordsField.getText());
        try {
            if (projectIdField.getText() != null && !projectIdField.getText().isEmpty()) {
                patentToSave.setProjectId(Long.parseLong(projectIdField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert("输入错误", "项目ID必须是数字。", Alert.AlertType.ERROR);
            return;
        }
        patentToSave.setRemarks(remarksArea.getText());

        // Handle attachment
        if (selectedAttachmentFile != null) {
            try {
                Path targetDir = Paths.get(ATTACHMENT_DIR);
                Files.createDirectories(targetDir); // Ensure directory exists
                String fileName = selectedAttachmentFile.getName();
                Path targetPath = targetDir.resolve(fileName);
                Files.copy(selectedAttachmentFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                patentToSave.setAttachmentPath(targetPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("文件错误", "保存附件失败: " + e.getMessage(), Alert.AlertType.ERROR);
                return;
            }
        } else if (attachmentPathField.getText() == null || attachmentPathField.getText().isEmpty()) {
             patentToSave.setAttachmentPath(null); // Clear attachment if field is empty and no new file selected
        }
        // If attachmentPathField has a value and selectedAttachmentFile is null, it means user didn't change the existing attachment.

        try {
            Patent savedPatent = patentService.savePatent(patentToSave);
            if (isNew) {
                patentList.add(savedPatent);
            } else {
                int index = patentList.indexOf(currentPatent);
                if (index >= 0) {
                    patentList.set(index, savedPatent);
                }
            }
            patentTableView.getSelectionModel().select(savedPatent);
            showAlert("成功", "专利信息已保存。", Alert.AlertType.INFORMATION);
            setFormEditable(false);
            saveButton.setDisable(true);
            deleteButton.setDisable(false);
            cancelButton.setDisable(true);
        } catch (IllegalArgumentException e) {
            showAlert("保存失败", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "保存专利信息失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeletePatent() {
        if (currentPatent == null) {
            showAlert("未选择", "请先选择一个专利进行删除。", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除专利: " + currentPatent.getPatentName());
        alert.setContentText("您确定要删除此专利吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete attachment file if it exists
                if (currentPatent.getAttachmentPath() != null && !currentPatent.getAttachmentPath().isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(currentPatent.getAttachmentPath()));
                    } catch (Exception e) {
                        // Log or show a warning, but proceed with DB deletion
                        System.err.println("Failed to delete attachment file: " + currentPatent.getAttachmentPath() + " - " + e.getMessage());
                    }
                }
                patentService.deletePatent(currentPatent.getPatentId());
                patentList.remove(currentPatent);
                clearForm();
                showAlert("成功", "专利已删除。", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("错误", "删除专利失败: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancelEdit() {
        if (currentPatent != null) {
            populateForm(currentPatent);
            setFormEditable(false);
            saveButton.setDisable(true);
            deleteButton.setDisable(false);
            cancelButton.setDisable(true);
        } else {
            clearForm();
        }
    }

    @FXML
    private void handleSearchPatent() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            patentTableView.setItems(patentList);
            return;
        }
        List<Patent> filteredList = patentList.stream()
                .filter(p -> (p.getPatentName() != null && p.getPatentName().toLowerCase().contains(query)) ||
                             (p.getPatentNumber() != null && p.getPatentNumber().toLowerCase().contains(query)) ||
                             (p.getKeywords() != null && p.getKeywords().toLowerCase().contains(query)))
                .collect(Collectors.toList());
        patentTableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        patentTableView.setItems(patentList);
    }

    @FXML
    private void handleSelectAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择专利附件");
        // Optionally set extension filters
        // fileChooser.getExtensionFilters().addAll(
        // new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
        // new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
        // new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(patentTableView.getScene().getWindow());
        if (file != null) {
            selectedAttachmentFile = file;
            attachmentPathField.setText(selectedAttachmentFile.getAbsolutePath());
            if (currentPatent == null || currentPatent.getPatentId() == null) { // New record or editing existing but changing attachment
                 saveButton.setDisable(false); // Enable save if a new attachment is selected for a new or existing record
            }
            downloadAttachmentButton.setDisable(true); // Disable download for unsaved/newly selected file
        }
    }

    @FXML
    private void handleClearAttachment() {
        selectedAttachmentFile = null;
        attachmentPathField.clear();
        if (currentPatent != null) { // If editing an existing record, clearing means we want to remove the attachment
            currentPatent.setAttachmentPath(null); // Mark for removal on save
        }
        saveButton.setDisable(false); // Enable save as attachment state changed
        downloadAttachmentButton.setDisable(true);
    }

    @FXML
    private void handleDownloadAttachment() {
        if (currentPatent == null || currentPatent.getAttachmentPath() == null || currentPatent.getAttachmentPath().isEmpty()) {
            showAlert("无附件", "当前专利没有附件可供下载。", Alert.AlertType.INFORMATION);
            return;
        }
        Path sourcePath = Paths.get(currentPatent.getAttachmentPath());
        if (!Files.exists(sourcePath)) {
            showAlert("文件未找到", "附件文件不存在: " + currentPatent.getAttachmentPath(), Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存附件");
        fileChooser.setInitialFileName(sourcePath.getFileName().toString());
        File saveFile = fileChooser.showSaveDialog(patentTableView.getScene().getWindow());

        if (saveFile != null) {
            try {
                Files.copy(sourcePath, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert("下载成功", "附件已保存到: " + saveFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("下载失败", "保存附件失败: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (patentNameField.getText() == null || patentNameField.getText().trim().isEmpty()) {
            errors.append("专利名称不能为空。\n");
        }
        if (patentNumberField.getText() == null || patentNumberField.getText().trim().isEmpty()) {
            errors.append("专利号不能为空。\n");
        }
        // Add more validations as per original website's requirements
        // e.g., date validation, project ID format, etc.

        if (errors.length() > 0) {
            showAlert("输入验证失败", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

