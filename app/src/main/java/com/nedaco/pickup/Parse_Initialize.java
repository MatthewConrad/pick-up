package com.nedaco.pickup;

import android.app.ProgressDialog;

import com.parse.Parse;

/**
 * Created by petrodanylewycz on 11/9/15.
 */
public class Parse_Initialize extends android.app.Application {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;

    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(this, "MfGSulwjt077DoDOUnacmw4UEEdLko2JvAUWt19V", "VyjWUzWwKRA0dmnD2yRHPy9zEG051q5cwGeQgzHx");

    }


}