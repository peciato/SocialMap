package pecia.socialmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Pierluca on 5/31/2017.
 */

public class PostFragment extends Fragment {

    View myView;
    private String id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_post, container, false);
        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        displayChatMess();

    }

    @Override
    public void onResume() {
        super.onResume();
        displayChatMess();
    }

    private void displayChatMess() {

        String id2 = id;
        ListView listView = (ListView) getActivity().findViewById(R.id.list_of_post);
        ListAdapter adapter = new FirebaseListAdapter<NewPost>(getActivity(),NewPost.class,R.layout.list_item,
                FirebaseDatabase.getInstance().getReference().child("postattivi").child(id2))
        {

            @Override
            protected void populateView(View v, NewPost model, int position) {

                TextView messText,messUser,messTime;
                messText = (TextView) v.findViewById(R.id.message_text);
                messTime = (TextView) v.findViewById(R.id.message_time);
                messUser = (TextView) v.findViewById(R.id.message_user);


                messText.setText(model.titolo);
                messUser.setText(model.messaggio);
                messTime.setText(model.utente);




            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NewPost post =  (NewPost) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), Chat.class);
                intent.putExtra("keyPost", post.key);
                startActivity(intent);



            }
        });
    }


}
