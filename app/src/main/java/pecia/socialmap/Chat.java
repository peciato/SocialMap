package pecia.socialmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Chat extends Activity {

    public String key;
    public Bitmap bitmap;
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
        displayDescPost();
        displayChatMess();
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
        ChatMess chatMess = new ChatMess(messaggio, "UTENTE2"); //FirebaseAuth.getInstance().getCurrentUser().toString());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat").push();
        mDatabase.setValue(chatMess);

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

