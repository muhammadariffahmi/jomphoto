package com.example.jomphoto;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class VideoOverlayActivity extends Activity implements SurfaceHolder.Callback {

    static {
        System.loadLibrary("opencv_java4");  // Load OpenCV native library
    }

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private OverlayView overlayView;

    private Bitmap imageOverlay;

    private String videoUrl;

    private Button playButton, pauseButton, replayButton;
    private Button overlayTextButton, overlayImageButton;
    private SeekBar videoSeekBar;

    private boolean isPlaying = false;
    private Thread playbackThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_overlay);

        String videoUriStr = getIntent().getStringExtra("video_uri");
        Uri videoUri = Uri.parse(videoUriStr);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        overlayView = findViewById(R.id.overlayView);

        // Create transparent base image same size as SurfaceView (delay until surfaceCreated for real size)
        surfaceView.post(() -> {
            Bitmap transparentBase = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
            overlayView.setBaseImage(transparentBase);
        });

        imageOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.sunsetphoto);
        overlayView.setOverlayImage(imageOverlay);



        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        replayButton = findViewById(R.id.replayButton);
        overlayTextButton = findViewById(R.id.overlayTextButton);
        overlayImageButton = findViewById(R.id.overlayImageButton);

        videoSeekBar = findViewById(R.id.videoSeekBar);
        videoSeekBar.setMax(100);




        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        replayButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        overlayTextButton.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setHint("Enter text to overlay");

            new AlertDialog.Builder(this)
                    .setTitle("Overlay Text")
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String userText = input.getText().toString().trim();
                        if (!userText.isEmpty()) {
                            overlayView.setOverlayText(userText);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        overlayImageButton.setOnClickListener(v -> {
            boolean current = overlayView.isImageOverlayShown();
            overlayView.showImageOverlay(!current);  // Toggle image overlay on/off
        });

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress * mediaPlayer.getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // optional
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // optional
            }
        });



    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(holder.getSurface());

        try {
            String videoUriStr = getIntent().getStringExtra("video_uri");
            Uri videoUri = Uri.parse(videoUriStr);

            videoUrl = videoUri.getPath();
            Log.d("VideoOverlayActivity", "Received video path: " + videoUrl);
            Log.d("VideoOverlayActivity", "Video exists? " + new File(videoUrl).exists());

            AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(videoUri, "r");
            if (afd == null) {
                Toast.makeText(this, "Failed to open video file", Toast.LENGTH_SHORT).show();
                return;
            }

            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            startPlayback();  // ✅ Keeps seek bar in sync

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
        }
    }





    private void startPlayback() {
        if (playbackThread == null || !playbackThread.isAlive()) {
            isPlaying = true;
            playbackThread = new Thread(() -> {
                while (isPlaying) {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        int progress = (int) ((currentPosition / (float) duration) * 100);
                        runOnUiThread(() -> videoSeekBar.setProgress(progress));
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            playbackThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            isPlaying = false;
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // No action needed
    }
}
