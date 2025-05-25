package com.example.jomphoto.imagemanip;

import android.graphics.Color;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;


public class ImageBorder {
    public Mat addImageBorder(Mat source, Color color, int size) {
        System.loadLibrary("opencv_java4");

        if (source == null) return null;

        Scalar borderColor = new Scalar(
                color.red() * 255,
                color.green() * 255,
                color.blue() * 255
        );

        Mat borderedImage = new Mat();
        Core.copyMakeBorder(source, borderedImage, size, size, size, size, Core.BORDER_CONSTANT, borderColor);

        return borderedImage;
    }


}