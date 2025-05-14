package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Literature;
import com.example.guangxishengkejavafx.service.LiteratureService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class LiteratureController {

    @Autowired
    private LiteratureService literatureService;

    @FXML
    private TextField searchTitleField;
    @FXML
    private TextField searchAuthorsField;
    @FXML
    private TextField searchKeywordsField;
    @FXML
    private ComboBox<String> searchCategoryComboBox;
    @FXML
    private TextField searchYearField;
    @FXML
    private TableView<Literature> literatureTable;
    @FXML
    private TableColumn<Literature, Long> literatureIdColumn;
    @FXML
    private TableColumn<Literature, String> titleColumn;
    @FXML
    private TableColumn<Literature, String> authorsColumn;
    @FXML
    private TableColumn<Literature, String> journalColumn;
    @FXML
    private TableColumn<Literature, Integer> yearColumn;
    @FXML
    private TableColumn<Literature, String> doiColumn;
    @FXML
    private TableColumn<Literature, String> categoryColumn;
    @FXML
    private TableColumn<Literature, String> addedAtColumn;
    @FXML
    private TableColumn<Literature, String> filePathColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField literatureIdField;
    @FXML
    private TextField formTitleField;
    @FXML
    private TextField formAuthorsField;
    @FXML
    private TextField formJournalField;
    @FXML
    private TextField formYearField;
    @FXML
    private DatePicker formPublicationDateField;
    @FXML
    private TextField formVolumeField;
    @FXML
    private TextField formIssueField;
    @FXML
    private TextField formPagesField;
    @FXML
    private TextField formDoiField;
    @FXML
    private TextArea formAbstractArea;
    @FXML
    private TextField formKeywordsField;
    @FXML
    private TextField formUrlField;
    @FXML
    private TextField formFilePathField;
    @FXML
    private Button browseFileButton;
    @FXML
    private TextArea formNotesArea;
    @FXML
    private ComboBox<String> formCategoryComboBox;

    private ObservableList<Literature> literatureData = FXCollections.observableArrayList();
    private Literature currentLiterature;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> categoryOptions = FXCollections.observableArrayList("全部", "内部研究", "外部发表", "综述", "会议论文", "其他");
    private final ObservableList<String> formCategoryOptions = FXCollections.observableArrayList("内部研究", "外部发表", "综述", "会议论文", "其他");

    @FXML
    public void initialize() {
        setupTableColumns();
        searchCategoryComboBox.setItems(categoryOptions);
        searchCategoryComboBox.setValue("全部");
        formCategoryComboBox.setItems(formCategoryOptions);
        loadLiteratures();
        literatureTable.setItems(literatureData);
        hideForm();
    }

    private void setupTableColumns() {
        literatureIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        journalColumn.setCellValueFactory(new PropertyValueFactory<>("journalOrConference"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        doiColumn.setCellValueFactory(new PropertyValueFactory<>("doi"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        addedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getAddedAt()));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadLiteratures() {
        literatureData.setAll(literatureService.findAll());
    }

    @FXML
    private void handleSearch() {
        String titleSearch = searchTitleField.getText().trim();
        String authorsSearch = searchAuthorsField.getText().trim();
        String keywordsSearch = searchKeywordsField.getText().trim();
        String categorySearch = searchCategoryComboBox.getValue();
        String yearSearch = searchYearField.getText().trim();

        Specification<Literature> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!titleSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + titleSearch.toLowerCase() + "%"));
            }
            if (!authorsSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("authors")), "%" + authorsSearch.toLowerCase() + "%"));
            }
            if (!keywordsSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("keywords")), "%" + keywordsSearch.toLowerCase() + "%"));
            }
            if (categorySearch != null && !"全部".equals(categorySearch)) {
                predicates.add(cb.equal(root.get("category"), categorySearch));
            }
            if (!yearSearch.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("publicationYear"), Integer.parseInt(yearSearch)));
                } catch (NumberFormatException e) {
                    // Ignore if year is not a valid number
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        literatureData.setAll(literatureService.findAll(spec));
    }

    @FXML
    private void handleRefreshTable() {
        searchTitleField.clear();
        searchAuthorsField.clear();
        searchKeywordsField.clear();
        searchCategoryComboBox.setValue("全部");
        searchYearField.clear();
        loadLiteratures();
        hideForm();
    }

    @FXML
    private void handleAddLiterature() {
        currentLiterature = null;
        clearForm();
        formTitleLabel.setText("新增文献");
        showForm();
    }

    @FXML
    private void handleEditLiterature() {
        Literature selected = literatureTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentLiterature = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑文献");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的文献。");
        }
    }

    @FXML
    private void handleDeleteLiterature() {
        Literature selected = literatureTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的文献吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        literatureService.deleteById(selected.getId());
                        loadLiteratures();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "文献已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除文献: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的文献。");
        }
    }

    @FXML
    private void handleSaveLiterature() {
        if (!validateForm()) return;

        Literature literatureToSave = (currentLiterature == null) ? new Literature() : currentLiterature;
        literatureToSave.setTitle(formTitleField.getText().trim());
        literatureToSave.setAuthors(formAuthorsField.getText().trim());
        literatureToSave.setJournalOrConference(formJournalField.getText().trim());
        try {
            if (!formYearField.getText().trim().isEmpty()) {
                literatureToSave.setPublicationYear(Integer.parseInt(formYearField.getText().trim()));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "发表年份必须是一个有效的数字。");
            return;
        }
        literatureToSave.setPublicationDate(formPublicationDateField.getValue());
        literatureToSave.setVolume(formVolumeField.getText().trim());
        literatureToSave.setIssue(formIssueField.getText().trim());
        literatureToSave.setPages(formPagesField.getText().trim());
        literatureToSave.setDoi(formDoiField.getText().trim());
        literatureToSave.setAbstractText(formAbstractArea.getText().trim());
        literatureToSave.setKeywords(formKeywordsField.getText().trim());
        literatureToSave.setUrl(formUrlField.getText().trim());
        literatureToSave.setFilePath(formFilePathField.getText().trim());
        literatureToSave.setNotes(formNotesArea.getText().trim());
        literatureToSave.setCategory(formCategoryComboBox.getValue());

        try {
            // Assuming a placeholder userId for now
            Integer currentUserId = 1; 
            literatureService.save(literatureToSave, currentUserId);
            loadLiteratures();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "文献已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存文献: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文献文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(formPane.getScene().getWindow());
        if (selectedFile != null) {
            formFilePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleViewLiteratureFile() {
        Literature selected = literatureTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getFilePath() != null && !selected.getFilePath().isEmpty()) {
            try {
                File file = new File(selected.getFilePath());
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert(Alert.AlertType.ERROR, "文件错误", "文件未找到: " + selected.getFilePath());
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "打开文件失败", "无法打开文件: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "无文件", "未选择文献或文献没有关联文件。");
        }
    }

    private void populateForm(Literature literature) {
        literatureIdField.setText(literature.getId() != null ? literature.getId().toString() : "");
        formTitleField.setText(literature.getTitle());
        formAuthorsField.setText(literature.getAuthors());
        formJournalField.setText(literature.getJournalOrConference());
        formYearField.setText(literature.getPublicationYear() != null ? literature.getPublicationYear().toString() : "");
        formPublicationDateField.setValue(literature.getPublicationDate());
        formVolumeField.setText(literature.getVolume());
        formIssueField.setText(literature.getIssue());
        formPagesField.setText(literature.getPages());
        formDoiField.setText(literature.getDoi());
        formAbstractArea.setText(literature.getAbstractText());
        formKeywordsField.setText(literature.getKeywords());
        formUrlField.setText(literature.getUrl());
        formFilePathField.setText(literature.getFilePath());
        formNotesArea.setText(literature.getNotes());
        formCategoryComboBox.setValue(literature.getCategory());
    }

    private void clearForm() {
        literatureIdField.clear();
        formTitleField.clear();
        formAuthorsField.clear();
        formJournalField.clear();
        formYearField.clear();
        formPublicationDateField.setValue(null);
        formVolumeField.clear();
        formIssueField.clear();
        formPagesField.clear();
        formDoiField.clear();
        formAbstractArea.clear();
        formKeywordsField.clear();
        formUrlField.clear();
        formFilePathField.clear();
        formNotesArea.clear();
        formCategoryComboBox.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (formTitleField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "文献标题不能为空。");
            return false;
        }
        if (!formYearField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(formYearField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "发表年份必须是一个有效的数字。");
                return false;
            }
        }
        // Add more validation as needed (e.g., DOI format, URL format)
        return true;
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentLiterature = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

