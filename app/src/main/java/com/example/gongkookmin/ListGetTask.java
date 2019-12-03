package com.example.gongkookmin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


class ListGetTask extends CommunicationTask{
    Context context;
    boolean ListEnd = false;

    public boolean isListEnd() {
        return ListEnd;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setListEnd(boolean listEnd) {
        ListEnd = listEnd;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    String nextPage = "";
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
                    nextPage = (next);
                }
                JSONArray list = jsonObject.getJSONArray("results");
                for(int i = 0;i<list.length();i++){
                    JSONObject offer = list.getJSONObject(i);
                    Drawable image;
                    String title = offer.getString("title");
                    String updateTime = offer.getString("created_at");
                    String owner = offer.getString("owner_name");
                    String expires = offer.getString("expires");
                    int id = offer.getInt("id");
                    if(offer.isNull("thumbnail")){
                        image = ContextCompat.getDrawable(context, R.drawable.basic_image);
                        listAdapter.addItem(image,title,owner,new DateHelper(updateTime),id);
                    }
                    else{
                        image = ContextCompat.getDrawable(context,R.drawable.load_image);
                        String value = offer.getString("thumbnail");
                        int list_id = listAdapter.getCount();
                        listAdapter.addItem(image,title,owner,new DateHelper(updateTime),id);
                        BackgroundTask task = new BackgroundTask(list_id);
                        task.execute(value);
                    }
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
    class BackgroundTask extends AsyncTask<String,Boolean,Boolean> {
        Drawable drawable;
        boolean isOK;

        int list_id;

        public BackgroundTask(int list_id){
            this.list_id = list_id;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String value = strings[0];

            try {
                URL url = new URL(value);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                drawable = new BitmapDrawable(Resources.getSystem(),bitmap);
                inputStream.close();
                isOK = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                isOK = false;
            }catch (IOException e){
                e.printStackTrace();
                isOK = false;
            }
            publishProgress(isOK);
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if(values[0])
                ((UserArticlesListViewItem)listAdapter.getItem(list_id)).setIcon(drawable);
            else{
                drawable = ContextCompat.getDrawable(context, R.drawable.basic_image);
                ((UserArticlesListViewItem)listAdapter.getItem(list_id)).setIcon(drawable);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}