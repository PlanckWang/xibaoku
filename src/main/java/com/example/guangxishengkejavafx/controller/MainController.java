package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.JavaFxApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainController {

    @FXML
    private Label statusMessageLabel; // For displaying status messages or welcome text

    private final ApplicationContext applicationContext;

    @Autowired
    public MainController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = applicationContext.getBean(FXMLLoader.class);
            fxmlLoader.setLocation(getClass().getResource(fxmlPath));
            Parent view = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(view));
            // Optional: Set modality if it should block the main window
            // stage.initModality(Modality.APPLICATION_MODAL);
            // Optional: Set owner if it's a child window
            // stage.initOwner(statusMessageLabel.getScene().getWindow()); 
            stage.show();
            statusMessageLabel.setText(title + " 界面已打开。");
        } catch (IOException e) {
            e.printStackTrace();
            statusMessageLabel.setText("无法加载界面: " + fxmlPath);
            showAlert(javafx.scene.control.Alert.AlertType.ERROR, "加载错误", "无法加载界面: " + title + "\n" + e.getMessage());
        }
    }

    // --- File Menu Actions ---
    @FXML
    void handleExitAction(ActionEvent event) {
        Platform.exit();
    }

    // --- Basic Settings Menu Actions ---
    @FXML
    void handleOpenInstitutionManagement(ActionEvent event) {
        loadView("/fxml/InstitutionView.fxml", "机构管理");
    }

    @FXML
    void handleOpenDepartmentManagement(ActionEvent event) {
        loadView("/fxml/DepartmentView.fxml", "部门管理");
    }

    @FXML
    void handleOpenPersonnelManagement(ActionEvent event) {
        loadView("/fxml/PersonnelView.fxml", "人员管理");
    }

    @FXML
    void handleOpenProductManagement(ActionEvent event) {
        loadView("/fxml/ProductView.fxml", "产品管理");
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        statusMessageLabel.setText("修改密码功能待实现。");
        // TODO: Load Change Password View (create FXML and Controller first)
    }

    // --- Material Management Menu Actions ---
    @FXML
    void handleOpenMaterialList(ActionEvent event) {
        loadView("/fxml/MaterialView.fxml", "物料列表");
    }

    @FXML
    void handleOpenInboundRecords(ActionEvent event) {
        loadView("/fxml/MaterialInboundRecordView.fxml", "入库记录");
    }

    @FXML
    void handleOpenOutboundRecords(ActionEvent event) {
        loadView("/fxml/MaterialOutboundRecordView.fxml", "出库记录");
    }

    @FXML
    void handleOpenReturnRecords(ActionEvent event) {
        loadView("/fxml/MaterialReturnRecordView.fxml", "退库记录");
    }
    
    // --- Sample Order Menu Actions ---
    @FXML
    void handleOpenCustomerManagement(ActionEvent event) {
        loadView("/fxml/DepositorView.fxml", "储户管理");
    }

    @FXML
    void handleOpenOrderList(ActionEvent event) {
        loadView("/fxml/OrderView.fxml", "订单列表");
    }

    @FXML
    void handleOpenOrderAudit(ActionEvent event) {
        loadView("/fxml/OrderAuditView.fxml", "订单审核");
    }

    // --- Storage Contract Menu Actions ---
    @FXML
    void handleOpenContractList(ActionEvent event) {
        loadView("/fxml/StorageContractView.fxml", "合同列表");
    }

    // --- Sample Reception Menu Actions ---
    @FXML
    void handleOpenSampleRegistration(ActionEvent event) {
        loadView("/fxml/SampleRegistrationView.fxml", "样本采集登记");
    }

    @FXML
    void handleOpenSampleInspectionRequest(ActionEvent event) {
        loadView("/fxml/SampleInspectionRequestView.fxml", "采集物请检");
    }

    @FXML
    void handleOpenSampleTestResult(ActionEvent event) {
        loadView("/fxml/SampleTestResultView.fxml", "采集物检验");
    }

    // --- Sample Preparation Menu Actions ---
    @FXML
    void handleOpenSamplePreparationList(ActionEvent event) {
        loadView("/fxml/SamplePreparationView.fxml", "制备列表");
    }

    @FXML
    void handleOpenSampleCulture(ActionEvent event) {
        loadView("/fxml/SampleCultureView.fxml", "样本培养");
    }

    @FXML
    void handleOpenDirectProductRelease(ActionEvent event) {
        loadView("/fxml/DirectProductReleaseView.fxml", "直接发放产品");
    }

    @FXML
    void handleOpenFrozenSample(ActionEvent event) {
        loadView("/fxml/FrozenSampleView.fxml", "冻存样本");
    }

    // --- Technical Statistics Menu Actions ---
    @FXML
    void handleOpenStorageReport(ActionEvent event) {
        loadView("/fxml/StorageReportView.fxml", "储存类报表");
    }

    @FXML
    void handleOpenApplicationReport(ActionEvent event) {
        loadView("/fxml/ApplicationReportView.fxml", "应用类报表");
    }

    @FXML
    void handleOpenDetectionReport(ActionEvent event) {
        loadView("/fxml/DetectionReportView.fxml", "检测类报表");
    }

    @FXML
    void handleOpenDerivativeProductReport(ActionEvent event) {
        loadView("/fxml/DerivativeProductReportView.fxml", "衍生品报表");
    }

    // --- Cell Bank Menu Actions ---
    @FXML
    void handleOpenOriginalCellBank(ActionEvent event) { 
        loadView("/fxml/OriginalCellBankView.fxml", "原始细胞库");
    }

    @FXML
    void handleOpenMasterCellBank(ActionEvent event) { // Added for MasterCellBank
        loadView("/fxml/MasterCellBankView.fxml", "主细胞库");
    }

    @FXML
    void handleOpenWorkingCellBank(ActionEvent event) { // Added for WorkingCellBank
        // Corrected FXML path for working cell bank view
        loadView("/fxml/CellBankView.fxml", "工作细胞库");
    }

    @FXML
    void handleOpenCellBankAudit(ActionEvent event) { // Added for CellBankAudit
        loadView("/fxml/CellBankAuditView.fxml", "细胞入库审核");
    }

    // --- Cell Outbound Menu Actions ---
    @FXML
    void handleOpenCellProductionPlan(ActionEvent event) { // Added for CellProductionPlan
        loadView("/fxml/CellProductionPlanView.fxml", "细胞生产计划");
    }

    @FXML
    void handleOpenCellDisposalRequest(ActionEvent event) { // Added for CellDisposalRequest
        loadView("/fxml/CellDisposalRequestView.fxml", "细胞废弃申请");
    }

    @FXML
    void handleOpenCellOutboundApplication(ActionEvent event) { // Added for CellOutboundApplication
        loadView("/fxml/CellOutboundApplicationView.fxml", "出库申请");
    }

    // --- Project Management Menu Actions ---
    @FXML
    void handleOpenProjectTypeManagement(ActionEvent event) { // Added for ProjectType
        loadView("/fxml/ProjectTypeView.fxml", "项目类型管理");
    }

    @FXML
    void handleOpenProjectManagement(ActionEvent event) { // Added for Project
        loadView("/fxml/ProjectView.fxml", "项目管理");
    }

    // --- Clinical Research Menu Actions ---
    @FXML
    void handleOpenHealthRecordManagement(ActionEvent event) { // Added for HealthRecord
        loadView("/fxml/HealthRecordView.fxml", "健康管理");
    }

    @FXML
    void handleOpenResearchProtocolManagement(ActionEvent event) { // Added for ResearchProtocol
        loadView("/fxml/ResearchProtocolView.fxml", "研究方案管理");
    }

    @FXML
    void handleOpenAssessmentReportManagement(ActionEvent event) { // Added for AssessmentReport
        loadView("/fxml/AssessmentReportView.fxml", "评估报告管理");
    }

    // --- Public Resources Menu Actions ---
    @FXML
    void handleOpenResourceStatisticManagement(ActionEvent event) { // Added for ResourceStatistic
        loadView("/fxml/ResourceStatisticView.fxml", "资源统计");
    }

    @FXML
    void handleOpenKeywordManagement(ActionEvent event) { // Added for Keyword
        loadView("/fxml/KeywordView.fxml", "Keyword 管理");
    }

    @FXML
    void handleOpenLiteratureManagement(ActionEvent event) { // Added for Literature
        loadView("/fxml/LiteratureView.fxml", "文献管理");
    }

    // --- Help Menu Actions ---
    @FXML
    void handleAboutAction(ActionEvent event) {
        statusMessageLabel.setText("关于信息：广西生科·创新诊疗中心管理系统 v1.0");
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("广西生科·创新诊疗中心管理系统");
        alert.setContentText("版本: 1.0\n开发者: Manus AI");
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        statusMessageLabel.setText("欢迎使用系统！");
    }

    private void showAlert(javafx.scene.control.Alert.AlertType type, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


