# Testing Tag Participant and Roster Models

# My Request(s)
## Request 1
Asked for systematic JUnit tests for only `Tag`, `Participant`, and `Roster`, using equivalence partitioning, boundary-value analysis, positive/negative cases, and selective input combinations. Required test-only changes initially and asked that defect-revealing failures be retained.

## Request 2
Asked why the tag test changes the default locale to Turkish.

## Request 3
Suggested adding a comment to clarify that Turkish-locale test.

## Request 4
Asked whether the tag test for embedded Unicode whitespace already covered the participant `U+2028` issue.

## Request 5
Asked how the participant Unicode line-separator defect should be fixed.

## Request 6
Accepted the proposed production fix and requested its implementation.

## Request 7
Questioned the plural wording and combined behavior in `editZone_indicesAtBothValidBoundaries_replaceOnlySelectedParticipants`.

## Request 8
Accepted the recommendation to split that test.

## Request 9
Pointed out the same issue in `untag_presentTagAtBothValidBoundaries_replacesOnlySelectedParticipants`.

# Your Response(s)
## Response 1
Derived behavior from documentation and usage before inspecting implementation, then added focused tests for validation, normalization, ordering, boundaries, immutability, collections, and temporal behavior. The suite compiled with one intentionally retained failure showing that `Participant` accepted `U+2028 LINE SEPARATOR` in names.

## Response 2
Explained that Turkish uppercasing can turn `i` into `İ`, so the test ensures tag normalization uses `Locale.ROOT` and remains consistent across systems.

## Response 3
Added a concise comment documenting the Turkish casing edge case and cross-system invariant.

## Response 4
Clarified that the cases are related but not redundant: tags reject all whitespace, while participant names allow spaces but must independently reject every line-break form.

## Response 5
Recommended validating participant names with a compiled `\t|\R` pattern so the model rejects the same line separators recognized by TSV decoding, without rejecting ordinary spaces.

## Response 6
Implemented the pattern-based validation in `Participant`; all tests and Checkstyle then passed.

## Response 7
Conceded that the plural name was technically accurate because the test called `editZone` twice, but ambiguous and less focused. Recommended separate lower- and upper-boundary tests.

## Response 8
Split the combined `editZone` test into two independent boundary tests and verified the suite.

## Response 9
Agreed that the `untag` test had the same problem, split it into two focused boundary tests, and retained final-tag-removal coverage in the dedicated temporal `hasTag` test. All 116 tests and Checkstyle passed.

# What You Got Wrong (if any)

The initial `editZone` and `untag` boundary tests combined two method calls, which made their plural names confusing and allowed one call to influence the state observed by the next. The user identified this, and both tests were split.

# Any Other Interesting Information (if any)

The tests exposed a real consistency issue: Java's `\R` treats `U+2028` as a record separator, but the original participant validation rejected only tab, carriage return, and newline. Aligning validation with `\R` prevents participant names from corrupting line-oriented TSV persistence.
