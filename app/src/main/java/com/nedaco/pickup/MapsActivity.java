package com.nedaco.pickup;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * Most of this was modified from Ben Jakuben's tutorial at
 * blog.teamtreehouse.com/beginners-guide-location-android
 */
public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    // member variables
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FloatingActionButton mCenterButton;
    private FloatingActionButton mAddButton;
    private HashMap<Marker, ParseObject> mGameObjects;

    // static constants and strings
    public static final String TAG = MapsActivity.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /**
     * Id to identity ACCESS_FINE_LOCATION permission request.
     */
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // instantiate buttons
        mCenterButton = (FloatingActionButton) this.findViewById(R.id.mapCenterBtn);
        mCenterButton.setOnClickListener(this);

        mAddButton = (FloatingActionButton) this.findViewById(R.id.mapAddBtn);
        mAddButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Activity onResume() called.");
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }else {
            initializeMapServices();
        }
        setUpMapIfNeeded();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Activity onPause() called.");
        // don't want to continue using api client when the app is not in the foreground, so disconnect
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_maps_activity; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
           // case R.id.map:
            //    intent = new Intent(GameOverviewActivity.this,MapsActivity.class);
            //    break;
            case R.id.create_game:
                intent = new Intent(MapsActivity.this,CreateGameActivity.class);
                break;
            case R.id.Preferences:
                intent = new Intent(MapsActivity.this,PreferencesActivity.class);
                break;
            case R.id.LogOut:
                ParseUser user = ParseUser.getCurrentUser();

                if(user!= null)
                {
                    user.logOut();
                }

                intent = new Intent(MapsActivity.this,LoginActivity.class);
                finish();

                break;
            default:
                intent = null;
                break;

        }
        if(intent!=null)
        {
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.mapCenterBtn:
                Log.i("OnClick", "recenter camera!");
                recenterCamera();
                break;
            case R.id.mapAddBtn:
                Intent intent = new Intent(MapsActivity.this, CreateGameActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        ParseObject game = mGameObjects.get(marker);
        Log.d("MapsActivity", "Info Window Clicked for " + game.getObjectId());

        Intent overviewIntent = new Intent(MapsActivity.this, GameOverviewActivity.class);
        overviewIntent.putExtra("gameObject", game.getObjectId());
        startActivity(overviewIntent);


    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     *
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     *
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // try to obtain the map from the SupportMapFragment
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     *
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        getGames();
    }

    private void initializeMapServices(){
        // instantiate the client
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        // create LocationRequest
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(60 * 1000).setFastestInterval(10 * 1000);
    }

    @SuppressWarnings("unchecked")
    private void getGames(){
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        ParseGeoPoint locGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        Log.d("Games", "Current location: " + location.getLatitude()+", "+location.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereWithinMiles("location", locGeoPoint, 50.0); //eventually this will come from user preferences
        query.setLimit(50);
        query.findInBackground(new FindCallback<ParseObject>(){
            public void done(List<ParseObject> gameList, com.parse.ParseException e){
                if(e == null){
                    mGameObjects = new HashMap<>();
                    Log.d("Games", "Retrieved " + gameList.size() + " games");
                    for (ParseObject game : gameList) {
                        ParseGeoPoint pos = (ParseGeoPoint) game.get("location");
                        ArrayList<String> playersArray = (ArrayList<String>) game.get("registered_players");
                        int numRegisteredPlayers = 0;
                        if(playersArray != null) numRegisteredPlayers = playersArray.size();
                        Marker gameMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(pos.getLatitude(), pos.getLongitude()))
                                .title((String) game.get("sport"))
                                .snippet("Players: " + numRegisteredPlayers + "/" + game.get("number_of_players")));
                        mGameObjects.put(gameMarker, game);
                    }
                }else{
                    Log.d("Games", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getLocationPermission(){
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(findViewById(android.R.id.content), R.string.location_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{ACCESS_FINE_LOCATION},
                                    REQUEST_ACCESS_FINE_LOCATION);
                        }
                    });

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMapServices();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } else {
                handleNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended; please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                // attempt to resolve whatever error occured
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }catch(IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "Location services connection failed with code " +
                    connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public void recenterCamera(){
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }


}
