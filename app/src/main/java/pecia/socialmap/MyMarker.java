package pecia.socialmap;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by Francesco on 14/06/17.
 */

public class MyMarker  {

    private Marker marker;
    private String key;

    public MyMarker (Marker marker, String key) {

        this.marker = marker;
        this.key = key;

    }

    public MyMarker(String key) {
        this.marker = null;
        this.key = key;
    }

    public MyMarker(Marker marker) {
        this.marker = marker;
        this.key = null;
    }


    public Marker getMarker() {
        return this.marker;
    }
    @Override
    public boolean equals(Object obj) {
        MyMarker myMarker= (MyMarker) obj;
        return (myMarker.key.equals(this.key));
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //serve per trovare la chiave di un marker quando lo seleziono nella mappa, per poter spedire il messaggio nel post giusto
    public String findIdMarker(ArrayList<MyMarker> markers) {
        int i=0;
        boolean trovato = false;
        String id = null;

        while(i<markers.size() &&  !trovato) {
            trovato = markers.get(i).marker.getId().equals(this.getMarker().getId());
            if(trovato) id = markers.get(i).getKey();
            i++;
        }

        return id;
    }

}
