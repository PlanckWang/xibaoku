package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.dto.ApplicationReportEntry;
import com.example.guangxishengkejavafx.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ApplicationReportController {

    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button generateReportButton;
    @FXML
    private Button exportReportButton; // TODO: Implement export functionality
    @FXML
    private TableView<ApplicationReportEntry> reportTableView;
    @FXML
    private TableColumn<ApplicationReportEntry, String> reportNameColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, String> itemCategoryColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, String> itemNameColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, Long> countColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, Double> totalQuantityColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, String> unitColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, LocalDate> reportStartDateColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, LocalDate> reportEndDateColumn;
    @FXML
    private TableColumn<ApplicationReportEntry, String> detailsColumn;

    private final ReportService reportService;
    private ObservableList<ApplicationReportEntry> reportData = FXCollections.observableArrayList();

    private static final String REPORT_TYPE_PRODUCT_RELEASE = "产品发放统计";
    private static final String REPORT_TYPE_PREPARATION_PURPOSE = "样本制备用途统计";

    @Autowired
    public ApplicationReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @FXML
    public void initialize() {
        setupReportTypeComboBox();
        setupTableView();
        startDatePicker.setValue(LocalDate.now().minusMonths(1)); // Default to last month
        endDatePicker.setValue(LocalDate.now()); // Default to today
        exportReportButton.setDisable(true); // Disable until report is generated or implement later
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                REPORT_TYPE_PRODUCT_RELEASE,
                REPORT_TYPE_PREPARATION_PURPOSE
        ));
        reportTypeComboBox.getSelectionModel().selectFirst(); // Default selection
    }

    private void setupTableView() {
        reportNameColumn.setCellValueFactory(new PropertyValueFactory<>("reportName"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        reportStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        reportEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        reportTableView.setItems(reportData);
    }

    @FXML
    private void handleGenerateReport() {
        String selectedReportType = reportTypeComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedReportType == null || selectedReportType.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "选择报表类型", "请先选择一个报表类型。");
            return;
        }
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "选择日期范围", "请选择统计的开始日期和结束日期。");
            return;
        }
        if (startDate.isAfter(endDate)) {
            showAlert(Alert.AlertType.WARNING, "日期错误", "开始日期不能晚于结束日期。");
            return;
        }

        reportData.clear();
        List<ApplicationReportEntry> entries = null;

        try {
            switch (selectedReportType) {
                case REPORT_TYPE_PRODUCT_RELEASE:
                    entries = reportService.generateProductReleaseReport(startDate, endDate);
                    break;
                case REPORT_TYPE_PREPARATION_PURPOSE:
                    entries = reportService.generateSamplePreparationPurposeReport(startDate, endDate);
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

