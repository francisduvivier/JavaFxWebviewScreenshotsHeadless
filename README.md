### How to build
- Make sure jdk 1.8 is properly installed.
- Create the directory out/production/JavaFXScreenshotProject/ if it does not exist
- Run `javac src/webview_screenshots/*.java -d out/production/JavaFXScreenshotProject/`
- Then cd to out/production/JavaFXScreenshotProject/ and run `jar cfm ../../javaFXScreenshots.jar ../../../MANIFEST.MF ./*`
 
### How to use
- Make sure the Monocle jar from the lib folder (openjfx-monocle-8u76-b04.jar) is in your classpath. To do this
  - On Windows, place the openjfx-monocle-8u76-b04.jar file into the lib\ext folder of your jre, typically located at `"C:\Program Files\Java\jdk1.8.0_201\jre\lib\ext"`
  - On Mac, place the openjfx-monocle-8u76-b04.jar file into `/Library/Java/JavaVirtualMachines/jdk1.8.0_201.jdk/Contents/Home/jre/lib/ext/`
- Then (from the project directory) use `java -Dglass.platform=Monocle -jar out/javaFXScreenshots.jar --url=https://example.com --path=path/to/file.png`

### Convenience bash scripts:
- Copy monocle library to lib/ext on MAC OS: `./scripts/install-monocle-mac.sh`
- Compile java: `./scripts/compile-java.sh`
- Run from class files: `./scripts/run-direct.sh` with args, eg. `./scripts/run-direct.sh --debug=true --url=https://example.com --path=path/to/file.png`
- Compile and package into jar: `./scripts/build-jar.sh`
- Run jar: `./scripts/run-jar.sh` with args, eg. `./scripts/run-jar.sh --debug=true --url=https://example.com --path=path/to/file.png`
