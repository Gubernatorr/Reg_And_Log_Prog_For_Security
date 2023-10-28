package com.example.reg_and_log_prog_for_security.controllers;

import java.io.IOException;
import java.net.URL;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

import com.example.reg_and_log_prog_for_security.animations.Shake;
import com.example.reg_and_log_prog_for_security.configs.DatabaseDispatcher;
import com.example.reg_and_log_prog_for_security.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.example.reg_and_log_prog_for_security.controllers.Reg_Window_Controller.hashPassword;

public class Log_Window_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button log_button;

    @FXML
    private Button reg_button;

    private int failedLoginAttempts = 0;
    private long blockedTime = 0;

    @FXML
    void reg_button_pressed(ActionEvent event) {
        openNewScene(reg_button, "/sample/reg_page.fxml");
    }
    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Text password_check_text;

    public void openNewScene(Button button, String window){
        button.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void log_button_pressed(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (isUserBlocked()) {
            modalWindow();
        }

        String loginText = login_field.getText().trim();
        String loginPassword = password_field.getText().trim();

        if (!loginText.equals("") && !loginPassword.equals("")) {
            System.out.println("All's good!");
            loginUser(loginText, loginPassword);
        } else {
            System.out.println("Fields are empty!");
            Shake userLoginAnima = new Shake(login_field);
            Shake userPasswordAnima = new Shake(password_field);
            userLoginAnima.playAnim();
            userPasswordAnima.playAnim();
        }
    }
    private boolean isUserBlocked() {
        if (failedLoginAttempts >= 3) {
            long currentTime = System.currentTimeMillis();
            if (blockedTime == 0) {
                blockedTime = currentTime + 15000; // 15 секунд блокування
            } else if (currentTime >= blockedTime) {
                // Після 15 секунд розблокувати користувача
                failedLoginAttempts = 0;
                blockedTime = 0;
            }
        }
        return failedLoginAttempts >= 3;
    }

    public static boolean verifyPassword(String enteredPassword, String hashedPassword) {
        String enteredPasswordHash = hashPassword(enteredPassword);
        assert enteredPasswordHash != null;
        return enteredPasswordHash.equals(hashedPassword);
    }
    private void loginUser(String loginText, String loginPassword) throws SQLException, ClassNotFoundException {
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher();

        if (loginText != null && loginPassword != null) {
            if(!dbDispatcher.isUserLoginExists(login_field.getText())){
                notSuchUserFoundWindow();
            } else if(dbDispatcher.isUserLoginExists(login_field.getText())){
                User user_for_sec = dbDispatcher.getAllUserInfo(login_field.getText());
                //Objects.equals(user_for_sec.getPassword(), password_field.getText())
                if(verifyPassword(password_field.getText(), user_for_sec.getPassword())){
                    // Успішний вхід, скинути лічильник невдалих спроб
                    failedLoginAttempts = 0;
                    blockedTime = 0;
                    openNewScene(log_button, "/sample/success_page.fxml");
                }else {
                    // Невдалий вхід, збільшити лічильник невдалих спроб
                    failedLoginAttempts++;
                    switch (failedLoginAttempts) {
                        case 1 -> {
                            password_check_text.setText("You have 2 more attempts");
                            password_check_text.setFill(Color.RED);
                        }
                        case 2 -> {
                            password_check_text.setText("You have 1 more attempts");
                            password_check_text.setFill(Color.RED);
                        }
                        case 3 -> {
                            password_check_text.setText("You were blocked for 15sec");
                            password_check_text.setFill(Color.RED);
                            blockedTime = System.currentTimeMillis() + 15000;
                        }
                        default -> System.out.println("Smth wrong with the input type or smth!");
                    }

                    Shake userLoginAnima = new Shake(login_field);
                    Shake userPasswordAnima = new Shake(password_field);
                    userLoginAnima.playAnim();
                    userPasswordAnima.playAnim();
                }
            }
        } else {
            Shake userLoginAnima = new Shake(login_field);
            Shake userPasswordAnima = new Shake(password_field);
            userLoginAnima.playAnim();
            userPasswordAnima.playAnim();
        }
    }

    public void notSuchUserFoundWindow() {
        Stage window = new Stage();
        window.setTitle("Alert");

        TextArea txt = new TextArea("There are no users with that kind of login. "+"\n"
                +"Do you wont to create an Account?");
        txt.setPrefWidth(325);
        txt.setPrefHeight(150);

        window.initModality(Modality.APPLICATION_MODAL);

        Pane pane = new Pane();

        Button btn1 = new Button("Close");
        btn1.setLayoutX(40);
        btn1.setLayoutY(100);

        btn1.setOnAction(event -> window.close());

        Button btn2 = new Button("Register");
        btn2.setLayoutX(200);
        btn2.setLayoutY(100);

        btn2.setOnAction(event -> {
            openNewScene(reg_button, "/sample/reg_page.fxml");
        });


        pane.getChildren().addAll(txt);
        pane.getChildren().addAll(btn1);
        pane.getChildren().addAll(btn2);

        Scene scene = new Scene(pane, 325, 150);
        window.setScene(scene);
        window.showAndWait();
    }
    public void modalWindow() {
        Stage window = new Stage();
        window.setTitle("Alert");

        TextArea txt = new TextArea("You have tried to enter an incorrect password more than 3 " +
                "\n" + "times. You have been blocked for 15 seconds");
        txt.setPrefWidth(325);
        txt.setPrefHeight(150);

        window.initModality(Modality.APPLICATION_MODAL);

        Pane pane = new Pane();

        Button btn1 = new Button("Close");
        btn1.setLayoutX(140);
        btn1.setLayoutY(100);

        btn1.setOnAction(event -> window.close());

        pane.getChildren().addAll(txt);
        pane.getChildren().addAll(btn1);

        Scene scene = new Scene(pane, 325, 150);
        window.setScene(scene);
        window.showAndWait();
    }
    @FXML
    void initialize() {

    }

}
