package pecia.socialmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Francesco on 14/06/17.
 */

public class NewPost implements Serializable {

    public String titolo;
    public String messaggio;
    public String utente;
    public LatLng latLng;


    public NewPost() { // construttore di default richiesto da Firebase
    }

    public NewPost(String titolo, String post, String utente) {

        this.titolo = titolo;
        this.messaggio = post;
        this.utente = utente;
        latLng = null;
    }


}