package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Keyword;
import com.example.guangxishengkejavafx.service.KeywordService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @FXML
    private TextField searchKeywordTextField;
    @FXML
    private ComboBox<String> searchCategoryComboBox;
    @FXML
    private TableView<Keyword> keywordTable;
    @FXML
    private TableColumn<Keyword, Long> keywordIdColumn;
    @FXML
    private TableColumn<Keyword, String> keywordTextColumn;
    @FXML
    private TableColumn<Keyword, String> keywordCategoryColumn;
    @FXML
    private TableColumn<Keyword, String> keywordDescriptionColumn;
    @FXML
    private TableColumn<Keyword, Integer> keywordFrequencyColumn;
    @FXML
    private TableColumn<Keyword, String> createdAtColumn;
    @FXML
    private TableColumn<Keyword, String> updatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField keywordIdField;
    @FXML
    private TextField formKeywordTextField;
    @FXML
    private ComboBox<String> formCategoryComboBox;
    @FXML
    private TextArea formDescriptionArea;
    @FXML
    private TextField formFrequencyField;

    private ObservableList<Keyword> keywordData = FXCollections.observableArrayList();
    private Keyword currentKeyword;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Define categories for keywords - these could be loaded from a config or another table in a real app
    private final ObservableList<String> categoryOptions = FXCollections.observableArrayList("全部", "文献", "专利", "临床研究", "通用", "其他");
    private final ObservableList<String> formCategoryOptions = FXCollections.observableArrayList("文献", "专利", "临床研究", "通用", "其他");

    @FXML
    public void initialize() {
        setupTableColumns();
        searchCategoryComboBox.setItems(categoryOptions);
        searchCategoryComboBox.setValue("全部");
        formCategoryComboBox.setItems(formCategoryOptions);
        loadKeywords();
        keywordTable.setItems(keywordData);
        hideForm();
    }

    private void setupTableColumns() {
        keywordIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        keywordTextColumn.setCellValueFactory(new PropertyValueFactory<>("keywordText"));
        keywordCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        keywordDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        keywordFrequencyColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFrequency() != null ? cellData.getValue().getFrequency() : 0).asObject());
        createdAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getUpdatedAt()));
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadKeywords() {
        keywordData.setAll(keywordService.findAll());
    }

    @FXML
    private void handleSearch() {
        String keywordSearch = searchKeywordTextField.getText().trim();
        String categorySearch = searchCategoryComboBox.getValue();

        Specification<Keyword> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!keywordSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("keywordText")), "%" + keywordSearch.toLowerCase() + "%"));
            }
            if (categorySearch != null && !"全部".equals(categorySearch)) {
                predicates.add(cb.equal(root.get("category"), categorySearch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        keywordData.setAll(keywordService.findAll(spec));
    }

    @FXML
    private void handleRefreshTable() {
        searchKeywordTextField.clear();
        searchCategoryComboBox.setValue("全部");
        loadKeywords();
        hideForm();
    }

    @FXML
    private void handleAddKeyword() {
        currentKeyword = null;
        clearForm();
        formTitleLabel.setText("新增 Keyword");
        showForm();
    }

    @FXML
    private void handleEditKeyword() {
        Keyword selected = keywordTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentKeyword = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑 Keyword");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的 Keyword。");
        }
    }

    @FXML
    private void handleDeleteKeyword() {
        Keyword selected = keywordTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的 Keyword 吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        keywordService.deleteById(selected.getId());
                        loadKeywords();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "Keyword 已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除 Keyword: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的 Keyword。");
        }
    }

    @FXML
    private void handleSaveKeyword() {
        if (!validateForm()) return;

        Keyword keywordToSave = (currentKeyword == null) ? new Keyword() : currentKeyword;
        keywordToSave.setKeywordText(formKeywordTextField.getText().trim());
        keywordToSave.setCategory(formCategoryComboBox.getValue());
        keywordToSave.setDescription(formDescriptionArea.getText().trim());
        try {
            keywordToSave.setFrequency(formFrequencyField.getText().isEmpty() ? null : Integer.parseInt(formFrequencyField.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "频率必须是一个有效的数字。");
            return;
        }

        try {
            // Assuming a placeholder userId for now, in a real app this would come from logged-in user context
            Integer currentUserId = 1; 
            keywordService.save(keywordToSave, currentUserId);
            loadKeywords();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "Keyword 已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存 Keyword: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(Keyword keyword) {
        keywordIdField.setText(keyword.getId() != null ? keyword.getId().toString() : "");
        formKeywordTextField.setText(keyword.getKeywordText());
        formCategoryComboBox.setValue(keyword.getCategory());
        formDescriptionArea.setText(keyword.getDescription());
        formFrequencyField.setText(keyword.getFrequency() != null ? keyword.getFrequency().toString() : "");
    }

    private void clearForm() {
        keywordIdField.clear();
        formKeywordTextField.clear();
        formCategoryComboBox.getSelectionModel().clearSelection();
        formDescriptionArea.clear();
        formFrequencyField.clear();
    }

    private boolean validateForm() {
        if (formKeywordTextField.getText().trim().isEmpty() || formCategoryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "关键词和分类不能为空。");
            return false;
        }
        if (!formFrequencyField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(formFrequencyField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "验证错误", "频率必须是一个有效的数字。");
                return false;
            }
        }
        return true;
    }

    private void showForm() {
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentKeyword = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

