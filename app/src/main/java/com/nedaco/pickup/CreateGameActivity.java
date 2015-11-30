package com.nedaco.pickup;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Calendar;

public class CreateGameActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "CreateGameActivity";

    private Button mCreateGame;
    private Spinner mSportSpinner;
    private EditText mNumPlayersEditText, mTimeEditText;
    private AutoCompleteTextView mLocationAutoTextView;
    private CheckBox mUseLocationCheckBox;
    private Location mLastLocation;
    private PlaceAutocompleteAdapter mPlaceAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            mLastLocation = location;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection suspened", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection suspened", "Connection suspended");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        buildGoogleApiClient();

        // create LocationRequest
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(60 * 1000).setFastestInterval(10 * 1000);

        mSportSpinner = (Spinner) findViewById(R.id.spin_sport);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sport_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSportSpinner.setAdapter(adapter);

        mNumPlayersEditText = (EditText) findViewById(R.id.editText_numPlayers);
        mNumPlayersEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String value = mNumPlayersEditText.getText().toString();
                    if (!value.matches("")) {
                        int intValue = Integer.parseInt(value);
                        if (intValue > 16) mNumPlayersEditText.setText("16");
                        else if (intValue < 2) mNumPlayersEditText.setText("2");
                    }
                }
            }
        });

        mTimeEditText = (EditText) findViewById(R.id.editText_time);
        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePicker = new TimePickerDialog(CreateGameActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String meridien = "AM";
                                if (selectedHour > 12) {
                                    selectedHour -= 11;
                                    meridien = "PM";
                                } else if (selectedHour == 0) {
                                    selectedHour = 12;
                                }
                                if(selectedMinute < 10){
                                    mTimeEditText.setText(selectedHour + ":0" + selectedMinute + " " + meridien);
                                }else{
                                    mTimeEditText.setText(selectedHour + ":" + selectedMinute + " " + meridien);
                                }
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });

        mLocationAutoTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_location);
        mPlaceAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);
        mLocationAutoTextView.setAdapter(mPlaceAdapter);

        mUseLocationCheckBox = (CheckBox) findViewById(R.id.checkBox_useLocation);
        mUseLocationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLocationAutoTextView.setEnabled(false);
                    mLocationAutoTextView.setFocusable(false);
                } else {
                    mLocationAutoTextView.setEnabled(true);
                    mLocationAutoTextView.setFocusable(true);
                }
            }
        });

        mCreateGame = (Button) findViewById(R.id.button_createGame);
        mCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected(getApplicationContext())) {
                    storeGame();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public boolean isConnected(Context context) {
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_maps_activity; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_game, menu);
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
        switch (id) {
            case R.id.map:
                intent = new Intent(CreateGameActivity.this, MapsActivity.class);
                break;
            //case R.id.create_game:
            //   intent = new Intent(CreateGameActivity.this,CreateGameActivity.class);
            //  break;
            case R.id.Preferences:
                intent = new Intent(CreateGameActivity.this, PreferencesActivity.class);
                break;
            case R.id.LogOut:

                ParseUser user = ParseUser.getCurrentUser();

                if(user!= null)
                {
                    user.logOut();
                }

                intent = new Intent(CreateGameActivity.this,MainActivity.class);
                finish();

                break;
            default:
                intent = null;
                break;

        }

        if (intent != null) {
            startActivity(intent);
        }
        //if (id == R.id.map) {
        //   return true;
        // }

        return super.onOptionsItemSelected(item);
    }


    public void storeGame() {
        ParseGeoPoint point = new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        ParseObject game = new ParseObject("Game");

        if (!mUseLocationCheckBox.isChecked()) {
            String addressString = mLocationAutoTextView.getText().toString();
            try {
                Address address = new Geocoder(this).getFromLocationName(addressString, 5).get(0);
                point = new ParseGeoPoint(address.getLatitude(), address.getLongitude());
            } catch (IOException ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
        }

        game.put("location", point);
        game.put("sport", mSportSpinner.getSelectedItem().toString());
        game.put("time", mTimeEditText.getText().toString());
        game.put("number_of_players", mNumPlayersEditText.getText().toString());
        game.addUnique("registered_players", ParseUser.getCurrentUser().get("email"));
        game.saveInBackground();
        Toast.makeText(getApplicationContext(), "Game created!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(CreateGameActivity.this, MapsActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Creategameactivity", "Activity onPause() called.");
        // don't want to continue using api client when the app is not in the foreground, so disconnect
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Creategameactivity", "Activity onResume() called.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d("CreateGameActivity", location.toString());

//        mLongitudeText.setText(String.valueOf(location.getLongitude()));
//        mLatitudeText.setText(String.valueOf(location.getLatitude()));


    }

    // Place adapter stuff
    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mPlaceAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i("CreateGameActivity", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i("CreateGameActivity", "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            mLocationAutoTextView.setText(place.getAddress().toString());

            Log.i(TAG, "Place details received: " + place.getAddress());

            places.release();
        }
    };
}
