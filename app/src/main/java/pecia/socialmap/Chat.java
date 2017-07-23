package pecia.socialmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.geofire.GeoFire;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


public class Chat extends Activity {

    private String key;
    private Bitmap bitmap;
    private String id;
    private boolean actived;
    private NewPost postattivo;
    private NewPost newPost;
    private boolean tokenPresent = false;
    private String tokenAttuale = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<ChatMess> listaMessaggi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //vede se è la prima volta che apre activity o c'è stato salvato
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                key = null;
            } else {
                key = extras.getString("keyPost");
            }
        } else {
            key = (String) savedInstanceState.getSerializable("keyPost");
        }




        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);



    }

    public void checkTokenPresente() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
        mDatabase.child("token").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String token = postSnapshot.getValue(String.class);
                    if (token.toString().equals(tokenAttuale)) {
                        tokenPresent = true;
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
        checkTokenPresente();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    postattivo = postSnapshot.getValue(NewPost.class);



                    if (postattivo.key != null && postattivo.key.equals(key)) {

                        actived = true;
                        ImageView deleteButton = (ImageView) findViewById(R.id.deletebutton);
                        deleteButton.setVisibility(View.VISIBLE);

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
        final ImageView imageView = (ImageView) findViewById(R.id.image_post);
        final LinearLayout fotoView = (LinearLayout) findViewById(R.id.fotoPost);
        final ImageView imgProfilePic = (ImageView) this.findViewById(R.id.imgUser);
        final TextView username = (TextView) this.findViewById(R.id.userName);
        final View view = findViewById(R.id.card_view6);
        final RequestManager pic = Glide.with(this);


        LinearLayout post = (LinearLayout) findViewById(R.id.post);
        post.setVisibility(View.VISIBLE);


        mDatabase.child("posts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPost = dataSnapshot.getValue(NewPost.class);



                if(newPost==null) {
                    title.setText(R.string.postnotavailble);
                    return;
                }
                displayChatMess();
                if (newPost.attivo == false) {
                    view.setVisibility(View.INVISIBLE);
                }
                //NewPost newPost = dataSnapshot.child("posts").child(key).getValue(NewPost.class);
                title.setText(newPost.titolo);
                description.setText(newPost.messaggio);
                if (newPost.image != null) {
                    byte[] decodedString = Base64.decode(newPost.image, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(bitmap);
                    fotoView.setVisibility(View.VISIBLE);
                } else {
                    fotoView.setVisibility(View.GONE);
                }

                username.setText(newPost.utente);
                pic.load(newPost.imageUser).into(imgProfilePic);

                if (newPost.utenteID.equals(id)) {
                    ImageButton deleteButton = (ImageButton) findViewById(R.id.deletebutton);
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Code here executes on main thread after user presses button
                            deletePost();
                        }
                    });

                }
                else{
                    ImageButton deleteButton = (ImageButton) findViewById(R.id.deletebutton);
                    //deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Code here executes on main thread after user presses button
                            deletePostNotMine();
                        }
                    });
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
        if (messaggio.matches("")) {
            Toast.makeText(this, "Inserisci Messaggio", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ChatMess chatMess = new ChatMess(messaggio,user.getDisplayName(),user.getUid(), key);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat").push();
        mDatabase.setValue(chatMess);

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();
        //mDatabase.setValue(refreshedToken);


        if (!actived && id != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("postattivi").child(id).push();
            mDatabase.setValue(newPost);
            actived = true;
        }
        //FirebaseMessaging.getInstance().;
        if (tokenPresent != true) {
            DatabaseReference mDatabaseToken = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token").push();
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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("chat");
        DatabaseReference mDatabaseToken = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token");

        mDatabase.addValueEventListener(new ValueEventListener() {
            public ArrayList<String> Userlist;



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference mDatabaseToken;
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
                linearLayout.removeAllViews();
                listaMessaggi = collectAllChatMess(dataSnapshot);

                LinearLayout ll = (LinearLayout) findViewById((R.id.linear1));

                ll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError data) {
            }


        });


    }

    public void pushToken() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key).child("token");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String token = postSnapshot.getValue(String.class);
                    if (token.toString().equals(tokenAttuale)) {
                        tokenPresent = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public ArrayList<ChatMess> collectAllChatMess(DataSnapshot dataSnapshot) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        int index = 0;

        ArrayList<ChatMess> listaMessaggi = new ArrayList<ChatMess>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

            LinearLayout commenti = (LinearLayout) findViewById(R.id.commenti);
            commenti.setVisibility(View.VISIBLE);

            ChatMess postattivo = postSnapshot.getValue(ChatMess.class);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.commment, null);
            TextView messText, messUser, messTime;
            messText = (TextView) v.findViewById(R.id.message_text);
            messUser = (TextView) v.findViewById(R.id.message_user);
            messTime = (TextView) v.findViewById(R.id.message_time);
            TextView username = (TextView) this.findViewById(R.id.userName);


            String utenteMsg = postattivo.getUserId();
            String utenteLoggato = FirebaseAuth.getInstance().getCurrentUser().getUid();


            if (utenteMsg.equals(utenteLoggato)) {
                TextView delComment = (TextView) v.findViewById(R.id.deletePostV);
                delComment.setVisibility(View.VISIBLE);

            } else {
                TextView delComment = (TextView) v.findViewById(R.id.deletePostV);
                delComment.setVisibility(View.GONE);

            }

            messText.setText(postattivo.getMessText());
            messUser.setText(postattivo.getMessUser());
            messTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", postattivo.getMessTime()));
            linearLayout.addView(v, 0);
            listaMessaggi.add(index, postattivo);
            v.setTag(index);
            index++;
        }
        return listaMessaggi;

    }

    public void openImage(View view) {
        Intent intent = new Intent(this, Image_Activity.class);
        intent.putExtra("image", bitmap);
        startActivity(intent);
    }

    public void deletePostNotMine() {

        final Activity x = this;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle(R.string.abbandone)
                .setMessage("Sei sicuro di voler abbandonare la conversazione?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Rimozione da postattivi
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("postattivi").child(id).orderByChild("key").equalTo(newPost.key);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
                                    mDatabase.child("token").addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String token = postSnapshot.getValue(String.class);
                                                if (token.toString().equals(tokenAttuale)) {
                                                    postSnapshot.getRef().removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError firebaseError) {
                                            //Username Does Not Exist
                                        }
                                    });
                                    appleSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        x.finish();
                    }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void deleteComment(View view) {
        View padre = (ViewGroup) view.getParent().getParent();
        int tagView = (int) padre.getTag();
        ChatMess mess = listaMessaggi.get(tagView);
        if (mess == null) return;
        if (newPost != null) {

            //Rimozione
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query applesQuery = ref.child("posts").child(newPost.key).child("chat").orderByChild("messTime").equalTo(mess.getMessTime());
            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        ((ViewGroup) view.getParent()).removeView(view);

    }

    public void deletePost() {

        final Activity x = this;

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Elimina post")
                .setMessage("Sei sicuro di voler eliminare il post?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (newPost != null) {


                            //rimozione da geofire
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.removeLocation(newPost.key);

                            //Rimozione da postattivi
                            ref = FirebaseDatabase.getInstance().getReference();
                            Query applesQuery = ref.child("postattivi").child(id).orderByChild("key").equalTo(newPost.key);
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            ref = FirebaseDatabase.getInstance().getReference().child("posts").child(newPost.key).child("attivo");
                            ref.setValue(false);


                            x.finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();









}


}

