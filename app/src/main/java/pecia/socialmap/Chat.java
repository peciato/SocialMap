package pecia.socialmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.text.format.DateFormat;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;


public class Chat extends AppCompatActivity {

    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        displayChatMess();
        Intent intent = this.getIntent();
        key = intent.getStringExtra("key");
    }


    private void displayChatMess() {

        ListView listView = (ListView) findViewById(R.id.list_of_messagge);
        ListAdapter adapter = new FirebaseListAdapter<ChatMess>(this,ChatMess.class,R.layout.list_item,
                FirebaseDatabase.getInstance().getReference().child("posts").child("key").child("chat")) {
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
