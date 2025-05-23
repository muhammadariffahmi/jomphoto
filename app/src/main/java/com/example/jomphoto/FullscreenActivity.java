package com.example.jomphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FullscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        ImageView imageView = findViewById(R.id.fullscreen_image);
        Button editButton = findViewById(R.id.edit_button);

        String uriString = getIntent().getStringExtra("photo_uri");
        Uri uri = Uri.parse(uriString);
        imageView.setImageURI(uri);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPhotoActivity.class);
            intent.putExtra("photo_uri", uriString);
            startActivity(intent);
        });

    }
}
