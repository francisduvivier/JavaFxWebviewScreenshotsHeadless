package webview_screenshots;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static webview_screenshots.StaticConfig.*;

public class WebviewScreenshots {
    public static void takeScreenshot(String outputPath, String url) {
        takeScreenshot(outputPath, url, DEFAULT_EXTRA_PAGE_LOAD_TIME, DEFAULT_WEBVIEW_WIDTH, DEFAULT_WEBVIEW_HEIGHT, success -> {
        });
    }

    public static void takeScreenshot(String outputPath, String url, int extraPageLoadTime, int webviewWidth, int webviewHeight, DoneCallback doneCallback) {
        try {
            url = new URL(url).toString();
        } catch (MalformedURLException e) {
            System.out.printf("WARNING: The given url is invalid, now using Demo Url instead%n");
            e.printStackTrace();
            url = DEMO_URL;
        }

        // Create the WebView
        WebView webView = new WebView();
        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();

        // Try to create the output file directory
        File outputFile = new File(outputPath);
        File dir = outputFile.getAbsoluteFile().getParentFile();
        dir.mkdirs();

        // Load the Page
        webEngine.load(url);

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (DEBUG_MODE) {
                    System.out.printf("Received page state: [%s], now waiting for %s seconds to make screenshot%n", newState, extraPageLoadTime / 1000D);
                }
                if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                    if (newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                        System.out.printf("WARNING: Page load was not successful, received state [%s] but trying to make a screenshot anyways%n", newState);
                    }
                    new Thread(() -> {
                        try {
                            Thread.sleep(extraPageLoadTime); //Wait extra because async javascript might still be executing and changing the page.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        doSnapshot(webView, outputFile, doneCallback);
                    }).start();
                }
            }
        });
        startWebviewRendering(webView, webviewWidth, webviewHeight);
    }

    private static void startWebviewRendering(WebView webView, int width, int height) {
        // Create the VBox
        VBox root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);
        webView.setPrefSize(width, height);
        Scene scene = new Scene(root);
        // Add  the Scene to a Stage
        Stage stage = new Stage(StageStyle.UNDECORATED);

        stage.setScene(scene);
        // Display the Stage (Headless because we use Monocle)
        stage.show();
    }

    private static void doSnapshot(WebView webView, File outputFile, DoneCallback doneCallback) {
        Platform.runLater(() -> {
            WritableImage snapshot = webView.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
            try {
                ImageIO.write(image, "png", outputFile);
                if (DEBUG_MODE) {
                    System.out.printf("Screenshot made and saved to [%s]%n", outputFile.getAbsolutePath());
                }
                doneCallback.onReady(true);
            } catch (IOException e) {
                e.printStackTrace();
                doneCallback.onReady(false);
            }
        });
    }
}
