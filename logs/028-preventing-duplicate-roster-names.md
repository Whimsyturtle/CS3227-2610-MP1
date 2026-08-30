# Preventing duplicate roster names

# My Request(s)
## Request 1
Asked whether duplicate participant names should be rejected by `Roster` and whether that failure should propagate to the user through the add-command flow.

## Request 2
Agreed that comparisons should be case-insensitive, but raised the edge case of `Alice Tan` versus `Alice  Tan`.

## Request 3
Pushed back on the proposed whitespace rule, asking why `Alice Tan` should remain different from `AliceTan`.

## Request 4
Accepted the distinction after considering names such as `Mary Ann` and `Maryann`, then requested case-insensitive duplicate detection, repeated-space handling, and relevant tests.

# Your Response(s)
## Response 1
Confirmed that uniqueness is a roster-level invariant. Recommended having `Roster.add()` throw `QuorumException`, allowing the existing command and UI error flow to report it, and checking duplicate names while loading saved TSV data.

## Response 2
Proposed trimming names, collapsing repeated ordinary spaces, comparing names case-insensitively, and preserving meaningful word boundaries.

## Response 3
Explained that removing all whitespace could create false duplicates such as `Mary Ann`/`Maryann` and `Ann A`/`Anna`. Clarified that names serve as unique roster labels rather than reliable real-world identities.

## Response 4
Implemented name normalization in `Participant`, case-insensitive uniqueness enforcement in `Roster`, error propagation through `AddCommand`, and line-aware duplicate detection during TSV loading. Added model, parser, command, and storage tests, including confirmation that `AliceTan` remains distinct from `Alice Tan`. All 240 tests and both Checkstyle tasks passed.

# What You Got Wrong (if any)

The initial normalization suggestion did not immediately explain why removing all whitespace would be unsafe, prompting valid pushback. The distinction was then refined to collapse repeated spaces without removing meaningful word boundaries.

# Any Other Interesting Information (if any)

N/A
