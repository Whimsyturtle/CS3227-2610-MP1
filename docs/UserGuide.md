# Quorum User Guide

App that helps you find the hours when everyone's awake across timezones for scheduling meetings.

## Table of Contents

- [Quick Start](#quick-start)
- [Five-Minute Tour](#five-minute-tour)
- [Command Summary](#command-summary)
- [Command Reference](#command-reference)
- [Data Storage](#data-storage)
- [Troubleshooting](#troubleshooting)
- [Known Limitations](#known-limitations)
- [Glossary](#glossary)

## Quick Start

### Prerequisites

Quorum runs on Windows, macOS, and Linux. It is built for Java 25.

Check your Java version in a terminal:

```text
java -version
```

Install Java 25 if the command fails or reports a version older than 25.

### Starting Quorum

1. Place `quorum.jar` in a folder where you have read and write access.
2. In a terminal, navigate to that folder.
3. Start the graphical interface:

   ```text
   java -jar quorum.jar
   ```

4. Type a command in the input box, then press <kbd>Enter</kbd> or click **Send**.

To use the command-line interface instead, start Quorum with:

```text
java -jar quorum.jar --cli
```

Enter `bye` to end the session.

## Five-Minute Tour

This tour assumes an empty roster. It adds three participants and shows how the `meeting` command works.

1. Search for timezones matching each city:

   ```text
   zones Singapore
   zones London
   zones Honolulu
   ```

2. Add three participants:

   ```text
   add Alice /tz Asia/Singapore
   add Bob /tz Europe/London
   add Carol /tz Pacific/Honolulu
   ```

3. Display the roster and note each participant's index:

   ```text
   list
   ```

4. Tag Alice and Bob with `FRIENDS`. Leave Carol untagged:

   ```text
   tag 1 FRIENDS
   tag 2 FRIENDS
   ```

5. Find one-hour meeting times on 30 August 2026, displayed in Singapore time:

   ```text
   meeting Asia/Singapore FRIENDS /on 2026-08-30 /for 1h
   ```

> [!NOTE]
> In the `meeting` command above, `Asia/Singapore` is your timezone. Quorum includes you when ranking the meeting times and displays the results in this timezone.

The `meeting` command considers only you, Alice, and Bob when ranking meeting times by wakefulness. Carol is excluded because she does not have the `FRIENDS` tag, so her timezone does not affect the results.

## Command Summary

| Command | Format | Purpose |
| --- | --- | --- |
| `zones` | `zones SEARCH_TERM` | Find valid timezones. |
| `add` | `add NAME /tz TIMEZONE` | Add a participant to the roster. |
| `list` | `list` | Display all participants and their indices. |
| `edit` | `edit INDEX /tz TIMEZONE` | Change a participant's timezone. |
| `delete` | `delete INDEX` | Delete a participant. |
| `tag` | `tag INDEX TAG` | Add a tag to a participant. |
| `untag` | `untag INDEX TAG` | Remove a tag from a participant. |
| `tags` | `tags` | Display all tags in use. |
| `meeting` | `meeting TIMEZONE TAG /on DATE /for DURATION` | Find meeting times. |
| `bye` | `bye` | End the session. |

## Command Reference

Words in `UPPERCASE` are values that you must replace. For example, replace `INDEX` with `1`.

An index is the number shown beside a participant by `list`. Indices start at 1 and may change after a participant is deleted.

> [!TIP]
> Run `list` before using `edit`, `delete`, `tag`, or `untag` to confirm the index.

### Finding timezones: `zones`

Finds timezones containing the search term. The search is not case-sensitive.

**Format:** `zones SEARCH_TERM`

**Example:** `zones Singapore`

- `SEARCH_TERM` must not be blank.
- Use a timezone returned by this command with `add`, `edit`, or `meeting`.

### Adding a participant: `add`

Adds a participant and their timezone to the roster.

**Format:** `add NAME /tz TIMEZONE`

**Example:** `add Alice Tan /tz Asia/Singapore`

- `NAME` must not be blank. It may contain spaces.
- Participant names must be unique, ignoring letter case and repeated spaces.
- `TIMEZONE` must be valid. It is case-sensitive.

> [!TIP]
> Use `zones` to find a valid timezone.

### Listing participants: `list`

Displays every participant in roster order, together with their current index and timezone.

**Format:** `list`

### Editing a participant: `edit`

Changes a participant's timezone. Their name and tags are unchanged.

**Format:** `edit INDEX /tz TIMEZONE`

**Example:** `edit 2 /tz America/New_York`

- `INDEX` must be the index of an existing participant.
- `TIMEZONE` must be valid. It is case-sensitive.

### Deleting a participant: `delete`

Deletes a participant from the roster.

**Format:** `delete INDEX`

**Example:** `delete 2`

- `INDEX` must be the index of an existing participant.

> [!WARNING]
> Deletion cannot be undone. The indices of later participants will change.

### Adding a tag: `tag`

Adds a tag to a participant. Tags select participants for the current `meeting` command.

**Format:** `tag INDEX TAG`

**Example:** `tag 1 FRIENDS`

- `INDEX` must be the index of an existing participant.
- `TAG` must not be blank or contain whitespace or commas.
- A participant cannot have the same tag more than once.

> [!NOTE]
> Tags are case-insensitive and normalized to uppercase.

### Removing a tag: `untag`

Removes a tag from a participant.

**Format:** `untag INDEX TAG`

**Example:** `untag 1 FRIENDS`

- `INDEX` must be the index of an existing participant.
- The participant must already have `TAG`.
- `TAG` must not be blank or contain whitespace or commas.

### Listing tags: `tags`

Displays the distinct tags in use, in alphabetical order.

**Format:** `tags`

### Finding meeting times: `meeting`

Finds meeting times for you and all participants with the requested tag. Currently, it ranks meeting times by how many are awake.

**Format:** `meeting TIMEZONE TAG /on DATE /for DURATION`

**Example:** `meeting Asia/Singapore FRIENDS /on 2026-08-30 /for 1h30m`

- `TIMEZONE` is your timezone and controls how results are displayed. It must be valid. It is case-sensitive.
- At least one participant must have `TAG`.
- `DATE` must use `YYYY-MM-DD`, such as `2026-08-30`.
- `DURATION` must be longer than zero and no longer than 24 hours. Use hours (`h`), minutes (`m`), or both: `1h`, `30m`, or `1h30m`.
- Arguments must appear in the order shown above.

### Ending the session: `bye`

Ends the current session. Your roster will still be available the next time you start Quorum.

**Format:** `bye`

## Data Storage

Quorum saves the roster automatically in `data/roster.tsv`. This path is relative to the folder from which Quorum was started.

Keep `roster.tsv` to preserve your participants and tags. Avoid editing it while Quorum is running.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| Quorum does not recognise a timezone. | Run `zones SEARCH_TERM`, then copy a returned timezone exactly into your command. |
| Quorum reports that no participant exists at an index. | Run `list` and use the participant's current index. |
| Quorum reports an invalid roster file during startup. | Restore a valid `data/roster.tsv`, or move the file elsewhere to start with an empty roster. |

## Known Limitations

- The current `meeting` command uses fixed awake hours of 08:00 to 22:00 local time.
- The current `meeting` command checks start times at 30-minute intervals.
- The current `meeting` command accepts one tag and displays at most five highest-scoring times.
- `edit` changes only a participant's timezone.

## Glossary

| Term | Meaning |
| --- | --- |
| Participant | A person in the roster, identified by a name and timezone. |
| Roster | The saved, ordered list of participants. |
| Index | A participant's current number in `list`, starting from 1. |
| Timezone | The local time rules for a location, such as `Asia/Singapore`. |
| Tag | A case-insensitive label used to group participants for meeting searches. |
| Your timezone | The `TIMEZONE` used in the current `meeting` command. It represents you and controls the requested date and displayed times. |
| Awake | Under the current wakefulness scoring, the entire meeting falls within 08:00 to 22:00 in the person's local timezone. |
| Best score | Under the current wakefulness scoring, the greatest number of included people who are awake. You are part of this count. |
