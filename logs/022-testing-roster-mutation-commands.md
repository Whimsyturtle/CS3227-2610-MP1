# Testing Roster Mutation Commands

# My Request(s)
## Request 1
Asked for systematic JUnit tests for only `AddCommand`, `DeleteCommand`, `EditCommand`, `TagCommand`, and `UntagCommand`, with request records covered indirectly. Required equivalence partitions, boundary values, positive and negative cases, selective input combinations, test-only edits, and retention of tests exposing production defects.

# Your Response(s)
## Response 1
Derived the command contracts from app behavior, comments, design logs, and existing test conventions before checking implementation branches. Added 29 focused tests covering empty and populated rosters, one-based index boundaries, exact mutations and UI calls, unchanged timezone edits, new and existing roster tags, duplicate tags, missing tags, shared tags, and last-use removal. The complete 145-test suite and Checkstyle passed without exposing a production defect, and no production files were changed.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

N/A
