# Implementing Zones Command

# My Request(s)
## Request 1
Asked where valid timezone information should live and whether documentation, a command, or another approach would work best.

## Request 2
Requested implementation of the `zones` command without README changes.

## Request 3
Questioned why a missing search term used `Ui.showZonesUsage()` instead of being rejected as invalid syntax with a `QuorumException`.

## Request 4
Noted inconsistent use of "time zone" and "timezone" across the repository.

## Request 5
Pushed back on using "time zone" and stated that "timezone" was the preferred project terminology.

## Request 6
Requested a thorough audit for remaining inconsistent terminology.

# Your Response(s)
## Response 1
Recommended using Java's `ZoneId` database as the source of truth, a searchable `zones` command for discovery, help text for syntax, and documentation for longer explanations.

## Response 2
Implemented case-insensitive, sorted `zones SEARCH` results using `ZoneId.getAvailableZoneIds()`, no-match feedback, parser wiring, invalid-zone guidance, and command-list updates. Verified compilation, Checkstyle, and manual behavior.

## Response 3
Conceded that required arguments should follow the repository's parser-validation pattern. Moved empty-search validation into `Parser`, raised a `QuorumException`, and removed `Ui.showZonesUsage()`.

## Response 4
Initially standardized Java prose and user-facing messages on "time zone," while leaving identifiers and `/tz` unchanged.

## Response 5
Accepted the project's preferred terminology and standardized source messages and Javadocs on "timezone" and "timezones."

## Response 6
Audited all tracked source, documentation, configuration, and log files. Found and corrected four spaced or hyphenated leftovers in historical logs, then confirmed only the chosen one-word terminology and Java references such as `java.time.ZoneId` remained.

# What You Got Wrong (if any)

- Initially treated bare `zones` as a help shortcut even though the repository consistently reports missing required arguments through `Parser` and `QuorumException`.
- Chose "time zone" as the standard without first following the user's preference for "timezone"; this was later reversed.

# Any Other Interesting Information (if any)

- The command derives valid region IDs at runtime rather than maintaining a hardcoded list.
