package com.example.gongkookmin;

import android.content.SharedPreferences;

public class TokenHelper {
    public static final String PREF_NAME = "pref";
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    SharedPreferences pref;
    public TokenHelper(SharedPreferences pref){
        this.pref = pref;
    }
    public String getToken(){
        return pref.getString(TOKEN,"");
    }
    public boolean setToken(String token){
        SharedPreferences.Editor editor = pref.edit();
        if(getToken() != "")
            return false;
        editor.putString(TOKEN,token);
        return editor.commit();
    }
    public String getUserName(){
        return pref.getString(USERNAME,"");
    }
    public String getEmail(){
        return pref.getString(EMAIL,"");
    }
    public boolean setUserName(String username){
        SharedPreferences.Editor editor = pref.edit();
        if(getUserName() != "")
            return false;
        editor.putString(USERNAME,username);
        return editor.commit();
    }
    public boolean setEmail(String email){
        SharedPreferences.Editor editor = pref.edit();
        if(getEmail()!="")
            return false;
        editor.putString(EMAIL,email);
        return editor.commit();
    }
    public void clearToken(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
