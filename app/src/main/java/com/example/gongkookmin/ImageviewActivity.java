package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageviewActivity extends AppCompatActivity {

    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        photoView = findViewById(R.id.photoView);
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("url"));
        if(uri.getScheme().contains("http")){
            BackgroundTask task = new BackgroundTask();
            task.execute(uri.toString());
        }
        else{
            photoView.setImageURI(uri);
        }
    }

    class BackgroundTask extends AsyncTask<String,Boolean,Boolean> {
        Bitmap bitmap;
        boolean isOK;
        public BackgroundTask(){
            isOK = false;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                bitmap = BitmapFactory.decodeStream(inputStream);
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
                photoView.setImageBitmap(bitmap);
            else
                photoView.setImageResource(R.drawable.basic_image);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}
