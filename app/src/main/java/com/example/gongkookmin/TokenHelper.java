package com.example.gongkookmin;

import android.content.SharedPreferences;

public class TokenHelper {
    SharedPreferences pref;
    public TokenHelper(SharedPreferences pref){
        this.pref = pref;
    }
    public String getToken(){
        return pref.getString("token","");
    }
    public boolean setToken(String token){
        SharedPreferences.Editor editor = pref.edit();
        if(getToken() != "")
            return false;
        editor.putString("token",token);
        return editor.commit();
    }
    public void clearToken(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
