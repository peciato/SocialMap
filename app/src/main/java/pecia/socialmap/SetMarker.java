package pecia.socialmap;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class SetMarker extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Boolean fotoPresente = false;
    FloatingActionButton fab;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);
        this.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        //setWindowParams();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);

        }

        fab = (FloatingActionButton) findViewById(R.id.pict);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        mImageView = (ImageView) findViewById(R.id.photo);
        mImageView.setVisibility(View.GONE);

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        GeoFire geoFire = new GeoFire(ref);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").push();
        NewPost newPost = new NewPost(titolo,post, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),mDatabase.getKey());
        LatLng latLng = ((MyApplication) this.getApplication()).getLatLng();
        newPost.lat = latLng.latitude;
        newPost.longi = latLng.longitude;

        if(fotoPresente) {
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            ImageView imageView = (ImageView) findViewById(R.id.photo);
            if (imageView != null) {
                Bitmap imageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,bYtE);
                byte[] byteArray = bYtE.toByteArray();
                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                newPost.image = encodedImage;
            }
        }


        mDatabase.setValue(newPost);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(id!=null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id).push();
            mDatabase.setValue(newPost);
        }

        geoFire.setLocation(newPost.key , new GeoLocation(newPost.lat,newPost.longi) );


        this.finish();
    }

    public void takePhoto() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
               ) {
            return;
        }


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mImageView.setImageBitmap(imageBitmap);
            mImageView.setVisibility(View.VISIBLE);
            fotoPresente = true;


        }
    }
}
