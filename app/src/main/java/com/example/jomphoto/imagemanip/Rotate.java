package com.example.jomphoto.imagemanip;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Rotate {
    public Mat rotateLeft(Mat source) {
        System.loadLibrary("opencv_java4");

        Mat rotatedImage = new Mat();
        Core.transpose(source, rotatedImage);
        Core.flip(rotatedImage, rotatedImage, 0);
        return rotatedImage;
    }

    public Mat rotateRight(Mat source) {
        System.loadLibrary("opencv_java4");

        Mat rotatedImage = new Mat();
        Core.transpose(source, rotatedImage);
        Core.flip(rotatedImage, rotatedImage, 1);
        return rotatedImage;
    }
}