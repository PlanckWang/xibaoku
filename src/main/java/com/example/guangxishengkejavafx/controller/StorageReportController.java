package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.dto.StorageReportEntry;
import com.example.guangxishengkejavafx.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StorageReportController {

    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private Button generateReportButton;
    @FXML
    private Button exportReportButton; // TODO: Implement export functionality
    @FXML
    private TableView<StorageReportEntry> reportTableView;
    @FXML
    private TableColumn<StorageReportEntry, String> categoryColumn;
    @FXML
    private TableColumn<StorageReportEntry, String> subCategoryColumn;
    @FXML
    private TableColumn<StorageReportEntry, Long> countColumn;
    @FXML
    private TableColumn<StorageReportEntry, Double> totalVolumeColumn;
    @FXML
    private TableColumn<StorageReportEntry, String> detailsColumn;

    private final ReportService reportService;
    private ObservableList<StorageReportEntry> reportData = FXCollections.observableArrayList();

    private static final String REPORT_TYPE_PRODUCT = "按产品统计库存";
    private static final String REPORT_TYPE_DEPOSITOR = "按储户统计合同(示例)";
    private static final String REPORT_TYPE_LOCATION = "按存储位置统计库存";

    @Autowired
    public StorageReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        setupReportTypeComboBox();
        setupTableView();
        exportReportButton.setDisable(true); // Disable until report is generated or implement later
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                REPORT_TYPE_PRODUCT,
                REPORT_TYPE_DEPOSITOR,
                REPORT_TYPE_LOCATION
        ));
        reportTypeComboBox.getSelectionModel().selectFirst(); // Default selection
    }

    private void setupTableView() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        subCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("subCategory"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        totalVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        reportTableView.setItems(reportData);
    }

    @FXML
    private void handleGenerateReport() {
        String selectedReportType = reportTypeComboBox.getValue();
        if (selectedReportType == null || selectedReportType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "选择报表类型", "请先选择一个报表类型。");
            return;
        }

        reportData.clear();
        List<StorageReportEntry> entries = null;

        try {
            switch (selectedReportType) {
                case REPORT_TYPE_PRODUCT:
                    entries = reportService.generateProductStorageReport();
                    break;
                case REPORT_TYPE_DEPOSITOR:
                    entries = reportService.generateDepositorStorageReport();
                    break;
                case REPORT_TYPE_LOCATION:
                    entries = reportService.generateStorageLocationReport();
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "未知报表类型", "选择的报表类型无法处理。");
                    return;
            }

            if (entries != null && !entries.isEmpty()) {
                reportData.addAll(entries);
                exportReportButton.setDisable(false); // Enable export once data is loaded
            } else {
                reportTableView.setPlaceholder(new Label("没有找到符合条件的数据生成报表。"));
                exportReportButton.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "报表生成失败", "生成报表时发生错误: " + e.getMessage());
            exportReportButton.setDisable(true);
        }
    }

    @FXML
    private void handleExportReport() {
        // TODO: Implement report export functionality (e.g., to CSV or Excel)
        showAlert(Alert.AlertType.INFORMATION, "功能待实现", "报表导出功能将在后续版本中提供。");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

