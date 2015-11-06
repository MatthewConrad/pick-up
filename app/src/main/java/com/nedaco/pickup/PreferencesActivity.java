package com.nedaco.pickup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.prefs.Preferences;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        final Switch discMode = (Switch) findViewById(R.id.switch_discoveryMode);
        discMode.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // The toggle is enabled
                discMode.setChecked(true);
            } else {
                discMode.setChecked(false);
            }
         }
     });
        final Switch sw_notifications = (Switch) findViewById(R.id.switch_notifications);
        sw_notifications.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    sw_notifications.setChecked(true);
                } else {
                    sw_notifications.setChecked(false);
                }
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
