package com.example.gongkookmin;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequestHelper {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";


    String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6Ijg5ODI2NzkiLCJleHAiOjE1NzUwMjI1NzgsImVtYWlsIjoiODk4MjY3OUBnbWFpbC5jb20ifQ.Lm9hMHdeSrtD1ArbmccMi7sWa_DCXPLe0Dk9Y79Yg8U";
    // 삭제해야됨.

    URL url;
    HttpURLConnection connection = null;
    String requestMethod;
    String data;

    String result = "";

    public HttpRequestHelper(URL url){
        this.url = url;
        try {
            connection = (HttpURLConnection) this.url.openConnection();
        }catch (IOException e){
            connection = null;
            e.printStackTrace();
        }
    }
    public HttpRequestHelper(String url){
        try {
            this.url = new URL(url);
            connection = (HttpURLConnection) this.url.openConnection();
        }catch (MalformedURLException e){
            e.printStackTrace();
            connection = null;
        }catch (IOException e){
            e.printStackTrace();
            connection = null;
        }
    }

    public boolean isConnectionNull(){
        return connection == null;
    }

    public void setRequestMethod(String requestMethod){
        this.requestMethod = requestMethod;
    }

    public void setData(String data){
        try {
            if (data == null) {
                this.data = new String("".getBytes(), "utf-8");
            } else {
                this.data = new String(data.getBytes(),"utf-8");
            }
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            this.data = new String("");
        }
    }

    public boolean sendByPost(){
        boolean isOK = true;
        result = "";
        try {
            Log.d("data ",data);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("authorization",TOKEN);
            connection.setRequestProperty("charset","UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept","*/*");
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            if(requestMethod != GET)
                connection.setDoOutput(true);
            connection.setDoInput(true);

            if(requestMethod != GET) {
                DataOutputStream writer;
                writer = new DataOutputStream(connection.getOutputStream());

                writer.write(data.getBytes("UTF-8"));
                writer.flush();
                writer.close();
            }

            BufferedReader reader;
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(),"UTF-8"));
                isOK = false;
            }
            else
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

            String line;
            result = "";
            while((line = reader.readLine())!=null){
                result += line;
            }
        }catch (ProtocolException e){
            result="";
            e.printStackTrace();
            return false;
        }catch (IOException e){
            result="";
            e.printStackTrace();
            return false;
        }catch (Exception e){
            result="";
            e.printStackTrace();
            return false;
        }
        finally {
            if(connection != null)
                connection.disconnect();
        }
        return isOK;

    }

    public boolean sendData(){
        if(isConnectionNull())
            return false;   // connection이 안만들어졋다면 문제가 있기때문에 false. 이 값으로 알림 만들기.
        switch(requestMethod){
            case GET :
            case POST :
                return sendByPost();
        }
        return false;
    }

    public String getData(){
        return result;
    }
    public JSONObject getDataByJSONObject(){
        try {
            if(result.trim() == "")
                return null;
            JSONObject json = new JSONObject(result);
            return json;
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
