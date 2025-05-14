package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.ResourceStatistic;
import com.example.guangxishengkejavafx.service.ResourceStatisticService;
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
public class ResourceStatisticController {

    @Autowired
    private ResourceStatisticService resourceStatisticService;

    @FXML
    private TextField searchNameField;
    @FXML
    private ComboBox<String> searchCategoryComboBox;
    @FXML
    private TableView<ResourceStatistic> resourceStatisticTable;
    @FXML
    private TableColumn<ResourceStatistic, Long> statisticIdColumn;
    @FXML
    private TableColumn<ResourceStatistic, String> statisticNameColumn;
    @FXML
    private TableColumn<ResourceStatistic, String> statisticValueColumn;
    @FXML
    private TableColumn<ResourceStatistic, String> statisticCategoryColumn;
    @FXML
    private TableColumn<ResourceStatistic, String> statisticDescriptionColumn;
    @FXML
    private TableColumn<ResourceStatistic, String> lastCalculatedAtColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField statisticIdField;
    @FXML
    private TextField formStatisticNameField;
    @FXML
    private ComboBox<String> formCategoryComboBox;
    @FXML
    private TextArea formDescriptionArea;
    @FXML
    private TextField formStatisticValueField; // Value might be manually set for definition or display only

    private ObservableList<ResourceStatistic> resourceStatisticData = FXCollections.observableArrayList();
    private ResourceStatistic currentStatisticDefinition;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObservableList<String> categoryOptions = FXCollections.observableArrayList("全部", "物料", "细胞库", "样本", "设备", "其他");

    @FXML
    public void initialize() {
        setupTableColumns();
        searchCategoryComboBox.setItems(categoryOptions);
        searchCategoryComboBox.setValue("全部");
        formCategoryComboBox.setItems(FXCollections.observableArrayList("物料", "细胞库", "样本", "设备", "其他")); // Exclude "全部"
        loadResourceStatistics();
        resourceStatisticTable.setItems(resourceStatisticData);
        hideForm();
    }

    private void setupTableColumns() {
        statisticIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statisticNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statisticValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        statisticCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statisticDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        lastCalculatedAtColumn.setCellValueFactory(cellData -> formatDateTime(cellData.getValue().getLastCalculatedAt()));
    }

    private SimpleStringProperty formatDateTime(LocalDateTime dateTime) {
        return new SimpleStringProperty(dateTime != null ? dateTime.format(dateTimeFormatter) : "");
    }

    private void loadResourceStatistics() {
        resourceStatisticData.setAll(resourceStatisticService.findAll());
    }

    @FXML
    private void handleSearch() {
        String nameSearch = searchNameField.getText().trim();
        String categorySearch = searchCategoryComboBox.getValue();

        Specification<ResourceStatistic> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!nameSearch.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + nameSearch.toLowerCase() + "%"));
            }
            if (categorySearch != null && !"全部".equals(categorySearch)) {
                predicates.add(cb.equal(root.get("category"), categorySearch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        resourceStatisticData.setAll(resourceStatisticService.findAll(spec));
    }

    @FXML
    private void handleRefreshTable() {
        searchNameField.clear();
        searchCategoryComboBox.setValue("全部");
        loadResourceStatistics();
        hideForm();
    }

    @FXML
    private void handleRefreshAllStats() {
        try {
            resourceStatisticService.refreshAllStatistics();
            loadResourceStatistics(); // Reload to show updated values
            showAlert(Alert.AlertType.INFORMATION, "刷新成功", "所有统计数据已刷新。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "刷新失败", "刷新统计数据时发生错误: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStatisticDefinition() {
        currentStatisticDefinition = null;
        clearForm();
        formTitleLabel.setText("新增统计项定义");
        formStatisticValueField.setPromptText("(通常自动计算)");
        formStatisticValueField.setDisable(true); // Value is typically calculated
        showForm();
    }

    @FXML
    private void handleEditStatisticDefinition() {
        ResourceStatistic selected = resourceStatisticTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentStatisticDefinition = selected;
            populateForm(selected);
            formTitleLabel.setText("编辑统计项定义");
            formStatisticValueField.setDisable(false); // Allow editing value for definition if needed, or keep disabled
            formStatisticValueField.setPromptText("(当前值，可手动调整或自动刷新)");
            showForm();
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的统计项定义。");
        }
    }

    @FXML
    private void handleDeleteStatisticDefinition() {
        ResourceStatistic selected = resourceStatisticTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的统计项定义吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        resourceStatisticService.deleteById(selected.getId());
                        loadResourceStatistics();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "统计项定义已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除统计项定义: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的统计项定义。");
        }
    }

    @FXML
    private void handleSaveStatistic() {
        if (!validateForm()) return;

        ResourceStatistic statToSave = (currentStatisticDefinition == null) ? new ResourceStatistic() : currentStatisticDefinition;
        statToSave.setName(formStatisticNameField.getText());
        statToSave.setCategory(formCategoryComboBox.getValue());
        statToSave.setDescription(formDescriptionArea.getText());
        // If allowing manual value override for definition, otherwise this should be from calculation
        if (!formStatisticValueField.isDisabled()) {
             statToSave.setValue(formStatisticValueField.getText());
        }
        // lastCalculatedAt is set by service during save/refresh

        try {
            resourceStatisticService.save(statToSave);
            loadResourceStatistics();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "统计项定义已保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存统计项定义: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelForm() {
        hideForm();
    }

    private void populateForm(ResourceStatistic stat) {
        statisticIdField.setText(stat.getId() != null ? stat.getId().toString() : "");
        formStatisticNameField.setText(stat.getName());
        formCategoryComboBox.setValue(stat.getCategory());
        formDescriptionArea.setText(stat.getDescription());
        formStatisticValueField.setText(stat.getValue());
    }

    private void clearForm() {
        statisticIdField.clear();
        formStatisticNameField.clear();
        formCategoryComboBox.getSelectionModel().clearSelection();
        formDescriptionArea.clear();
        formStatisticValueField.clear();
    }

    private boolean validateForm() {
        if (formStatisticNameField.getText().isEmpty() || formCategoryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "统计名称和分类不能为空。");
            return false;
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
        currentStatisticDefinition = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

