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

        if (extraTimeArgument != null) {
            try {
                tempExtraPageLoadTime = Integer.parseInt(extraTimeArgument);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tempExtraPageLoadTime == -1) {
            System.out.printf("WARNING: No or bad extraTime argument was passed. The extraTime in milliseconds can be passed with eg. --extraTime=4000 for 4 seconds, now using default: %s millis%n", DEFAULT_EXTRA_PAGE_LOAD_TIME);
            tempExtraPageLoadTime = DEFAULT_EXTRA_PAGE_LOAD_TIME;
        }
        final int extraPageLoadTime = tempExtraPageLoadTime;
        takeScreenshot(outputPath, url, extraPageLoadTime, (success -> {
            Platform.exit();
        }));
    }


}