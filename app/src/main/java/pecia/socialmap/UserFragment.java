package pecia.socialmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Pierluca on 5/31/2017.
 */

public class UserFragment extends Fragment {

    View myView;
    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    String personPhoto;
    ImageView imgProfilePic;
    TextView username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_user, container, false);
        imgProfilePic = (ImageView) myView.findViewById(R.id.imageView2);
        username = (TextView) myView.findViewById(R.id.textView4);
        getInfoAccount();


        return myView;
    }


    public void getInfoAccount(){


        GoogleSignInAccount acct = ((MyApplication) this.getActivity().getApplication()).getAccount();

        personName = acct.getDisplayName();
        personGivenName = acct.getGivenName();
        personFamilyName = acct.getFamilyName();
        personEmail = acct.getEmail();
        personId = acct.getId();
        personPhoto = acct.getPhotoUrl().toString();

        Log.d("ciao", "Name: " + personName + ", Image: " + personPhoto);



        Glide.with(this.getActivity()).load(personPhoto).into(imgProfilePic);
        username.setText(personName);
    }

}
