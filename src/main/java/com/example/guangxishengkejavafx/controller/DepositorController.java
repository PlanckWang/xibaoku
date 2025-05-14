package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Depositor;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.DepositorService;
import com.example.guangxishengkejavafx.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DepositorController {

    @FXML
    private TableView<DepositorWrapper> depositorTableView;
    @FXML
    private TableColumn<DepositorWrapper, Integer> depositorIdColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> nameColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> genderColumn;
    @FXML
    private TableColumn<DepositorWrapper, LocalDate> dateOfBirthColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> idCardNumberColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> phoneNumberColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> addressColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> emailColumn;
    @FXML
    private TableColumn<DepositorWrapper, LocalDateTime> registrationDateColumn;
    @FXML
    private TableColumn<DepositorWrapper, String> registeredByUserNameColumn;

    @FXML
    private TextField searchNameField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private TextField idCardNumberField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField occupationField;
    @FXML
    private TextField emergencyContactNameField;
    @FXML
    private TextField emergencyContactPhoneField;
    @FXML
    private DatePicker registrationDatePicker;
    @FXML
    private ComboBox<User> registeredByUserComboBox;
    @FXML
    private TextArea addressArea;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label statusLabel;

    private final DepositorService depositorService;
    private final UserService userService; // For loading users into ComboBox

    private final ObservableList<DepositorWrapper> depositorWrapperList = FXCollections.observableArrayList();
    private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private Depositor selectedDepositor = null;

    @Autowired
    public DepositorController(DepositorService depositorService, UserService userService) {
        this.depositorService = depositorService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        depositorIdColumn.setCellValueFactory(new PropertyValueFactory<>("depositorId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        idCardNumberColumn.setCellValueFactory(new PropertyValueFactory<>("idCardNumber"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        registeredByUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("registeredByUserName"));

        depositorTableView.setItems(depositorWrapperList);
        genderComboBox.setItems(FXCollections.observableArrayList("男", "女", "其他"));
        loadUsersForComboBox();
        setupRegisteredByUserComboBox();
        loadDepositors();

        depositorTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedDepositor = newSelection.getDepositor();
                populateForm(selectedDepositor);
            }
        });
    }

    private void loadUsersForComboBox() {
        try {
            userObservableList.setAll(userService.getAllActiveUsers());
        } catch (Exception e) {
            statusLabel.setText("加载用户列表失败: " + e.getMessage());
        }
    }

    private void setupRegisteredByUserComboBox() {
        registeredByUserComboBox.setItems(userObservableList);
        registeredByUserComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? null : user.getFullName() + " (" + user.getUsername() + ")";
            }

            @Override
            public User fromString(String string) {
                return userObservableList.stream()
                        .filter(user -> (user.getFullName() + " (" + user.getUsername() + ")").equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void loadDepositors() {
        try {
            List<Depositor> depositors = depositorService.getAllDepositors();
            List<DepositorWrapper> wrappers = depositors.stream()
                .map(d -> new DepositorWrapper(d, userService))
                .collect(Collectors.toList());
            depositorWrapperList.setAll(wrappers);
            statusLabel.setText("储户列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载储户列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(Depositor depositor) {
        nameField.setText(depositor.getName());
        genderComboBox.setValue(depositor.getGender());
        dateOfBirthPicker.setValue(depositor.getDateOfBirth());
        idCardNumberField.setText(depositor.getIdCardNumber());
        phoneNumberField.setText(depositor.getPhoneNumber());
        emailField.setText(depositor.getEmail());
        occupationField.setText(depositor.getOccupation());
        emergencyContactNameField.setText(depositor.getEmergencyContactName());
        emergencyContactPhoneField.setText(depositor.getEmergencyContactPhone());
        registrationDatePicker.setValue(depositor.getRegistrationDate() != null ? depositor.getRegistrationDate().toLocalDate() : null);
        addressArea.setText(depositor.getAddress());
        notesArea.setText(depositor.getNotes());
        if (depositor.getRegisteredByUserId() != null) {
            userService.getUserById(depositor.getRegisteredByUserId()).ifPresent(registeredByUserComboBox::setValue);
        } else {
            registeredByUserComboBox.getSelectionModel().clearSelection();
        }
    }

    private void clearForm() {
        nameField.clear();
        genderComboBox.getSelectionModel().clearSelection();
        dateOfBirthPicker.setValue(null);
        idCardNumberField.clear();
        phoneNumberField.clear();
        emailField.clear();
        occupationField.clear();
        emergencyContactNameField.clear();
        emergencyContactPhoneField.clear();
        registrationDatePicker.setValue(null);
        registeredByUserComboBox.getSelectionModel().clearSelection();
        addressArea.clear();
        notesArea.clear();
        selectedDepositor = null;
        depositorTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddDepositor(ActionEvent event) {
        if (nameField.getText().isEmpty() || idCardNumberField.getText().isEmpty()) {
            statusLabel.setText("姓名和身份证号不能为空。");
            return;
        }
        Depositor newDepositor = new Depositor();
        updateDepositorFromForm(newDepositor);

        try {
            depositorService.saveDepositor(newDepositor);
            statusLabel.setText("储户添加成功！");
            loadDepositors();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加储户失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdateDepositor(ActionEvent event) {
        if (selectedDepositor == null) {
            statusLabel.setText("请先选择一个储户进行修改。");
            return;
        }
        if (nameField.getText().isEmpty() || idCardNumberField.getText().isEmpty()) {
            statusLabel.setText("姓名和身份证号不能为空。");
            return;
        }
        updateDepositorFromForm(selectedDepositor);
        try {
            depositorService.updateDepositor(selectedDepositor.getDepositorId(), selectedDepositor);
            statusLabel.setText("储户信息更新成功！");
            loadDepositors();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新储户信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateDepositorFromForm(Depositor depositor) {
        depositor.setName(nameField.getText());
        depositor.setGender(genderComboBox.getValue());
        depositor.setDateOfBirth(dateOfBirthPicker.getValue());
        depositor.setIdCardNumber(idCardNumberField.getText());
        depositor.setPhoneNumber(phoneNumberField.getText());
        depositor.setEmail(emailField.getText());
        depositor.setOccupation(occupationField.getText());
        depositor.setEmergencyContactName(emergencyContactNameField.getText());
        depositor.setEmergencyContactPhone(emergencyContactPhoneField.getText());
        if (registrationDatePicker.getValue() != null) {
            depositor.setRegistrationDate(registrationDatePicker.getValue().atStartOfDay());
        }
        User selectedUser = registeredByUserComboBox.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            depositor.setRegisteredByUserId(selectedUser.getUserId());
        }
        depositor.setAddress(addressArea.getText());
        depositor.setNotes(notesArea.getText());
    }

    @FXML
    void handleDeleteDepositor(ActionEvent event) {
        if (selectedDepositor == null) {
            statusLabel.setText("请先选择一个储户进行删除。");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除储户: " + selectedDepositor.getName());
        alert.setContentText("您确定要删除此储户吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                depositorService.deleteDepositor(selectedDepositor.getDepositorId());
                statusLabel.setText("储户删除成功！");
                loadDepositors();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除储户失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleSearchDepositor(ActionEvent event) {
        String searchTerm = searchNameField.getText();
        if (searchTerm == null || searchTerm.isEmpty()) {
            loadDepositors();
            return;
        }
        try {
            List<Depositor> depositors = depositorService.findDepositorsByName(searchTerm);
            List<DepositorWrapper> wrappers = depositors.stream()
                .map(d -> new DepositorWrapper(d, userService))
                .collect(Collectors.toList());
            depositorWrapperList.setAll(wrappers);
            statusLabel.setText("找到 " + depositors.size() + " 个储户。");
        } catch (Exception e) {
            statusLabel.setText("搜索储户失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleRefreshDepositors(ActionEvent event) {
        searchNameField.clear();
        loadDepositors();
        clearForm();
        statusLabel.setText("储户列表已刷新。");
    }

    // Wrapper class for TableView display
    public static class DepositorWrapper {
        private final Depositor depositor;
        private final UserService userService;

        public DepositorWrapper(Depositor depositor, UserService userService) {
            this.depositor = depositor;
            this.userService = userService;
        }

        public Depositor getDepositor() { return depositor; }
        public Integer getDepositorId() { return depositor.getDepositorId(); }
        public String getName() { return depositor.getName(); }
        public String getGender() { return depositor.getGender(); }
        public LocalDate getDateOfBirth() { return depositor.getDateOfBirth(); }
        public String getIdCardNumber() { return depositor.getIdCardNumber(); }
        public String getPhoneNumber() { return depositor.getPhoneNumber(); }
        public String getAddress() { return depositor.getAddress(); }
        public String getEmail() { return depositor.getEmail(); }
        public LocalDateTime getRegistrationDate() { return depositor.getRegistrationDate(); }
        public String getRegisteredByUserName() {
            if (depositor.getRegisteredByUserId() == null) return "N/A";
            return userService.getUserById(depositor.getRegisteredByUserId())
                    .map(User::getFullName)
                    .orElse("未知用户 (" + depositor.getRegisteredByUserId() + ")");
        }
    }
}

