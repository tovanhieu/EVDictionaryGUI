package Handling;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
    public static Map<String, String> Dict = new HashMap<>();

    public Dictionary() {
    }

    public static void addWord(String raw, String meaning) {
        Dict.put(raw, meaning);
    }
}
