# Adding Checkstyle and CI/CD to Quorum

# My Request(s)

## Request 1
Add Checkstyle to the Quorum repo, since the current code felt messy and non-standardized.

## Request 2
Set up simple CI/CD, loosely referring to the CS3227 trimmed Project Duke page (no need to follow strictly).

# Your Response(s)

## Response 1

**Checkstyle:** downloaded the AB3 config unmodified into `config/checkstyle/`, added the `checkstyle` plugin to `build.gradle` with `toolVersion = '11.0.0'` pinned to match AB3. Ran it against the real source and got **24 violations**.

**CI:** `.github/workflows/gradle.yml` adapted from the current se-edu template (checkout@v7, setup-java@v5, Zulu JDK 25, `jdk+fx`, three-platform matrix, wrapper validation).

**CD:** `.github/workflows/release.yml` — tag-triggered, runs `check` before `shadowJar`, smoke-tests the JAR, then publishes via the preinstalled `gh` CLI rather than a third-party marketplace action.

# What You Got Wrong

- While rewriting `Quorum.java`, accidentally dropped the `executeAdd` method; caught by the compiler and restored.

# Any Other Interesting Information

N/A