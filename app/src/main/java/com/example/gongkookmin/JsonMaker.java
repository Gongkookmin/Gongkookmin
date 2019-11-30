package com.example.gongkookmin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class JsonMaker {
    private HashMap<String, Object> json;

    public JsonMaker(){
        json = new HashMap<>();
    }

    public boolean putData(String name, Object data){
        json.put(name,data);
        return true;
    }

    public String toString() {
        String str = "";
        Set set = json.keySet();
        Iterator iter = set.iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            str += key + "=" + json.get(key);
            if(iter.hasNext())
                str += "&";
        }
        return str;
    }
}
