package com.example.gongkookmin;

import android.content.Context;
import android.graphics.Bitmap;
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
    private ArrayList<Bitmap> pictureList;
    private Context context;

    public PictureListViewAdapter(Context context, ArrayList<Bitmap> pictureList){
        this.pictureList = pictureList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_post_pictures,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = pictureList.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        public ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.pictureImageView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try{
                int position = getAdapterPosition();
                pictureList.remove(position);
                notifyItemRemoved(position);
                Log.d("adapter ", " delete ");
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
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
