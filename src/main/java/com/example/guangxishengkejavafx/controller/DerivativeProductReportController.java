package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.dto.DerivativeProductReportEntry;
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
public class DerivativeProductReportController {

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
    private TableView<DerivativeProductReportEntry> reportTableView;

    @FXML
    private TableColumn<DerivativeProductReportEntry, String> reportNameColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> derivativeProductNameColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> derivativeProductTypeColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> sourceSampleIdColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> sourceSampleTypeColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, Long> quantityProducedColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, Double> totalAmountColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> amountUnitColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, LocalDate> creationDateStartColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, LocalDate> creationDateEndColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> createdByColumn;
    @FXML
    private TableColumn<DerivativeProductReportEntry, String> detailsColumn;

    private final ReportService reportService;
    private ObservableList<DerivativeProductReportEntry> reportData = FXCollections.observableArrayList();

    private static final String REPORT_TYPE_DERIVATIVE_TYPE = "衍生品类型统计";
    private static final String REPORT_TYPE_DERIVATIVE_PRODUCTION = "特定衍生品产量统计";

    @Autowired
    public DerivativeProductReportController(ReportService reportService) {
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
                REPORT_TYPE_DERIVATIVE_TYPE,
                REPORT_TYPE_DERIVATIVE_PRODUCTION
        ));
        reportTypeComboBox.getSelectionModel().selectFirst(); // Default selection
    }

    private void setupTableView() {
        reportNameColumn.setCellValueFactory(new PropertyValueFactory<>("reportName"));
        derivativeProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("derivativeProductName"));
        derivativeProductTypeColumn.setCellValueFactory(new PropertyValueFactory<>("derivativeProductType"));
        sourceSampleIdColumn.setCellValueFactory(new PropertyValueFactory<>("sourceSampleId"));
        sourceSampleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sourceSampleType"));
        quantityProducedColumn.setCellValueFactory(new PropertyValueFactory<>("quantityProduced"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        amountUnitColumn.setCellValueFactory(new PropertyValueFactory<>("amountUnit"));
        creationDateStartColumn.setCellValueFactory(new PropertyValueFactory<>("creationDateStart"));
        creationDateEndColumn.setCellValueFactory(new PropertyValueFactory<>("creationDateEnd"));
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
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
        List<DerivativeProductReportEntry> entries = null;

        try {
            switch (selectedReportType) {
                case REPORT_TYPE_DERIVATIVE_TYPE:
                    entries = reportService.generateDerivativeProductTypeReport(startDate, endDate);
                    break;
                case REPORT_TYPE_DERIVATIVE_PRODUCTION:
                    entries = reportService.generateDerivativeProductProductionReport(startDate, endDate);
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

