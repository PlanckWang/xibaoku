package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.SampleCulture;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.SampleCultureService;
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
public class SampleCultureController {

    @FXML
    private TableView<SampleCulture> cultureTableView;
    @FXML
    private TableColumn<SampleCulture, Integer> cultureIdColumn;
    @FXML
    private TableColumn<SampleCulture, SamplePreparation> samplePreparationColumn;
    @FXML
    private TableColumn<SampleCulture, Personnel> operatorColumn;
    @FXML
    private TableColumn<SampleCulture, LocalDateTime> cultureStartDateColumn;
    @FXML
    private TableColumn<SampleCulture, LocalDateTime> cultureEndDateColumn;
    @FXML
    private TableColumn<SampleCulture, String> cultureMediumColumn;
    @FXML
    private TableColumn<SampleCulture, String> cultureConditionsColumn;
    @FXML
    private TableColumn<SampleCulture, Integer> passageNumberColumn;
    @FXML
    private TableColumn<SampleCulture, Long> cellCountColumn;
    @FXML
    private TableColumn<SampleCulture, Double> viabilityColumn;
    @FXML
    private TableColumn<SampleCulture, String> statusColumn;
    @FXML
    private TableColumn<SampleCulture, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<SampleCulture, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<SampleCulture, String> observationsColumn;
    @FXML
    private TableColumn<SampleCulture, String> notesColumn;

    @FXML
    private ComboBox<SamplePreparation> samplePreparationComboBox;
    @FXML
    private ComboBox<Personnel> operatorComboBox;
    @FXML
    private DatePicker cultureStartDatePicker;
    @FXML
    private DatePicker cultureEndDatePicker;
    @FXML
    private TextField cultureMediumField;
    @FXML
    private TextArea cultureConditionsArea;
    @FXML
    private TextField passageNumberField;
    @FXML
    private TextField cellCountField;
    @FXML
    private TextField viabilityField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea observationsArea;
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

    private final SampleCultureService sampleCultureService;
    private final SamplePreparationService samplePreparationService;
    private final PersonnelService personnelService;

    private ObservableList<SampleCulture> cultureList = FXCollections.observableArrayList();
    private SampleCulture currentCulture = null;

    @Autowired
    public SampleCultureController(SampleCultureService sampleCultureService,
                                   SamplePreparationService samplePreparationService,
                                   PersonnelService personnelService) {
        this.sampleCultureService = sampleCultureService;
        this.samplePreparationService = samplePreparationService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadCultureData();

        cultureTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        cultureIdColumn.setCellValueFactory(new PropertyValueFactory<>("cultureId"));
        samplePreparationColumn.setCellValueFactory(new PropertyValueFactory<>("samplePreparation"));
        samplePreparationColumn.setCellFactory(column -> new TableCell<SampleCulture, SamplePreparation>() {
            @Override
            protected void updateItem(SamplePreparation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.valueOf(item.getPreparationId()));
            }
        });
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        operatorColumn.setCellFactory(column -> new TableCell<SampleCulture, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cultureStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("cultureStartDate"));
        cultureEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("cultureEndDate"));
        cultureMediumColumn.setCellValueFactory(new PropertyValueFactory<>("cultureMedium"));
        cultureConditionsColumn.setCellValueFactory(new PropertyValueFactory<>("cultureConditions"));
        passageNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passageNumber"));
        cellCountColumn.setCellValueFactory(new PropertyValueFactory<>("cellCount"));
        viabilityColumn.setCellValueFactory(new PropertyValueFactory<>("viability"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        observationsColumn.setCellValueFactory(new PropertyValueFactory<>("observations"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        cultureTableView.setItems(cultureList);
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
        operatorComboBox.getItems().add(0, null); // Allow null selection

        statusComboBox.setItems(FXCollections.observableArrayList("培养中", "培养完成", "污染", "终止"));
    }

    private void loadCultureData() {
        List<SampleCulture> currentCultures = sampleCultureService.findAllSampleCultures();
        cultureList.setAll(currentCultures);
    }

    @FXML
    private void handleAddCulture() {
        currentCulture = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        samplePreparationComboBox.requestFocus();
    }

    @FXML
    private void handleEditCulture() {
        SampleCulture selectedCulture = cultureTableView.getSelectionModel().getSelectedItem();
        if (selectedCulture != null) {
            currentCulture = selectedCulture;
            populateForm(currentCulture);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个培养记录进行编辑。");
        }
    }

    @FXML
    private void handleDeleteCulture() {
        SampleCulture selectedCulture = cultureTableView.getSelectionModel().getSelectedItem();
        if (selectedCulture != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除培养记录 ID: " + selectedCulture.getCultureId() + " (制备ID: " + selectedCulture.getSamplePreparation().getPreparationId() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    sampleCultureService.deleteSampleCultureById(selectedCulture.getCultureId().intValue());
                    loadCultureData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "培养记录已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除培养记录时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个培养记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshCultures() {
        loadCultureData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveCulture() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentCulture == null || currentCulture.getCultureId() == null);
        SampleCulture cultureToSave = isNew ? new SampleCulture() : currentCulture;

        cultureToSave.setSamplePreparation(samplePreparationComboBox.getValue());
        cultureToSave.setOperator(operatorComboBox.getValue());
        cultureToSave.setCultureStartDate(cultureStartDatePicker.getValue());
        cultureToSave.setCultureEndDate(cultureEndDatePicker.getValue());
        cultureToSave.setCultureMedium(cultureMediumField.getText());
        cultureToSave.setCultureConditions(cultureConditionsArea.getText());
        try {
            cultureToSave.setPassageNumber(passageNumberField.getText().isEmpty() ? null : Integer.parseInt(passageNumberField.getText()));
            cultureToSave.setCellCount(cellCountField.getText().isEmpty() ? null : Long.parseLong(cellCountField.getText()));
            cultureToSave.setViability(viabilityField.getText().isEmpty() ? null : Double.parseDouble(viabilityField.getText()));
        } catch (NumberFormatException e) {
            // Validation should catch this, but as a safeguard
        }
        cultureToSave.setStatus(statusComboBox.getValue());
        cultureToSave.setObservations(observationsArea.getText());
        cultureToSave.setNotes(notesArea.getText());

        try {
            sampleCultureService.saveSampleCulture(cultureToSave);
            loadCultureData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            cultureTableView.getSelectionModel().select(cultureToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "培养记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存培养记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelCulture() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        cultureTableView.getSelectionModel().clearSelection();
        currentCulture = null;
    }

    private void populateForm(SampleCulture culture) {
        if (culture != null) {
            samplePreparationComboBox.setValue(culture.getSamplePreparation());
            operatorComboBox.setValue(culture.getOperator());
            cultureStartDatePicker.setValue(culture.getCultureStartDate());
            cultureEndDatePicker.setValue(culture.getCultureEndDate());
            cultureMediumField.setText(culture.getCultureMedium());
            cultureConditionsArea.setText(culture.getCultureConditions());
            passageNumberField.setText(culture.getPassageNumber() != null ? String.valueOf(culture.getPassageNumber()) : "");
            cellCountField.setText(culture.getCellCount() != null ? String.valueOf(culture.getCellCount()) : "");
            viabilityField.setText(culture.getViability() != null ? String.valueOf(culture.getViability()) : "");
            statusComboBox.setValue(culture.getStatus());
            observationsArea.setText(culture.getObservations());
            notesArea.setText(culture.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        samplePreparationComboBox.setValue(null);
        operatorComboBox.setValue(null);
        cultureStartDatePicker.setValue(null);
        cultureEndDatePicker.setValue(null);
        cultureMediumField.clear();
        cultureConditionsArea.clear();
        passageNumberField.clear();
        cellCountField.clear();
        viabilityField.clear();
        statusComboBox.setValue(null);
        observationsArea.clear();
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        samplePreparationComboBox.setDisable(!editable);
        operatorComboBox.setDisable(!editable);
        cultureStartDatePicker.setDisable(!editable);
        cultureEndDatePicker.setDisable(!editable);
        cultureMediumField.setEditable(editable);
        cultureConditionsArea.setEditable(editable);
        passageNumberField.setEditable(editable);
        cellCountField.setEditable(editable);
        viabilityField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        observationsArea.setEditable(editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (samplePreparationComboBox.getValue() == null) {
            errors.append("必须选择一个关联制备记录。\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("必须选择状态。\n");
        }
        if (!passageNumberField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(passageNumberField.getText());
            } catch (NumberFormatException e) {
                errors.append("传代次数必须是有效的整数。\n");
            }
        }
        if (!cellCountField.getText().trim().isEmpty()) {
            try {
                Long.parseLong(cellCountField.getText());
            } catch (NumberFormatException e) {
                errors.append("细胞数量必须是有效的长整数。\n");
            }
        }
        if (!viabilityField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(viabilityField.getText());
            } catch (NumberFormatException e) {
                errors.append("细胞活力必须是有效的数字。\n");
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

