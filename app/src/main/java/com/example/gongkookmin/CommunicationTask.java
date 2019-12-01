package com.example.gongkookmin;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

class CommunicationTask extends AsyncTask<String,Boolean,Boolean> {

    HttpRequestHelper httpRequestHelper;
    String Token = "";

    @Override
    protected Boolean doInBackground(String... strings) {
        httpRequestHelper = new HttpRequestHelper(strings[0]);  // [0] 은 url 주소.
        httpRequestHelper.setRequestMethod(strings[1]); // [1] 은 http method
        httpRequestHelper.setData(strings[2]);  // [2] 는 데이터
        if(!Token.trim().equals("")){
            httpRequestHelper.setToken(Token);
        }
        publishProgress(httpRequestHelper.sendData());

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
    }
}