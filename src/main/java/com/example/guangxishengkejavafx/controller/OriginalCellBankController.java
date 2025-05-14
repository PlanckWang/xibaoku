package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.OriginalCellBank;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.service.OriginalCellBankService;
import com.example.guangxishengkejavafx.service.SampleRegistrationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class OriginalCellBankController {

    @FXML
    private TextField searchCellLineNameField;
    @FXML
    private TextField searchStatusField;
    @FXML
    private TableView<OriginalCellBank> originalCellBankTableView;
    @FXML
    private TableColumn<OriginalCellBank, String> bankIdColumn;
    @FXML
    private TableColumn<OriginalCellBank, String> sampleIdColumn; // Will display sampleRegistration.sampleId
    @FXML
    private TableColumn<OriginalCellBank, String> cellLineNameColumn;
    @FXML
    private TableColumn<OriginalCellBank, String> cellTypeColumn;
    @FXML
    private TableColumn<OriginalCellBank, Integer> passageNumberColumn;
    @FXML
    private TableColumn<OriginalCellBank, LocalDate> cryopreservationDateColumn;
    @FXML
    private TableColumn<OriginalCellBank, Integer> numberOfVialsColumn;
    @FXML
    private TableColumn<OriginalCellBank, String> storageLocationColumn;
    @FXML
    private TableColumn<OriginalCellBank, String> statusColumn;
    @FXML
    private TableColumn<OriginalCellBank, String> operatorColumn;

    @FXML
    private TextField bankIdField;
    @FXML
    private TextField sampleRegistrationIdField; // Input for SampleRegistration's sampleId
    @FXML
    private TextField cellLineNameField;
    @FXML
    private TextField cellTypeField;
    @FXML
    private TextField passageNumberField;
    @FXML
    private DatePicker cryopreservationDateField;
    @FXML
    private TextField numberOfVialsField;
    @FXML
    private TextField cellsPerVialField;
    @FXML
    private TextField volumePerVialField;
    @FXML
    private TextField volumeUnitField;
    @FXML
    private TextField cryopreservationMediumField;
    @FXML
    private TextField storageLocationField;
    @FXML
    private TextField operatorField;
    @FXML
    private TextField qualityControlStatusField;
    @FXML
    private TextField mycoplasmaTestResultField;
    @FXML
    private TextField sterilityTestResultField;
    @FXML
    private TextField viabilityPostThawField;
    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarksArea;

    private final OriginalCellBankService originalCellBankService;
    private final SampleRegistrationService sampleRegistrationService; // To fetch SampleRegistration by sampleId

    private ObservableList<OriginalCellBank> originalCellBankData = FXCollections.observableArrayList();
    private OriginalCellBank selectedOriginalCellBank = null;

    @Autowired
    public OriginalCellBankController(OriginalCellBankService originalCellBankService, SampleRegistrationService sampleRegistrationService) {
        this.originalCellBankService = originalCellBankService;
        this.sampleRegistrationService = sampleRegistrationService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadOriginalCellBankData();

        originalCellBankTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateFormFields(newValue)
        );
    }

    private void setupTableColumns() {
        bankIdColumn.setCellValueFactory(new PropertyValueFactory<>("bankId"));
        // For sampleId, we need to get it from the nested SampleRegistration object
        sampleIdColumn.setCellValueFactory(cellData -> {
            SampleRegistration sr = cellData.getValue().getSampleRegistration();
            return sr != null ? new javafx.beans.property.SimpleStringProperty(sr.getSampleId()) : new javafx.beans.property.SimpleStringProperty("N/A");
        });
        cellLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("cellLineName"));
        cellTypeColumn.setCellValueFactory(new PropertyValueFactory<>("cellType"));
        passageNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passageNumber"));
        cryopreservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("cryopreservationDate"));
        numberOfVialsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfVials"));
        storageLocationColumn.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));

        originalCellBankTableView.setItems(originalCellBankData);
    }

    private void loadOriginalCellBankData() {
        originalCellBankData.clear();
        originalCellBankData.addAll(originalCellBankService.findAll());
    }

    @FXML
    private void handleSearch() {
        String searchName = searchCellLineNameField.getText().trim().toLowerCase();
        String searchStat = searchStatusField.getText().trim().toLowerCase();

        List<OriginalCellBank> filteredList = originalCellBankService.findAll().stream()
                .filter(bank -> (searchName.isEmpty() || bank.getCellLineName().toLowerCase().contains(searchName)) &&
                                (searchStat.isEmpty() || bank.getStatus().toLowerCase().contains(searchStat)))
                .collect(Collectors.toList());
        originalCellBankData.clear();
        originalCellBankData.addAll(filteredList);
    }

    @FXML
    private void handleResetSearch() {
        searchCellLineNameField.clear();
        searchStatusField.clear();
        loadOriginalCellBankData();
    }

    private void populateFormFields(OriginalCellBank bank) {
        selectedOriginalCellBank = bank;
        if (bank != null) {
            bankIdField.setText(bank.getBankId());
            sampleRegistrationIdField.setText(bank.getSampleRegistration() != null ? bank.getSampleRegistration().getSampleId() : "");
            cellLineNameField.setText(bank.getCellLineName());
            cellTypeField.setText(bank.getCellType());
            passageNumberField.setText(bank.getPassageNumber() != null ? bank.getPassageNumber().toString() : "");
            cryopreservationDateField.setValue(bank.getCryopreservationDate());
            numberOfVialsField.setText(bank.getNumberOfVials() != null ? bank.getNumberOfVials().toString() : "");
            cellsPerVialField.setText(bank.getCellsPerVial() != null ? bank.getCellsPerVial().toString() : "");
            volumePerVialField.setText(bank.getVolumePerVial() != null ? bank.getVolumePerVial().toString() : "");
            volumeUnitField.setText(bank.getVolumeUnit());
            cryopreservationMediumField.setText(bank.getCryopreservationMedium());
            storageLocationField.setText(bank.getStorageLocation());
            operatorField.setText(bank.getOperator());
            qualityControlStatusField.setText(bank.getQualityControlStatus());
            mycoplasmaTestResultField.setText(bank.getMycoplasmaTestResult());
            sterilityTestResultField.setText(bank.getSterilityTestResult());
            viabilityPostThawField.setText(bank.getViabilityPostThaw() != null ? bank.getViabilityPostThaw().toString() : "");
            statusField.setText(bank.getStatus());
            remarksArea.setText(bank.getRemarks());
            bankIdField.setEditable(false); // Bank ID should not be editable after creation for selected item
        } else {
            clearFormFields();
        }
    }

    @FXML
    private void handleNew() {
        clearFormFields();
        selectedOriginalCellBank = null;
        bankIdField.setEditable(true);
        bankIdField.requestFocus();
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        String sampleIdInput = sampleRegistrationIdField.getText().trim();
        Optional<SampleRegistration> sampleRegistrationOpt = sampleRegistrationService.findBySampleId(sampleIdInput);

        if (!sampleRegistrationOpt.isPresent()) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "关联的样本ID '" + sampleIdInput + "' 不存在，请先登记样本。");
            return;
        }

        OriginalCellBank bankToSave = (selectedOriginalCellBank == null) ? new OriginalCellBank() : selectedOriginalCellBank;

        bankToSave.setBankId(bankIdField.getText().trim());
        bankToSave.setSampleRegistration(sampleRegistrationOpt.get());
        bankToSave.setCellLineName(cellLineNameField.getText().trim());
        bankToSave.setCellType(cellTypeField.getText().trim());
        bankToSave.setPassageNumber(Integer.parseInt(passageNumberField.getText().trim()));
        bankToSave.setCryopreservationDate(cryopreservationDateField.getValue());
        bankToSave.setNumberOfVials(Integer.parseInt(numberOfVialsField.getText().trim()));
        if (!cellsPerVialField.getText().trim().isEmpty()) bankToSave.setCellsPerVial(Long.parseLong(cellsPerVialField.getText().trim()));
        if (!volumePerVialField.getText().trim().isEmpty()) bankToSave.setVolumePerVial(Double.parseDouble(volumePerVialField.getText().trim()));
        bankToSave.setVolumeUnit(volumeUnitField.getText().trim());
        bankToSave.setCryopreservationMedium(cryopreservationMediumField.getText().trim());
        bankToSave.setStorageLocation(storageLocationField.getText().trim());
        bankToSave.setOperator(operatorField.getText().trim());
        bankToSave.setQualityControlStatus(qualityControlStatusField.getText().trim());
        bankToSave.setMycoplasmaTestResult(mycoplasmaTestResultField.getText().trim());
        bankToSave.setSterilityTestResult(sterilityTestResultField.getText().trim());
        if (!viabilityPostThawField.getText().trim().isEmpty()) bankToSave.setViabilityPostThaw(Double.parseDouble(viabilityPostThawField.getText().trim()));
        bankToSave.setStatus(statusField.getText().trim());
        bankToSave.setRemarks(remarksArea.getText().trim());
        // createdBy, lastModifiedBy should be handled by security context or service layer if needed

        try {
            OriginalCellBank savedBank = originalCellBankService.save(bankToSave);
            loadOriginalCellBankData();
            originalCellBankTableView.getSelectionModel().select(savedBank);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "原始细胞库记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存原始细胞库记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedOriginalCellBank == null) {
            showAlert(Alert.AlertType.WARNING, "删除失败", "请先选择一条记录进行删除。");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除选定的原始细胞库记录吗？", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("确认删除");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                originalCellBankService.deleteById(selectedOriginalCellBank.getId());
                loadOriginalCellBankData();
                clearFormFields();
                showAlert(Alert.AlertType.INFORMATION, "删除成功", "原始细胞库记录已成功删除。");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "删除失败", "删除原始细胞库记录时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClearFields() {
        clearFormFields();
        selectedOriginalCellBank = null;
        bankIdField.setEditable(true);
        originalCellBankTableView.getSelectionModel().clearSelection();
    }

    private void clearFormFields() {
        bankIdField.clear();
        sampleRegistrationIdField.clear();
        cellLineNameField.clear();
        cellTypeField.clear();
        passageNumberField.clear();
        cryopreservationDateField.setValue(null);
        numberOfVialsField.clear();
        cellsPerVialField.clear();
        volumePerVialField.clear();
        volumeUnitField.clear();
        cryopreservationMediumField.clear();
        storageLocationField.clear();
        operatorField.clear();
        qualityControlStatusField.clear();
        mycoplasmaTestResultField.clear();
        sterilityTestResultField.clear();
        viabilityPostThawField.clear();
        statusField.clear();
        remarksArea.clear();
        bankIdField.setEditable(true);
        selectedOriginalCellBank = null;
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (bankIdField.getText().trim().isEmpty()) errors.append("- 细胞库编号不能为空\n");
        if (sampleRegistrationIdField.getText().trim().isEmpty()) errors.append("- 关联样本ID不能为空\n");
        if (cellLineNameField.getText().trim().isEmpty()) errors.append("- 细胞系名称不能为空\n");
        if (cryopreservationDateField.getValue() == null) errors.append("- 冻存日期不能为空\n");
        if (numberOfVialsField.getText().trim().isEmpty()) errors.append("- 冻存管数不能为空\n");
        else {
            try { Integer.parseInt(numberOfVialsField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 冻存管数必须是有效数字\n"); }
        }
        if (!passageNumberField.getText().trim().isEmpty()){
            try { Integer.parseInt(passageNumberField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 传代次数必须是有效数字\n"); }
        }
        if (!cellsPerVialField.getText().trim().isEmpty()){
            try { Long.parseLong(cellsPerVialField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 每管细胞数必须是有效数字\n"); }
        }
        if (!volumePerVialField.getText().trim().isEmpty()){
            try { Double.parseDouble(volumePerVialField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 每管体积必须是有效数字\n"); }
        }
        if (!viabilityPostThawField.getText().trim().isEmpty()){
            try { Double.parseDouble(viabilityPostThawField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 复苏后活率必须是有效数字\n"); }
        }
        if (storageLocationField.getText().trim().isEmpty()) errors.append("- 存储位置不能为空\n");
        if (statusField.getText().trim().isEmpty()) errors.append("- 状态不能为空\n");

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "输入验证失败", errors.toString());
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

