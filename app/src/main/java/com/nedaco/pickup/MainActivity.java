package com.nedaco.pickup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;

/**
 * Created by petrodanylewycz on 11/9/15.
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Parse.initialize(this, "MfGSulwjt077DoDOUnacmw4UEEdLko2JvAUWt19V", "VyjWUzWwKRA0dmnD2yRHPy9zEG051q5cwGeQgzHx");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}