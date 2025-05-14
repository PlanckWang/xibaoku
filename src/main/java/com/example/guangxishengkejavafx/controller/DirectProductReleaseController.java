package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.DirectProductRelease;
import com.example.guangxishengkejavafx.model.entity.Product;
import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.service.DirectProductReleaseService;
import com.example.guangxishengkejavafx.service.ProductService;
import com.example.guangxishengkejavafx.service.PersonnelService;
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
public class DirectProductReleaseController {

    @FXML
    private TableView<DirectProductRelease> releaseTableView;
    @FXML
    private TableColumn<DirectProductRelease, Integer> releaseIdColumn;
    @FXML
    private TableColumn<DirectProductRelease, Product> productColumn;
    @FXML
    private TableColumn<DirectProductRelease, String> batchNumberColumn;
    @FXML
    private TableColumn<DirectProductRelease, Double> quantityReleasedColumn;
    @FXML
    private TableColumn<DirectProductRelease, String> unitColumn;
    @FXML
    private TableColumn<DirectProductRelease, LocalDateTime> releaseDateColumn;
    @FXML
    private TableColumn<DirectProductRelease, String> recipientNameColumn;
    @FXML
    private TableColumn<DirectProductRelease, Personnel> operatorColumn;
    @FXML
    private TableColumn<DirectProductRelease, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<DirectProductRelease, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<DirectProductRelease, String> purposeColumn;
    @FXML
    private TableColumn<DirectProductRelease, String> notesColumn;

    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField batchNumberField;
    @FXML
    private TextField quantityReleasedField;
    @FXML
    private TextField unitField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private TextField recipientNameField;
    @FXML
    private ComboBox<Personnel> operatorComboBox;
    @FXML
    private TextArea purposeArea;
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

    private final DirectProductReleaseService directProductReleaseService;
    private final ProductService productService;
    private final PersonnelService personnelService;

    private ObservableList<DirectProductRelease> releaseList = FXCollections.observableArrayList();
    private DirectProductRelease currentRelease = null;

    @Autowired
    public DirectProductReleaseController(DirectProductReleaseService directProductReleaseService,
                                          ProductService productService,
                                          PersonnelService personnelService) {
        this.directProductReleaseService = directProductReleaseService;
        this.productService = productService;
        this.personnelService = personnelService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadReleaseData();

        releaseTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        releaseIdColumn.setCellValueFactory(new PropertyValueFactory<>("releaseId"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        productColumn.setCellFactory(column -> new TableCell<DirectProductRelease, Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProductName());
            }
        });
        batchNumberColumn.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        quantityReleasedColumn.setCellValueFactory(new PropertyValueFactory<>("quantityReleased"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        recipientNameColumn.setCellValueFactory(new PropertyValueFactory<>("recipientName"));
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operator"));
        operatorColumn.setCellFactory(column -> new TableCell<DirectProductRelease, Personnel>() {
            @Override
            protected void updateItem(Personnel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        releaseTableView.setItems(releaseList);
    }

    private void loadComboBoxData() {
        productComboBox.setItems(FXCollections.observableArrayList(productService.findAllProducts()));
        productComboBox.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getProductName();
            }
            @Override
            public Product fromString(String string) { return null; }
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
    }

    private void loadReleaseData() {
        List<DirectProductRelease> currentReleases = directProductReleaseService.findAllDirectProductReleases();
        releaseList.setAll(currentReleases);
    }

    @FXML
    private void handleAddRelease() {
        currentRelease = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        productComboBox.requestFocus();
    }

    @FXML
    private void handleEditRelease() {
        DirectProductRelease selectedRelease = releaseTableView.getSelectionModel().getSelectedItem();
        if (selectedRelease != null) {
            currentRelease = selectedRelease;
            populateForm(currentRelease);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个发放记录进行编辑。");
        }
    }

    @FXML
    private void handleDeleteRelease() {
        DirectProductRelease selectedRelease = releaseTableView.getSelectionModel().getSelectedItem();
        if (selectedRelease != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除发放记录 ID: " + selectedRelease.getReleaseId() + " (产品: " + selectedRelease.getProduct().getProductName() + ")?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    directProductReleaseService.deleteDirectProductReleaseById(selectedRelease.getReleaseId());
                    loadReleaseData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "发放记录已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除发放记录时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个发放记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshReleases() {
        loadReleaseData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveRelease() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentRelease == null || currentRelease.getReleaseId() == null);
        DirectProductRelease releaseToSave = isNew ? new DirectProductRelease() : currentRelease;

        releaseToSave.setProduct(productComboBox.getValue());
        releaseToSave.setBatchNumber(batchNumberField.getText());
        try {
            releaseToSave.setQuantityReleased(Double.parseDouble(quantityReleasedField.getText()));
        } catch (NumberFormatException e) {
            // Handled by validateInputs
        }
        releaseToSave.setUnit(unitField.getText());
        releaseToSave.setReleaseDate(releaseDatePicker.getValue() != null ? releaseDatePicker.getValue().atStartOfDay() : LocalDateTime.now());
        releaseToSave.setRecipientName(recipientNameField.getText());
        releaseToSave.setOperator(operatorComboBox.getValue());
        releaseToSave.setPurpose(purposeArea.getText());
        releaseToSave.setNotes(notesArea.getText());

        try {
            directProductReleaseService.saveDirectProductRelease(releaseToSave);
            loadReleaseData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            releaseTableView.getSelectionModel().select(releaseToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "发放记录已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存发放记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelRelease() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        releaseTableView.getSelectionModel().clearSelection();
        currentRelease = null;
    }

    private void populateForm(DirectProductRelease release) {
        if (release != null) {
            productComboBox.setValue(release.getProduct());
            batchNumberField.setText(release.getBatchNumber());
            quantityReleasedField.setText(release.getQuantityReleased() != null ? String.valueOf(release.getQuantityReleased()) : "");
            unitField.setText(release.getUnit());
            releaseDatePicker.setValue(release.getReleaseDate() != null ? release.getReleaseDate().toLocalDate() : null);
            recipientNameField.setText(release.getRecipientName());
            operatorComboBox.setValue(release.getOperator());
            purposeArea.setText(release.getPurpose());
            notesArea.setText(release.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        productComboBox.setValue(null);
        batchNumberField.clear();
        quantityReleasedField.clear();
        unitField.clear();
        releaseDatePicker.setValue(null);
        recipientNameField.clear();
        operatorComboBox.setValue(null);
        purposeArea.clear();
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        productComboBox.setDisable(!editable);
        batchNumberField.setEditable(editable);
        quantityReleasedField.setEditable(editable);
        unitField.setEditable(editable);
        releaseDatePicker.setDisable(!editable);
        recipientNameField.setEditable(editable);
        operatorComboBox.setDisable(!editable);
        purposeArea.setEditable(editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (productComboBox.getValue() == null) {
            errors.append("必须选择一个产品。\n");
        }
        if (quantityReleasedField.getText() == null || quantityReleasedField.getText().trim().isEmpty()) {
            errors.append("发放数量不能为空。\n");
        } else {
            try {
                Double.parseDouble(quantityReleasedField.getText());
            } catch (NumberFormatException e) {
                errors.append("发放数量必须是有效的数字。\n");
            }
        }
        if (unitField.getText() == null || unitField.getText().trim().isEmpty()) {
            errors.append("单位不能为空。\n");
        }
        if (releaseDatePicker.getValue() == null) {
            errors.append("发放日期不能为空。\n");
        }
        if (recipientNameField.getText() == null || recipientNameField.getText().trim().isEmpty()) {
            errors.append("接收人不能为空。\n");
        }
        if (operatorComboBox.getValue() == null) {
            errors.append("必须选择操作员。\n");
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

