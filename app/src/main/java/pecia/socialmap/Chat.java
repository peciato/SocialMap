package pecia.socialmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.function.Consumer;


public class Chat extends Activity {

    private String key;
    private Bitmap bitmap;
    private String id;
    private boolean actived;

    private ImageView imgProfilePic;
    private TextView username;
    private NewPost postattivo;
    private NewPost newPost;
    private boolean tokenPresent = false;
    private String tokenAttuale = FirebaseInstanceId.getInstance().getToken();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //vede se è la prima volta che apre activity o c'è stato salvato
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                key= null;
            } else {
                key= extras.getString("keyPost");
            }
        } else {
            key = (String) savedInstanceState.getSerializable("keyPost");
        }

        imgProfilePic = (ImageView) this.findViewById(R.id.imgUser);
        username = (TextView) this.findViewById(R.id.userName);
        displayChatMess();
        checkTokenPresente();


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    public void checkTokenPresente(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
        Log.d("qua", "sono quaaaaaaaaaaa1");
        mDatabase.child("token").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("qua", "sono quaaaaaaaaaaa2");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Log.d("ciao", "sono quaaaaaaaaaaa5");
                    String token = postSnapshot.getValue(String.class);
                    Log.d("ciao", "siamo seri13.5"+token.toString()+"::::"+tokenAttuale);
                    if (token.toString().equals(tokenAttuale)){
                        Log.d("ciao", "sono quaaaaaaaaaa62");
                        tokenPresent = true;
                        Log.d("ciao", "sono quaaaaaaaaaaa9" + tokenPresent );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                //Username Does Not Exist
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        actived = false;
        displayDescPost();
        //displayChatMess();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl().toString()).into(imgProfilePic);
        username.setText(user.getDisplayName());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    postattivo = postSnapshot.getValue(NewPost.class);


                    if(postattivo.key.equals(key)) {

                        if(postattivo.utenteID.equals(id)) {
                            Button deleteButton = (Button) findViewById(R.id.deletebutton);
                            deleteButton.setVisibility(View.VISIBLE);
                        }

                        actived = true;

                        break;
                    }
                }
            }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    private void displayDescPost() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final TextView title = (TextView) findViewById(R.id.titleChat);
        final TextView description = (TextView) findViewById(R.id.descrChat);
        final ImageView imageView  = (ImageView) findViewById(R.id.image_post);


        mDatabase.child("posts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost = dataSnapshot.getValue(NewPost.class);
                //NewPost newPost = dataSnapshot.child("posts").child(key).getValue(NewPost.class);
                title.setText(newPost.titolo);
                description.setText(newPost.messaggio);
                if (newPost.image != null) {
                    byte[] decodedString = Base64.decode(newPost.image, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessToServer(View view) {

        EditText me = (EditText) findViewById(R.id.input);
        String messaggio = me.getText().toString();
        me.setText("");
        if(messaggio.matches("")) {
            Toast.makeText(this, "Inserisci Messaggio", Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMess chatMess = new ChatMess(messaggio, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), key);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat").push();
        mDatabase.setValue(chatMess);

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();

        //mDatabase.setValue(refreshedToken);


        if(!actived && id!=null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id).push();
            mDatabase.setValue(newPost);
        }
        //FirebaseMessaging.getInstance().;
        if(tokenPresent != true){
            DatabaseReference mDatabaseToken = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();
            Log.d("ciao", "siamo seriiiiiiiiiiiiiiiiiiiiiiii" );
            mDatabaseToken.setValue(tokenAttuale);
            checkTokenPresente();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    private void displayChatMess() {

        DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat");
        DatabaseReference  mDatabaseToken = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        ListAdapter adapter = new FirebaseListAdapter<ChatMess>(this,ChatMess.class,R.layout.commment,
                FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat"))
        {

            @Override
            protected void populateView(View v, ChatMess model, int position) {

                TextView messText,messUser,messTime;
                messText = (TextView) v.findViewById(R.id.message_text);
                messUser = (TextView) v.findViewById(R.id.message_user);
                messTime = (TextView) v.findViewById(R.id.message_time);

                messText.setText(model.getMessText());
                messUser.setText(model.getMessUser());
                messTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessTime()));


            }
        };

        //linearLayout.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            public ArrayList<String> Userlist;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference mDatabaseToken;
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
                linearLayout.removeAllViews();
                collectAllChatMess(dataSnapshot);
                /**Log.d("ciao", "siamo seri0.57876" + tokenPresent);
                pushToken();
                Log.d("ciao", "siamo seri0.5" + tokenPresent);
                if(tokenPresent == false){
                    mDatabaseToken = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();
                    Log.d("ciao", "siamo seri" + tokenPresent );
                    mDatabaseToken.setValue(tokenAttuale);
                }**/
            }

            @Override
            public void onCancelled(DatabaseError data){}



        });


    }

    public void pushToken(){
        final DatabaseReference  mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ciao", "siamo seri5");
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Log.d("ciao", "siamo seri3");
                    String token = postSnapshot.getValue(String.class);
                    Log.d("ciao", "siamo seri13.5"+token.toString()+"::::"+tokenAttuale);
                    if (token.toString().equals(tokenAttuale)){
                        Log.d("ciao", "siamo seri1");
                        tokenPresent = true;
                        Log.d("ciao", "siamo seri1.5" + tokenPresent );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void collectAllChatMess(DataSnapshot dataSnapshot){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);

        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

            Log.d("cnahb","prova1111");


            ChatMess postattivo = postSnapshot.getValue(ChatMess.class);
            LayoutInflater inflater= (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.commment, null);
            TextView messText,messUser,messTime;
            messText = (TextView) v.findViewById(R.id.message_text);
            messUser = (TextView) v.findViewById(R.id.message_user);
            messTime = (TextView) v.findViewById(R.id.message_time);

            messText.setText(postattivo.getMessText());
            messUser.setText(postattivo.getMessUser());
            messTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",postattivo.getMessTime()));
            linearLayout.addView(v,0);
        }

    }

    public void openImage(View view) {
        Intent intent = new Intent(this,Image_Activity.class);
        intent.putExtra("image",bitmap );
        startActivity(intent);
    }

    public void deletePost(View view) {
        if(postattivo!=null) {


            //Rimozione da postattivi
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("postattivi").child(id).orderByChild("key").equalTo(postattivo.key);
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //rimozione da geofire
            ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(postattivo.key);

            this.finish();
        }
    }


}

