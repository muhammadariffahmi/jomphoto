package com.example.jomphoto.imagemanip;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Scale {
    public Mat scale(Mat source, float scaleFactor)
    {
        System.loadLibrary("opencv_java4");

        Mat scaledImage = new Mat();

        Imgproc.resize(source, scaledImage, new Size(0,0), scaleFactor, scaleFactor,
                Imgproc.INTER_AREA);

        return scaledImage;
    }
}