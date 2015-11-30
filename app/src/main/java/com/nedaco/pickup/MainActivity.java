package com.nedaco.pickup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by petrodanylewycz on 11/9/15.
 */
public class MainActivity extends AppCompatActivity{
private boolean rem = false;

    private String username, password;
    User_LocalDB userLocalDB;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        userLocalDB = new User_LocalDB(this);
        userLocalDB.getLogin();
        username = userLocalDB.getUsername(username);
        password = userLocalDB.getPassword(password);
         rem = userLocalDB.getRemember();


        Handler handler = new Handler();
        //handler.postDelayed(new Runnable() {

           // @Override
          //  public void run() {
                if (!username.equals("")) {
                    if(rem)
                    {
                        Intent openLoginScreen = new Intent(MainActivity.this, LoginActivity.class);
                        openLoginScreen.putExtra("usrname",userLocalDB.getUsername(""));
                        openLoginScreen.putExtra("pwd",userLocalDB.getPassword(""));

                        startActivity(openLoginScreen);

                        finish();
                    }
                    else
                    {
                        userLocalDB.clearData();
                        Intent openLoginScreen = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(openLoginScreen);
                        finish();
                    }
                  /*  ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                //LOGIN GOOD
                                //Start new activity
                                //grab_Game_Stats();
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });*/
                } else {
                    userLocalDB.clearData();
                    Intent openLoginScreen = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(openLoginScreen);
                    finish();
                }
         //   }
       // }, 2500);


    }
}