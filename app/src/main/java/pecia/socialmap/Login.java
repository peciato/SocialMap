package pecia.socialmap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.StorageReference;

public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    CallbackManager mCallbackManager = null;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setBackground(getDrawable(R.drawable.com_facebook_button_login_background));
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                appStart();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }


        });

        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed
                    //t.setText("PRE5");
                } else {
                    // User is signed out
                    //t.setText("PRE6");
                }
                // ...
            }
        };
        //t = (TextView) findViewById(R.id.text3);
        ///t.setText("PRE");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:

                signIn();


                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("NOLOG", "NOLOG3");
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.e("NOLOG", "NOLOG1");
                firebaseAuthWithGoogle(account);
                Log.e("NOLOG", "NOLOG2");
            } else {
                Log.e("NOLOG", "NOLOG4");
                appStart();
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            appStart();
        }
        else {
            Log.e("NOLOG", "NOLOG");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * public void login(View view) {
     * <p>
     * t.setText("loginEntrato");
     * EditText em = (EditText) findViewById(R.id.email);
     * email = em.getText().toString();
     * <p>
     * EditText pw = (EditText) findViewById(R.id.pass);
     * password = pw.getText().toString();
     * <p>
     * t.setText(email + password);
     * <p>
     * mAuth.signInWithEmailAndPassword(email, password)
     * .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
     *
     * @Override public void onComplete(@NonNull Task<AuthResult> task) {
     * Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
     * t.setText("loginOk");
     * // If sign in fails, display a message to the user. If sign in succeeds
     * // the auth state listener will be notified and logic to handle the
     * // signed in user can be handled in the listener.
     * if (!task.isSuccessful()) {
     * Log.w(TAG, "signInWithEmail:failed", task.getException());
     * t.setText("loginFail");
     * }
     * <p>
     * // ...
     * }
     * });
     * }
     * <p>
     * public void newuser(View view) {
     * EditText em = (EditText) findViewById(R.id.email);
     * email = em.getText().toString();
     * <p>
     * EditText pw = (EditText) findViewById(R.id.pass);
     * password = pw.getText().toString();
     * mAuth.createUserWithEmailAndPassword(email, password)
     * .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
     * @Override public void onComplete(@NonNull Task<AuthResult> task) {
     * Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
     * <p>
     * // If sign in fails, display a message to the user. If sign in succeeds
     * // the auth state listener will be notified and logic to handle the
     * // signed in user can be handled in the listener.
     * if (!task.isSuccessful()) {
     * <p>
     * }
     * <p>
     * // ...
     * }
     * });
     * }
     **/

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //updateUI(null);

                        }

                        // ...
                    }
                });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            appStart();
                        } else {

                        }

                        // ...
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void appStart() {
        Intent myIntent = new Intent(Login.this, Mappa.class);
        Login.this.startActivity(myIntent);
        this.finish();
    }
}
