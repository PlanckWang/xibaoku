package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.model.entity.Department;
import com.example.guangxishengkejavafx.model.entity.Institution;
import com.example.guangxishengkejavafx.model.entity.Personnel;
import com.example.guangxishengkejavafx.model.entity.User;
import com.example.guangxishengkejavafx.service.DepartmentService;
import com.example.guangxishengkejavafx.service.InstitutionService;
import com.example.guangxishengkejavafx.service.PersonnelService;
import com.example.guangxishengkejavafx.service.UserService; // Assuming a UserService for User details
import javafx.beans.property.SimpleStringProperty;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PersonnelController {

    @FXML
    private TableView<PersonnelWrapper> personnelTableView;
    @FXML
    private TableColumn<PersonnelWrapper, Integer> personnelIdColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> employeeIdColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> fullNameColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> usernameColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> positionColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> departmentNameColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> institutionNameColumn;
    @FXML
    private TableColumn<PersonnelWrapper, LocalDate> hireDateColumn;
    @FXML
    private TableColumn<PersonnelWrapper, String> userActiveColumn;

    @FXML
    private TextField employeeIdField;
    @FXML
    private TextField positionField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextArea notesArea;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<Institution> institutionComboBox;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private TextField roleField;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Label statusLabel;

    private final PersonnelService personnelService;
    private final InstitutionService institutionService;
    private final DepartmentService departmentService;
    private final UserService userService; // To fetch user details for display

    private final ObservableList<PersonnelWrapper> personnelWrapperList = FXCollections.observableArrayList();
    private final ObservableList<Institution> institutionObservableList = FXCollections.observableArrayList();
    private final ObservableList<Department> departmentObservableList = FXCollections.observableArrayList();

    @Autowired
    public PersonnelController(PersonnelService personnelService, InstitutionService institutionService, DepartmentService departmentService, UserService userService) {
        this.personnelService = personnelService;
        this.institutionService = institutionService;
        this.departmentService = departmentService;
        this.userService = userService;
    }

    @FXML
    public void initialize() {
        personnelIdColumn.setCellValueFactory(new PropertyValueFactory<>("personnelId"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("userFullName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userUsername"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        departmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("userDepartmentName"));
        institutionNameColumn.setCellValueFactory(new PropertyValueFactory<>("userInstitutionName"));
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        userActiveColumn.setCellValueFactory(new PropertyValueFactory<>("userActiveStatus"));

        personnelTableView.setItems(personnelWrapperList);
        setupComboBoxes();
        loadInstitutionsForComboBox();
        loadPersonnelData();

        personnelTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateForm(newValue)
        );

        institutionComboBox.setOnAction(event -> {
            Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
            if (selectedInstitution != null) {
                loadDepartmentsForComboBox(selectedInstitution.getInstitutionId());
            } else {
                departmentComboBox.getItems().clear();
                departmentComboBox.setDisable(true);
            }
        });
    }

    private void setupComboBoxes() {
        institutionComboBox.setItems(institutionObservableList);
        institutionComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Institution institution) {
                return institution == null ? null : institution.getInstitutionName();
            }
            @Override
            public Institution fromString(String string) {
                return institutionObservableList.stream().filter(i -> i.getInstitutionName().equals(string)).findFirst().orElse(null);
            }
        });

        departmentComboBox.setItems(departmentObservableList);
        departmentComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department department) {
                return department == null ? null : department.getDepartmentName();
            }
            @Override
            public Department fromString(String string) {
                return departmentObservableList.stream().filter(d -> d.getDepartmentName().equals(string)).findFirst().orElse(null);
            }
        });
        departmentComboBox.setDisable(true); // Disable until an institution is selected
    }

    private void loadInstitutionsForComboBox() {
        try {
            institutionObservableList.setAll(institutionService.getAllInstitutions());
        } catch (Exception e) {
            statusLabel.setText("加载机构列表失败: " + e.getMessage());
        }
    }

    private void loadDepartmentsForComboBox(Integer institutionId) {
        try {
            departmentObservableList.setAll(departmentService.getDepartmentsByInstitutionId(institutionId));
            departmentComboBox.setDisable(departmentObservableList.isEmpty());
        } catch (Exception e) {
            statusLabel.setText("加载部门列表失败: " + e.getMessage());
        }
    }

    private void loadPersonnelData() {
        try {
            List<Personnel> personnelList = personnelService.getAllPersonnel();
            List<PersonnelWrapper> wrappers = personnelList.stream()
                .map(p -> new PersonnelWrapper(p, userService, institutionService, departmentService))
                .collect(Collectors.toList());
            personnelWrapperList.setAll(wrappers);
            statusLabel.setText("人员列表已加载。");
        } catch (Exception e) {
            statusLabel.setText("加载人员列表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateForm(PersonnelWrapper wrapper) {
        if (wrapper != null) {
            Personnel personnel = wrapper.getPersonnel();
            User user = wrapper.getUser(); 

            employeeIdField.setText(personnel.getEmployeeId());
            positionField.setText(personnel.getPosition());
            hireDatePicker.setValue(personnel.getHireDate());
            notesArea.setText(personnel.getNotes());

            if (user != null) {
                fullNameField.setText(user.getFullName());
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhoneNumber());
                roleField.setText(user.getRole());
                activeCheckBox.setSelected(user.getActive());
                passwordField.clear(); // Clear password for security

                if (user.getInstitutionId() != null) {
                    institutionService.getInstitutionById(user.getInstitutionId()).ifPresent(institutionComboBox::setValue);
                }
                if (user.getDepartmentId() != null) {
                     // Ensure departments are loaded for the selected institution first
                    if(institutionComboBox.getValue()!=null && institutionComboBox.getValue().getInstitutionId().equals(user.getInstitutionId())){
                        departmentService.getDepartmentById(user.getDepartmentId()).ifPresent(departmentComboBox::setValue);
                    } else {
                        departmentComboBox.setValue(null);
                    }
                }
            } else {
                clearUserFields();
            }
        } else {
            clearForm();
        }
    }

    private void clearUserFields(){
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        institutionComboBox.getSelectionModel().clearSelection();
        departmentComboBox.getSelectionModel().clearSelection();
        departmentObservableList.clear();
        departmentComboBox.setDisable(true);
        roleField.clear();
        activeCheckBox.setSelected(false);
    }

    private void clearForm() {
        employeeIdField.clear();
        positionField.clear();
        hireDatePicker.setValue(null);
        notesArea.clear();
        clearUserFields();
        personnelTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleAddPersonnel(ActionEvent event) {
        User newUser = new User();
        newUser.setFullName(fullNameField.getText());
        newUser.setUsername(usernameField.getText());
        newUser.setPasswordHash(passwordField.getText()); // Raw password, will be hashed in service
        newUser.setEmail(emailField.getText());
        newUser.setPhoneNumber(phoneField.getText());
        newUser.setRole(roleField.getText());
        newUser.setActive(activeCheckBox.isSelected());
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
        if (selectedInstitution != null) newUser.setInstitutionId(selectedInstitution.getInstitutionId());
        Department selectedDepartment = departmentComboBox.getSelectionModel().getSelectedItem();
        if (selectedDepartment != null) newUser.setDepartmentId(selectedDepartment.getDepartmentId());

        Personnel newPersonnel = new Personnel();
        newPersonnel.setEmployeeId(employeeIdField.getText());
        newPersonnel.setPosition(positionField.getText());
        newPersonnel.setHireDate(hireDatePicker.getValue());
        newPersonnel.setNotes(notesArea.getText());

        if (newUser.getUsername().isEmpty() || newUser.getPasswordHash().isEmpty() || newUser.getFullName().isEmpty()) {
            statusLabel.setText("用户名、密码和姓名不能为空。");
            return;
        }

        try {
            personnelService.savePersonnel(newUser, newPersonnel);
            statusLabel.setText("人员添加成功！");
            loadPersonnelData();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("添加人员失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdatePersonnel(ActionEvent event) {
        PersonnelWrapper selectedWrapper = personnelTableView.getSelectionModel().getSelectedItem();
        if (selectedWrapper == null) {
            statusLabel.setText("请先选择一个人员进行修改。");
            return;
        }

        User userUpdates = new User(); // Populate with fields from the form
        userUpdates.setFullName(fullNameField.getText());
        userUpdates.setUsername(usernameField.getText());
        if (passwordField.getText() != null && !passwordField.getText().isEmpty()) {
            userUpdates.setPasswordHash(passwordField.getText());
        }
        userUpdates.setEmail(emailField.getText());
        userUpdates.setPhoneNumber(phoneField.getText());
        userUpdates.setRole(roleField.getText());
        userUpdates.setActive(activeCheckBox.isSelected());
        Institution selectedInstitution = institutionComboBox.getSelectionModel().getSelectedItem();
        if (selectedInstitution != null) userUpdates.setInstitutionId(selectedInstitution.getInstitutionId());
        Department selectedDepartment = departmentComboBox.getSelectionModel().getSelectedItem();
        if (selectedDepartment != null) userUpdates.setDepartmentId(selectedDepartment.getDepartmentId());

        Personnel personnelUpdates = new Personnel();
        personnelUpdates.setEmployeeId(employeeIdField.getText());
        personnelUpdates.setPosition(positionField.getText());
        personnelUpdates.setHireDate(hireDatePicker.getValue());
        personnelUpdates.setNotes(notesArea.getText());
        
        if (userUpdates.getUsername().isEmpty() || userUpdates.getFullName().isEmpty()) {
            statusLabel.setText("用户名和姓名不能为空。");
            return;
        }

        try {
            personnelService.updatePersonnel(selectedWrapper.getPersonnel().getPersonnelId(), userUpdates, personnelUpdates);
            statusLabel.setText("人员更新成功！");
            loadPersonnelData();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("更新人员失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeletePersonnel(ActionEvent event) {
        PersonnelWrapper selectedWrapper = personnelTableView.getSelectionModel().getSelectedItem();
        if (selectedWrapper == null) {
            statusLabel.setText("请先选择一个人员进行删除。");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除人员: " + selectedWrapper.getUserFullName());
        alert.setContentText("您确定要删除此人员及其关联的用户账户吗？此操作无法撤销。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                personnelService.deletePersonnel(selectedWrapper.getPersonnel().getPersonnelId());
                statusLabel.setText("人员删除成功！");
                loadPersonnelData();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("删除人员失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleRefreshPersonnel(ActionEvent event) {
        loadPersonnelData();
        clearForm();
        statusLabel.setText("人员列表已刷新。");
    }

    // Inner class to wrap Personnel and related User, Institution, Department details for TableView
    public static class PersonnelWrapper {
        private final Personnel personnel;
        private final User user;
        private final Institution institution;
        private final Department department;

        public PersonnelWrapper(Personnel personnel, UserService userService, InstitutionService institutionService, DepartmentService departmentService) {
            this.personnel = personnel;
            this.user = userService.getUserById(personnel.getUserId()).orElse(null);
            if (this.user != null) {
                this.institution = this.user.getInstitutionId() != null ? institutionService.getInstitutionById(this.user.getInstitutionId()).orElse(null) : null;
                this.department = this.user.getDepartmentId() != null ? departmentService.getDepartmentById(this.user.getDepartmentId()).orElse(null) : null;
            } else {
                this.institution = null;
                this.department = null;
            }
        }

        public Personnel getPersonnel() { return personnel; }
        public User getUser() { return user; }

        public Integer getPersonnelId() { return personnel.getPersonnelId(); }
        public String getEmployeeId() { return personnel.getEmployeeId(); }
        public String getPosition() { return personnel.getPosition(); }
        public LocalDate getHireDate() { return personnel.getHireDate(); }
        
        public String getUserFullName() { return user != null ? user.getFullName() : "N/A"; }
        public String getUserUsername() { return user != null ? user.getUsername() : "N/A"; }
        public String getUserDepartmentName() { return department != null ? department.getDepartmentName() : (user != null && user.getDepartmentId() != null ? "Dept:"+user.getDepartmentId() : "N/A"); }
        public String getUserInstitutionName() { return institution != null ? institution.getInstitutionName() : (user != null && user.getInstitutionId() != null ? "Inst:"+user.getInstitutionId() : "N/A"); }
        public String getUserActiveStatus() { return user != null ? (user.getActive() ? "激活" : "禁用") : "N/A"; }
    }
}

