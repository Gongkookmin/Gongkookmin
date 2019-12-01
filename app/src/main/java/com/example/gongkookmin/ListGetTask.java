package com.example.gongkookmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gongkookmin.CommunicationTask;
import com.example.gongkookmin.R;
import com.example.gongkookmin.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


class ListGetTask extends CommunicationTask{
    Context context;
    boolean ListEnd = false;

    public boolean isListEnd() {
        return ListEnd;
    }

    public String getNextURL() {
        return nextURL;
    }

    public void setListEnd(boolean listEnd) {
        ListEnd = listEnd;
    }

    public void setNextURL(String nextURL) {
        this.nextURL = nextURL;
    }

    String nextURL = "";
    ListViewAdapter listAdapter;

    public ListGetTask(Context context, ListViewAdapter listAdapter){
        this.context = context;
        this.listAdapter = listAdapter;
    }


    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        JSONObject jsonObject;
        jsonObject = httpRequestHelper.getDataByJSONObject();   // 서버에게 받은 json
        if(jsonObject == null) {
            Toast.makeText(context, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        if(values[0]){
            try {
                if(jsonObject.isNull("next")){
                    ListEnd = true;
                }
                else{
                    String next = jsonObject.getString("next");
                    nextURL = (next);
                }
                JSONArray list = jsonObject.getJSONArray("results");
                for(int i = 0;i<list.length();i++){
                    JSONObject offer = list.getJSONObject(i);
                    Drawable image;
                    if(offer.isNull("image")){
                        image = ContextCompat.getDrawable(context, R.drawable.basic_image);
                    }
                    else{
                        byte[] encodeByte = Base64.decode(offer.getString("image"),Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                        image = new BitmapDrawable(context.getResources(),bitmap);
                    }
                    String title = offer.getString("title");
                    String updateTime = offer.getString("created_at");
                    String owner = offer.getString("owner_name");
                    int id = offer.getInt("id");
                    listAdapter.addItem(image,title,owner,new DateHelper(updateTime),id);
                }
                listAdapter.notifyDataSetChanged();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(context, ""+httpRequestHelper.getData(),
                    Toast.LENGTH_LONG).show();
        }
    }
}