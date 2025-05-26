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
import java.util.HashSet;
import java.util.List;
import android.widget.ImageButton;
import java.util.Set;



public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Photo> photoList;
    private Context context;
    private OnItemClickListener listener;
    private Set<Integer> selectedPositions = new HashSet<>();

    public List<Photo> getSelectedPhotos() {
        List<Photo> selectedPhotos = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selectedPhotos.add(photoList.get(pos));
        }
        return selectedPhotos;
    }

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
        View selectionOverlay;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_item);
            loveIcon = itemView.findViewById(R.id.annotate_symbol);
            selectionOverlay = itemView.findViewById(R.id.selection_overlay);

        }

        public void bind(Photo photo, Context context, OnItemClickListener listener, boolean isSelected) {
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
            loveIcon.setVisibility((photo.getAnnotate() != null && !photo.getAnnotate().isEmpty()) ? View.VISIBLE : View.GONE);

            // Show selection overlay if selected
            selectionOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            // Single click opens fullscreen only if no selection mode active (optional)
            imageView.setOnClickListener(v -> {
                if (context instanceof MainActivity) {
                    MainActivity activity = (MainActivity) context;
                    if (activity.adapter.getSelectedCount() > 0) {
                        // If selection mode active, toggle selection instead of opening fullscreen
                        activity.toggleSelection(getAdapterPosition());
                    } else {
                        listener.onItemClick(photo.getUri());
                    }
                } else {
                    listener.onItemClick(photo.getUri());
                }
            });

            // Long click toggles selection, consume event by returning true
            itemView.setOnLongClickListener(v -> {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).toggleSelection(getAdapterPosition());
                    return true;  // Important to consume the event
                }
                return false;
            });

            imageView.setOnLongClickListener(v -> {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).toggleSelection(getAdapterPosition());
                    return true;  // Consume event
                }
                return false;
            });

        }

    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        boolean isSelected = selectedPositions.contains(position);
        holder.bind(photoList.get(position), context, listener, isSelected);
    }


    // Methods to toggle selection
    public void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedPositions.size();
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}

