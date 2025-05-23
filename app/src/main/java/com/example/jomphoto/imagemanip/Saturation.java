package com.example.jomphoto.imagemanip;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Saturation {
    public Mat changeSaturation(Mat image, float saturationFactor) {
        System.loadLibrary("opencv_java4");

        if (image == null) return null;

        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        List<Mat> hsvChannels = new ArrayList<>(3);
        Core.split(hsvImage, hsvChannels);
        Mat saturationChannel = hsvChannels.get(1);

        Core.multiply(saturationChannel, new Scalar(saturationFactor), saturationChannel); // Adjust saturation
        Core.merge(hsvChannels, hsvImage);

        Mat resultImage = new Mat();
        Imgproc.cvtColor(hsvImage, resultImage, Imgproc.COLOR_HSV2RGB);

        return resultImage;
    }
}
