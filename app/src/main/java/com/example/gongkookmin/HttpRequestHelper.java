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

    String boundary = "^-----^";
    String LINE_FEED = "\r\n";

    String TOKEN = "";
    boolean hasTOKEN = false;

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

    public void setToken(String token){
        TOKEN = token;
        hasTOKEN = true;
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
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(requestMethod);
            if(hasTOKEN)
                connection.setRequestProperty("authorization","Bearer "+TOKEN);
            connection.setRequestProperty("charset","UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Accept","*/*");
            if(requestMethod != GET)
                connection.setDoOutput(true);
            if(requestMethod != DELETE)
                connection.setDoInput(true);

            if(requestMethod != GET) {
                DataOutputStream writer;
                writer = new DataOutputStream(connection.getOutputStream());

                writer.write(data.getBytes("UTF-8"));
                writer.flush();
                writer.close();
            }

            BufferedReader reader;
            int code = connection.getResponseCode();
            if(requestMethod == DELETE){
                result = ""+code;
                isOK = true;
                if(connection != null)
                    connection.disconnect();
                return isOK;
            }
            if (!(code == HttpURLConnection.HTTP_OK ||
                    code == HttpURLConnection.HTTP_CREATED ||
                    code == HttpURLConnection.HTTP_ACCEPTED)) {
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
            case DELETE:
            case PUT:
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
