package GoogleAPI;

/*
 * Class "Audio" handling all about sounds using API...
 * @Author: Meoki
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Voice {

    //Get audio of the need-pronounce word
    public static InputStream getAudio(String text, String languageOutput) throws IOException {
        URL url = new URL("http://translate.google.com/translate_tts?ie=UTF-8&tl=" + languageOutput + "&client=tw-ob&q=" + text.replace(" ", "%20"));
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        InputStream audioSrc = urlConn.getInputStream();
        return new BufferedInputStream(audioSrc);
    }

    //Play audio gotten above
    public static void play(InputStream sound) throws JavaLayerException {
        new Player(sound).play();
    }

}

