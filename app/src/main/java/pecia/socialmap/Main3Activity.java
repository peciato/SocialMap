package pecia.socialmap;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;

import android.location.LocationManager;

import android.os.Bundle;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;



public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;


    private MyMapFragment mapFrag;
    public static LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //aggiungo mapfragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        mapFrag = new MyMapFragment();
        mapFrag.getMaps();
        transaction.add(R.id.content_frame, mapFrag);
        transaction.commit();

    }



    //FUNZIONI PERSONALIZZATE

    //Apre la activity che mette il nuovo post
    public void newMarker(View view) {

        Intent myIntent = new Intent(this, SetMarker.class);
        startActivity(myIntent);

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
            Boolean fragmentPopped = mManager.popBackStackImmediate (MyMapFragment.class.getName(), 1);
            transaction.replace(R.id.content_frame, mapFrag);
            transaction.addToBackStack(MyMapFragment.class.getName());
            transaction.commit();

        } else if (id == R.id.nav_user_layout) {

            UserFragment userFragment;

            FragmentManager mManager = this.getFragmentManager();

            Boolean fragmentPopped = mManager.popBackStackImmediate (UserFragment.class.getName(), 1);

            userFragment = new UserFragment();

            transaction.replace(R.id.content_frame, userFragment);
            transaction.addToBackStack(UserFragment.class.getName());
            transaction.commit();

        } else if (id == R.id.nav_post_layout) {

            PostFragment postFragment;
            FragmentManager mManager = this.getFragmentManager();

            //controllo se gia c'è un user fragment aperto
            Boolean fragmentPopped = mManager.popBackStackImmediate (PostFragment.class.getName(), 1);

            Log.e("frag",fragmentPopped.toString());

            postFragment = new PostFragment();

            transaction.replace(R.id.content_frame, postFragment);
            transaction.addToBackStack(PostFragment.class.getName());
            transaction.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }











}

