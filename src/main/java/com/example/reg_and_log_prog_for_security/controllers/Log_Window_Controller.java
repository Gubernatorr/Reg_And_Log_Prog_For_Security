package com.example.reg_and_log_prog_for_security.controllers;

import java.io.IOException;
import java.net.URL;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Log_Window_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button log_button;

    @FXML
    private Button reg_button;

    @FXML
    void reg_button_pressed(ActionEvent event) {
        openNewScene(reg_button, "/sample/reg_page.fxml");
    }

    @FXML
    void log_button_pressed(ActionEvent event) throws SQLException, ClassNotFoundException {

        String loginText = login_field.getText().trim();
        String loginPassword = password_field.getText().trim();

        if(!loginText.equals("") && !loginPassword.equals("")){
            System.out.println("All's good!");
            //loginUser(loginText, loginPassword);
        }else{
            System.out.println("Fields are empty!");
            Shake userLoginAnima = new Shake(login_field);
            Shake userPasswordAnima = new Shake(password_field);
            userLoginAnima.playAnim();
            userPasswordAnima.playAnim();
        }
    }

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

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

    private void loginUser(String loginText, String loginPassword) throws SQLException, ClassNotFoundException {
        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher();

        User user = new User();

        if(loginText != null && loginPassword != null){
            user.setLogin(loginText);
            user.setPassword(loginPassword);

            ResultSet rs = dbDispatcher.getUser(user);

            int counter = 0;

            while(rs.next()){
                counter++;
            }

            if(counter >= 1){
                User userOut = dbDispatcher.getAllUserInfo(user);

                openNewScene(log_button, "/sample/success_page.fxml");

            } else {
                Shake userLoginAnima = new Shake(login_field);
                Shake userPasswordAnima = new Shake(password_field);
                userLoginAnima.playAnim();
                userPasswordAnima.playAnim();
            }
        }else{
            Shake userLoginAnima = new Shake(login_field);
            Shake userPasswordAnima = new Shake(password_field);
            userLoginAnima.playAnim();
            userPasswordAnima.playAnim();
        }
    }

    @FXML
    void initialize() {

    }

}
