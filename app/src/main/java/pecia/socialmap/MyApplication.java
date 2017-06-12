package pecia.socialmap;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pierluca on 6/12/2017.
 */

public class MyApplication extends Application {

    private String dati[] = new String[2];
    private LatLng latLng;

    public void setLatLng( LatLng position){
        Log.d("ciao",position.toString());
        this.latLng = position;

    }

    public LatLng getLatLng(){

        return this.latLng;

    }

    public String[] getValue(){
        return this.dati;
    }

    public void putValue(String[] value){
        this.dati = value;
    }

}
