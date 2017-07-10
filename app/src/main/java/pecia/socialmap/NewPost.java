package pecia.socialmap;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by Francesco on 14/06/17.
 */

public class NewPost{

    public String titolo;
    public String messaggio;
    public String utente;
    public String utenteID;
    public double lat;
    public double longi;
    public String image;
    public String imageUser;
    public String key;
    public long durata;//espressa in minuti
    public double data;

    public NewPost() { // construttore di default richiesto da Firebase
    }

    public NewPost(String titolo, String post, String utente, String utenteID, String key) {

        this.titolo = titolo;
        this.messaggio = post;
        this.utente = utente;
        this.utenteID = utenteID;
        this.key = key;

    }


}