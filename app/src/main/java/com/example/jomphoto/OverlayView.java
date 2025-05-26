package com.example.jomphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {

    private boolean showText = false;
    private boolean showImage = false;
    private Bitmap image;
    private Paint textPaint;
    private String overlayText = "";  // dynamic text here

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(60);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setImage(Bitmap bmp) {
        image = bmp;
        invalidate();
    }

    // Set custom text
    public void setOverlayText(String text) {
        overlayText = text;
        showText = true;
        invalidate(); // Redraw the view
    }

    public void showTextOverlay(boolean show) {
        showText = show;
        invalidate(); // Redraw the view
    }

    public void showImageOverlay(boolean show) {
        showImage = show;
        invalidate(); // Redraw the view
    }

    // Scale image to fit within view dimensions
    private Bitmap scaleImage(Bitmap originalImage, int maxWidth, int maxHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Calculate the scaling factor to ensure the image fits within the view
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Calculate the new width and height based on the scaling factor
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        // Return the resized bitmap
        return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showText && !overlayText.isEmpty()) {
            canvas.drawText(overlayText, 100, 100, textPaint);
        }

        if (showImage && image != null) {
            // Resize the image to fit within the view dimensions
            Bitmap scaledImage = scaleImage(image, getWidth(), getHeight());

            // Draw the resized image on the canvas
            int xPosition = (getWidth() - scaledImage.getWidth()) / 2;  // Center the image horizontally
            int yPosition = (getHeight() - scaledImage.getHeight()) / 2;  // Center the image vertically

            canvas.drawBitmap(scaledImage, xPosition, yPosition, null);
        }
    }
}
