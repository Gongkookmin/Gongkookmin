package com.example.gongkookmin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JsonMaker {
    private JSONObject json;

    public JsonMaker(){
        json = new JSONObject();
    }

    public boolean putData(String name, Object data){
        try {
            json.put(name, data);
            return true;
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
    }

    public String toString() {
        return json.toString();
    }
}
