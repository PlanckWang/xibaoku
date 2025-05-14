package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.Order;
import com.example.guangxishengkejavafx.model.entity.SampleRegistration;
import com.example.guangxishengkejavafx.service.DepositorService;
import com.example.guangxishengkejavafx.service.OrderService;
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
public class SampleRegistrationController {

    @FXML
    private TableView<SampleRegistration> registrationTableView;
    @FXML
    private TableColumn<SampleRegistration, Integer> registrationIdColumn;
    @FXML
    private TableColumn<SampleRegistration, String> sampleCodeColumn;
    @FXML
    private TableColumn<SampleRegistration, Depositor> customerColumn;
    @FXML
    private TableColumn<SampleRegistration, Order> orderColumn;
    @FXML
    private TableColumn<SampleRegistration, String> sampleTypeColumn;
    @FXML
    private TableColumn<SampleRegistration, LocalDateTime> collectionDateColumn;
    @FXML
    private TableColumn<SampleRegistration, String> collectorColumn;
    @FXML
    private TableColumn<SampleRegistration, String> collectionSiteColumn;
    @FXML
    private TableColumn<SampleRegistration, String> statusColumn;
    @FXML
    private TableColumn<SampleRegistration, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<SampleRegistration, String> notesColumn;

    @FXML
    private TextField sampleCodeField;
    @FXML
    private ComboBox<Depositor> customerComboBox;
    @FXML
    private ComboBox<Order> orderComboBox;
    @FXML
    private TextField sampleTypeField;
    @FXML
    private DatePicker collectionDatePicker;
    @FXML
    private TextField collectorField;
    @FXML
    private TextField collectionSiteField;
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

    private final SampleRegistrationService sampleRegistrationService;
    private final DepositorService depositorService;
    private final OrderService orderService;

    private ObservableList<SampleRegistration> registrationList = FXCollections.observableArrayList();
    private SampleRegistration currentRegistration = null;

    @Autowired
    public SampleRegistrationController(SampleRegistrationService sampleRegistrationService, DepositorService depositorService, OrderService orderService) {
        this.sampleRegistrationService = sampleRegistrationService;
        this.depositorService = depositorService;
        this.orderService = orderService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadComboBoxData();
        loadRegistrationData();

        registrationTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateForm(newValue));

        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    private void setupTableColumns() {
        registrationIdColumn.setCellValueFactory(new PropertyValueFactory<>("registrationId"));
        sampleCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sampleCode"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        customerColumn.setCellFactory(column -> new TableCell<SampleRegistration, Depositor>() {
            @Override
            protected void updateItem(Depositor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCustomerName());
            }
        });
        orderColumn.setCellValueFactory(new PropertyValueFactory<>("order"));
        orderColumn.setCellFactory(column -> new TableCell<SampleRegistration, Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getOrderNumber());
            }
        });
        sampleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("sampleType"));
        collectionDateColumn.setCellValueFactory(new PropertyValueFactory<>("collectionDate"));
        collectorColumn.setCellValueFactory(new PropertyValueFactory<>("collector"));
        collectionSiteColumn.setCellValueFactory(new PropertyValueFactory<>("collectionSite"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        registrationTableView.setItems(registrationList);
    }

    private void loadComboBoxData() {
        customerComboBox.setItems(FXCollections.observableArrayList(depositorService.findAllDepositors()));
        customerComboBox.setConverter(new javafx.util.StringConverter<Depositor>() {
            @Override
            public String toString(Depositor depositor) {
                return depositor == null ? null : depositor.getCustomerName();
            }
            @Override
            public Depositor fromString(String string) { return null; }
        });

        orderComboBox.setItems(FXCollections.observableArrayList(orderService.findAllOrders()));
        orderComboBox.setConverter(new javafx.util.StringConverter<Order>() {
            @Override
            public String toString(Order order) {
                return order == null ? null : order.getOrderNumber();
            }
            @Override
            public Order fromString(String string) { return null; }
        });
        orderComboBox.getItems().add(0, null); // Allow null selection

        statusComboBox.setItems(FXCollections.observableArrayList("待接收", "已接收", "检测中", "已入库", "已废弃"));
    }

    private void loadRegistrationData() {
        List<SampleRegistration> currentRegistrations = sampleRegistrationService.findAllSampleRegistrations();
        registrationList.setAll(currentRegistrations);
    }

    @FXML
    private void handleAddRegistration() {
        currentRegistration = null;
        clearForm();
        setFormEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        sampleCodeField.requestFocus();
    }

    @FXML
    private void handleEditRegistration() {
        SampleRegistration selectedRegistration = registrationTableView.getSelectionModel().getSelectedItem();
        if (selectedRegistration != null) {
            currentRegistration = selectedRegistration;
            populateForm(currentRegistration);
            setFormEditable(true);
            saveButton.setDisable(false);
            cancelButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个登记记录进行编辑。");
        }
    }

    @FXML
    private void handleDeleteRegistration() {
        SampleRegistration selectedRegistration = registrationTableView.getSelectionModel().getSelectedItem();
        if (selectedRegistration != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除样本登记: " + selectedRegistration.getSampleCode() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    sampleRegistrationService.deleteSampleRegistrationById(selectedRegistration.getRegistrationId());
                    loadRegistrationData();
                    clearForm();
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "样本登记已成功删除。");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "删除样本登记时发生错误: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "未选择", "请先选择一个登记记录进行删除。");
        }
    }

    @FXML
    private void handleRefreshRegistrations() {
        loadRegistrationData();
        loadComboBoxData();
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @FXML
    private void handleSaveRegistration() {
        if (!validateInputs()) {
            return;
        }

        boolean isNew = (currentRegistration == null || currentRegistration.getRegistrationId() == null);
        SampleRegistration registrationToSave = isNew ? new SampleRegistration() : currentRegistration;

        registrationToSave.setSampleCode(sampleCodeField.getText());
        registrationToSave.setCustomer(customerComboBox.getValue());
        registrationToSave.setOrder(orderComboBox.getValue()); // Can be null
        registrationToSave.setSampleType(sampleTypeField.getText());
        if (collectionDatePicker.getValue() != null) {
            registrationToSave.setCollectionDate(collectionDatePicker.getValue().atStartOfDay());
        }
        registrationToSave.setCollector(collectorField.getText());
        registrationToSave.setCollectionSite(collectionSiteField.getText());
        registrationToSave.setStatus(statusComboBox.getValue());
        registrationToSave.setNotes(notesArea.getText());

        try {
            sampleRegistrationService.saveSampleRegistration(registrationToSave);
            loadRegistrationData();
            clearForm();
            setFormEditable(false);
            saveButton.setDisable(true);
            cancelButton.setDisable(true);
            registrationTableView.getSelectionModel().select(registrationToSave);
            showAlert(Alert.AlertType.INFORMATION, "保存成功", "样本登记已成功保存。");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "保存失败", "保存样本登记时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelRegistration() {
        clearForm();
        setFormEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        registrationTableView.getSelectionModel().clearSelection();
        currentRegistration = null;
    }

    private void populateForm(SampleRegistration registration) {
        if (registration != null) {
            sampleCodeField.setText(registration.getSampleCode());
            customerComboBox.setValue(registration.getCustomer());
            orderComboBox.setValue(registration.getOrder());
            sampleTypeField.setText(registration.getSampleType());
            collectionDatePicker.setValue(registration.getCollectionDate() != null ? registration.getCollectionDate().toLocalDate() : null);
            collectorField.setText(registration.getCollector());
            collectionSiteField.setText(registration.getCollectionSite());
            statusComboBox.setValue(registration.getStatus());
            notesArea.setText(registration.getNotes());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        sampleCodeField.clear();
        customerComboBox.setValue(null);
        orderComboBox.setValue(null);
        sampleTypeField.clear();
        collectionDatePicker.setValue(null);
        collectorField.clear();
        collectionSiteField.clear();
        statusComboBox.setValue(null);
        notesArea.clear();
    }

    private void setFormEditable(boolean editable) {
        sampleCodeField.setEditable(editable);
        customerComboBox.setDisable(!editable);
        orderComboBox.setDisable(!editable);
        sampleTypeField.setEditable(editable);
        collectionDatePicker.setDisable(!editable);
        collectorField.setEditable(editable);
        collectionSiteField.setEditable(editable);
        statusComboBox.setDisable(!editable);
        notesArea.setEditable(editable);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        if (sampleCodeField.getText() == null || sampleCodeField.getText().trim().isEmpty()) {
            errors.append("样本编号不能为空。\n");
        }
        if (customerComboBox.getValue() == null) {
            errors.append("必须选择一个储户。\n");
        }
        if (sampleTypeField.getText() == null || sampleTypeField.getText().trim().isEmpty()) {
            errors.append("样本类型不能为空。\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("必须选择状态。\n");
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

