# Adding Delete Command

# My Request(s)

## Request 1
Add a `delete INDEX` command that removes the participant at the corresponding one-based `list` index.

## Request 2
Make index parsing reusable for future index-based commands instead of tying it to participant deletion.

## Request 3
Remove all remaining assumptions that an index refers to participants or a particular list.

# Your Response(s)

## Response 1
Implemented the delete command across command parsing, roster removal, and UI feedback. Added validation for missing, invalid, non-positive, and out-of-range indexes.

## Response 2
Renamed `parseParticipantIndex` to `parseIndex` and changed most validation messages to be command-agnostic.

## Response 3
Removed the remaining participant-specific validation message.

# What You Got Wrong (if any)

The initial parser method mixed generic index parsing with delete- and participant-specific error messages. The first refactor also missed one remaining `Participant index` message, which was corrected after further feedback.

# Any Other Interesting Information (if any)

N/A
