package pecia.socialmap;

import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
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
    final NewPost newPost1 = new NewPost();


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

    private void displayChatMess() {

        String id2 = id;
        ListView listView = (ListView) getActivity().findViewById(R.id.list_of_post);
        ListAdapter adapter = new FirebaseListAdapter<String>(getActivity(),String.class,R.layout.list_item,
                FirebaseDatabase.getInstance().getReference().child("postattivi").child(id2))
        {

            @Override
            protected void populateView(View v, String model, int position) {

                TextView messText,messUser,messTime;
                messText = (TextView) v.findViewById(R.id.message_text);
                messUser = (TextView) v.findViewById(R.id.message_user);


                messText.setText(model);




            }
        };

        listView.setAdapter(adapter);
    }

    private void retPost(String key){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                NewPost newPost = snapshot.getValue(NewPost.class);
                if(newPost!=null) {

                    newPost1.titolo = newPost.titolo;
                    newPost1.messaggio = newPost.messaggio;
                    displayChatMess();
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
