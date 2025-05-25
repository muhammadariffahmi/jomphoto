package com.example.jomphoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;

public class VideoOverlayActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private OverlayView overlayView;
    private Bitmap imageOverlay;

    // Declare videoUrl here but initialize later
    private String videoUrl;

    private Button playButton, pauseButton, replayButton;
    private Button overlayTextButton, overlayImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_overlay);

        // Initialize videoUrl inside onCreate where context is valid
        videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.sunset_video;

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        overlayView = findViewById(R.id.overlayView);
        imageOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.fsktm1);
        overlayView.setImage(imageOverlay);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        replayButton = findViewById(R.id.replayButton);
        overlayTextButton = findViewById(R.id.overlayTextButton);
        overlayImageButton = findViewById(R.id.overlayImageButton);

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
            // Show AlertDialog with EditText for user input
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
        overlayImageButton.setOnClickListener(v -> overlayView.showImageOverlay(true));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(videoUrl));
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Usually no action needed here
    }
}
