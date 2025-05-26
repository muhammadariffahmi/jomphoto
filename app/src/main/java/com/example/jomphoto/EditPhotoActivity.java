package com.example.jomphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.jomphoto.databinding.ActivityEditPhotoBinding;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.io.InputStream;

public class EditPhotoActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ImageViewModel imageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load OpenCV library
        System.loadLibrary("opencv_java4");

        super.onCreate(savedInstanceState);
        ActivityEditPhotoBinding binding = ActivityEditPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // === Load URI from Intent ===
        String uriString = getIntent().getStringExtra("photo_uri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);

            imageViewModel.setPhotoUri(uriString);
            ImageView imageView = findViewById(R.id.imageView);

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);

                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Mat imageMat = new Mat();
                    Utils.bitmapToMat(bmp32, imageMat);

                    imageViewModel.setOriginalImage(imageMat);
                    imageViewModel.setProcessedImage(imageMat);
                } else {
                    Log.e("EditPhotoActivity", "Failed to decode bitmap from URI.");
                }
            } catch (IOException e) {
                Log.e("EditPhotoActivity", "Error loading image from URI", e);
            }

        } else {
            Log.e("EditPhotoActivity", "No URI received from FullscreenActivity.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
