# Designing Parser Unit Tests

# My Request(s)
## Request 1
Requested systematic JUnit tests for all non-meeting `Parser` routes and helpers, using equivalence partitioning, boundary analysis, focused positive/negative cases, and limited combinations. Required test-only changes and preservation of tests exposing production defects.

## Request 2
Questioned the inconsistent organization of a general `ParserTest` alongside the existing `ParserMeetingTest`.

## Request 3
Asked to merge all parser tests into one file.

## Request 4
Reconsidered the merge, proposed multiple files, and questioned whether command-specific whitespace handling was sufficiently tested.

## Request 5
Agreed to split by parser responsibility, retain the test codifying surplus `tags` arguments, and add `/tz` delimiter boundary coverage.

## Request 6
Pushed back on ambiguous index-boundary names such as `twoJustAboveLowerBoundary`.

## Request 7
Pointed out that similar naming problems remained in `ParserZoneTest`.

## Request 8
Questioned names that treated strings themselves as numeric boundaries instead of explicitly referring to their lengths.

## Request 9
Questioned the artificial boundary terminology used for malformed `/tz` delimiter cases.

# Your Response(s)
## Response 1
Added 50 non-meeting parser tests in `ParserTest`; the complete 206-test suite and Checkstyle passed with no production changes.

## Response 2
Conceded that `ParserTest` plus `ParserMeetingTest` was awkward, recommending either a single combined file or an explicitly named non-meeting file.

## Response 3
Merged all 73 parser tests into `ParserTest`, removed `ParserMeetingTest`, and verified all tests and Checkstyle passed.

## Response 4
Explained that whitespace parsing is shared and already covered through top-level parsing and helpers, so repeating it for every command would be redundant. Recommended splitting by parser responsibility and identified `/tz` token handling as a worthwhile gap.

## Response 5
Split the suite into routing, participant, edit, index, tag-request, meeting, and zone test files. Added `/tz` syntax cases, exposing that `Alice /tzZ` is incorrectly accepted because `/tz` is matched as a substring. Kept the failing test and left production code unchanged.

## Response 6
Renamed all index boundary tests to use clear `justBelow`, `at`, and `justAbove` terminology.

## Response 7
Conceded the naming audit was incomplete and updated analogous participant, tag-request, and zone-search names.

## Response 8
Renamed string boundary cases around the measured properties: `nameLength`, `tagLength`, and `termLength`.

## Response 9
Reclassified the delimiter cases as syntax partitions, renaming them to describe an incomplete delimiter and missing whitespace after the delimiter. Test compilation and Checkstyle passed.

# What You Got Wrong (if any)

- Initially created an arbitrarily split `ParserTest` beside `ParserMeetingTest`, then merged everything before settling on the clearer responsibility-based split.
- Used awkward numeric-boundary wording for concrete string and index samples.
- Corrected only index names at first and needed further user pushback to audit the analogous parser tests.
- Initially described malformed delimiter syntax using artificial below/exact/above boundary terminology instead of observable syntax conditions.

# Any Other Interesting Information (if any)

- Shared whitespace and case-normalization behavior is sampled once rather than repeated for every command route.
