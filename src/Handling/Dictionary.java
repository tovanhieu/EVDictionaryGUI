package Handling;

/*
 * Class "Dictionary" define the way to store an English word and its Vietnamese meaning and some action related to Dictionary's entry
 * @Author: Meoki
 */

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    //Use HashMap to store the data
    public static Map<String, String> Dict = new HashMap<>();

    //Constructor
    public Dictionary() {
    }

    //Add more words in Dictionary
    public static void addWord(String raw, String meaning) {
        Dict.put(raw, meaning);
    }

    //Search a word in Dictionary
    public static String searchWord(String raw) {
        return Dict.get(raw);
    }

    //Delete a word in Dictionary
    public static void deleteWord(String raw) { Dict.remove(raw);}

}
