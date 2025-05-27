package com.example.jomphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.graphics.Bitmap;

public class MosaicActivity extends AppCompatActivity {
    private static final int REQUEST_TARGET = 1001;
    private static final int REQUEST_TILES = 1002;
    private static final int TILE_SIZE = 50;

    private ImageView imgPreview;
    private ProgressBar progressBar;
    private Bitmap targetBitmap;
    private final List<Bitmap> tileBitmaps = new ArrayList<>();
    private Bitmap mosaicBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic);

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV!");
        } else {
            Log.d("OpenCV", "OpenCV loaded successfully");
        }

        imgPreview = findViewById(R.id.imgPreview);
        progressBar = findViewById(R.id.progressBar);

        Button btnSelectTarget = findViewById(R.id.btnSelectTarget);
        Button btnSelectTiles = findViewById(R.id.btnSelectTiles);
        Button btnCreateMosaic = findViewById(R.id.btnCreateMosaic);
        Button btnSaveMosaic = findViewById(R.id.btnSaveMosaic);

        btnSelectTarget.setOnClickListener(v -> openImagePicker(REQUEST_TARGET));
        btnSelectTiles.setOnClickListener(v -> openImagePicker(REQUEST_TILES));
        btnCreateMosaic.setOnClickListener(v -> createMosaic());
        btnSaveMosaic.setOnClickListener(v -> saveMosaic());
    }

    private void openImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (requestCode == REQUEST_TILES) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_TARGET) {
                    targetBitmap = loadBitmapFromUri(data.getData());
                    imgPreview.setImageBitmap(targetBitmap);
                } else if (requestCode == REQUEST_TILES) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Bitmap tile = loadBitmapFromUri(data.getClipData().getItemAt(i).getUri());
                            tileBitmaps.add(tile);
                        }
                    } else if (data.getData() != null) {
                        Bitmap tile = loadBitmapFromUri(data.getData());
                        tileBitmaps.add(tile);
                    }
                    Toast.makeText(this, "Selected " + tileBitmaps.size() + " tiles", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createMosaic() {
        if (targetBitmap == null || tileBitmaps.isEmpty()) {
            Toast.makeText(this, "Please select target and tile images", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {

                if (targetBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                    targetBitmap = targetBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }

                // Convert Bitmap to OpenCV Mat (RGBA)
                Mat targetMatRGBA = new Mat();
                Utils.bitmapToMat(targetBitmap, targetMatRGBA);

// Convert target from RGBA to BGR (3 channels)
                Mat targetMat = new Mat();
                Imgproc.cvtColor(targetMatRGBA, targetMat, Imgproc.COLOR_RGBA2BGR);
                targetMatRGBA.release();

// Convert tiles to Mats and convert each from RGBA to BGR
                List<Mat> tileMats = new ArrayList<>();
                for (Bitmap tile : tileBitmaps) {
                    if (tile == null || tile.isRecycled() || tile.getWidth() == 0 || tile.getHeight() == 0) {
                        continue;
                    }
                    if (tile.getConfig() != Bitmap.Config.ARGB_8888) {
                        tile = tile.copy(Bitmap.Config.ARGB_8888, true);
                    }

                    Mat tileMatRGBA = new Mat();
                    Utils.bitmapToMat(tile, tileMatRGBA);

                    Mat tileMatBGR = new Mat();
                    Imgproc.cvtColor(tileMatRGBA, tileMatBGR, Imgproc.COLOR_RGBA2BGR);
                    tileMatRGBA.release();

                    tileMats.add(tileMatBGR);
                }



                Log.d("Debug", "Target Bitmap: " + (targetBitmap == null ? "null" : targetBitmap.getWidth() + "x" + targetBitmap.getHeight()));
                Log.d("Debug", "Number of tile bitmaps: " + tileBitmaps.size());
                for (int i = 0; i < tileBitmaps.size(); i++) {
                    Bitmap tile = tileBitmaps.get(i);
                    Log.d("Debug", "Tile " + i + ": " + tile.getWidth() + "x" + tile.getHeight());
                }


                // Create mosaic
                mosaicBitmap = MosaicGenerator.generateMosaic(
                        targetMat,
                        tileMats,
                        TILE_SIZE,
                        TILE_SIZE
                );

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (mosaicBitmap != null) {
                        imgPreview.setImageBitmap(mosaicBitmap);
                        Log.d("MosaicDebug", "Mosaic generated and set to ImageView.");
                    }
                    else {
                        Log.e("MosaicDebug", "generateMosaic returned null!");
                        Toast.makeText(MosaicActivity.this, "Mosaic creation failed. Try with different images.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MosaicError", "Exception in createMosaic", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MosaicActivity.this, "Error creating mosaic", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveMosaic() {
        if (mosaicBitmap != null) {
            // Implement your save logic here
            Toast.makeText(this, "Mosaic saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No mosaic to save", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } else {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        }
    }


}