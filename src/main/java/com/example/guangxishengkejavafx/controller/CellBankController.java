package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.CellBank;
import com.example.guangxishengkejavafx.service.CellBankService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class CellBankController {

    @Autowired
    private CellBankService cellBankService;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<CellBank> cellBankTable;
    @FXML
    private TableColumn<CellBank, Integer> cellBankIdColumn;
    @FXML
    private TableColumn<CellBank, Integer> cryoSampleIdColumn;
    @FXML
    private TableColumn<CellBank, String> cellLineNameColumn;
    @FXML
    private TableColumn<CellBank, Integer> passageNumberColumn;
    @FXML
    private TableColumn<CellBank, Long> cellCountColumn;
    @FXML
    private TableColumn<CellBank, Double> viabilityColumn;
    @FXML
    private TableColumn<CellBank, String> storageDateColumn;
    @FXML
    private TableColumn<CellBank, String> storageLocationColumn;
    @FXML
    private TableColumn<CellBank, Integer> operatorIdColumn;
    @FXML
    private TableColumn<CellBank, String> statusColumn;
    @FXML
    private TableColumn<CellBank, String> notesColumn;

    @FXML
    private AnchorPane formPane;
    @FXML
    private TextField cellBankIdField;
    @FXML
    private TextField cryoSampleIdField;
    @FXML
    private TextField cellLineNameField;
    @FXML
    private TextField passageNumberField;
    @FXML
    private TextField cellCountField;
    @FXML
    private TextField viabilityField;
    @FXML
    private TextField storageDateField;
    @FXML
    private TextField storageLocationField;
    @FXML
    private TextField operatorIdField;
    @FXML
    private TextField statusField;
    @FXML
    private TextField notesField;

    private ObservableList<CellBank> cellBankData = FXCollections.observableArrayList();
    private CellBank currentCellBank;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        cellBankIdColumn.setCellValueFactory(new PropertyValueFactory<>("cellBankId"));
        cryoSampleIdColumn.setCellValueFactory(new PropertyValueFactory<>("cryoSampleId"));
        cellLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("cellLineName"));
        passageNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passageNumber"));
        cellCountColumn.setCellValueFactory(new PropertyValueFactory<>("cellCount"));
        viabilityColumn.setCellValueFactory(new PropertyValueFactory<>("viability"));
        storageLocationColumn.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));
        operatorIdColumn.setCellValueFactory(new PropertyValueFactory<>("operatorId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        storageDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getStorageDate();
            return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateTimeFormatter) : "");
        });

        // Load and display the cell banks of type "\u5de5\u4f5c\u7ec6\u80de\u5e93"
        loadCellBanks();
        cellBankTable.setItems(cellBankData);
        hideForm();
    }

    private void loadCellBanks() {
        List<CellBank> allBanks = cellBankService.findAll();
        // Ensure we only show "工作细胞库" type, though service layer should also handle this
        cellBankData.setAll(allBanks.stream().filter(bank -> "工作细胞库".equals(bank.getBankType())).collect(Collectors.toList()));
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadCellBanks();
        } else {
            List<CellBank> filteredList = cellBankService.findAll().stream()
                    .filter(bank -> "工作细胞库".equals(bank.getBankType()) &&
                                   (bank.getCellLineName() != null && bank.getCellLineName().toLowerCase().contains(searchTerm)))
                    .collect(Collectors.toList());
            cellBankData.setAll(filteredList);
        }
        cellBankTable.setItems(cellBankData);
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadCellBanks();
        hideForm();
    }

    @FXML
    private void handleAddCellBank() {
        currentCellBank = null;
        clearForm();
        showForm("新增工作细胞库");
    }

    @FXML
    private void handleEditCellBank() {
        CellBank selectedBank = cellBankTable.getSelectionModel().getSelectedItem();
        if (selectedBank != null) {
            currentCellBank = selectedBank;
            populateForm(selectedBank);
            showForm("编辑工作细胞库");
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要编辑的工作细胞库。");
        }
    }

    @FXML
    private void handleDeleteCellBank() {
        CellBank selectedBank = cellBankTable.getSelectionModel().getSelectedItem();
        if (selectedBank != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的工作细胞库吗？", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        cellBankService.deleteById(selectedBank.getCellBankId());
                        loadCellBanks();
                        hideForm();
                        showAlert(Alert.AlertType.INFORMATION, "删除成功", "工作细胞库已删除。");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除工作细胞库: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请选择要删除的工作细胞库。");
        }
    }

    @FXML
    private void handleSaveCellBank() {
        if (!validateForm()) {
            return;
        }

        CellBank bankToSave = (currentCellBank == null) ? new CellBank() : currentCellBank;

        try {
            if (!cryoSampleIdField.getText().isEmpty()) bankToSave.setCryoSampleId(Integer.parseInt(cryoSampleIdField.getText()));
            bankToSave.setCellLineName(cellLineNameField.getText());
            if (!passageNumberField.getText().isEmpty()) bankToSave.setPassageNumber(Integer.parseInt(passageNumberField.getText()));
            if (!cellCountField.getText().isEmpty()) bankToSave.setCellCount(Long.parseLong(cellCountField.getText()));
            if (!viabilityField.getText().isEmpty()) bankToSave.setViability(Double.parseDouble(viabilityField.getText()));
            if (!storageDateField.getText().isEmpty()) bankToSave.setStorageDate(LocalDateTime.parse(storageDateField.getText(), dateTimeFormatter));
            bankToSave.setStorageLocation(storageLocationField.getText());
            if (!operatorIdField.getText().isEmpty()) bankToSave.setOperatorId(Integer.parseInt(operatorIdField.getText())); else {
                 showAlert(Alert.AlertType.ERROR, "保存失败", "操作员ID不能为空"); return;
            }
            bankToSave.setStatus(statusField.getText());
            bankToSave.setNotes(notesField.getText());
            bankToSave.setBankType("工作细胞库"); // Ensure type is set

            cellBankService.save(bankToSave);
            loadCellBanks();
            hideForm();
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "工作细胞库信息已保存。");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "数据格式错误，请检查数字字段。 " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "无法保存工作细胞库: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelEdit() {
        hideForm();
    }

    private void populateForm(CellBank bank) {
        cellBankIdField.setText(bank.getCellBankId() != null ? bank.getCellBankId().toString() : "");
        cryoSampleIdField.setText(bank.getCryoSampleId() != null ? bank.getCryoSampleId().toString() : "");
        cellLineNameField.setText(bank.getCellLineName());
        passageNumberField.setText(bank.getPassageNumber() != null ? bank.getPassageNumber().toString() : "");
        cellCountField.setText(bank.getCellCount() != null ? bank.getCellCount().toString() : "");
        viabilityField.setText(bank.getViability() != null ? bank.getViability().toString() : "");
        storageDateField.setText(bank.getStorageDate() != null ? bank.getStorageDate().format(dateTimeFormatter) : "");
        storageLocationField.setText(bank.getStorageLocation());
        operatorIdField.setText(bank.getOperatorId() != null ? bank.getOperatorId().toString() : "");
        statusField.setText(bank.getStatus());
        notesField.setText(bank.getNotes());
    }

    private void clearForm() {
        cellBankIdField.clear();
        cryoSampleIdField.clear();
        cellLineNameField.clear();
        passageNumberField.clear();
        cellCountField.clear();
        viabilityField.clear();
        storageDateField.clear();
        storageLocationField.clear();
        operatorIdField.clear();
        statusField.clear();
        notesField.clear();
    }

    private void showForm(String title) {
        // In a real app, you might change a Label's text for the title
        formPane.setVisible(true);
        formPane.setManaged(true);
    }

    private void hideForm() {
        formPane.setVisible(false);
        formPane.setManaged(false);
        currentCellBank = null;
    }

    private boolean validateForm() {
        // Basic validation, extend as needed
        if (operatorIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "操作员ID 不能为空。");
            return false;
        }
        try {
            if (!cryoSampleIdField.getText().isEmpty()) Integer.parseInt(cryoSampleIdField.getText());
            if (!passageNumberField.getText().isEmpty()) Integer.parseInt(passageNumberField.getText());
            if (!cellCountField.getText().isEmpty()) Long.parseLong(cellCountField.getText());
            if (!viabilityField.getText().isEmpty()) Double.parseDouble(viabilityField.getText());
            if (!storageDateField.getText().isEmpty()) LocalDateTime.parse(storageDateField.getText(), dateTimeFormatter);
            Integer.parseInt(operatorIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "验证错误", "数字字段格式不正确: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

