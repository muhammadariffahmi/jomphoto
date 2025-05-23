package com.example.jomphoto;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

public class EditPhotoActivity extends AppCompatActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV");
        } else {
            Log.d("OpenCV", "OpenCV loaded successfully");
            System.loadLibrary("opencv_java4");  // Add this line! (or "opencv_java3" depending on your version)
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        // Step 1: Get URI from intent
        String uriString = getIntent().getStringExtra("photo_uri");
        if (uriString != null) {
            Uri uri = Uri.parse(uriString);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                // Step 2: Convert to Mat
                Mat mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);

                // Step 3: Set to ViewModel
                ImageViewModel imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
                imageViewModel.setOriginalImage(mat);
                imageViewModel.setProcessedImage(mat); // Optional: show original first

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Step 4: Load FirstFragment
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new FirstFragment())
//                    .commit();
//        }

//        if (savedInstanceState == null) {
//            // Delay navigation until NavHostFragment is fully attached
//            findViewById(R.id.nav_host_fragment).post(() -> {
//                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//                navController.navigate(R.id.action_FirstFragment_to_SecondFragment);
//            });
//        }
    }

}
