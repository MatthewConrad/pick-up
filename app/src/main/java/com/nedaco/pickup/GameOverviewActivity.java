package com.nedaco.pickup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameOverviewActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mSportField;
    TextView mLocationField;
    TextView mNumPlayersField;
    TextView mTimeField;
    ListView mPlayersList;
    Button mJoinButton;
    ParseObject mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_overview);
        setContentView(R.layout.activity_game_overview);

        mSportField = (TextView) this.findViewById(R.id.viewSportField);
        mLocationField = (TextView) this.findViewById(R.id.viewLocationField);
        mNumPlayersField = (TextView) this.findViewById(R.id.viewNumPlayersField);
        mTimeField = (TextView) this.findViewById(R.id.viewTimeField);

        mPlayersList = (ListView) this.findViewById(R.id.viewPlayersList);
        mPlayersList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mJoinButton = (Button) this.findViewById(R.id.joinGameButton);
        mJoinButton.setOnClickListener(this);

        initializeContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_maps_activity; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_overview, menu);
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
            case R.id.map:
                intent = new Intent(GameOverviewActivity.this,MapsActivity.class);
                break;
            case R.id.create_game:
                intent = new Intent(GameOverviewActivity.this,CreateGameActivity.class);
                break;
            case R.id.Preferences:
                intent = new Intent(GameOverviewActivity.this,PreferencesActivity.class);
                break;
            case R.id.LogOut:

                ParseUser user = ParseUser.getCurrentUser();

                if(user!= null)
                {
                    ParseUser.logOut();
                }
                intent = new Intent(GameOverviewActivity.this,MainActivity.class);
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
            case R.id.joinGameButton:
                Log.i("OnClick", "Join Game");
                mGame.addUnique("registered_players", ParseUser.getCurrentUser().get("email"));
                try {
                    mGame.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public void initializeContent(){
        String objectId = this.getIntent().getExtras().getString("gameObject");
        Log.d("GameOverviewActivity", "Game: " + objectId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    mGame = objects.get(0);
                    mSportField.setText(mGame.get("sport").toString());
                    mTimeField.setText(mGame.get("time").toString());
                    mNumPlayersField.setText(mGame.get("number_of_players").toString());

                    ParseGeoPoint location = (ParseGeoPoint) mGame.get("location");
                    mLocationField.setText(location.toString());
                    try{
                        Address address = new Geocoder(GameOverviewActivity.this).getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                        mLocationField.setText(address.getAddressLine(0));
                    }catch(IOException ex) {
                        Log.e("GameOverviewActivity", "Error: " + ex.getMessage());
                    }

                    ArrayList<String> playersArray = (ArrayList<String>) mGame.get("registered_players");
                    if(playersArray == null){
                        playersArray = new ArrayList<>();
                        playersArray.add("No players!");
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GameOverviewActivity.this, android.R.layout.simple_list_item_1, playersArray);
                    mPlayersList.setAdapter(arrayAdapter);
                    setListViewHeightBasedOnChildren(mPlayersList);

                }else{
                    Log.e("GameOverview", "Error: " + e.getMessage());
                }
            }
        });
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView  ***
     * <p/>
     * Taken from http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
