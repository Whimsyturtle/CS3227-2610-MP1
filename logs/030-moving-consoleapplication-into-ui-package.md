# Moving ConsoleApplication into UI Package

# My Request(s)
## Request 1
Asked whether `ConsoleApplication` should be under `quorum.ui` alongside `ConsoleUi`.

## Request 2
Accepted the recommendation and requested the refactor.

# Your Response(s)
## Response 1
Recommended moving `ConsoleApplication` into `quorum.ui` for symmetry with `ChatApplication`, while keeping `Launcher` in the root package.

## Response 2
Moved `ConsoleApplication`, made `run()` public for cross-package access, updated `Launcher`, and verified the full test and Checkstyle suite.

# What You Got Wrong (if any)

The first Checkstyle run found missing Javadoc on the newly public `run()` method; this was corrected before final verification.

# Any Other Interesting Information (if any)

N/A
