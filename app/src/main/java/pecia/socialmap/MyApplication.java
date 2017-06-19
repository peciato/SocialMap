package pecia.socialmap;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pierluca on 6/12/2017.
 */

public class MyApplication extends Application {

    private LatLng latLng;
    private GoogleSignInAccount account;
    private String username;
    private String img;

    public void setLatLng( LatLng position){
        this.latLng = position;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }

    public void setAccount( GoogleSignInAccount acc ){ this.account = acc; }

    public GoogleSignInAccount getAccount(){ return this.account; }




}
