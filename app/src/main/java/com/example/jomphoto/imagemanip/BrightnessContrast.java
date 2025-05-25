package com.example.jomphoto.imagemanip;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BrightnessContrast {
    public Mat changeBrightnessAndContrast(Mat source, float contrast, float brightness) {
        System.loadLibrary("opencv_java4");

        if (source == null) return null;

        Mat rgbImage = new Mat();
        Imgproc.cvtColor(source, rgbImage, Imgproc.COLOR_BGR2RGB);

        Mat brightImage = new Mat();
        rgbImage.convertTo(brightImage, -1, contrast, brightness); // Apply contrast and brightness

        return brightImage;
    }
}
