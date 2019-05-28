package server;

import java.sql.*;

public class AuthServ {
    private static Connection connection;
    private static Statement stm;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:DBUsers.db");
            stm = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNameByLogPass(String login, String psw) {

        String sql = String.format("SELECT nickname FROM main WHERE login = '%s' and password = '%s'", login, psw);

        try {
            ResultSet res = stm.executeQuery(sql);
            if(res.next()) {
                return res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
