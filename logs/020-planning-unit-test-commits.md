# Planning Unit Test Commits

# My Request(s)
## Request 1
Asked whether adding unit tests across multiple commits made sense and how to group the many currently untested classes without making each commit too small or too large.

# Your Response(s)
## Response 1
Confirmed that cohesive behavioral groups were preferable to arbitrary class counts and proposed seven commits:

1. Roster model: `Tag`, `Participant`, and `Roster`.
2. Mutation commands: `AddCommand`, `DeleteCommand`, `EditCommand`, `TagCommand`, and `UntagCommand`, with `EditRequest` and `TagRequest` covered indirectly.
3. Query and control commands: `ListCommand`, `TagsCommand`, `ZonesCommand`, `UnknownCommand`, and `ByeCommand`, with `Command` and `CommandOutcome` covered indirectly.
4. Non-meeting parsing: the remaining command routes and parsing helpers in `Parser`, complementing the existing `ParserMeetingTest`.
5. Persistence: `TsvRosterCodec` and `FileRosterStorage`, with `RosterCodec` and `RosterStorage` covered through their implementations.
6. Console interaction and formatting: `Ui`.
7. Application command-loop orchestration: `Quorum`, with `QuorumException` exercised indirectly.

Also advised against creating low-value direct tests for trivial interfaces, records, enums, and exception wrappers solely to increase class coverage.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

- Recommended keeping every commit independently passing and avoiding unrelated production refactors in test-only commits.
- Noted that `Parser`, `Ui`, and `Quorum` justify focused commits despite each being a single production class because they have substantial and distinct responsibilities.
