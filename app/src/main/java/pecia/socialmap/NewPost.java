package pecia.socialmap;

/**
 * Created by Francesco on 14/06/17.
 */

public class NewPost{

    public String titolo;
    public String messaggio;
    public String utente;
    public double lat;
    public double longi;
    public String key;

    public NewPost() { // construttore di default richiesto da Firebase
    }

    public NewPost(String titolo, String post, String utente, String key) {

        this.titolo = titolo;
        this.messaggio = post;
        this.utente = utente;
        this.key = key;

    }


}