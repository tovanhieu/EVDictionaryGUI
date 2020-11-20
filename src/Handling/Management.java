package Handling;

import java.sql.*;
import java.util.Arrays;
import java.util.Set;

import static Handling.Dictionary.*;

public class Management {

    //Connect to SQLite and get data from database
    public static void connectSQLite() {
        Connection connect = null;
        try {
            // Database parameters
            String url = "jdbc:sqlite:EVDatabase.db";
            // Create a connection to the database
            connect = DriverManager.getConnection(url);
            //Query to get all record from table "av" in database
            String query = "SELECT * FROM av";
            Statement statetment = null;
            ResultSet resultSet = null;
            try {
                statetment = connect.createStatement();
                resultSet = statetment.executeQuery(query);
                while (resultSet.next()) {
                    //Get raw word (English)
                    String raw = resultSet.getString("word");
                    //Get meaning word (Vietnamese)
                    String meaning = resultSet.getString("html");
                    //Put them to Dictionary
                    addWord(raw, meaning);
                }
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

    //Get an array of English words
    public static String[] getRaw()
    {
        //A set collection to save list
        Set<String> raw = Dict.keySet();
        //Convert to string array
        String[] rawArray = raw.toArray(new String[raw.size()]);
        //Sort array in alphabetical order
        Arrays.sort(rawArray);
        return rawArray;
    }

}
