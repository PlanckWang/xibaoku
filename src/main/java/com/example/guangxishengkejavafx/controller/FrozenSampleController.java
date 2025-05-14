package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.FrozenSample;
import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.service.FrozenSampleService;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.SamplePreparationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FrozenSampleController {

    @FXML
    private TableView<FrozenSample> frozenSampleTableView;
    @FXML
    private TableColumn<FrozenSample, Integer> frozenSampleIdColumn;
    @FXML
    private TableColumn<FrozenSample, SamplePreparation> samplePreparationColumn;
    @FXML
    private TableColumn<FrozenSample, Personnel> operatorColumn;
    @FXML
    private TableColumn<FrozenSample, LocalDateTime> frozenDateColumn;
    @FXML
    private TableColumn<FrozenSample, String> storageLocationColumn;
    @FXML
    private TableColumn<FrozenSample, String> cryoprotectantColumn;
    @FXML
    private TableColumn<FrozenSample, Double> volumeColumn;
    @FXML
    private TableColumn<FrozenSample, String> unitColumn;
    @FXML
    private TableColumn<FrozenSample, Long> cellCountPerUnitColumn;
    @FXML
    private TableColumn<FrozenSample, Integer> passageNumberColumn;
    @FXML
    private TableColumn<FrozenSample, String> statusColumn;
    @FXML
    private TableColumn<FrozenSample, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<FrozenSample, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<FrozenSample, String> notesColumn;

    @FXML
    private ComboBox<SamplePreparation> samplePreparationComboBox;
    @FXML
    private ComboBox<Personnel> operatorComboBox;
    @FXML
    private DatePicker frozenDatePicker;
    @FXML
    private TextField storageLocationField;
    @FXML
    private TextField cryoprotectantField;
    @FXML
    private TextField volumeField;
    @FXML
    private TextField unitField;
    @FXML
    private TextField cellCountPerUnitField;
    @FXML
    private TextField passageNumberField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea notesArea;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final FrozenSampleService frozenSampleService;
    private final SamplePreparationService samplePreparationService;
    private final PersonnelService personnelService;

    private ObservableList<FrozenSample> frozenSampleList = FXCollections.observableArrayList();
    private FrozenSample currentFrozenSample = null;

    @Autowired
    public FrozenSampleController(FrozenSampleService frozenSampleService,
                                  SamplePreparationService samplePreparationService,
                                  PersonnelService personnelService) {
        this.frozenSampleService = frozenSampleService;
        this.samplePreparationService = samplePreparationService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadFrozenSampleData();

        frozenSampleTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        frozenSampleIdColumn.setCellValueFactory(new PropertyValueFactory<>("frozenSampleId"));
        samplePreparationColumn.setCellValueFactory(new PropertyValueFactory<>("samplePreparation"));
        samplePreparationColumn.setCellFactory(column -> new TableCell<FrozenSample, SamplePreparation>() {
            @Override
            protected void updateItem(SamplePreparation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.valueOf(item.getPreparationId()));
            }
        });
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        operatorColumn.setCellFactory(column -> new TableCell<FrozenSample, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        frozenDateColumn.setCellValueFactory(new PropertyValueFactory<>("frozenDate"));
        storageLocationColumn.setCellValueFactory(new PropertyValueFactory<>("storageLocation"));
        cryoprotectantColumn.setCellValueFactory(new PropertyValueFactory<>("cryoprotectant"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        cellCountPerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("cellCountPerUnit"));
        passageNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passageNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        frozenSampleTableView.setItems(frozenSampleList);
    }

    private void loadComboBoxData() {
        samplePreparationComboBox.setItems(FXCollections.observableArrayList(samplePreparationService.findAllSamplePreparations()));
        samplePreparationComboBox.setConverter(new javafx.util.StringConverter<SamplePreparation>() {
            @Override
            public String toString(SamplePreparation prep) {
                return prep == null ? null : "PrepID: " + prep.getPreparationId() + (prep.getSampleRegistration() != null ? " (Sample: " + prep.getSampleRegistration().getSampleCode() + ")" : "");
            }
            @Override
            public SamplePreparation fromString(String string) { return null; }
        });

        operatorComboBox.setItems(FXCollections.observableArrayList(personnelService.findAllPersonnel()));
        operatorComboBox.setConverter(new javafx.util.StringConverter<Personnel>() {
            @Override
            public String toString(Personnel personnel) {
                return personnel == null ? null : personnel.getName();
            }
            @Override
            public Personnel fromString(String string) { return null; }
        });

        statusComboBox.setItems(FXCollections.observableArrayList("已冻存", "已复苏", "已消耗", "已废弃"));
    }

    private void loadFrozenSampleData() {
        List<FrozenSample> currentFrozenSamples = frozenSampleService.findAllFrozenSamples();
        frozenSampleList.setAll(currentFrozenSamples);
    }

    @FXML
    private void handleAddFrozenSample() {
        currentFrozenSample = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        samplePreparationComboBox.requestFocus();
    }

    @FXML
    private void handleEditFrozenSample() {
        FrozenSample selectedFrozenSample = frozenSampleTableView.getSelectionModel().getSelectedItem();
        if (selectedFrozenSample != null) {
            currentFrozenSample = selectedFrozenSample;
            populateForm(currentFrozenSample);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个冻存记录进行编辑。");
        }
    }

    @FXML
    private void handleDeleteFrozenSample() {
        FrozenSample selectedFrozenSample = frozenSampleTableView.getSelectionModel().getSelectedItem();
        if (selectedFrozenSample != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除冻存记录 ID: " + selectedFrozenSample.getFrozenSampleId() + " (制备ID: " + selectedFrozenSample.getSamplePreparation().getPreparationId() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    frozenSampleService.deleteFrozenSampleById(selectedFrozenSample.getFrozenSampleId());
                    loadFrozenSampleData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "冻存记录已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除冻存记录时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个冻存记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshFrozenSamples() {
        loadFrozenSampleData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveFrozenSample() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentFrozenSample == null || currentFrozenSample.getFrozenSampleId() == null);
        FrozenSample fsToSave = isNew ? new FrozenSample() : currentFrozenSample;

        fsToSave.setSamplePreparation(samplePreparationComboBox.getValue());
        fsToSave.setOperator(operatorComboBox.getValue());
        fsToSave.setFrozenDate(frozenDatePicker.getValue() != null ? frozenDatePicker.getValue().atStartOfDay() : LocalDateTime.now());
        fsToSave.setStorageLocation(storageLocationField.getText());
        fsToSave.setCryoprotectant(cryoprotectantField.getText());
        try {
            fsToSave.setVolume(volumeField.getText().isEmpty() ? null : Double.parseDouble(volumeField.getText()));
            fsToSave.setCellCountPerUnit(cellCountPerUnitField.getText().isEmpty() ? null : Long.parseLong(cellCountPerUnitField.getText()));
            fsToSave.setPassageNumber(passageNumberField.getText().isEmpty() ? null : Integer.parseInt(passageNumberField.getText()));
        } catch (NumberFormatException e) {
            // Validation should catch this
        }
        fsToSave.setUnit(unitField.getText());
        fsToSave.setStatus(statusComboBox.getValue());
        fsToSave.setNotes(notesArea.getText());

        try {
            frozenSampleService.saveFrozenSample(fsToSave);
            loadFrozenSampleData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            frozenSampleTableView.getSelectionModel().select(fsToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "冻存记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存冻存记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelFrozenSample() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        frozenSampleTableView.getSelectionModel().clearSelection();
        currentFrozenSample = null;
    }

    private void populateForm(FrozenSample frozenSample) {
        if (frozenSample != null) {
            samplePreparationComboBox.setValue(frozenSample.getSamplePreparation());
            operatorComboBox.setValue(frozenSample.getOperator());
            frozenDatePicker.setValue(frozenSample.getFrozenDate() != null ? frozenSample.getFrozenDate().toLocalDate() : null);
            storageLocationField.setText(frozenSample.getStorageLocation());
            cryoprotectantField.setText(frozenSample.getCryoprotectant());
            volumeField.setText(frozenSample.getVolume() != null ? String.valueOf(frozenSample.getVolume()) : "");
            unitField.setText(frozenSample.getUnit());
            cellCountPerUnitField.setText(frozenSample.getCellCountPerUnit() != null ? String.valueOf(frozenSample.getCellCountPerUnit()) : "");
            passageNumberField.setText(frozenSample.getPassageNumber() != null ? String.valueOf(frozenSample.getPassageNumber()) : "");
            statusComboBox.setValue(frozenSample.getStatus());
            notesArea.setText(frozenSample.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        samplePreparationComboBox.setValue(null);
        operatorComboBox.setValue(null);
        frozenDatePicker.setValue(null);
        storageLocationField.clear();
        cryoprotectantField.clear();
        volumeField.clear();
        unitField.clear();
        cellCountPerUnitField.clear();
        passageNumberField.clear();
        statusComboBox.setValue(null);
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        samplePreparationComboBox.setDisable(!editable);
        operatorComboBox.setDisable(!editable);
        frozenDatePicker.setDisable(!editable);
        storageLocationField.setEditable(editable);
        cryoprotectantField.setEditable(editable);
        volumeField.setEditable(editable);
        unitField.setEditable(editable);
        cellCountPerUnitField.setEditable(editable);
        passageNumberField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (samplePreparationComboBox.getValue() == null) {
            errors.append("必须选择一个关联制备记录。\n");
        }
        if (operatorComboBox.getValue() == null) {
            errors.append("必须选择操作员。\n");
        }
        if (frozenDatePicker.getValue() == null) {
            errors.append("冻存日期不能为空。\n");
        }
        if (storageLocationField.getText() == null || storageLocationField.getText().trim().isEmpty()) {
            errors.append("冻存位置不能为空。\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("必须选择状态。\n");
        }
        if (!volumeField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(volumeField.getText());
            } catch (NumberFormatException e) {
                errors.append("体积必须是有效的数字。\n");
            }
        }
        if (!cellCountPerUnitField.getText().trim().isEmpty()) {
            try {
                Long.parseLong(cellCountPerUnitField.getText());
            } catch (NumberFormatException e) {
                errors.append("细胞数/单位必须是有效的长整数。\n");
            }
        }
        if (!passageNumberField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(passageNumberField.getText());
            } catch (NumberFormatException e) {
                errors.append("传代次数必须是有效的整数。\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "输入验证错误", errors.toString());
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

