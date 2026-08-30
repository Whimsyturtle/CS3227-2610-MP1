# Testing Roster Persistence and Fixing Timezone Parsing

# My Request(s)
## Request 1
Requested systematic JUnit tests for only `TsvRosterCodec` and `FileRosterStorage`, using equivalence partitioning, boundary analysis, focused positive/negative cases, and limited input combinations. Required test-only changes and preservation of tests exposing production defects.

## Request 2
Asked to fix the parser defect exposed by the existing `/tz` delimiter test.

## Request 3
Questioned what `existingDirectory` meant in a file-storage test and why it should produce an I/O error.

## Request 4
Accepted the explanation and requested a clearer test name.

## Request 5
Questioned what `existingLongerFile` meant in the overwrite-and-truncation test.

## Request 6
Conceded that the truncation test name was understandable, but pushed back on ambiguous TSV field-boundary names such as `twoFieldsJustBelowRequiredCount`.

## Request 7
Accepted the proposed `fieldCount` terminology and requested the renames.

# Your Response(s)
## Response 1
Added 26 persistence tests covering TSV format boundaries, validation failures, BOM and line-ending handling, UTF-8 file I/O, missing and invalid paths, directory creation, overwriting, and exception wrapping. All new tests passed, while the full suite exposed the pre-existing `/tzZ` parser defect; Checkstyle passed.

## Response 2
Changed the shared `/tz` delimiter pattern so `/tz` must be a standalone whitespace-delimited token. Verified all 234 tests and both Checkstyle tasks passed.

## Response 3
Explained that the configured roster path deliberately points to a directory rather than a regular file, causing `Files.readString` to throw an `IOException` before decoding. Proposed a name describing that state directly.

## Response 4
Renamed the test to `load_pathPointsToDirectory_wrapsReadExceptionWithoutDecoding`.

## Response 5
Explained that the existing file contains more text than the replacement, so the test detects leftover bytes if saving fails to truncate it. Suggested a more explicit name, which was not adopted after the user understood the original.

## Response 6
Agreed that `twoFieldsJustBelow` could sound like "boundary minus two" and recommended naming the measured dimension as `fieldCount`.

## Response 7
Renamed the below- and above-boundary tests to use `fieldCountJustBelowRequiredCount` and `fieldCountJustAboveRequiredCount`.

# What You Got Wrong (if any)

- Initially used unclear names for the directory-path test and TSV field-count boundary tests; both were improved after user pushback.

# Any Other Interesting Information (if any)

N/A
