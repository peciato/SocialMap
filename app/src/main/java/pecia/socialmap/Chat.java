package pecia.socialmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Chat extends Activity {

    private String key;
    private Bitmap bitmap;
    private String id;
    private boolean actived;

    private ImageView imgProfilePic;
    private TextView username;

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        actived = false;
        Log.d("qua", "mannaggia1");
        displayDescPost();
        Log.d("qua", "mannaggia2");
        displayChatMess();
        Log.d("qua", "mannaggi3");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl().toString()).into(imgProfilePic);
        username.setText(user.getDisplayName());
        Log.d("qua", "mannaggi4");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("qua", "mannaggi5");


                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Log.d("qua", "mannaggi6");

                    //String postattivo = postSnapshot.getValue(String.class);
                    //String postattivo = postSnapshot.child("posts").getValue(String.class);
                    NewPost postattivo = postSnapshot.getValue(NewPost.class);
                    Log.d("qua", "mannaggi7");

                    if(postattivo.key.equals(key)) {
                        Log.d("qua", "mannaggi8");

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
                NewPost newPost = dataSnapshot.getValue(NewPost.class);
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
        ChatMess chatMess = new ChatMess(messaggio, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat").push();
        mDatabase.setValue(chatMess);


        if(!actived && id!=null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id).push();
            mDatabase.setValue(key);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void displayChatMess() {

        ListView listView = (ListView) findViewById(R.id.list_of_messagge);
        ListAdapter adapter = new FirebaseListAdapter<ChatMess>(this,ChatMess.class,R.layout.list_item,
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

        listView.setAdapter(adapter);
    }

    public void openImage(View view) {
        Intent intent = new Intent(this,Image_Activity.class);
        intent.putExtra("image",bitmap );
        startActivity(intent);
    }

}

