package com.nedaco.pickup;

/**
 * Created by petrodanylewycz on 11/9/15.
 */

import android.content.Context;
import android.content.SharedPreferences;


public class User_LocalDB {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDB;


    public User_LocalDB(Context context){
        userLocalDB = context.getSharedPreferences(SP_NAME, 0);

    }
    /*public void storeLogin(User_Class user_class) {

        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString("username", user_class.username);
        spEditor.putString("password", user_class.password);
        spEditor.apply();
    }*/
    public void storeData(User user){

        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
    }
    /*public void storeData(User_Class user_class){

        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString("username", user_class.username);
        spEditor.putString("password", user_class.password);
        spEditor.putString("total_plays", user_class.getTotal_plays());
        spEditor.putString("total_time", user_class.getTotal_time());
        spEditor.putString("available_plays", user_class.getAvailable_plays());
        spEditor.putString("bolus_belt", user_class.getBolus_betl());
        spEditor.putString("glucose_belt", user_class.getGlucose_belt());
        spEditor.putString("monkey_bucks", user_class.getMonkey_bucks());

        spEditor.apply();
    }*/

    public User getLogin(){

        String password = userLocalDB.getString("password", "password");
        String username = userLocalDB.getString("username", "username");

        User registeredUser = new User(username, password);
        return registeredUser;
    }


    public void userLoggedIn(boolean log){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putBoolean("loggedIn", log);
        spEditor.apply();
    }

    public void clearData(){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public boolean isUserLoggedIn(){
        if(userLocalDB.getBoolean("loggedIn", false)){
            return true;
        }
        else{
            return false;
        }
    }

    public String getUsername(String username){
        username = userLocalDB.getString("username", "");
        return username;
    }

    public String getPassword(String password){
        password = userLocalDB.getString("password","");
        return password;
    }
    /*
    public String getTotal_time(String total_time){
        total_time = userLocalDB.getString("total_time","");
        return total_time;
    }
    public String getTotal_plays(String total_plays){
        total_plays = userLocalDB.getString("total_plays","");
        return total_plays;
    }
    public String getAvailable_plays(String available_plays){
        available_plays = userLocalDB.getString("available_plays","");
        return available_plays;
    }
    public String getBolus_Belt(String bolus_belt){
        bolus_belt = userLocalDB.getString("bolus_belt","");
        return bolus_belt;
    }
    public String getGlucose_Belt(String glucose_belt){
        glucose_belt = userLocalDB.getString("glucose_belt","");
        return glucose_belt;
    }
    public String getMonkey_bucks(String monkey_bucks){
        monkey_bucks = userLocalDB.getString("monkey_bucks","");
        return monkey_bucks;
    }
    */
}

