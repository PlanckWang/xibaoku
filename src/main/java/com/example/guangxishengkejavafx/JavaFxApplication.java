package com.example.guangxishengkejavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext; // Instance variable
    private static ConfigurableApplicationContext staticApplicationContext; // Static variable

    @Override
    public void init() {
        // Initialize the instance variable first
        this.applicationContext = new SpringApplicationBuilder(MainApplication.class).run();
        // Then assign it to the static variable
        JavaFxApplication.staticApplicationContext = this.applicationContext;
    }

    public static ConfigurableApplicationContext getSpringContext() {
        return staticApplicationContext;
    }

    @Override
    public void start(Stage stage) {
        // Use the instance variable here
        if (this.applicationContext != null) {
            this.applicationContext.publishEvent(new StageReadyEvent(stage));
            // Load the initial FXML, e.g., login screen
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml")); // Placeholder FXML
                // It's good practice to set the controller factory if Spring manages controllers
                fxmlLoader.setControllerFactory(this.applicationContext::getBean); // Use instance variable
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root, 800, 600);
                stage.setTitle("广西生科·创新诊疗中心");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Platform.exit();
            }
        } else {
            System.err.println("ApplicationContext not initialized in start method.");
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // Use the instance variable here
        if (this.applicationContext != null) {
            this.applicationContext.close();
        }
        Platform.exit();
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}

