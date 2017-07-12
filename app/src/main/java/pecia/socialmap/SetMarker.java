package pecia.socialmap;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class SetMarker extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Boolean fotoPresente = false;
    FloatingActionButton fab;
    ImageView mImageView;
    private String key;

    private boolean isKeyboardOpen = false;

    //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_marker);
        //this.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
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


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    public void setWindowParams(){
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
               WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(wlp);
    }




   /*Hide button when keyboard is open*/


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public void submit(View view){

        //Intserimento testo e controllo
        EditText ti = (EditText) findViewById(R.id.editText3);
        EditText pos = (EditText) findViewById(R.id.editText4);
        EditText tim = (EditText) findViewById(R.id.editText7);

        String titolo = ti.getText().toString();
        String post = pos.getText().toString();
        String time = tim.getText().toString();

        if (titolo.matches("")) {
            Toast.makeText(this, "Inserisci titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (post.matches("")) {
            Toast.makeText(this, "Inserisci messaggio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.matches("")) {
            Toast.makeText(this, "Inserisci minuti", Toast.LENGTH_SHORT).show();
            return;
        }

        //Inserimento post online
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        GeoFire geoFire = new GeoFire(ref);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").push();
        String utente = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String utenteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        NewPost newPost = new NewPost(titolo,post,utente,utenteId ,mDatabase.getKey());
        LatLng latLng = ((MyApplication) this.getApplication()).getLatLng();

        if (latLng == null) {
            Toast.makeText(this, "POSIZIONE NON TROVATA", Toast.LENGTH_SHORT).show();
            return;
        }
        newPost.lat = latLng.latitude;
        newPost.longi = latLng.longitude;
        key = newPost.key;
        newPost.durata=Long.parseLong(time, 10);
        newPost.data = new Date().getTime();


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


        /*ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        ImageView profile = new ImageView(getBaseContext());
        Glide.with(this.getParent()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile);
        Bitmap imageBitmap = ((BitmapDrawable)profile.getDrawable()).getBitmap();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,bYtE);
        byte[] byteArray = bYtE.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);*/
        newPost.imageUser = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();



        mDatabase.setValue(newPost);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(id!=null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id).push();
            mDatabase.setValue(newPost);
        }

        geoFire.setLocation(newPost.key , new GeoLocation(newPost.lat,newPost.longi) );

        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();
        String token = FirebaseInstanceId.getInstance().getToken();
        mDatabase.setValue(token.toString());


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
