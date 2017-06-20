package pecia.socialmap;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Chat extends Activity {

    public String key;
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

        displayChatMess();
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
}
