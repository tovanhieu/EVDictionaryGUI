package Handling;

public class Word {
    private String raw;
    private String meaning;

    public Word(String r, String m) {
        raw = r;
        meaning = m;
    }
    public Word(Word w) {
        raw = w.raw;
        meaning = w.meaning;
    }

    public void copy(Word w) {
        raw = w.raw;
        meaning = w.meaning;
    }

    // Setter
    public void setWordTarget(String r) {
        raw = r;
    }
    public void setWordExplain(String m) {
        meaning = m;
    }

    // Getter
    public String getWordTarget() {
        return raw;
    }
    public String getWordExplain() {
        return meaning;
    }
}
