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

import static javafx.concurrent.Worker.State;

public class WebViewSnapshotter extends Application {
    @NotNull
    private final static String[] programArguments = new String[2];
    private static final int EXTRA_PAGE_LOAD_TIME = 5000;
    private static boolean DEBUG_MODE = true;
    private static String DEMO_URL = "http://urlecho.appspot.com/echo?body=%3Ch1%3EPlease%20pass%20the%20URL%20as%20the%20first%20parameter%20to%20the%20program%3C/h1%3E";
    private static String DEMO_PATH = "screenshot.png";

    public static void main(String[] args) {
        if (DEBUG_MODE) {
            System.out.printf("Main application method called programArguments: [%s]%n", args);
        }
//        if (args.length < 2) {
//            System.out.printf("No outputPath argument was passed. The outputPath should be passed as the second argument, using  default path [%s] instead%n", DEMO_PATH);
//            programArguments[1] = DEMO_PATH;
//        } else {
//            programArguments[1] = args[1];
//        }
        if (args.length < 1) {
            System.out.printf("No outputPath argument was passed. The outputPath should be passed as the second argument, using  Demo Url instead%n");
            programArguments[0] = DEMO_URL;
        } else {
            programArguments[0] = args[0].split(",")[0];
            programArguments[1] = args[0].split(",")[1];
        }


        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        // Create the WebView
        WebView webView = new WebView();
        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();

        // Load the Page
        String url = null;
        try {
            URL url1 = new URL(programArguments[0]);

            url = url1.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String pathLocation = programArguments[1];
        File outputFile = new File(pathLocation);
        System.out.println(outputFile.getAbsolutePath());
        File dir = outputFile.getAbsoluteFile().getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        webEngine.load(url);

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (DEBUG_MODE) {
                    System.out.printf("Received page state: [%s]%n", newState);
                }
                if (newState == State.SUCCEEDED) {

                    new Thread(() -> {
                        try {
                            Thread.sleep(EXTRA_PAGE_LOAD_TIME); //Wait extra because async javascript might still be executing and changing the page.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            doSnapshot(webView, outputFile);
                            if (DEBUG_MODE) {
                                System.out.println("Did snapshot");
                            }
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