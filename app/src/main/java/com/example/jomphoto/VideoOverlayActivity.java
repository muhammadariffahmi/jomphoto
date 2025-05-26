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
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;

public class VideoOverlayActivity extends Activity implements SurfaceHolder.Callback {

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

        // Initialize videoUrl with the resource video
        videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.sunset_video;

        // Initialize SurfaceView and SurfaceHolder for video playback
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // Initialize OverlayView for text/image overlays
        overlayView = findViewById(R.id.overlayView);
        imageOverlay = BitmapFactory.decodeResource(getResources(), R.drawable.sunsetphoto);
        overlayView.setImage(imageOverlay);

        // Initialize buttons
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        replayButton = findViewById(R.id.replayButton);
        overlayTextButton = findViewById(R.id.overlayTextButton);
        overlayImageButton = findViewById(R.id.overlayImageButton);

        // Initialize SeekBar
        videoSeekBar = findViewById(R.id.videoSeekBar);
        videoSeekBar.setMax(100); // Set SeekBar max value (percentage)

        // Play button functionality
        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        // Pause button functionality
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        // Replay button functionality
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

        // Overlay Text button functionality (Open dialog to input text)
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

        // Overlay Image button functionality
        overlayImageButton.setOnClickListener(v -> overlayView.showImageOverlay(true));

        // SeekBar listener to change video position when the user seeks
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    // If the user is seeking, set the video position
                    mediaPlayer.seekTo(progress * mediaPlayer.getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optionally pause or add any behavior when seeking starts
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optionally handle after seeking stops
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(this, Uri.parse(videoUrl));
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                startPlayback();
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing media player", Toast.LENGTH_SHORT).show();
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
                        Thread.sleep(500);  // Update every half second
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
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle any changes to the surface if necessary
    }
}
