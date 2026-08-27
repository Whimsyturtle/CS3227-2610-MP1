# Implementing Edit Command

# My Request(s)

## Request 1

Implement an `edit` command that updates a participant's time zone using their list index.

## Request 2

Refactor time-zone parsing into a reusable method similar to index parsing.

## Request 3

Evaluate whether `Participant` should provide an immutable copy method for editing.

## Request 4

Implement the proposed immutable participant copy method.

# Your Response(s)

## Response 1

Implemented `edit INDEX /tz TIMEZONE` across command parsing, roster updates, UI feedback, and validation.

## Response 2

Added reusable `parseZone(String)` logic and made both add and edit parsing delegate to it.

## Response 3

Recommended a focused `withZone` method instead of a generic `edit` method or coupling `Participant` to `EditRequest`.

## Response 4

Added `Participant.withZone`, updated `Roster` to use it, and tested that editing returns a new copy without changing the original.

# What You Got Wrong (if any)

The initial implementation duplicated time-zone validation and made `Roster` construct replacement participants directly. Both issues were corrected after feedback.

# Any Other Interesting Information (if any)

The final implementation preserves participant immutability and separation of concerns.
