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

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<String> photoUris;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String uri);
    }

    public PhotoAdapter(Context context, List<String> photoUris, OnItemClickListener listener) {
        this.context = context;
        this.photoUris = photoUris;
        this.listener = listener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_item);
        }

        public void bind(String uriString, Context context, OnItemClickListener listener) {
            Uri uri = Uri.parse(uriString);
            try {
                // Open stream safely
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // fallback - clear image or set placeholder
                imageView.setImageDrawable(null);
            }

            imageView.setOnClickListener(v -> listener.onItemClick(uriString));
        }

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(photoUris.get(position), context, listener);
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }
}

