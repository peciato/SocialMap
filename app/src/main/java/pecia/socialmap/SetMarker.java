package pecia.socialmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetMarker extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);
        setWindowParams();
    }

    public void setWindowParams(){
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
    }

    public void submit(View view){

        //Intserimento testo e controllo
        EditText ti = (EditText) findViewById(R.id.editText3);
        EditText pos = (EditText) findViewById(R.id.editText4);

        String titolo = ti.getText().toString();
        String post = pos.getText().toString();

        if (titolo.matches("")) {
            Toast.makeText(this, "Inserisci titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (post.matches("")) {
            Toast.makeText(this, "Inserisci messaggio", Toast.LENGTH_SHORT).show();
            return;
        }

        //Inserimento post online
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").push();
        NewPost newPost = new NewPost(titolo,post,"UTENTE!",mDatabase.getKey());
        LatLng latLng = ((MyApplication) this.getApplication()).getLatLng();
        newPost.lat = latLng.latitude;
        newPost.longi = latLng.longitude;
        mDatabase.setValue(newPost);


        this.finish();
    }
}
