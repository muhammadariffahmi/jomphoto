package com.example.jomphoto.imagemanip;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BrightnessContrast {
    public Mat changeBrightnessAndContrast(Mat image, float contrast, float brightness) {
        System.loadLibrary("opencv_java4");

        if (image == null) return null;

        Mat rgbImage = new Mat();
        Imgproc.cvtColor(image, rgbImage, Imgproc.COLOR_BGR2RGB);

        Mat brightImage = new Mat();
        rgbImage.convertTo(brightImage, -1, contrast, brightness); // Apply contrast and brightness

        return brightImage;
    }
}
