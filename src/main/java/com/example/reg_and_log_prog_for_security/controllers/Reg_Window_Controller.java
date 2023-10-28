package com.example.reg_and_log_prog_for_security.controllers;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.example.reg_and_log_prog_for_security.animations.Shake;
import com.example.reg_and_log_prog_for_security.configs.DatabaseDispatcher;
import com.example.reg_and_log_prog_for_security.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Reg_Window_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text login_check_text;

    @FXML
    private Text password_check_text;

    @FXML
    private Text email_check_text;

    @FXML
    private Text password_length_text;

    @FXML
    private Text password_number_check_text;

    @FXML
    private Text password_symbol_check_text;

    @FXML
    private Text password_upper_and_lower_check_text;
    @FXML
    private TextField email_field;

    @FXML
    private TextField login_field;

    @FXML
    private TextField password_field;

    @FXML
    private Button reg_button;

    @FXML
    private Button goback_button;

    @FXML
    private TextField captcha_check_field;

    @FXML
    private Text captcha_ckeck_text;

    @FXML
    private Text captcha_check_answer_text;

    @FXML
    void goback_button_pressed(ActionEvent event) {
        goback_button.getScene().getWindow().hide();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/log_page.fxml"));

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
    void reg_button_pressed(ActionEvent event) throws SQLException, ClassNotFoundException {
        signUpNewUser();
    }

    public static String generateCaptcha() {
        Random random = new Random();
        // Генеруємо випадковий код з цифр і букв
        int length = random.nextInt(5) + 5;
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((char) (random.nextInt(10) + '0'));
        }
        return code.toString();
    }
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return truncateHash(bytesToHex(hashedBytes), 16); // Зменшуємо хеш до 16 символів
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private static String truncateHash(String hash, int length) {
        if (hash.length() < length) {
            return hash;
        }
        return hash.substring(0, length);
    }
    static String valid_code = "";
    private void signUpNewUser() throws SQLException, ClassNotFoundException {
        String password = password_field.getText();
        boolean isLengthValid = password.length() >= 8;
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");

        DatabaseDispatcher dbDispatcher = new DatabaseDispatcher();

        boolean isLoginTaken = dbDispatcher.isUserLoginExists(login_field.getText());
        boolean isEmailTaken = dbDispatcher.isUserEmailExists(email_field.getText());

        if(login_field.getText() != null && email_field.getText() != null && password_field.getText() != null){

            if (isLoginTaken && isEmailTaken) {

                login_check_text.setText("A user with that name already exists");
                login_check_text.setFill(Color.RED);
                email_check_text.setText("A user with this email already exists");
                email_check_text.setFill(Color.RED);

                Shake userLogin = new Shake(login_field);
                Shake userEmail = new Shake(email_field);
                Shake userLoginText = new Shake(login_check_text);
                Shake userEmailText = new Shake(email_check_text);

                userLogin.playAnim();
                userEmail.playAnim();
                userLoginText.playAnim();
                userEmailText.playAnim();

            } else if (isLoginTaken) {

                login_check_text.setText("A user with that name already exists");
                login_check_text.setFill(Color.RED);

                Shake userLogin = new Shake(login_field);
                userLogin.playAnim();

            } else if (isEmailTaken) {

                email_check_text.setText("A user with this email already exists");
                email_check_text.setFill(Color.RED);

                Shake userEmail = new Shake(email_field);
                userEmail.playAnim();

            } else if(!email_field.getText().endsWith("@gmail.com")){

                email_check_text.setFill(Color.RED);
                email_check_text.setText("Email must ends with @gmail.com");

            } else if(!valid_code.equals(captcha_ckeck_text.getText())){

                captcha_check_answer_text.setText("bad captcha my man");
                captcha_check_answer_text.setFill(Color.RED);

                captcha_ckeck_text.setText(generateCaptcha());

            } else if(isLengthValid && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar && captcha_check_field.getText().equals(captcha_ckeck_text.getText())){

                login_check_text.setFill(Color.GREEN);
                login_check_text.setText("Login is valid");
                email_check_text.setFill(Color.GREEN);
                email_check_text.setText("Email is valid");

                User inserted_user = new User();

                inserted_user.setLogin(login_field.getText());
                inserted_user.setEmail(email_field.getText());
                inserted_user.setPassword(hashPassword(password_field.getText()));

                System.out.println(password_field.getText());
                System.out.println(inserted_user.getPassword());

                dbDispatcher.signUpUser(inserted_user);

                reg_button.getScene().getWindow().hide();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/sample/success_page.fxml"));

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

        } else {
            Shake userFirstNameAnim = new Shake(login_field);
            Shake userLastNameAnim = new Shake(email_field);
            Shake userLoginAnim = new Shake(password_field);

            userFirstNameAnim.playAnim();
            userLastNameAnim.playAnim();
            userLoginAnim.playAnim();
        }
    }

    @FXML
    void initialize() {
        password_length_text.setFill(Color.RED);
        password_upper_and_lower_check_text.setFill(Color.RED);
        password_number_check_text.setFill(Color.RED);
        password_symbol_check_text.setFill(Color.RED);
        captcha_ckeck_text.setText(generateCaptcha());

        password_field.textProperty().addListener((observable, oldValue, newValue) -> {
            String password = password_field.getText();
            boolean isLengthValid = password.length() >= 8;
            boolean hasUpperCase = !password.equals(password.toLowerCase());
            boolean hasLowerCase = !password.equals(password.toUpperCase());
            boolean hasNumber = password.matches(".*\\d.*");
            boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");

            if(isLengthValid){
                password_length_text.setFill(Color.GREEN);
            } else {
                password_length_text.setFill(Color.RED);
            }

            if(hasUpperCase && hasLowerCase){
                password_upper_and_lower_check_text.setFill(Color.GREEN);
            }else {
                password_upper_and_lower_check_text.setFill(Color.RED);
            }

            if(hasNumber){
                password_number_check_text.setFill(Color.GREEN);
            }else {
                password_number_check_text.setFill(Color.RED);
            }

            if(hasSpecialChar){
                password_symbol_check_text.setFill(Color.GREEN);
            }else {
                password_symbol_check_text.setFill(Color.RED);
            }

            if (isLengthValid && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar) {
                password_check_text.setFill(Color.GREEN);
                password_check_text.setText("Password is valid");

            } else {
                password_check_text.setFill(Color.RED);
                password_check_text.setText("Password is not valid");
            }
        });

        email_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.endsWith("@gmail.com")) {
                email_check_text.setFill(Color.GREEN);
                email_check_text.setText("Email is valid");
            } else {
                email_check_text.setFill(Color.RED);
                email_check_text.setText("Email is not valid");
            }
        });

        captcha_ckeck_text.setText(generateCaptcha());

        captcha_check_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!captcha_check_field.getText().equals(captcha_ckeck_text.getText())) {
                captcha_check_answer_text.setText("Wrong code, try again");
                captcha_check_answer_text.setFill(Color.RED);

            } else {
                captcha_check_answer_text.setText("Correct code!");
                captcha_check_answer_text.setFill(Color.GREEN);
                valid_code = captcha_ckeck_text.getText();
            }
        });
    }

}
