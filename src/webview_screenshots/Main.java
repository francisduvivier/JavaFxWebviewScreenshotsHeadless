package webview_screenshots;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Map;

import static webview_screenshots.StaticConfig.*;
import static webview_screenshots.WebviewScreenshots.takeScreenshot;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {

        Map<String, String> namedParameters = getParameters().getNamed();
        if (namedParameters.get("debug").equals("true")) {
            DEBUG_MODE = true;
            System.out.printf("Running application in debug mode, more logs!%n");
        }
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

        int extraPageLoadTime = -1;
        String extraTimeArgument = namedParameters.get("extraTime");

        if (extraTimeArgument != null) {
            try {
                extraPageLoadTime = Integer.parseInt(extraTimeArgument);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (extraPageLoadTime == -1) {
            if (DEBUG_MODE) {
                System.out.printf("INFO: No or bad extraTime argument was passed. The extraTime in milliseconds can be passed with eg. --extraTime=4000 for 4 seconds, now using default: %s millis%n", DEFAULT_EXTRA_PAGE_LOAD_TIME);
            }
            extraPageLoadTime = DEFAULT_EXTRA_PAGE_LOAD_TIME;
        }

        int webviewWidth = -1;
        String widthArgument = namedParameters.get("width");

        if (widthArgument != null) {
            try {
                webviewWidth = Integer.parseInt(widthArgument);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (webviewWidth == -1) {
            if (DEBUG_MODE) {
                System.out.printf("INFO: No or bad width argument was passed. The width in pixels can be passed with eg. --width=1000, now using default: %s pixels%n", DEFAULT_WEBVIEW_WIDTH);
            }
            webviewWidth = DEFAULT_WEBVIEW_WIDTH;
        }

        int webviewHeight = -1;
        String heightArgument = namedParameters.get("height");

        if (heightArgument != null) {
            try {
                webviewHeight = Integer.parseInt(heightArgument);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (webviewHeight == -1) {
            if (DEBUG_MODE) {
                System.out.printf("INFO: No or bad height argument was passed. The height in pixels can be passed with eg. --height=1000, now using default: %s pixels%n", DEFAULT_WEBVIEW_HEIGHT);
            }
            webviewHeight = DEFAULT_WEBVIEW_HEIGHT;
        }
        takeScreenshot(outputPath, url, extraPageLoadTime, webviewWidth, webviewHeight, (success -> {
            Platform.exit();
        }));
    }
}
