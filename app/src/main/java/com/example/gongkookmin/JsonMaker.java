package com.example.gongkookmin;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonMaker {
    private JSONObject file;

    public JsonMaker(){
        file = new JSONObject();
    }

    public boolean putData(String name, Object data){
        try{
            file.put(name,data);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return file.toString();
    }
}
