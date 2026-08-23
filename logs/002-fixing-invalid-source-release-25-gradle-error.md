# Fixing invalid source release 25 Gradle error

# My Request(s)

## Request 1
Asked why `./gradlew run` failed on my Java project "Quorum" with `error: invalid source release: 25`.

## Request 2
Asked for a step-by-step guide to install JDK 25 on Windows, suggesting the CS2103 course website as a reference.

## Request 3
Pointed out that the CS2103/T information used was outdated, linking the current AY26/27 S1 tP constraints page which explicitly requires Java 25.

## Request 4
Asked for confirmation on whether to proceed with installing Temurin JDK 25.

## Request 5
Reported that `winget install EclipseAdoptium.Temurin.25.JDK` ran without any PATH/JAVA_HOME prompts, and `java -version` still showed OpenJDK 17.0.17 (Microsoft build). Asked how to fix.

# Your Response(s)

## Response 1
Diagnosed a JDK version mismatch, not a code problem: the build targets Java 25 but Gradle is compiling with an older JDK. Suggested checking `./gradlew -version`, then three options — install JDK 25, use a Gradle toolchain with the foojay resolver, or lower the target in `build.gradle`.

## Response 2
Provided a Windows install guide (winget or Adoptium `.msi`, verification, `gradle.properties`, daemon restart). Incorrectly claimed CS2103 required Java 17 and advised keeping JDK 17 alongside 25.

## Response 3
Fetched the current course site and confirmed Java 25 is required. Retracted the `gradle.properties` workaround as unnecessary, and flagged two things: Mac teammates need the specific Azul JDK 25 + JavaFX build, and the tP constraint about running on a Java-25-only machine is a packaging concern for later.

## Response 4
Confirmed to proceed — Temurin is an OpenJDK build, which the course accepts; the Azul restriction is Mac-only.

## Response 5
Explained that winget installs silently with defaults, so the absence of prompts was expected. Gave diagnostics (`where.exe java`, `$env:JAVA_HOME`) and the fix: set `JAVA_HOME` and place `%JAVA_HOME%\bin` above the Microsoft OpenJDK entry under *System* variables (not User), then verify in a new terminal via `./gradlew -version`.

# What You Got Wrong

- **Cited a past-semester course site.** Claimed CS2103/T required Java 17 and cast doubt on a correct Java 25 setting. The site URL scheme changed this year (`ay2627-s1` vs `ay2526s1`) and search returned stale pages; I should have verified I had the current semester before asserting a requirement.
- **Built advice on that wrong premise.** The `gradle.properties` / `org.gradle.java.home` workaround was framed around preserving a JDK 17 that was never needed.
- **Described the wrong installer flow.** Detailed the interactive MSI "Custom Setup" checkboxes without noting that the winget path I recommended installs silently, so the expected prompts never appeared.

# Any Other Interesting Information

- Root cause of the persistent version mismatch was a pre-existing **Microsoft build of OpenJDK 17** taking precedence on PATH — likely bundled with another tool rather than deliberately installed.
- Minor oddity: the repo is named `CS3227-2610-MP1` while the constraints referenced come from CS2103/T.
