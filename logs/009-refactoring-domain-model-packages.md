# Refactoring Domain Model Packages

# My Request(s)

## Request 1

Asked whether moving `Participant`, `Roster`, and `EditRequest` into a model subpackage would improve the repository's organization.

## Request 2

Accepted the recommended package split and asked for the refactor to be implemented.

# Your Response(s)

## Response 1

Recommended moving `Participant` and `Roster` to the singular `quorum.model` package. Explained that `EditRequest` is command-specific rather than a domain model, so it should live in `quorum.command` or eventually be removed.

## Response 2

Moved the three classes to their agreed packages, updated all affected imports, and verified the refactor with compilation, Checkstyle, and `git diff --check`.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

The refactor leaves the root `quorum` package focused on application coordination, parsing, UI, and the shared exception. The user accepted the proposed distinction between domain models and command-specific data without pushback.
