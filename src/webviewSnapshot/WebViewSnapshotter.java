package webviewSnapshot;

import com.sun.istack.internal.NotNull;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static javafx.concurrent.Worker.State;

public class WebViewSnapshotter extends Application {
    @NotNull
    private static final int DEFAULT_EXTRA_PAGE_LOAD_TIME = 5000;
    private static final int MAX_PAGE_LOAD_TIME = 20000;
    private static boolean DEBUG_MODE = true;
    private static String DEMO_URL = "http://urlecho.appspot.com/echo?body=%3Ch1%3EPlease%20pass%20the%20URL%20as%20the%20first%20parameter%20to%20the%20program%3C/h1%3E";
    private static String DEMO_PATH = "screenshot.png";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {

        Map<String, String> namedParameters = getParameters().getNamed();
        if (DEBUG_MODE) {
            System.out.printf("Application 'start' method called with parameters: [%s]%n", namedParameters);
        }
        String outputPath = namedParameters.get("path");
        if (outputPath == null) {
            System.out.printf("WARNING: No path argument was passed. The outputPath should be passed as with --path=path/to/screenshot.png. Will default path [%s] instead%n", DEMO_PATH);
            outputPath = DEMO_PATH;
        }

        String url = namedParameters.get("url");
        if (url == null) {
            System.out.printf("WARNING: No url argument was passed. The url should be passed with --url=http://example.com, now using Demo Url instead%n");
            url = DEMO_URL;
        }

        int tempExtraPageLoadTime = -1;
        String extraTimeArgument = namedParameters.get("extraTime");

        try {
            tempExtraPageLoadTime = Integer.parseInt(extraTimeArgument);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tempExtraPageLoadTime == -1) {
            System.out.printf("WARNING: No or bad extraTime argument was passed. The extraTime in milliseconds can be passed with eg. --extraTime=4000 for 4 seconds, now using default: %s millis%n", DEFAULT_EXTRA_PAGE_LOAD_TIME);
            tempExtraPageLoadTime = DEFAULT_EXTRA_PAGE_LOAD_TIME;
        }
        final int extraPageLoadTime = tempExtraPageLoadTime;
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
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (DEBUG_MODE) {
                    System.out.printf("Received page state: [%s], now waiting for %s seconds to make screenshot%n", newState, extraPageLoadTime / 1000D);
                }
                if (newState == State.SUCCEEDED || newState == State.FAILED || newState == State.CANCELLED) {
                    if (newState == State.FAILED || newState == State.CANCELLED) {
                        System.out.printf("WARNING: Page load was not successful, received state [%s] but trying to make a screenshot anyways%n", newState);
                    }
                    new Thread(() -> {
                        try {
                            Thread.sleep(extraPageLoadTime); //Wait extra because async javascript might still be executing and changing the page.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        doSnapshot(webView, outputFile);
                    }).start();
                }
            }
        });
        startWebviewRendering(stage, webView);
    }

    private void startWebviewRendering(Stage stage, WebView webView) {
        // Create the VBox
        VBox root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);
        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage (Headless because we use Monocle)
        stage.show();
    }

    private void doSnapshot(WebView webView, File outputFile) {
        Platform.runLater(() -> {
            WritableImage snapshot = webView.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
            try {
                ImageIO.write(image, "png", outputFile);
                if (DEBUG_MODE) {
                    System.out.printf("Screenshot made and saved to [%s]%n", outputFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}