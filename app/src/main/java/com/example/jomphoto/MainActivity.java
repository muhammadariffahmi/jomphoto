package com.example.jomphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.jomphoto.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private PhotoDatabaseHelper dbHelper;
    public PhotoAdapter adapter;
    private List<Photo> photoList;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Initialization Failed");
        } else {
            Log.d("OpenCV", "OpenCV Initialized");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dbHelper = new PhotoDatabaseHelper(this);
        photoList = dbHelper.getAllPhotos();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new PhotoAdapter(this, photoList, uri -> {
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

        // Create mosaic
        Button mosaicButton = findViewById(R.id.mosaicButton);
        mosaicButton.setOnClickListener(v -> {
//            List<Photo> selectedPhotos = adapter.getSelectedPhotos();
//
//            if (selectedPhotos.isEmpty()) {
//                Snackbar.make(recyclerView, "Please select some photos first", Snackbar.LENGTH_SHORT).show();
//                return;
//            }
//
//            Intent intent = new Intent(MainActivity.this, MosaicActivity.class);
//
//            ArrayList<String> photoUris = new ArrayList<>();
//            for (Photo photo : selectedPhotos) {
//                photoUris.add(photo.getUri());  // make sure Photo.getUri() returns a String
//            }
            Intent intent = new Intent(MainActivity.this, MosaicActivity.class);
            startActivity(intent);

        });


        // Create video
        Button videoButton = findViewById(R.id.videoButton);
        videoButton.setOnClickListener(v -> {
            List<Photo> selectedPhotos = adapter.getSelectedPhotos();

            if (selectedPhotos.isEmpty()) {
                Snackbar.make(recyclerView, "Please select some photos first", Snackbar.LENGTH_SHORT).show();
                return;
            }

            String outputPath = getExternalFilesDir(null).getAbsolutePath() + "/jomvideo.mp4";
            int fourcc = VideoWriter.fourcc('H','2','6','4');

            Snackbar.make(recyclerView, "Video saved to: " + outputPath, Snackbar.LENGTH_LONG).show();

            File testFile = new File(outputPath);
            Log.d("MainActivity", "Video file path: " + outputPath);
            Log.d("MainActivity", "Video file exists? " + testFile.exists());


            File videoFile = saveVideoFromSelectedPhotos(selectedPhotos, outputPath);

            if (videoFile != null) {
                Uri videoUri = FileProvider.getUriForFile(
                        this,
                        "com.example.jomphoto.fileprovider",
                        videoFile
                );

                Intent intent = new Intent(MainActivity.this, VideoOverlayActivity.class);
                intent.putExtra("video_uri", videoUri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Snackbar.make(recyclerView, "Video generation failed", Snackbar.LENGTH_SHORT).show();
            }


        });



    }

    public void toggleSelection(int position) {
        if (adapter != null) {
            adapter.toggleSelection(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the photo list from database
        photoList.clear();
        photoList.addAll(dbHelper.getAllPhotos());
        adapter.notifyDataSetChanged();
    }


    // Method to open gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public Mat uriToMat(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            return mat;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            photoList.clear();
            photoList.addAll(dbHelper.getAllPhotos());
            adapter.notifyDataSetChanged();
        }
    }

    public File saveVideoFromSelectedPhotos(List<Photo> selectedPhotos, String outputPath) {
        if (selectedPhotos.isEmpty()) {
            Log.d("Video", "No photos selected");
            return null;
        }

        List<Mat> frames = new ArrayList<>();
        int width = 0, height = 0;

        // Convert all selected images to Mats
        for (Photo photo : selectedPhotos) {
            Mat mat = uriToMat(this, Uri.parse(photo.getUri()));
            if (mat == null || mat.empty()) {
                Log.d("Video", "Skipped photo: " + photo.getUri());
                continue;
            }

            int channels = mat.channels();
            if (channels == 1) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2BGR);
            } else if (channels == 4) {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2BGR);
            }


            if (width == 0 || height == 0) {
                width = mat.cols();
                height = mat.rows();
            } else {
                Imgproc.resize(mat, mat, new Size(width, height));
            }

            frames.add(mat);
        }

        if (frames.isEmpty()) return null;

        // Write video
        String videoFilePath = outputPath;  // e.g. getExternalFilesDir(null)+"/video.avi"
        int fourcc = VideoWriter.fourcc('H','2','6','4');
        double fps = 24.0;

        VideoWriter writer = new VideoWriter(videoFilePath, fourcc, fps, new Size(width, height),true);
        if (!writer.isOpened()) {
            Log.d("Video", "Failed to open VideoWriter");
            return null;
        }

        int framesPerPhoto = 24; // show each photo for 1 second at 24 fps

        for (Mat frame : frames) {
            for (int i = 0; i < framesPerPhoto; i++) {
                writer.write(frame);
            }
        }


        writer.release();
        File videoFile = new File(videoFilePath);
        if (videoFile.exists()) {
            Log.d("Video", "Video file exists at " + videoFilePath);
            return videoFile;
        } else {
            Log.d("Video", "Video file DOES NOT exist!");
            return null;
        }

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(videoUri, "video/*");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        startActivity(intent);


    }



}






