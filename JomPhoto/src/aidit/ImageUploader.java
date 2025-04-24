/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aidit;

import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author user
 */
public class ImageUploader {

    public static File chooseTargetImage(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Target Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );
        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static List<File> chooseTileImagesFolder(Window ownerWindow) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Tile Images Folder");
        File folder = directoryChooser.showDialog(ownerWindow);

        if (folder != null && folder.isDirectory()) {
            return Arrays.asList(folder.listFiles((dir, name) ->
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
            ));
        }
        return null;
    }
}
