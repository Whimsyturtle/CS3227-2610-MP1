# Updating JavaFX Gradle Configuration

# My Request(s)
## Request 1
Asked to review and fix the JavaFX setup in `build.gradle`, especially the suspected JavaFX version and any other issues.

## Request 2
Questioned what the added `Enable-Native-Access` manifest entry was for.

## Request 3
Asked whether native-access configuration would still be needed after changing the classpath.

# Your Response(s)
## Response 1
Updated JavaFX from 17.0.7 to 25.0.4 for the Java 25 project, retained Windows/macOS/Linux dependencies for the cross-platform fat JAR, replaced Gradle 10-incompatible dependency syntax, and added native-access settings. Verified tests, dependency resolution, `shadowJar`, manifest contents, and the packaged JAR.

## Response 2
Explained that JavaFX loads native OS libraries and JDK 25 warns unless classpath code is granted native access. Clarified that the Gradle JVM argument covers `gradlew run`, while the manifest entry covers `java -jar`, and that this is not an OS-level permission.

## Response 3
Clarified that changing classpath contents alone does not matter: a classpath/fat-JAR setup still needs `ALL-UNNAMED`, while moving JavaFX to the module path should use `--enable-native-access=javafx.graphics` and remove the manifest entry. Removing JavaFX allows both settings to be removed.

# What You Got Wrong (if any)

- Added native-access configuration proactively while the app was still CLI-only, without first explaining why it would become relevant when JavaFX starts.

# Any Other Interesting Information (if any)

- The copied dependency notation already produced Gradle 9.6 deprecation warnings and would fail under Gradle 10.
- Initially treated JavaFX 17.0.7 as needing correction; it was outdated but still compatible with JDK 25, so the change was an alignment and maintenance update rather than a strict compatibility fix.
