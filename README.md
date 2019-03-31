### How to build
- Make sure jdk 1.8 is properly installed.
- Create the directory out/production/JavaFXScreenshotProject/ if it does not exist
- Run `javac src/webview_screenshots/*.java -d out/production/JavaFXScreenshotProject/`
- Then cd to out/production/JavaFXScreenshotProject/ and run `jar cfm ../../javaFXScreenshots.jar ../../../MANIFEST.MF ./*`
 
### How to use
- Make sure the Monocle jar from the lib folder (openjfx-monocle-8u76-b04.jar) is in your classpath. To do this
  - On Windows place the openjfx-monocle-8u76-b04.jar file into the lib\ext folder of your jre, typically located at `"C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext"`
  - On Mac, this place the openjfx-monocle-8u76-b04.jar file into `/Library/Java/JavaVirtualMachines/<jdk version>/Contents/Home/` 
- Then (from the project directory) use `java -Dglass.platform=Monocle -jar out/javaFXScreenshots.jar --url=https://example.com --path=path/to/file.png`