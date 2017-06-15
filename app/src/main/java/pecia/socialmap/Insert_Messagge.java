package pecia.socialmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Insert_Messagge extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert__messagge);
    }

    public void sendMessToServer(View view) {

        EditText me = (EditText) findViewById(R.id.editText8);
        String messaggio = me.getText().toString();
        if(messaggio.matches("")) {
            Toast.makeText(this, "Inserisci titolo", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = getIntent();
        String keyPost = intent.getStringExtra("keyPost");
        ChatMess chatMess = new ChatMess(messaggio, "UTENTE2"); //FirebaseAuth.getInstance().getCurrentUser().toString());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(keyPost).child("chat").push();
        mDatabase.setValue(chatMess);

        intent = new Intent(this,Chat.class);
        intent.putExtra("key",keyPost);
        startActivity(intent);
        this.finish();
    }
}
