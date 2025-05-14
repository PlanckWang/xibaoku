package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.MasterCellBank;
import com.example.guangxishengkejavafx.model.entity.OriginalCellBank;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.service.MasterCellBankService;
import com.example.guangxishengkejavafx.service.OriginalCellBankService;
import com.example.guangxishengkejavafx.service.SamplePreparationService;
import com.example.guangxishengkejavafx.service.SampleRegistrationService;
import javafx.beans.property.SimpleStringProperty;
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
public class MasterCellBankController {

    @FXML
    private TextField searchCellLineNameField;
    @FXML
    private TextField searchStatusField;
    @FXML
    private TableView<MasterCellBank> masterCellBankTableView;
    @FXML
    private TableColumn<MasterCellBank, String> bankIdColumn;
    @FXML
    private TableColumn<MasterCellBank, String> originalCellBankIdColumn;
    @FXML
    private TableColumn<MasterCellBank, String> samplePrepIdColumn;
    @FXML
    private TableColumn<MasterCellBank, String> sampleRegIdColumn;
    @FXML
    private TableColumn<MasterCellBank, String> cellLineNameColumn;
    @FXML
    private TableColumn<MasterCellBank, Integer> passageNumberColumn;
    @FXML
    private TableColumn<MasterCellBank, LocalDate> cryopreservationDateColumn;
    @FXML
    private TableColumn<MasterCellBank, Integer> numberOfVialsColumn;
    @FXML
    private TableColumn<MasterCellBank, String> statusColumn;

    @FXML
    private TextField bankIdField;
    @FXML
    private TextField originalCellBankIdField;
    @FXML
    private TextField samplePreparationIdField;
    @FXML
    private TextField sampleRegistrationIdField;
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
    private TextField passageLimitField;
    @FXML
    private TextField statusField;
    @FXML
    private TextArea remarksArea;

    private final MasterCellBankService masterCellBankService;
    private final OriginalCellBankService originalCellBankService;
    private final SamplePreparationService samplePreparationService;
    private final SampleRegistrationService sampleRegistrationService;

    private ObservableList<MasterCellBank> masterCellBankData = FXCollections.observableArrayList();
    private MasterCellBank selectedMasterCellBank = null;

    @Autowired
    public MasterCellBankController(MasterCellBankService masterCellBankService,
                                    OriginalCellBankService originalCellBankService,
                                    SamplePreparationService samplePreparationService,
                                    SampleRegistrationService sampleRegistrationService) {
        this.masterCellBankService = masterCellBankService;
        this.originalCellBankService = originalCellBankService;
        this.samplePreparationService = samplePreparationService;
        this.sampleRegistrationService = sampleRegistrationService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMasterCellBankData();

        masterCellBankTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateFormFields(newValue)
        );
    }

    private void setupTableColumns() {
        bankIdColumn.setCellValueFactory(new PropertyValueFactory<>("bankId"));
        originalCellBankIdColumn.setCellValueFactory(cellData -> {
            OriginalCellBank ocb = cellData.getValue().getOriginalCellBank();
            return ocb != null ? new SimpleStringProperty(ocb.getBankId()) : new SimpleStringProperty("");
        });
        samplePrepIdColumn.setCellValueFactory(cellData -> {
            SamplePreparation sp = cellData.getValue().getSamplePreparation();
            return sp != null && sp.getPreparationId() != null ? new SimpleStringProperty(String.valueOf(sp.getPreparationId())) : new SimpleStringProperty("");
        });
        sampleRegIdColumn.setCellValueFactory(cellData -> {
            SampleRegistration sr = cellData.getValue().getSampleRegistration();
            return sr != null ? new SimpleStringProperty(sr.getSampleId()) : new SimpleStringProperty("N/A");
        });
        cellLineNameColumn.setCellValueFactory(new PropertyValueFactory<>("cellLineName"));
        passageNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passageNumber"));
        cryopreservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("cryopreservationDate"));
        numberOfVialsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfVials"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        masterCellBankTableView.setItems(masterCellBankData);
    }

    private void loadMasterCellBankData() {
        masterCellBankData.clear();
        masterCellBankData.addAll(masterCellBankService.findAll());
    }

    @FXML
    private void handleSearch() {
        String searchName = searchCellLineNameField.getText().trim().toLowerCase();
        String searchStat = searchStatusField.getText().trim().toLowerCase();

        List<MasterCellBank> filteredList = masterCellBankService.findAll().stream()
                .filter(bank -> (searchName.isEmpty() || bank.getCellLineName().toLowerCase().contains(searchName)) &&
                                (searchStat.isEmpty() || bank.getStatus().toLowerCase().contains(searchStat)))
                .collect(Collectors.toList());
        masterCellBankData.clear();
        masterCellBankData.addAll(filteredList);
    }

    @FXML
    private void handleResetSearch() {
        searchCellLineNameField.clear();
        searchStatusField.clear();
        loadMasterCellBankData();
    }

    private void populateFormFields(MasterCellBank bank) {
        selectedMasterCellBank = bank;
        if (bank != null) {
            bankIdField.setText(bank.getBankId());
            originalCellBankIdField.setText(bank.getOriginalCellBank() != null ? bank.getOriginalCellBank().getBankId() : "");
            samplePreparationIdField.setText(bank.getSamplePreparation() != null && bank.getSamplePreparation().getPreparationId() != null ? String.valueOf(bank.getSamplePreparation().getPreparationId()) : "");
            sampleRegistrationIdField.setText(bank.getSampleRegistration() != null ? bank.getSampleRegistration().getSampleId() : "");
            cellLineNameField.setText(bank.getCellLineName());
            cellTypeField.setText(bank.getCellType());
            passageNumberField.setText(bank.getPassageNumber() != null ? bank.getPassageNumber().toString() : "");
            cryopreservationDateField.setValue(bank.getCryopreservationDate());
            numberOfVialsField.setText(bank.getNumberOfVials() != null ? bank.getNumberOfVials().toString() : "");
            cellsPerVialField.setText(bank.getCellsPerVial() != null ? String.valueOf(bank.getCellsPerVial()) : "");
            volumePerVialField.setText(bank.getVolumePerVial() != null ? bank.getVolumePerVial().toString() : "");
            volumeUnitField.setText(bank.getVolumeUnit());
            cryopreservationMediumField.setText(bank.getCryopreservationMedium());
            storageLocationField.setText(bank.getStorageLocation());
            operatorField.setText(bank.getOperator());
            qualityControlStatusField.setText(bank.getQualityControlStatus());
            mycoplasmaTestResultField.setText(bank.getMycoplasmaTestResult());
            sterilityTestResultField.setText(bank.getSterilityTestResult());
            viabilityPostThawField.setText(bank.getViabilityPostThaw() != null ? bank.getViabilityPostThaw().toString() : "");
            passageLimitField.setText(bank.getPassageLimit() != null ? bank.getPassageLimit().toString() : "");
            statusField.setText(bank.getStatus());
            remarksArea.setText(bank.getRemarks());
            bankIdField.setEditable(false);
        } else {
            clearFormFields();
        }
    }

    @FXML
    private void handleNew() {
        clearFormFields();
        selectedMasterCellBank = null;
        bankIdField.setEditable(true);
        bankIdField.requestFocus();
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        String ocbIdInput = originalCellBankIdField.getText().trim();
        String spIdInput = samplePreparationIdField.getText().trim();
        String srIdInput = sampleRegistrationIdField.getText().trim();

        MasterCellBank bankToSave = (selectedMasterCellBank == null) ? new MasterCellBank() : selectedMasterCellBank;

        bankToSave.setBankId(bankIdField.getText().trim());
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
        if (!passageLimitField.getText().trim().isEmpty()) bankToSave.setPassageLimit(Integer.parseInt(passageLimitField.getText().trim()));
        bankToSave.setStatus(statusField.getText().trim());
        bankToSave.setRemarks(remarksArea.getText().trim());

        try {
            MasterCellBank savedBank;
            if (selectedMasterCellBank == null) {
                savedBank = masterCellBankService.save(bankToSave, ocbIdInput, spIdInput, srIdInput);
            } else {
                savedBank = masterCellBankService.update(selectedMasterCellBank.getId(), bankToSave, ocbIdInput, spIdInput, srIdInput);
            }
            loadMasterCellBankData();
            masterCellBankTableView.getSelectionModel().select(savedBank);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "主细胞库记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存主细胞库记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedMasterCellBank == null) {
            showAlert(Alert.AlertType.WARNING, "删除失败", "请先选择一条记录进行删除。");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除选定的主细胞库记录吗？", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("确认删除");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                masterCellBankService.deleteById(selectedMasterCellBank.getId());
                loadMasterCellBankData();
                clearFormFields();
                showAlert(Alert.AlertType.INFORMATION, "删除成功", "主细胞库记录已成功删除。");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "删除失败", "删除主细胞库记录时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClearFields() {
        clearFormFields();
        selectedMasterCellBank = null;
        bankIdField.setEditable(true);
        masterCellBankTableView.getSelectionModel().clearSelection();
    }

    private void clearFormFields() {
        bankIdField.clear();
        originalCellBankIdField.clear();
        samplePreparationIdField.clear();
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
        passageLimitField.clear();
        statusField.clear();
        remarksArea.clear();
        bankIdField.setEditable(true);
        selectedMasterCellBank = null;
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        if (bankIdField.getText().trim().isEmpty()) errors.append("- 主细胞库编号不能为空\n");
        if (sampleRegistrationIdField.getText().trim().isEmpty()) errors.append("- 样本登记ID (必填)不能为空\n");
        if (cellLineNameField.getText().trim().isEmpty()) errors.append("- 细胞系名称不能为空\n");
        if (passageNumberField.getText().trim().isEmpty()) errors.append("- 传代次数不能为空\n");
        else {
            try { Integer.parseInt(passageNumberField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 传代次数必须是有效数字\n"); }
        }
        if (cryopreservationDateField.getValue() == null) errors.append("- 冻存日期不能为空\n");
        if (numberOfVialsField.getText().trim().isEmpty()) errors.append("- 冻存管数不能为空\n");
        else {
            try { Integer.parseInt(numberOfVialsField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 冻存管数必须是有效数字\n"); }
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
        if (!passageLimitField.getText().trim().isEmpty()){
            try { Integer.parseInt(passageLimitField.getText().trim()); } catch (NumberFormatException e) { errors.append("- 传代限次必须是有效数字\n"); }
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

