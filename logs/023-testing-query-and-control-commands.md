# Testing Query and Control Commands

# My Request(s)
## Request 1
Asked for systematic JUnit tests for only `ListCommand`, `TagsCommand`, `ZonesCommand`, `UnknownCommand`, and `ByeCommand`, with `Command` and `CommandOutcome` covered indirectly. Required equivalence partitions, boundary values, positive and negative cases, selective input combinations, behavior-first derivation, test-only edits, and retention of tests exposing production defects.

# Your Response(s)
## Response 1
Derived the command contracts from observable behavior, comments, design logs, and existing test conventions before checking implementation branches. Added 11 focused tests covering empty, single, and multiple roster/tag states; ordering and deduplication; zero, one, and multiple timezone matches; trimming, substring and case-insensitive search; Turkish-locale independence; exact UI interactions; state preservation; and `CONTINUE`/`EXIT` outcomes. The complete 156-test suite and Checkstyle passed without exposing a production defect, and no production files were changed.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

N/A
