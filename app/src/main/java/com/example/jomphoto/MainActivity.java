package com.example.jomphoto;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.jomphoto.imagemanip.BrightnessContrast;
import com.example.jomphoto.imagemanip.Saturation;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.jomphoto.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements Slider.OnChangeListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String imagePath;
    private Mat processedImage;
    private Slider brightnessSlider;
    private Slider contrastSlider;
    private Slider saturationSlider;
    private float brightness;
    private float contrast;
    private float saturation;



    BrightnessContrast bc = new BrightnessContrast();
    Saturation sat = new Saturation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.loadLibrary("opencv_java4");

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(uri);
                        imagePath = getFilePathFromURI(uri);

                        processedImage = Imgcodecs.imread(imagePath);

                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });


               brightnessSlider = findViewById(R.id.brightnessSlider);
               brightnessSlider.addOnChangeListener(this);

               contrastSlider = findViewById(R.id.contrastSlider);
               contrastSlider.addOnChangeListener(this);

                saturationSlider = findViewById(R.id.saturationSlider);
                saturationSlider.addOnChangeListener(this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                        .build());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
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

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        brightness = brightnessSlider.getValue();
        contrast = contrastSlider.getValue();
        saturation = saturationSlider.getValue();

        if (processedImage == null) return;

        Mat image = Imgcodecs.imread(imagePath);
        Mat rgbImage = new Mat();
        Imgproc.cvtColor(image, rgbImage, Imgproc.COLOR_BGR2RGB);

        rgbImage = bc.changeBrightnessAndContrast(rgbImage, contrast, brightness);
        rgbImage = sat.changeSaturation(rgbImage, saturation);

        Bitmap bitmap = Bitmap.createBitmap(rgbImage.width(), rgbImage.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbImage, bitmap);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

    private String getFilePathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
}