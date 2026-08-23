# Building Quorum JavaFX timezone app CS2103 style

# My Request(s)

## Request 1
Build a JavaFX app "Quorum" that finds hours when everyone is awake across timezones, for scheduling meetings. Wanted a step-by-step guide (what files to commit, what they contain) modelled on the NUS CS2103 AY2627-S1 `ip` template style — but not graded by CS2103 standards. Explicitly excluded `AGENTS.md`/`CLAUDE.md` and any agentic workflow; manual prompting only.

## Request 2
The plan was too detailed. Just wanted basic repo setup (Gradle etc.) and a simple CLI with a few commands.

## Request 3
Even a basic app should follow good SWE principles — no single mega-class doing everything.

## Request 4
Where do the files actually go (paths), and what's the commit order?

# Your Response(s)

## Response 1
Fetched the real AY2627-S1 template repo, the module timeline, and the SE-EDU Gradle/JavaFX guides to get current facts (JDK 25, Gradle arrives later via `add-gradle-support` branch, shadow plugin `com.gradleup.shadow` 9.5.1, JavaFX 17.0.7 with all three OS classifiers). Produced a full build guide mapping every CS2103 increment (`Level-0`…`A-MoreTesting`) onto Quorum features, with two deliberate deviations: Level-8 becomes the overlap engine, and Level-4's polymorphism sits in the awake-window subclasses rather than three separate commands.

## Response 2
Cut back to four things: repo setup, a minimal `build.gradle` (no JavaFX/shadow/checkstyle/JUnit yet), a single-file CLI with `add`/`list`/`bye`, and `./gradlew run`.

## Response 3
Agreed the previous response was wrong and rewrote it as eight small classes — `Quorum` (wiring), `Ui` (all console I/O), `Parser`, `Command`, `CommandType`, `Participant`, `Roster`, `QuorumException` — plus Gradle files. Also listed what was deliberately *not* added (no `Command` hierarchy, no subpackages, no interfaces, no constructor injection) with the trigger condition for adding each.

## Response 4
Gave the full repo tree with paths, and an eight-commit sequence (skeleton → Gradle → rename/package → `Level-0` → `Level-1` → model → parsing → `Level-2`), each compiling on its own, with tag-push commands and the do-not-commit list.

# What You Got Wrong

- **Over-delivered twice.** Answered a "how do I start" question with a full semester-length guide, then had to be told to cut it down.
- **Over-corrected into a mega-class.** The trimmed version put parsing, I/O, and state in one `Quorum.java` — the exact anti-pattern the user then had to call out. "Minimal" was wrongly read as "one file" instead of "few features, still separated".
- **Buried the explicit ask.** The original request said "what files to commit" — concrete paths and commit ordering only appeared in Response 4, after being asked directly.

# Any Other Interesting Information

- Facts verified from source rather than memory: the AY2627-S1 template is leaner than older iterations (no Gradle in the initial commit), and the increment list this semester includes newer items (`A-Enums`, `A-CI`, `A-Personality`, `A-MoreErrorHandling`, `A-MoreTesting`).
- Two knowing deviations from the CS2103 ladder were flagged to the user: packages and Gradle land early (commits 2–3) rather than at `A-Packages`/`A-Gradle`, because retrofitting them is a no-behaviour-change churn commit. Tags can be applied retroactively if the trail matters.
- Claude Code and other agentic tooling were not suggested at any point, per the user's stated constraint.
