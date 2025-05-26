package com.example.jomphoto;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PhotoDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "photo.db";
    private static final int DB_VERSION = 1;

    public PhotoDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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

    public List<String> getAllPhotos() {
        List<String> photoUris = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT uri FROM photos", null);
        if (cursor.moveToFirst()) {
            do {
                photoUris.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return photoUris;
    }

    public boolean updatePhotoUri(String oldUri, String newUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uri", newUri);

        int rowsUpdated = db.update("photos", values, "uri = ?", new String[]{oldUri});

        return rowsUpdated > 0;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS photos");
        onCreate(db);
    }

    public boolean updateAnnotation(String uri, String annotation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("annotate", annotation);

        int rowsUpdated = db.update("photos", values, "uri = ?", new String[]{uri});
        return rowsUpdated > 0;
    }

    public String getAnnotation(String uri) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT annotate FROM photos WHERE uri = ?", new String[]{uri});
        if (cursor.moveToFirst()) {
            String annotation = cursor.getString(0);
            cursor.close();
            return annotation;
        }
        cursor.close();
        return "";
    }

}
