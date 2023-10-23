package com.example.reg_and_log_prog_for_security;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dispatcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Dispatcher.class.getResource("/sample/log_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1535, 795);
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}