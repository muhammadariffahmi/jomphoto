package com.example.jomphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OverlayView extends View {

    private Bitmap baseImage;       // Transparent or base bitmap to draw overlays on
    private Bitmap overlayImage;    // Actual image to overlay (sunsetphoto)
    private Bitmap displayedBitmap; // Result bitmap after overlays applied
    private String overlayText = "";
    private boolean showText = false;
    private boolean showImage = false;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Set base image (transparent or blank) for overlay drawing
    public void setBaseImage(Bitmap bmp) {
        baseImage = bmp;
        applyOverlays();
        invalidate();
    }

    // Set actual image (e.g., sunsetphoto) to overlay on top
    public void setOverlayImage(Bitmap bmp) {
        overlayImage = bmp;
        applyOverlays();
        invalidate();
    }

    public void setOverlayText(String text) {
        overlayText = text;
        showText = true;
        applyOverlays();
        invalidate();
    }

    public void showTextOverlay(boolean show) {
        showText = show;
        applyOverlays();
        invalidate();
    }

    public void showImageOverlay(boolean show) {
        showImage = show;
        applyOverlays();
        invalidate();
    }

    public boolean isImageOverlayShown() {
        return showImage;
    }

    private void applyOverlays() {
        if (baseImage == null) return;

        Mat mat = new Mat();
        Utils.bitmapToMat(baseImage, mat);

        if (showImage && overlayImage != null) {
            Mat overlayMat = new Mat();
            Utils.bitmapToMat(overlayImage, overlayMat);

            // Resize overlay to half width, keep aspect ratio
            int newWidth = mat.cols() / 2;
            int newHeight = (int) (overlayMat.rows() * (newWidth / (float) overlayMat.cols()));
            Imgproc.resize(overlayMat, overlayMat, new Size(newWidth, newHeight));

            // Center position for overlay
            int x = (mat.cols() - newWidth) / 2;
            int y = (mat.rows() - newHeight) / 2;
            Rect roiRect = new Rect(x, y, newWidth, newHeight);

            Mat roi = mat.submat(roiRect);

            // Blend overlay with base mat region
            double alpha = 0.7;
            Core.addWeighted(overlayMat, alpha, roi, 1.0 - alpha, 0.0, roi);
        }

        if (showText && !overlayText.isEmpty()) {
            Imgproc.putText(
                    mat,
                    overlayText,
                    new Point(50, 100),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    2.0,
                    new Scalar(255, 255, 255, 255), // white text
                    3
            );
        }

        displayedBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, displayedBitmap);

        postInvalidate();
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        if (displayedBitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(displayedBitmap, getWidth(), getHeight(), true);
            canvas.drawBitmap(scaled, 0, 0, null);
        }
    }
}
