package com.example.jomphoto;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class MosaicGenerator {


    public static Bitmap generateMosaic(Mat target, List<Mat> tiles, int tileWidth, int tileHeight) {

        Log.d("Debug", "Target Mat size: " + target.size() + " Channels: " + target.channels());
        Log.d("Debug", "Tiles count: " + tiles.size());
        for (int i = 0; i < tiles.size(); i++) {
            Mat tile = tiles.get(i);
            Log.d("Debug", "Tile " + i + " size: " + tile.size() + " Channels: " + tile.channels());
        }


        // Create output mat
        Mat mosaic = new Mat(target.rows(), target.cols(), CvType.CV_8UC3);
        mosaic.setTo(new Scalar(0, 0, 0));


        // Calculate grid dimensions
        int cols = target.cols() / tileWidth;
        int rows = target.rows() / tileHeight;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int startX = col * tileWidth;
                int startY = row * tileHeight;

                // Get region of interest
                Mat roi = target.submat(startY, startY + tileHeight, startX, startX + tileWidth);

                // Calculate average color
                Scalar avgColor = Core.mean(roi);

                // Find best matching tile
                Mat bestTile = findBestTile(tiles, avgColor);

                if (bestTile.width() != tileWidth || bestTile.height() != tileHeight) {
                    Mat resizedTile = new Mat();
                    Imgproc.resize(bestTile, resizedTile, new Size(tileWidth, tileHeight));
                    resizedTile.copyTo(mosaic.submat(startY, startY + tileHeight, startX, startX + tileWidth));
                    resizedTile.release();
                } else {
                    bestTile.copyTo(mosaic.submat(startY, startY + tileHeight, startX, startX + tileWidth));
                }

            }
        }
        Imgproc.cvtColor(mosaic, mosaic, Imgproc.COLOR_BGR2RGBA);


        // Convert back to Bitmap
        Bitmap result = Bitmap.createBitmap(mosaic.cols(), mosaic.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mosaic, result);
        return result;
    }

    private static Mat findBestTile(List<Mat> tiles, Scalar targetColor) {
        Mat bestTile = tiles.get(0);
        double minDiff = Double.MAX_VALUE;

        for (Mat tile : tiles) {
            // Calculate average color of tile
            Scalar tileColor = Core.mean(tile);

            // Calculate color difference
            double diff = Math.pow(tileColor.val[0] - targetColor.val[0], 2) +
                    Math.pow(tileColor.val[1] - targetColor.val[1], 2) +
                    Math.pow(tileColor.val[2] - targetColor.val[2], 2);

            if (diff < minDiff) {
                minDiff = diff;
                bestTile = tile;
            }
        }

        return bestTile;
    }
}
