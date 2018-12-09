package com.buggyarts.showcase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.buggyarts.showcase.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;

    public interface Callback{
        void onImageClick(String imageUrl);
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public ImagesAdapter(Context context, ArrayList<String> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_images, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String imageUrl = images.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onImageClick(imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(images.size() > 0){
            return images.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}