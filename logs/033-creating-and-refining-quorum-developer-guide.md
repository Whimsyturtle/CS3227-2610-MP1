# Creating and Refining Quorum Developer Guide

# My Request(s)

## Request 1

Proposed a Developer Guide containing acknowledgements, architecture and implementation diagrams, and future enhancements. Asked whether the structure worked and what else to include.

## Request 2

Suggested documenting the iterative development process and asked for the Git history to be checked.

## Request 3

Requested `docs/DeveloperGuide.md`. Required concise explanations, repository terminology, and thorough verification after every section.

## Request 4

Questioned whether the Gradle wrapper always downloads Gradle and dependencies, and flagged inconsistent use of “JavaFX interface” instead of “graphical interface.”

## Request 5

Applied KISS and asked why the Gradle-download sentence should not simply be removed.

## Request 6

Asked to simplify the architecture diagram to package names, with a user icon and a connection from storage to disk.

## Request 7

Asked whether separate unidirectional arrows would be clearer than double-headed arrows.

## Request 8

Noted that mixing the `Launcher` class with package nodes in one diagram felt inconsistent.

## Request 9

Asked the prose to identify `Launcher` explicitly as the entrypoint without giving `QuorumEngine` the same emphasis.

## Request 10

After making a minor edit, noticed that the shared behaviour of the graphical and console interfaces was no longer stated.

## Request 11

Requested more precise design-decision bullets: `Command` implementations, explicit frontend decoupling through `ResponseView`, similar coupling explanations for storage interfaces, a separate current-state point for `TextResponseView`, and an introduction to `ChatUi` and `ConsoleUi`.

## Request 12

Questioned the Meeting Model diagram’s empty class compartments, aggregation diamond, `WakefulnessPlanner` dependency, and participant–tag relationship.

## Request 13

Asked to revert the replacement flowchart because it did not render correctly.

## Request 14

Noted that the reverted diagram restored the unwanted aggregation diamond and lost the explanation of tags’ conceptual many-to-many role.

## Request 15

Clarified that the implementation is not many-to-many and that this distinction should be explained only in prose.

## Request 16

Asked to show the shared `name` and `zone` fields of `Participant` and `MeetingAttendee` in the class diagram, and to clarify who “the user” represents.

## Request 17

Pointed out that tags should not appear both as a `Participant` field and as an association.

# Your Response(s)

## Response 1

Recommended adding setup, testing, and software engineering process sections. Proposed architecture, meeting-command sequence, and meeting-model diagrams.

## Response 2

Reviewed the commit history and identified incremental phases covering the skeleton, features, refactoring, testing, JavaFX, and documentation. Avoided describing the process as strict TDD, Scrum, or continuous deployment.

## Response 3

Inspected the implementation, tests, build, logs, and history, then created a concise Developer Guide with three Mermaid diagrams. Verified all tests, Checkstyle, links, terminology, and JAR packaging.

## Response 4

Conceded that cached files are not downloaded again. Revised the DG to use “graphical interface” consistently, reserving “JavaFX” for implementation details.

## Response 5

Agreed that the download explanation added no setup value and removed it entirely.

## Response 6

Simplified the architecture diagram to show the user, `Launcher`, package nodes, and `data/roster.tsv`, leaving responsibilities to the table.

## Response 7

Replaced double-headed arrows with labelled flows for input, responses, loading, and saving.

## Response 8

Agreed that the diagram mixed abstraction levels. Removed `Launcher` from the diagram, used fully qualified package names, and retained `Launcher` in the prose.

## Response 9

Changed the paragraph to identify `Launcher` as the application entrypoint and removed the unnecessary `QuorumEngine` emphasis.

## Response 10

Added that both interfaces share command processing, model, storage, and response formatting, while their input and display handling differ.

## Response 11

Rewrote the design decisions to use “`Command` implementation,” identify both UI classes, explain `ResponseView` decoupling, separate the current `TextResponseView` choice, and explain the `RosterStorage` and `RosterCodec` boundaries.

## Response 12

Confirmed that `MeetingCommand`, not `WakefulnessPlanner`, reads `Roster`; the planner receives `List<Participant>`. Replaced the class diagram with a single-box flowchart and explained that tags are conceptually many-to-many by value but are not shared entities.

## Response 13

Reverted only the Meeting Model section to the original `classDiagram` and prose.

## Response 14

Removed the hollow aggregation diamond and restored the tag explanation, but incorrectly changed the diagram itself to show a many-to-many participant–tag relationship.

## Response 15

Conceded the conceptual/implementation conflation. Restored the implemented one-to-many association and explained the conceptual grouping only in prose.

## Response 16

Added `name` and `zone` fields to `Participant` and `MeetingAttendee`. Clarified that `You` is the person running Quorum, represented by the timezone supplied in the `meeting` command and not stored in the roster.

## Response 17

Removed `tags` from the `Participant` field compartment and kept tags solely as an association.

# What You Got Wrong (if any)

- Initially overstated Gradle wrapper download behaviour and retained an unnecessary explanation until the user applied KISS.
- Used “JavaFX interface” inconsistently with the repository’s “graphical interface” terminology.
- The first simplified architecture diagram mixed a class with package-level nodes and used ambiguous double-headed arrows.
- Replaced the Meeting Model with a flowchart that did not render correctly, then reverted too much and restored the unwanted hollow diamond.
- Misread the user’s tag comment and temporarily represented the implementation as many-to-many instead of explaining only its conceptual grouping role.
- Briefly duplicated tags as both a `Participant` field and an association.
- Used the imprecise phrase “each `Command` handles execution” instead of “each `Command` implementation.”

# Any Other Interesting Information (if any)

N/A
