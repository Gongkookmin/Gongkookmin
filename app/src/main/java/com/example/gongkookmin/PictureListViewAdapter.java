package com.example.gongkookmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class PictureListViewAdapter extends RecyclerView.Adapter<PictureListViewAdapter.ViewHolder> {
    public static final int VIEW_MODE = 1;  // 사진 보기 모드
    public static final int EDIT_MODE = 2;  // 사진 삭제 모드

    private ArrayList<Bitmap> pictureList;  // 사진 목록
    private Context context;
    int mode;

    public PictureListViewAdapter(Context context, ArrayList<Bitmap> pictureList, int mode){
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = pictureList.get(position);
        holder.imageView.setImageBitmap(bitmap);
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
                    // TODO

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

    public Bitmap getItem(int position){
        return pictureList.get(position);
    }
}
