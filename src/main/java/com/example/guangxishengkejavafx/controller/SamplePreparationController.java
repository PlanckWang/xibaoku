package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.SamplePreparation;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.ProductService;
import com.example.guangxishengkejavafx.service.SamplePreparationService;
import com.example.guangxishengkejavafx.service.SampleRegistrationService;
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
public class SamplePreparationController {

    @FXML
    private TableView<SamplePreparation> preparationTableView;
    @FXML
    private TableColumn<SamplePreparation, Integer> preparationIdColumn;
    @FXML
    private TableColumn<SamplePreparation, SampleRegistration> sampleRegistrationColumn;
    @FXML
    private TableColumn<SamplePreparation, Product> productColumn;
    @FXML
    private TableColumn<SamplePreparation, Personnel> operatorColumn;
    @FXML
    private TableColumn<SamplePreparation, LocalDateTime> preparationDateColumn;
    @FXML
    private TableColumn<SamplePreparation, String> batchNumberColumn;
    @FXML
    private TableColumn<SamplePreparation, Integer> quantityPreparedColumn;
    @FXML
    private TableColumn<SamplePreparation, String> unitColumn;
    @FXML
    private TableColumn<SamplePreparation, String> statusColumn;
    @FXML
    private TableColumn<SamplePreparation, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<SamplePreparation, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<SamplePreparation, String> processDetailsColumn;
    @FXML
    private TableColumn<SamplePreparation, String> notesColumn;

    @FXML
    private ComboBox<SampleRegistration> sampleRegistrationComboBox;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private ComboBox<Personnel> operatorComboBox;
    @FXML
    private DatePicker preparationDatePicker;
    @FXML
    private TextField batchNumberField;
    @FXML
    private TextField quantityPreparedField;
    @FXML
    private TextField unitField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea processDetailsArea;
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

    private final SamplePreparationService samplePreparationService;
    private final SampleRegistrationService sampleRegistrationService;
    private final ProductService productService;
    private final PersonnelService personnelService;

    private ObservableList<SamplePreparation> preparationList = FXCollections.observableArrayList();
    private SamplePreparation currentPreparation = null;

    @Autowired
    public SamplePreparationController(SamplePreparationService samplePreparationService,
                                       SampleRegistrationService sampleRegistrationService,
                                       ProductService productService,
                                       PersonnelService personnelService) {
        this.samplePreparationService = samplePreparationService;
        this.sampleRegistrationService = sampleRegistrationService;
        this.productService = productService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadPreparationData();

        preparationTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        preparationIdColumn.setCellValueFactory(new PropertyValueFactory<>("preparationId"));
        sampleRegistrationColumn.setCellValueFactory(new PropertyValueFactory<>("sampleRegistration"));
        sampleRegistrationColumn.setCellFactory(column -> new TableCell<SamplePreparation, SampleRegistration>() {
            @Override
            protected void updateItem(SampleRegistration item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSampleCode());
            }
        });
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        productColumn.setCellFactory(column -> new TableCell<SamplePreparation, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProductName());
            }
        });
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        operatorColumn.setCellFactory(column -> new TableCell<SamplePreparation, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        preparationDateColumn.setCellValueFactory(new PropertyValueFactory<>("preparationDate"));
        batchNumberColumn.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        quantityPreparedColumn.setCellValueFactory(new PropertyValueFactory<>("quantityPrepared"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        processDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("processDetails"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        preparationTableView.setItems(preparationList);
    }

    private void loadComboBoxData() {
        sampleRegistrationComboBox.setItems(FXCollections.observableArrayList(sampleRegistrationService.findAllSampleRegistrations()));
        sampleRegistrationComboBox.setConverter(new javafx.util.StringConverter<SampleRegistration>() {
            @Override
            public String toString(SampleRegistration registration) {
                return registration == null ? null : registration.getSampleCode() + " (" + registration.getCustomer().getCustomerName() + ")";
            }
            @Override
            public SampleRegistration fromString(String string) { return null; }
        });

        productComboBox.setItems(FXCollections.observableArrayList(productService.findAllProducts()));
        productComboBox.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getProductName();
            }
            @Override
            public Product fromString(String string) { return null; }
        });
        productComboBox.getItems().add(0, null); // Allow null selection

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

        statusComboBox.setItems(FXCollections.observableArrayList("待制备", "制备中", "制备完成", "已入库", "已发放", "已废弃"));
    }

    private void loadPreparationData() {
        List<SamplePreparation> currentPreparations = samplePreparationService.findAllSamplePreparations();
        preparationList.setAll(currentPreparations);
    }

    @FXML
    private void handleAddPreparation() {
        currentPreparation = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        sampleRegistrationComboBox.requestFocus();
    }

    @FXML
    private void handleEditPreparation() {
        SamplePreparation selectedPreparation = preparationTableView.getSelectionModel().getSelectedItem();
        if (selectedPreparation != null) {
            currentPreparation = selectedPreparation;
            populateForm(currentPreparation);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个制备记录进行编辑。");
        }
    }

    @FXML
    private void handleDeletePreparation() {
        SamplePreparation selectedPreparation = preparationTableView.getSelectionModel().getSelectedItem();
        if (selectedPreparation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除制备记录 ID: " + selectedPreparation.getPreparationId() + " (样本: " + selectedPreparation.getSampleRegistration().getSampleCode() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    samplePreparationService.deleteSamplePreparationById(selectedPreparation.getPreparationId().intValue());
                    loadPreparationData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "制备记录已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除制备记录时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个制备记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshPreparations() {
        loadPreparationData();
        loadComboBoxData(); // Refresh combo box data as well
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSavePreparation() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentPreparation == null || currentPreparation.getPreparationId() == null);
        SamplePreparation prepToSave = isNew ? new SamplePreparation() : currentPreparation;

        prepToSave.setSampleRegistration(sampleRegistrationComboBox.getValue());
        prepToSave.setProduct(productComboBox.getValue());
        prepToSave.setOperator(operatorComboBox.getValue());
        if (preparationDatePicker.getValue() != null) {
            prepToSave.setPreparationDate(preparationDatePicker.getValue().atStartOfDay());
        } else {
            prepToSave.setPreparationDate(LocalDateTime.now()); // Default to now if not set
        }
        prepToSave.setBatchNumber(batchNumberField.getText());
        try {
            prepToSave.setQuantityPrepared(new java.math.BigDecimal(quantityPreparedField.getText()));
        } catch (NumberFormatException e) {
            // Handled by validateInputs, but good to have a fallback or log
            prepToSave.setQuantityPrepared(java.math.BigDecimal.ZERO);
        }
        prepToSave.setUnit(unitField.getText());
        prepToSave.setStatus(statusComboBox.getValue());
        prepToSave.setProcessDetails(processDetailsArea.getText());
        prepToSave.setNotes(notesArea.getText());

        try {
            samplePreparationService.saveSamplePreparation(prepToSave);
            loadPreparationData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            preparationTableView.getSelectionModel().select(prepToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "制备记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存制备记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelPreparation() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        preparationTableView.getSelectionModel().clearSelection();
        currentPreparation = null;
    }

    private void populateForm(SamplePreparation preparation) {
        if (preparation != null) {
            sampleRegistrationComboBox.setValue(preparation.getSampleRegistration());
            productComboBox.setValue(preparation.getProduct());
            operatorComboBox.setValue(preparation.getOperator());
            preparationDatePicker.setValue(preparation.getPreparationDate() != null ? preparation.getPreparationDate().toLocalDate() : null);
            batchNumberField.setText(preparation.getBatchNumber());
            quantityPreparedField.setText(preparation.getQuantityPrepared() != null ? String.valueOf(preparation.getQuantityPrepared()) : "");
            unitField.setText(preparation.getUnit());
            statusComboBox.setValue(preparation.getStatus());
            processDetailsArea.setText(preparation.getProcessDetails());
            notesArea.setText(preparation.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        sampleRegistrationComboBox.setValue(null);
        productComboBox.setValue(null);
        operatorComboBox.setValue(null);
        preparationDatePicker.setValue(null);
        batchNumberField.clear();
        quantityPreparedField.clear();
        unitField.clear();
        statusComboBox.setValue(null);
        processDetailsArea.clear();
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        sampleRegistrationComboBox.setDisable(!editable);
        productComboBox.setDisable(!editable);
        operatorComboBox.setDisable(!editable);
        preparationDatePicker.setDisable(!editable);
        batchNumberField.setEditable(editable);
        quantityPreparedField.setEditable(editable);
        unitField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        processDetailsArea.setEditable(editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (sampleRegistrationComboBox.getValue() == null) {
            errors.append("必须选择一个关联样本。\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("必须选择状态。\n");
        }
        if (quantityPreparedField.getText() != null && !quantityPreparedField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(quantityPreparedField.getText());
            } catch (NumberFormatException e) {
                errors.append("制备数量必须是有效的整数。\n");
            }
        } else {
             errors.append("制备数量不能为空。\n");
        }
        // Add more specific validations as needed

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

