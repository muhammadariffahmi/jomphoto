package com.example.jomphoto;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.jomphoto.imagemanip.BrightnessContrast;
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

public class MainActivity extends AppCompatActivity implements Slider.OnChangeListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String imagePath;
    private Slider brightnessSlider;
    private Slider contrastSlider;

    private float brightness;
    private float contrast;


    BrightnessContrast bc = new BrightnessContrast();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(uri);
                        imagePath = getFilePathFromURI(uri);


                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });


               brightnessSlider = findViewById(R.id.brightnessSlider);
               brightnessSlider.addOnChangeListener(this);

               contrastSlider = findViewById(R.id.contrastSlider);
               contrastSlider.addOnChangeListener(this);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose images and videos.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                        .build());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

        if(slider.getId() == R.id.brightnessSlider) {

            brightness = brightnessSlider.getValue();
            Mat processedImage = bc.changeBrightness(imagePath, brightness);

            if (processedImage != null) {
                Bitmap bitmap = Bitmap.createBitmap(processedImage.width(), processedImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(processedImage, bitmap);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        }

        else if(slider.getId() == R.id.contrastSlider){
            contrast = contrastSlider.getValue();
            Mat processedImage = bc.changeContrast(imagePath, contrast);

            if (processedImage != null) {
                Bitmap bitmap = Bitmap.createBitmap(processedImage.width(), processedImage.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(processedImage, bitmap);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }

        }

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