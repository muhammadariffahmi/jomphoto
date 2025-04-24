/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aidit;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
/**
 *
 * @author user
 */
public class MosaicCreator extends Application {
    private File targetImageFile;
    private List<File> tileImageFiles;
    private WritableImage mosaicResult;

    @Override
    public void start(Stage primaryStage) {
        Button uploadTargetBtn = new Button("Upload Target Image");
        Button uploadTilesBtn = new Button("Upload Tile Images");
        Button createMosaicBtn = new Button("Create Mosaic");
        Button saveMosaicBtn = new Button("Save Mosaic");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);
        progressBar.setVisible(false);

        ImageView preview = new ImageView();
        preview.setFitWidth(600);
        preview.setPreserveRatio(true);

        uploadTargetBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Target Image");
            targetImageFile = fileChooser.showOpenDialog(primaryStage);
        });

        uploadTilesBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Tile Images");
            tileImageFiles = fileChooser.showOpenMultipleDialog(primaryStage);
        });

        createMosaicBtn.setOnAction(e -> {
            if (targetImageFile != null && tileImageFiles != null && !tileImageFiles.isEmpty()) {
                Image targetImage = new Image(targetImageFile.toURI().toString());

                progressBar.setVisible(true);
                progressBar.setProgress(0);

                Task<WritableImage> mosaicTask = new Task<>() {
                    @Override
                    protected WritableImage call() {
                        return MosaicGenerator.generateMosaic(targetImage, tileImageFiles, 20, 20, this);
                    }
                };

                mosaicTask.setOnSucceeded(ev -> {
                    mosaicResult = mosaicTask.getValue();
                    preview.setImage(mosaicResult);
                    progressBar.setVisible(false);
                });

                mosaicTask.setOnFailed(ev -> {
                    System.err.println("Mosaic creation failed.");
                    progressBar.setVisible(false);
                });

                progressBar.progressProperty().bind(mosaicTask.progressProperty());
                new Thread(mosaicTask).start();
            } else {
                System.out.println("Please upload both target and tile images.");
            }
        });

        saveMosaicBtn.setOnAction(e -> {
            if (mosaicResult != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Mosaic Image");

                String suggestedName = "mosaic_" + System.currentTimeMillis();
                fileChooser.setInitialFileName(suggestedName);

                FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png");
                FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG Files (*.jpg)", "*.jpg");
                fileChooser.getExtensionFilters().addAll(pngFilter, jpgFilter);
                fileChooser.setSelectedExtensionFilter(pngFilter);

                File saveFile = fileChooser.showSaveDialog(primaryStage);

                if (saveFile != null) {
                    String chosenExt = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).replace("*", "");
                    if (!saveFile.getName().toLowerCase().endsWith(chosenExt.substring(1))) {
                        saveFile = new File(saveFile.getAbsolutePath() + chosenExt.substring(1));
                    }
                    MosaicGenerator.saveMosaicToFile(mosaicResult, saveFile);
                }
            } else {
                System.out.println("No mosaic image to save yet.");
            }
        });

        VBox root = new VBox(10, uploadTargetBtn, uploadTilesBtn, createMosaicBtn, saveMosaicBtn, progressBar, preview);
        Scene scene = new Scene(root, 800, 700);

        primaryStage.setTitle("Image Mosaic Creator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
