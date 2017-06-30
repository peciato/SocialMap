package pecia.socialmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Pierluca on 5/31/2017.
 */

public class UserFragment extends Fragment {

    private View myView;
    private ImageView imgProfilePic;
    private TextView username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_user, container, false);
        imgProfilePic = (ImageView) myView.findViewById(R.id.profile_image);
        username = (TextView) myView.findViewById(R.id.textView4);


        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this.getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this.getActivity()).load(user.getPhotoUrl().toString()).into(imgProfilePic);
        username.setText(user.getDisplayName());

    }
}
