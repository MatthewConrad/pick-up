package com.nedaco.pickup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static com.nedaco.pickup.R.id.register_button;

public class RegisterActivity extends AppCompatActivity {

    //UI references
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mNameView;
    private Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mNameView = (EditText) findViewById(R.id.register_Name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.registerEmail);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mSignup = (Button) findViewById(register_button);
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });


    }

    private void signUp()
    {
        //create new ParseUser object with appropriate strings from text views
        ParseUser user = new ParseUser();
        user.setUsername(mEmailView.getText().toString());
        user.setPassword(mPasswordView.getText().toString());
        user.setEmail(mEmailView.getText().toString());

// other fields can be set just like with ParseObject
       //user.put("phone", "650-253-0000");
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();
        //use parse function to register the new user
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject preferences = new ParseObject("Preferences");
                    preferences.put("user",ParseUser.getCurrentUser());
                    preferences.put("distance", 20);
                    //String[] games =  getResources().getStringArray(R.array.sport_spinner);
                    //preferences.put("Games", games);
                    preferences.saveInBackground();
                    Toast.makeText(getApplicationContext(), "Sign-Up Succesful, Thank you", Toast.LENGTH_SHORT).show();
                    //Start new activity
                   // Intent intent = new Intent(RegisterActivity.this, Logi.class);
                    //startActivity(intent);
                    //userLocalDB.userLoggedIn(true);
                    finish();
                            
                } else {
                    Toast.makeText(getApplicationContext(), "Sign Up Failed, Try Again", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
}
