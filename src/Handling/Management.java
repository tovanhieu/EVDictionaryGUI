package Handling;

/*
 * Class "Management" performs some method that impacts all the entries in Dictionary
 * @Author: Meoki
 */


import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    public static String[] getRaw() {
        //A set collection to save list
        Set<String> raw = Dict.keySet();
        //Convert to string array
        String[] rawArray = raw.toArray(new String[raw.size()]);
        //Sort array in alphabetical order
        Arrays.sort(rawArray);
        return rawArray;
    }

    //Get all words in dictionary to write to file later
    public static void getWordsEntry(Map<String, Entry> storeEntry) {
        Connection connect = null;
        try {
            //Make a connection and get data, like "connectSQLite" above
            String url = "jdbc:sqlite:EVDatabase.db";
            connect = DriverManager.getConnection(url);
            String query = "SELECT * FROM av";
            Statement statetment = null;
            ResultSet resultSet = null;
            Entry e;
            try {
                statetment = connect.createStatement();
                resultSet = statetment.executeQuery(query);
                while (resultSet.next()) {
                    //Get raw word (English)
                    String id = resultSet.getString("id");
                    //Get meaning word (Vietnamese)
                    String word = resultSet.getString("word");
                    //Get pronounce of words
                    String pronounce = resultSet.getString("pronounce");
                    //Get description of words
                    String description = resultSet.getString("description");
                    //Store them to Entry to write to file later
                    e = new Entry(id, word, pronounce, description);
                    storeEntry.put(id, e);
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

    //Export dictionary to file
    public static void exportDictionary(File directory) throws IOException {
        //Output file directory
        Map<String, Entry> storeEntry = new HashMap<>();
        //Eexecute method defined above
        getWordsEntry(storeEntry);
        //Create writer
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory), "UTF8"));
        //Loop to write all word to file

        for (String i : storeEntry.keySet()) {
            Entry ex = storeEntry.get(i);
            //Format output entry's format
            String result = "#" + ex.getId() + "\n- " + ex.getWord() + "\t(" + ex.getPronounce() + ")\n- " + ex.getDescription() + "\n";
            writer.write(result);
            writer.newLine();
        }
        //Close writer
        writer.close();
    }

}
