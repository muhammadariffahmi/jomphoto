package com.example.jomphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private PhotoDatabaseHelper dbHelper;
    private PhotoAdapter adapter;
    private List<String> photoUris;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dbHelper = new PhotoDatabaseHelper(this);
        photoUris = dbHelper.getAllPhotos();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new PhotoAdapter(this, photoUris, uri -> {
            // show photo fullscreen or in dialog
            Intent intent = new Intent(this, FullscreenActivity.class);
            intent.putExtra("photo_uri", uri);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);


        // Listener for add photo button
        FloatingActionButton addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }
    // Method to open gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    // Handle result from gallery
    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // ✅ Persist read permission
            final int takeFlags = data.getFlags() &
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getContentResolver().takePersistableUriPermission(imageUri, takeFlags);



            // 1. Save to database
            dbHelper.insertPhoto(imageUri.toString());

            // 2. Update RecyclerView
            photoUris.clear();
            photoUris.addAll(dbHelper.getAllPhotos());
            adapter.notifyDataSetChanged();
        }
    }

}






