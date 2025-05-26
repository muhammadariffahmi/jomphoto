package com.example.jomphoto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PhotoDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "photo.db";
    private static final int DB_VERSION = 3;

    public PhotoDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void updateAnnotate(String uri, String annotate) {
        Toast.makeText(context, "updateAnnotate() called", Toast.LENGTH_SHORT).show();
        Log.d("DB_DEBUG", "updateAnnotate() called with uri = " + uri + ", annotate = " + annotate);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("annotate", annotate);
        int rows = db.update("photos", values, "uri = ?", new String[]{uri});

        if (rows == 0) {
            Toast.makeText(context, "No matching photo to annotate", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Annotation saved", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }





    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE photos (id INTEGER PRIMARY KEY AUTOINCREMENT, uri TEXT, annotate TEXT)";
        db.execSQL(createTable);
    }

    public void insertPhoto(String uri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uri", uri);
        long result = db.insert("photos", null, values);
        if(result == -1){
            Toast.makeText(context, "Add photo failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Photo added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Photo> getAllPhotos() {
        List<Photo> photoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT uri, annotate FROM photos", null);
        if (cursor.moveToFirst()) {
            do {
                String uri = cursor.getString(0);
                String annotate = cursor.getString(1);
                photoList.add(new Photo(uri, annotate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return photoList;
    }

    public boolean updatePhotoUri(String oldUri, String newUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uri", newUri);

        int rowsUpdated = db.update("photos", values, "uri = ?", new String[]{oldUri});

        return rowsUpdated > 0;
    }

    public String getAnnotation(String uri) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("photos", new String[]{"annotate"},
                "uri=?", new String[]{uri}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String result = cursor.getString(cursor.getColumnIndexOrThrow("annotate"));
            cursor.close();
            return result;
        }

        if (cursor != null) cursor.close();
        return null;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS photos");
        onCreate(db);
    }
}
