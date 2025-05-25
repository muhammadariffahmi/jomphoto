package com.example.jomphoto;

import android.content.Context;
import android.graphics.Bitmap;
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
        showImage = false;
        invalidate();
    }

    public void showTextOverlay(boolean show) {
        showText = show;
        if (show) showImage = false;
        invalidate();
    }

    public void showImageOverlay(boolean show) {
        showImage = show;
        if (show) showText = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showText && !overlayText.isEmpty()) {
            canvas.drawText(overlayText, 100, 100, textPaint);
        } else if (showImage && image != null) {
            canvas.drawBitmap(image, 200, 200, null);
        }
    }
}

