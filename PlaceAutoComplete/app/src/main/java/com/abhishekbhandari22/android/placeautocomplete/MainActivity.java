package com.abhishekbhandari22.android.placeautocomplete;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private PlaceAutocompleteFragment autocompleteFragment;

    private final int LOCATION_PERMISSION_REQUEST_CODE=1;
    private boolean mPermissionDenied;
    private Location mLocation;
    private LatLng userLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionDenied=false;

        //setup your placeautocomplete widget if needed
        setUpPlaceAutoComplete();

        //initialize your map
        setmMapIfNeeded();

        //initialize your GoogleApiClient for getting your current location
        setUpClientIfNeeded();
    }
    //MainActivity method
    @Override
    protected void onStart(){
        super.onStart();
        //in order to make a call yur client must first try to connect to server
        // this will trigger onConnected method if connected successfully
        client.connect();
    }
    //MainActivity method
    @Override
    protected void onStop(){
        super.onStop();
        //Whenever working with GoogleAPIClient it is a good practise to disconnect from the server when activity is stopped
        // it is according to google guidelines
        if(client.isConnected())
            client.disconnect();

        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private void setmMapIfNeeded(){
        if(mMap==null){
            //initialize your map
            SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }
    //OnMapReadyCallback method
    @Override
    public void onMapReady(GoogleMap map){
        mMap=map;

    }
    private void setUpClientIfNeeded(){
        if(client==null){
            //initialize your GoogleApiClient for getting your current location
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
    //ConnectionCallback methods to override
    @Override
    public void onConnected(Bundle connectionHint){
        //Before making a call to get location using FusedLocationApi I have to make sure that I have
        //the permission to access location therefore first make a check that you have the necessary permissions
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else if(client!=null){
            //now we are ready to make call to GoogleLocationServices API
            mLocation=LocationServices.FusedLocationApi.getLastLocation(client);
            if(mLocation!=null){
                userLocation=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
               if(mMap!=null) {
                   mMap.addMarker(new MarkerOptions().position(userLocation));
                   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
               }
            }

        }
    }
    //ConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
    @Override
    public void  onConnectionSuspended(int cause){
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("LocationAPI", "Connection suspended");
        client.connect();

    }
    //whenever we are making runtime request for android permissions we have to
    // implement ActivityCompat.OnRequestPermissionsResultCallback
    //and override onRequestPermissionsResult and then trigger necessary information to user
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,
                                          @NonNull int[] grantResults){
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if(grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)  {
            // Enable the my location layer if the permission has been granted.
            //enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    //mapFragment method
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    //setup your placeautocompletewidget if needed
    private void setUpPlaceAutoComplete(){
        if(autocompleteFragment==null){
            autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.i("selected ", "Place: " + place.getName());
                    Log.i("Selected", "LatLong" + place.getLatLng().toString());
                    moveCameraToSelectedPlace(place.getLatLng());
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("error ", "An error occurred: " + status);
                }
            });
        }
    }
    //method to show missing permission error
    private void showMissingPermissionError(){
        Toast.makeText(this,"Permission was not granted",Toast.LENGTH_LONG).show();
    }

    //method to move your camera location from your current location to the place selected by user on placeAutoComplete
    //widget
    private void moveCameraToSelectedPlace(LatLng latLng){
        if(mMap!=null){
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }

}
