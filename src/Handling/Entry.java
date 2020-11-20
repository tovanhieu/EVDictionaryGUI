package Handling;

/*
 * Class "Entry" is made to define entries's format when export the dictionary to file.
 * @Author: Meoki
 */

public class Entry {
    private String id;
    private String word;
    private String pronounce;
    private String description;

    public Entry(){}
    public Entry(String a, String b,String c,String d){
        this.id = a;
        this.word = b;
        this.pronounce = c;
        this.description = d;
    }

    public String getId(){
        return this.id;
    }

    public String getWord(){
        return this.word;
    }

    public String getPronounce(){
        return this.pronounce;
    }

    public String getDescription() {
        return this.description;
    }
}
