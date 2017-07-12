package pecia.socialmap;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;

import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;


public class Mappa extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {




    private MyMapFragment mapFrag;
    public static LocationManager locationManager;
    private ImageView imgProfilePic;
    FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("bubu", "Refreshed token: " + refreshedToken);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMarker();
            }
        });

        Intent intent = new Intent(this, FirebaseIDService.class);
        startService(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Resources res = getResources();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(Color.parseColor("#353535"));

        try {
            navigationView.setItemTextColor(ColorStateList.createFromXml(res,res.getXml(R.color.color_menu)));
            navigationView.setItemIconTintList(ColorStateList.createFromXml(res,res.getXml(R.color.color_menu)));
        } catch (Exception ex) {
            Log.e("Error", "Exception loading drawable");
        }

        imgProfilePic = (ImageView) this.findViewById(R.id.profile_user);



        //aggiungo mapfragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        mapFrag = new MyMapFragment();
        mapFrag.getMaps();
        transaction.add(R.id.content_frame, mapFrag);
        transaction.commit();

        navigationView.setCheckedItem(R.id.nav_map_layout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            this.finish();
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Glide.with(this).load(user.getPhotoUrl().toString()).into(imgProfilePic);

    }

//FUNZIONI PERSONALIZZATE

    //Apre la activity che mette il nuovo post
    public void newMarker() {

        Intent myIntent = new Intent(this, SetMarker.class);
        startActivity(myIntent);

    }

    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        Log.d("cioooooo", "bliblu");
        startActivity(intent);
        finish();
    }



    //FUNZIONI DI SISTEMA
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_map_layout) {

            FragmentManager mManager = this.getFragmentManager();
            //Boolean fragmentPopped = mManager.popBackStackImmediate (MyMapFragment.class.getName(), 1);
            transaction.replace(R.id.content_frame, mapFrag);
            //transaction.addToBackStack(MyMapFragment.class.getName());
            transaction.commit();
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_user_layout) {

            UserFragment userFragment;

            FragmentManager mManager = this.getFragmentManager();

            //Boolean fragmentPopped = mManager.popBackStackImmediate (UserFragment.class.getName(), 1);

            userFragment = new UserFragment();

            transaction.replace(R.id.content_frame, userFragment);
            //transaction.addToBackStack(UserFragment.class.getName());
            transaction.commit();
            fab.setVisibility(View.GONE);

        } else if (id == R.id.nav_post_layout) {

            PostFragment postFragment;
            FragmentManager mManager = this.getFragmentManager();

            //controllo se gia c'è un user fragment aperto
            Boolean fragmentPopped = mManager.popBackStackImmediate (PostFragment.class.getName(), 1);

            postFragment = new PostFragment();

            transaction.replace(R.id.content_frame, postFragment);
            //transaction.addToBackStack(PostFragment.class.getName());
            transaction.commit();
            fab.setVisibility(View.GONE);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }











}

