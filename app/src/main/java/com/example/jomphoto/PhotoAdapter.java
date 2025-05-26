package com.example.jomphoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Photo> photoList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String uri);
    }

    public PhotoAdapter(Context context, List<Photo> photoList, OnItemClickListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton loveIcon;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_item);
            loveIcon = itemView.findViewById(R.id.annotate_symbol);
        }

        public void bind(Photo photo, Context context, OnItemClickListener listener) {
            Uri uri = Uri.parse(photo.getUri());
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageDrawable(null);
            }

            // Show love icon only if annotate is not null/empty
            if (photo.getAnnotate() != null && !photo.getAnnotate().isEmpty()) {
                loveIcon.setVisibility(View.VISIBLE);
            } else {
                loveIcon.setVisibility(View.GONE);
            }

            imageView.setOnClickListener(v -> listener.onItemClick(photo.getUri()));
        }
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(photoList.get(position), context, listener);
    }


    @Override
    public int getItemCount() {
        return photoList.size();
    }
}

