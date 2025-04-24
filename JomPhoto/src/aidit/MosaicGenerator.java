/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aidit;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author user
 */
public class MosaicGenerator {

    public static WritableImage generateMosaic(Image targetImage, List<File> tileFiles, int tileWidth, int tileHeight, Task<?> task) {
        int width = (int) targetImage.getWidth();
        int height = (int) targetImage.getHeight();

        int cols = width / tileWidth;
        int rows = height / tileHeight;

        WritableImage mosaic = new WritableImage(cols * tileWidth, rows * tileHeight);
        PixelReader targetReader = targetImage.getPixelReader();

        // Preload tile images
        Image[] tiles = tileFiles.stream()
                .map(f -> new Image(f.toURI().toString(), tileWidth, tileHeight, false, true))
                .toArray(Image[]::new);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int startX = col * tileWidth;
                int startY = row * tileHeight;

                int avgColor = getAverageColor(targetReader, startX, startY, tileWidth, tileHeight);

                // Pick a tile image based on average brightness (for simplicity)
                Image bestTile = tiles[(avgColor % tiles.length)];

                mosaic.getPixelWriter().setPixels(
                        startX, startY,
                        tileWidth, tileHeight,
                        bestTile.getPixelReader(), 0, 0
                );

                // Update progress
                int total = rows * cols;
                int current = row * cols + col;
                if (task != null) {
                    task.updateProgress(current, total);
                }
            }
        }

        return mosaic;
    }

    public static void saveMosaicToFile(WritableImage image, File file) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            String format = "png";
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                format = "jpg";
            }
            ImageIO.write(bufferedImage, format, file);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    private static int getAverageColor(PixelReader reader, int startX, int startY, int width, int height) {
        long red = 0, green = 0, blue = 0;
        int count = 0;

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                int argb = reader.getArgb(x, y);
                red += (argb >> 16) & 0xFF;
                green += (argb >> 8) & 0xFF;
                blue += argb & 0xFF;
                count++;
            }
        }

        int avgRed = (int) (red / count);
        int avgGreen = (int) (green / count);
        int avgBlue = (int) (blue / count);

        return (avgRed + avgGreen + avgBlue) / 3;
    }
}
