package com.example.jomphoto.imagemanip;

import android.graphics.Color;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class ImageBorder {
    public Mat addImageBorder(Mat source, Color color, int size) {
        System.loadLibrary( "opencv_java4");

        if (source == null) return null;

        Mat rgbImage = new Mat();
        Imgproc.cvtColor(source, rgbImage, Imgproc.COLOR_BGR2RGB);

        Mat borderedImage = new Mat(rgbImage.rows(), rgbImage.cols(), rgbImage.type());
        Core.copyMakeBorder(rgbImage, borderedImage, size, size, size, size, Core.BORDER_CONSTANT);

        return borderedImage;
    }
}