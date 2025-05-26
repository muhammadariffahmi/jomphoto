package com.example.jomphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        ImageView imageView = findViewById(R.id.fullscreen_image);
        Button editButton = findViewById(R.id.edit_button);
        Button homeButton = findViewById(R.id.home_button);
        TextView annotationText = findViewById(R.id.annotation_text);

        // Get the URI string
        String uriString = getIntent().getStringExtra("photo_uri");
        Uri uri = Uri.parse(uriString);
        imageView.setImageURI(uri);

        // Load annotation from DB
        PhotoDatabaseHelper dbHelper = new PhotoDatabaseHelper(this);
        String annotation = dbHelper.getAnnotation(uriString); // Assume this method exists

        if (annotation == null || annotation.trim().isEmpty()) {
            annotationText.setText("No annotation");
        } else {
            annotationText.setText(annotation);
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPhotoActivity.class);
            intent.putExtra("photo_uri", uriString);
            startActivity(intent);
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }
}
