package pecia.socialmap;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
    private String idU;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_post, container, false);
        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();
        idU = FirebaseAuth.getInstance().getCurrentUser().getUid();
        displayChatMess();

    }

    @Override
    public void onResume() {
        super.onResume();
        displayChatMess();
    }

    private void displayChatMess() {

        String id2 = idU;

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
                if(idU.equals(model.utenteID)){
                    messText.setTextColor(Color.RED);
                    ImageView icona = (ImageView) v.findViewById(R.id.imageViewMio);
                    icona.setVisibility(View.VISIBLE);
                }
                else{
                    messText.setTextColor(Color.BLUE);
                    ImageView icona = (ImageView) v.findViewById(R.id.imageViewMio);
                    icona.setVisibility(View.GONE);
                }
                messText.setText(model.messaggio);
                messUser.setText(model.titolo);
                messTime.setText(model.utente);
                if(model.daLeggere.equals("true") ){
                    TextView mess;
                    mess = (TextView) v.findViewById(R.id.textView2);
                    mess.setVisibility(View.VISIBLE);
                }
                else{
                    TextView mess;
                    mess = (TextView) v.findViewById(R.id.textView2);
                    mess.setVisibility(View.GONE);
                }



            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final NewPost post =  (NewPost) parent.getItemAtPosition(position);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("postattivi").child(idU);
                // Attach a listener to read the data at our posts reference
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            NewPost postI = postSnapshot.getValue(NewPost.class);
                            Log.d("porco", "dio "+postI.key +" e la madonna "+post.key);
                            if(postI.key.equals(post.key)) {
                                if(postI.daLeggere.equals("true")){
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("postattivi").child(idU).child(postSnapshot.getKey()).child("daLeggere");
                                    ref.setValue("false");
                                }
                            }
                        }
                        //NewPost post = dataSnapshot.getValue(NewPost.class);
                        //System.out.println(post);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                Intent intent = new Intent(getActivity(), Chat.class);
                intent.putExtra("keyPost", post.key);
                startActivity(intent);



            }
        });
    }


}
