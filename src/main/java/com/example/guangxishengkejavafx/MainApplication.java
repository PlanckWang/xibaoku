package com.example.guangxishengkejavafx;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        // This will launch the JavaFX application through a separate Application class
        // SpringApplication.run(MainApplication.class, args); // Keep this if you want Spring context for non-JavaFX parts
        Application.launch(JavaFxApplication.class, args);
    }
}

