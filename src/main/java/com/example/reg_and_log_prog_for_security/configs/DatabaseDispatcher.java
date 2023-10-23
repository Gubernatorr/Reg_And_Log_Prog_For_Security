package com.example.reg_and_log_prog_for_security.configs;

import com.example.reg_and_log_prog_for_security.models.User;

import java.sql.*;

public class DatabaseDispatcher extends Configs{

    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException{

        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort +"/" +dbName + "?" + "autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void signUpUser(User user){
        String insert = "INSERT INTO " + Const.USER_TABLE + " (" + Const.USER_LOGIN + "," + Const.USER_EMAIL +
                "," + Const.USER_PASSWORD + ")" + "VALUES(?,?,?)";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(insert);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ResultSet getUser(User user){

        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USER_LOGIN + "=? AND " + Const.USER_EMAIL +
                "=? AND " + Const.USER_PASSWORD + "=?";

        try {
            PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            resSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resSet;

    }

    public User getAllUserInfo(User user) throws SQLException, ClassNotFoundException {
        String select = "SELECT * FROM " + Const.USER_TABLE + " WHERE " + Const.USER_LOGIN + "=? AND " + Const.USER_PASSWORD + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(select);
        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPassword());

        ResultSet resultSet = preparedStatement.executeQuery();
        User userOut = new User();
        while (resultSet.next()) {
            userOut.setId(resultSet.getString(1));
            userOut.setLogin(resultSet.getString(2));
            userOut.setEmail(resultSet.getString(3));
            userOut.setPassword(resultSet.getString(4));
        }

        return userOut;
    }

    public void deleteUser(User user){
        String delete = "DELETE FROM " + Const.USER_TABLE + " WHERE " + Const.USER_ID + " =?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(delete);
            prSt.setString(1, user.getId());

            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean isUserLoginExists(String login) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM " + Const.USER_TABLE + " WHERE " + Const.USER_LOGIN + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setString(1, login);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }

        return false;

    }
    public boolean isUserEmailExists(String email) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM " + Const.USER_TABLE + " WHERE " + Const.USER_EMAIL + "=?";

        PreparedStatement preparedStatement = getDbConnection().prepareStatement(query);
        preparedStatement.setString(1, email);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }

        return false;

    }
}
