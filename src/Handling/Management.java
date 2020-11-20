package Handling;

import java.sql.*;
import static Handling.Dictionary.*;

public class Management {

    //Connect to SQLite
    public static void connectSQLite() {
        Connection connect = null;
        try {
            // Database parameters
            String url = "jdbc:sqlite:EVDatabase.db";
            // Create a connection to the database
            connect = DriverManager.getConnection(url);
            String query = "SELECT * FROM av";
            Statement statetment = null;
            ResultSet resultSet = null;
            try {
                statetment = connect.createStatement();
                resultSet = statetment.executeQuery(query);
                while(resultSet.next())
                {
                    String raw = resultSet.getString("word");
                    String meaning = resultSet.getString("html");
                    addWord(raw,meaning);
                }
                System.out.println(Dict.get("house"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

}
