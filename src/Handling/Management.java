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
            Statement statement;
            ResultSet resultSet;
            try {
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String raw = resultSet.getString("word");
                    String meaning = resultSet.getString("html");
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
        Set<String> raw = Dict.keySet();
        String[] rawArray = raw.toArray(new String[raw.size()]);
        Arrays.sort(rawArray);
        return rawArray;
    }

    //Get all words in dictionary to write to file later
    public static void getWordsEntry(Map<String, Entry> storeEntry) {
        Connection connect = null;
        try {
            String url = "jdbc:sqlite:EVDatabase.db";
            connect = DriverManager.getConnection(url);
            String query = "SELECT * FROM av";
            Statement statement;
            ResultSet resultSet;
            Entry e;
            try {
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String word = resultSet.getString("word");
                    String pronounce = resultSet.getString("pronounce");
                    String description = resultSet.getString("description");
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

    //Load HTML text from file to display in About tab pane
    public static String aboutInfo() throws IOException
    {
        File aboutDir = new File("./About.html");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aboutDir), "UTF8"));
        StringBuilder stringBuilder = new StringBuilder();
        while (br.ready()) {
            stringBuilder.append(br.readLine());
        }
        String information = stringBuilder.toString();
        br.close();

        return information;
    }

    //Export dictionary to file
    public static void exportDictionary(File directory) throws IOException {
        Map<String, Entry> storeEntry = new HashMap<>();
        getWordsEntry(storeEntry);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directory), "UTF8"));
        for (String i : storeEntry.keySet()) {
            Entry ex = storeEntry.get(i);
            String result = "#" + ex.getId() + "\n- " + ex.getWord() + "\t(" + ex.getPronounce() + ")\n- " + ex.getDescription() + "\n";
            writer.write(result);
            writer.newLine();
        }
        writer.close();
    }

}
