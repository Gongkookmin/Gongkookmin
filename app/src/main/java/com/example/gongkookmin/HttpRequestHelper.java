package com.example.gongkookmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpRequestHelper {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";


    URL url;
    HttpURLConnection connection = null;
    String requestMethod;
    StringBuffer data;

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
        this.data = new StringBuffer();
        if(data == null){
            this.data.append("");
        }
        else{
            this.data.append(data);
        }
    }

    public boolean sendByPost(){
        try {
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod(requestMethod);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            PrintWriter writer;
            writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));

            writer.write(data.toString());
            writer.flush();
            writer.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return false;

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

            String line;
            result = "";
            while((line = reader.readLine())!=null){
                result += line;
            }
        }catch (ProtocolException e){
            e.printStackTrace();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(connection != null)
                connection.disconnect();
        }
        return true;

    }

    public boolean sendData(){
        if(isConnectionNull())
            return false;   // connection이 안만들어졋다면 문제가 있기때문에 false. 이 값으로 알림 만들기.
        switch(requestMethod){
            case POST :
                return sendByPost();
        }
        return false;
    }

    public String getData(){
        return result;
    }
}
