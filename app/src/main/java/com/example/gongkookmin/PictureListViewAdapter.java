package com.example.gongkookmin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PictureListViewAdapter extends RecyclerView.Adapter<PictureListViewAdapter.ViewHolder> {
    public static final int VIEW_MODE = 1;  // 사진 보기 모드
    public static final int EDIT_MODE = 2;  // 사진 삭제 모드

    private ArrayList<Uri> pictureList;  // 사진 목록
    private Context context;
    int mode;

    public PictureListViewAdapter(Context context, ArrayList<Uri> pictureList, int mode){
        this.pictureList = pictureList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_post_pictures,parent,false);
        return new ViewHolder(view, mode);
    }

    class BackgroundTask extends AsyncTask<String,Boolean,Boolean>{
        ImageView imageView;
        Bitmap bitmap;
        boolean isOK;
        public BackgroundTask(ImageView imageView){
            this.imageView = imageView;
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
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(R.drawable.basic_image);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageResource(R.drawable.load_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = pictureList.get(position);
        if(uri.getScheme().contains("http")) {
            Log.d("image from", "http");
            BackgroundTask task = new BackgroundTask(holder.imageView);
            task.execute(uri.toString());
        }
        else {
            holder.imageView.setImageURI(uri);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        int mode;
        public ViewHolder(View itemView, int mode){
            super(itemView);
            this.mode = mode;
            if(mode == VIEW_MODE)
                itemView.setBackgroundColor(Color.WHITE);
            imageView = itemView.findViewById(R.id.pictureImageView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (mode) {
                case EDIT_MODE: {
                    try {
                        int position = getAdapterPosition();
                        pictureList.remove(position);
                        notifyItemRemoved(position);
                        Log.d("adapter ", " delete ");
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case VIEW_MODE: {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, ImageviewActivity.class);
                    intent.putExtra("url",pictureList.get(position).toString());
                    context.startActivity(intent);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Uri getItem(int position){
        return pictureList.get(position);
    }
}
