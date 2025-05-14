package com.example.guangxishengkejavafx.controller;

import com.example.guangxishengkejavafx.JavaFxApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("用户名和密码不能为空。");
            return;
        }

        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                statusLabel.setText("登录成功!");
                // Proceed to the main application window
                loadMainApplicationWindow();
            } else {
                statusLabel.setText("登录失败，请检查您的凭据。");
            }
        } catch (AuthenticationException e) {
            statusLabel.setText("认证失败: " + e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMainApplicationWindow() {
        try {
            // Get the current stage (window) from the login button
            Stage currentStage = (Stage) loginButton.getScene().getWindow();

            // Load the FXML for the main application window
            // Assuming you have a MainView.fxml or similar for the post-login screen
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml")); 
            // Important: Set the controller factory if your main view controller is also Spring managed
            fxmlLoader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            Parent mainRoot = fxmlLoader.load();
            
            Scene mainScene = new Scene(mainRoot, 1200, 800); // Adjust size as needed
            currentStage.setScene(mainScene);
            currentStage.setTitle("广西生科·创新诊疗中心 - 主界面"); // Update title
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("无法加载主界面: " + e.getMessage());
        }
    }
}

