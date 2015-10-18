package com.nedaco.pickup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.prefs.Preferences;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
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
        switch (id)
        {
            case R.id.map:
                intent = new Intent(CreateGameActivity.this,MapsActivity.class);
                break;
            //case R.id.create_game:
             //   intent = new Intent(CreateGameActivity.this,CreateGameActivity.class);
              //  break;
            case R.id.Preferences:
                intent = new Intent(CreateGameActivity.this,PreferencesActivity.class);
                break;
            default:
                intent = null;
                break;

        }

        if(intent!=null)
        {
            startActivity(intent);
        }
        //if (id == R.id.map) {
         //   return true;
       // }

        return super.onOptionsItemSelected(item);
    }
}
