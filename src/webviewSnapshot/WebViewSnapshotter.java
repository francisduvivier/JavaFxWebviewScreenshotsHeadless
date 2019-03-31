package webviewSnapshot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javafx.concurrent.Worker.State;

public class WebViewSnapshotter extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        // Create the WebView
        WebView webView = new WebView();
        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();

        // Load the Page
        String url = "https://francisduvivier.github.io/canvas-white-to-transparent/";
        webEngine.load(url);

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    //stage.setTitle(webEngine.getLocation());
                    stage.setTitle(webEngine.getTitle());
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000); //Wait extra because async javascript might still be executing and changing the page.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            doSnapshot(webView, new File("outputPath.png"));
                            System.out.println("Did snapshot");
                        });
                    }).start();
                }
            }
        });
        // Create the VBox
        VBox root = new VBox();

        // Add the WebView to the VBox
        root.getChildren().add(webView);

        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();

    }

    private void doSnapshot(WebView webView, File outputFile) {
        WritableImage snapshot = webView.snapshot(null, null);
        BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}