package com.nedaco.pickup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

//        final Switch discMode = (Switch) findViewById(R.id.switch_discoveryMode);
//        discMode.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//                // The toggle is enabled
//                discMode.setChecked(true);
//            } else {
//                discMode.setChecked(false);
//            }
//         }
//     });
//        final Switch sw_notifications = (Switch) findViewById(R.id.switch_notifications);
//        sw_notifications.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // The toggle is enabled
//                    sw_notifications.setChecked(true);
//                } else {
//                    sw_notifications.setChecked(false);
//                }
//            }
//        });

        final TextView mDistanceText = (TextView)findViewById(R.id.distanceDisplay);
        final SeekBar distBar = (SeekBar) findViewById(R.id.seekBar_distance);
        ParseQuery<ParseObject> distanceQuery = ParseQuery.getQuery("Preferences");
        distanceQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        //ParseObject preferences;
        distanceQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    distBar.setProgress(objects.get(0).getInt("distance"));
                    distBar.setProgress(distBar.getProgress());
                    mDistanceText.setText(Integer.toString(distBar.getProgress()));
                } else {

                }
            }
        });
        distBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChanged = 0;
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser){
                progressChanged = progress;
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //after user has made selection, update display items and save the new preference
                distBar.setSecondaryProgress(progressChanged);
                mDistanceText.setText(Integer.toString(progressChanged));
                //save updated preference to parse database
                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> preferenceQuery = ParseQuery.getQuery("Preferences");
                preferenceQuery.whereEqualTo("user", currentUser);
                preferenceQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {
                            ParseObject preferences = objects.get(0);
                            preferences.put("distance",progressChanged);
                            preferences.saveInBackground();
                        }
                    }
                });

                SharedPreferences sharedPref = getSharedPreferences("PickUp", MODE_PRIVATE);
                sharedPref.edit().putInt("distance", progressChanged).apply();
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
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
                intent = new Intent(PreferencesActivity.this,MapsActivity.class);
                break;
            case R.id.create_game:
                intent = new Intent(PreferencesActivity.this,CreateGameActivity.class);
                break;
            case R.id.LogOut:

                ParseUser user = ParseUser.getCurrentUser();

                if(user!= null)
                {
                    ParseUser.logOut();
                }

                intent = new Intent(PreferencesActivity.this,MainActivity.class);
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
}
