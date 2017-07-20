package pecia.socialmap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Francesco on 20/06/17.
 */

public class MyMapFragment extends MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static LocationListener locationListener, locationListener1;
    public static LocationManager locationManager;
    private DatabaseReference mDatabase;
    private LatLng latLng;
    GoogleApiClient mGoogleApiClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private BitmapDescriptor icon;
    private DatabaseReference ref;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private boolean ready;

    ArrayList<MyMarker> arrayMarker;

    GoogleMap mMap;

    public MyMapFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name);
        ready = false;
        ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        geoFire = new GeoFire(ref);
        geoQuery = null;
        arrayMarker = new ArrayList<MyMarker>();
        locationManager = (LocationManager)this.getActivity().getSystemService(LOCATION_SERVICE);

    }


    @Override
    public void onResume() {
        super.onResume();
        getMaps();

    }


    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        ready = false;
    }


    public void getMaps() {
        super.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ready == false) {
            ready = true;

            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //Listener per quando un marker è cliccato
            mMap.setOnInfoWindowClickListener(getInfoWindowClickListener());


            //Initialize Google Play Services
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this.getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Location Permission already granted
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }


            Zoom();
            //Gestisce posizionamento Marker
            posMarker();


        }
    }


    //Apre activity per mandare messaggio
    private void sendMess(Marker marker) {

        Intent intent = new Intent(this.getActivity(), Chat.class);
        MyMarker myMarker = new MyMarker(marker);
        String keyPost = myMarker.findIdMarker(arrayMarker);
        intent.putExtra("keyPost", keyPost);
        startActivity(intent);
    }


    //primo Zoom
    private void Zoom() {

        Log.e("ENTRI?","ENTRI");
        locationManager = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);
        locationListener1 = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.e("ZOOM","ZOOM");
                LatLng latilong = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latilong, 15));
                locationManager.removeUpdates(this);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.e("ZOOM","ZOOM!");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener1);
        Log.e("ZOOM","ZOOM!");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener1);
        Log.e("ZOOM","ZOOM!");

    }


    //Posiziona marker
    private void posMarker() {

        final Activity con = this.getActivity();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if (geoQuery != null) {
                    geoQuery.removeAllListeners();
                }
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                ((MyApplication) con.getApplication()).setLatLng(latLng);
                geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 5);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(final String key, GeoLocation location) {

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(key);
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                NewPost newPost = dataSnapshot.getValue(NewPost.class);

                                //se gia c'è questo marker controlla la durata ed esce
                                if (arrayMarker.indexOf(new MyMarker(key)) != -1) {

                                    if (newPost != null) {
                                        if ((new Date().getTime() - newPost.data) > TimeUnit.MINUTES.toMillis(newPost.durata)) {
                                            int index = arrayMarker.indexOf(new MyMarker(newPost.key));
                                            Marker m = arrayMarker.get(index).getMarker();
                                            m.remove();
                                            arrayMarker.remove(index);
                                        }
                                    }
                                    return;
                                } else {

                                    if (newPost != null) {

                                        if ((new Date().getTime() - newPost.data) < TimeUnit.MINUTES.toMillis(newPost.durata)) {
                                            //altrimenti lo aggiunge
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(newPost.lat, newPost.longi))
                                                    .title(newPost.titolo)
                                                    .snippet(newPost.messaggio)
                                                    .icon(icon)
                                            );

                                            arrayMarker.add(new MyMarker(marker, newPost.key));
                                        }


                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onKeyExited(String key) {


                        int index = arrayMarker.indexOf(new MyMarker(key));
                        if (index > -1) {
                            Marker m = arrayMarker.get(index).getMarker();
                            m.remove();
                            arrayMarker.remove(index);
                        }

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);


    }


    public GoogleMap.OnInfoWindowClickListener getInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                sendMess(marker);
            }
        };
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}