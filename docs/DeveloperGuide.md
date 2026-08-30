# Quorum Developer Guide

This guide describes Quorum's design and the software engineering process used to develop it. For commands and user-facing behaviour, see the [User Guide](UserGuide.md).

## Table of Contents

- [Setting Up](#setting-up)
- [Design](#design)
- [Implementation](#implementation)
- [Testing](#testing)
- [Software Engineering Process](#software-engineering-process)
- [Acknowledgements](#acknowledgements)
- [Appendix: Proposed Enhancements](#appendix-proposed-enhancements)

## Setting Up

### Prerequisites

- JDK 25
- Git

Clone the repository and import `build.gradle` as a Gradle project in your IDE.

On macOS or Linux, use these commands from the repository root:

```text
./gradlew run                 # Run the graphical interface
./gradlew run --args="--cli"  # Run the console interface
./gradlew check               # Run tests and Checkstyle
./gradlew shadowJar           # Build build/libs/quorum.jar
```

On Windows, replace `./gradlew` with `.\gradlew.bat`.

## Design

### Architecture

Quorum uses responsibility-based packages. Arrows below show the main control and data flow.

```mermaid
flowchart LR
    User(["👤 User"]) --> UI["quorum.ui"]
    UI -->|input| Logic["quorum.logic"]
    Logic --> Command["quorum.command"]
    Logic --> Storage["quorum.storage"]
    Logic --> Model["quorum.model"]
    Command --> Model
    Command -->|responses| UI
    Storage --> Model
    Disk[("data/roster.tsv")] -->|load| Storage
    Storage -->|save| Disk
```

| Package | Responsibility |
| --- | --- |
| `quorum.ui` | Receives input and displays text responses through the graphical or console interface. |
| `quorum.logic` | Parses input and coordinates command execution and storage. |
| `quorum.command` | Represents and executes each supported command. |
| `quorum.model` | Stores roster data and ranks meeting times. |
| `quorum.storage` | Loads, encodes, and saves the roster. |

`Launcher` is the application's entrypoint. It starts the graphical interface by default or the console interface when `--cli` is present. The graphical interface is implemented with JavaFX. Both interfaces share command processing, model, storage, and response formatting; only their input and display handling differ.

### Main Design Decisions

- `ChatUi` is the response view for the graphical interface, while `ConsoleUi` is the response view for the console interface.
- `Parser` handles command syntax, while each `Command` implementation handles execution.
- Commands depend on `ResponseView` rather than `ChatUi` or `ConsoleUi`, so they are not coupled to a specific frontend.
- Currently, `ChatUi` and `ConsoleUi` inherit `TextResponseView` and share the same text response formatting.
- `RosterStorage` keeps application logic from being coupled to a specific storage mechanism, while `RosterCodec` keeps file access from being coupled to a specific roster format.
- `QuorumException` carries user-correctable errors from parsing, command execution, and storage to the frontend.

### Meeting Model

```mermaid
classDiagram
    class Participant {
        -String name
        -ZoneId zone
    }
    class MeetingAttendee {
        -String name
        -ZoneId zone
    }
    Roster "1" --> "0..*" Participant : stores
    Participant "1" --> "0..*" Tag : has
    WakefulnessPlanner ..> Participant : reads
    WakefulnessPlanner ..> MeetingAttendee : creates
    WakefulnessPlanner ..> WakefulnessResult : returns
    WakefulnessResult *-- TimeSlot
    WakefulnessResult "1" --> "0..*" MeetingAttendee : records not awake
```

Each `Participant` stores its own collection of `Tag` values; there is no shared `Tag` entity or explicit many-to-many association. The same normalized tag value can nevertheless appear on several participants, so tags serve a conceptual many-to-many grouping role.

During a meeting search, the person running Quorum is included as `You`, using the timezone supplied in the `meeting` command. `MeetingAttendee` gives `You` and the selected roster participants the same name-and-timezone representation without adding `You` to the roster.

## Implementation

### Command Execution

The following sequence shows a successful `meeting` command from the graphical interface. All commands use the same parse-execute-save path. During execution, commands send responses through `ResponseView`, except `ByeCommand`, which returns `EXIT` for the frontend to handle.

```mermaid
sequenceDiagram
    actor User
    participant ChatApplication
    participant QuorumEngine
    participant Parser
    participant MeetingCommand
    participant Roster
    participant WakefulnessPlanner
    participant View as ChatUi / TextResponseView
    participant Storage as RosterStorage

    User->>ChatApplication: Submit meeting command
    ChatApplication->>QuorumEngine: execute(input, ChatUi)
    QuorumEngine->>Parser: parse(input)
    Parser-->>QuorumEngine: MeetingCommand
    QuorumEngine->>MeetingCommand: execute(roster, ChatUi)
    MeetingCommand->>Roster: getParticipantsWithTag(tag)
    Roster-->>MeetingCommand: Matching participants
    MeetingCommand->>WakefulnessPlanner: findBestResults(...)
    WakefulnessPlanner-->>MeetingCommand: Best results
    MeetingCommand->>View: showMeeting(...)
    View-->>ChatApplication: Append formatted message
    MeetingCommand-->>QuorumEngine: CONTINUE
    QuorumEngine->>Storage: save(roster)
    Storage-->>QuorumEngine: Saved
    QuorumEngine-->>ChatApplication: CONTINUE
```

Parsing, command execution, and storage can throw `QuorumException`. The active frontend displays its message. After a command error, the graphical and console interfaces remain available; a startup storage error ends the console application or disables graphical input.

### Meeting-Time Ranking

`WakefulnessPlanner` ranks candidate slots as follows:

1. Add the user and every participant with the requested tag to the attendee list.
2. Generate starts at 30-minute intervals across the requested date in the user's timezone.
3. Count an attendee as awake only when the full meeting is within 08:00 to 22:00 in that attendee's timezone.
4. Keep every slot with the greatest awake count, in chronological order.

Calculations use `Instant` for a shared point on the timeline and `ZoneId` when applying each attendee's local time rules. `TextResponseView` displays at most five maximum-scoring slots and reports how many are not shown.

### Roster Persistence

`QuorumEngine` loads `data/roster.tsv`, relative to the working directory, when it starts. It saves the roster after every successfully executed command. A missing file produces an empty roster. `TsvRosterCodec` stores one participant per line using these fields:

```text
name<TAB>zoneId<TAB>comma-separated tags
```

The codec validates stored participants, timezones, and tags while loading. Invalid data is reported as a startup error instead of being silently discarded.

### Ending a Session

`ByeCommand` returns `EXIT` instead of using an exception for normal control flow. The console leaves its command loop and displays the goodbye message. The graphical interface displays the message, disables input, and closes after a three-second `PauseTransition`.

## Testing

JUnit tests are organised to mirror the corresponding production packages.

| Area | Main behaviours tested |
| --- | --- |
| Model | Validation, immutable updates, roster operations, meeting ranking, and daylight-saving transitions. |
| Commands | Roster changes, responses, errors, and `CommandOutcome` values. |
| Parser | Command routing, argument validation, and boundary values such as indices and durations. |
| Storage | TSV round trips, malformed data, missing files, and file-system errors using temporary directories. |

Command tests use `SilentUi` to suppress console output and small focused spies to record the relevant `ResponseView` call. This keeps the tests independent of displayed text and unrelated UI methods.

Run `./gradlew check` (or `.\gradlew.bat check` on Windows) before committing. It runs the tests and Checkstyle checks. GitHub Actions runs the same task on Windows, macOS, and Linux for every push and pull request.

The graphical interface has no automated UI tests. Command behaviour remains covered through the shared parser, commands, model, and `ResponseView` boundary.

## Software Engineering Process

### Iterative Development

Quorum was developed in small functional increments. New responsibilities were separated when the existing design became difficult to extend, rather than being introduced before they were needed.

| Milestone | Main changes |
| --- | --- |
| Foundation | Added Gradle, the console command loop, roster, `add`, and `list`. |
| Automated checks | Added Checkstyle and continuous integration. |
| Roster management | Added `delete`, `edit`, command classes, persistence, and initial package refactoring. |
| Meeting search | Designed and added timezone search, tags, and wakefulness-based meeting ranking. |
| Broader testing | Added focused model, command, parser, and storage tests; fixed defects exposed by testing. |
| Graphical interface and documentation | Extracted shared engine and response abstractions, added the graphical interface, and documented the product. |

This produced a recurring cycle of scoping a change, implementing it, checking it, and then fixing or refactoring it before the next increment. The history therefore reflects iterative development, but not strict test-driven development.

### AI-Assisted Development

LLMs assisted with design discussions, implementation, tests, refactoring, and documentation. Their suggestions were reviewed against the requirements and repository conventions, then checked through source inspection, tests, Checkstyle, or manual execution as appropriate.

The numbered [`logs`](../logs/) summarise the requests, responses, mistakes, and revisions. A branch-based [unit-test prompt experiment](../logs/016-designing-prompt-quality-unit-test-experiment.md) also compared basic and structured prompts; its [results](../logs/018-evaluating-unit-test-prompt-results-across-sol-and-luna.md) informed the later testing approach.

### Documentation and Traceability

Feature and refactoring commits were kept separate where practical. Significant AI-assisted interactions were summarised in numbered logs and checked before being committed. User-facing documentation was verified against the parser, model, storage behaviour, and packaged application.

## Acknowledgements

- The [CS3227 Project Duke guidance](https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/cs3227.html), NUS CS2103/T iP materials, and [SE-EDU guides](https://se-education.org/guides/) informed the incremental project structure. Gradle, JavaFX, and GitHub Actions configuration was copied or adapted from the CS2103/T iP template.
- The Checkstyle configuration was copied from [SE-EDU AddressBook-Level3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle) and follows the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
- OpenAI LLMs assisted throughout development. [OpenAI Codex](https://openai.com/codex/), including GPT-5.6 Sol and GPT-5.6 Luna, was used for the unit-test prompt experiment and later development. All accepted output was reviewed by the developer; details are recorded in [`logs`](../logs/).
- Quorum uses [OpenJFX](https://openjfx.io/), [JUnit 5](https://junit.org/junit5/), [Checkstyle](https://checkstyle.org/), and the [Shadow Gradle plugin](https://gradleup.com/shadow/).

## Appendix: Proposed Enhancements

These are possible future changes, not current features.

| Current limitation | Proposed enhancement |
| --- | --- |
| Every attendee uses the same 08:00 to 22:00 awake hours. | Store configurable awake hours for each participant. |
| A meeting search accepts one tag. | Allow selection using multiple tags. |
| Meeting times are ranked only by the number awake. | Add optional ranking methods such as minimising inconvenience. |
| Roster saves overwrite the existing file directly. | Write to a temporary file and replace the roster atomically. |
