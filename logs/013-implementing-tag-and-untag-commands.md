# Implementing Tag and Untag Commands

# My Request(s)
## Request 1
Asked to read Log 12 and implement only the agreed `tag` and `untag` commands, excluding tag discovery and meeting scheduling.

## Request 2
Questioned why tag normalization lived on `Participant` and proposed a dedicated `Tag` value class for future extensibility.

## Request 3
Asked whether `Tag` really needed custom `equals()` and `hashCode()` implementations.

## Request 4
Pointed out duplicated message construction and control flow in `Ui.showTagged()`, suggesting one final `show(...)` call.

## Request 5
Questioned why `Participant.withTag()` and `withoutTag()` accessed the tag set directly instead of calling `hasTag()`.

# Your Response(s)
## Response 1
Implemented `tag <INDEX> <TAG>` and `untag <INDEX> <TAG>` with uppercase normalization, whitespace/comma rejection, per-participant uniqueness, first-use feedback, explicit duplicate/missing-tag errors, and sorted comma-separated tags in a new three-column TSV format.

## Response 2
Agreed that normalization did not belong on `Participant`. Introduced an immutable `Tag` value object owning validation, normalization, ordering, equality, rendering, and serialization value, then changed parsing, commands, roster operations, UI, and persistence to use `Tag` end-to-end.

## Response 3
Recommended retaining `equals()` and `hashCode()` because normalized tags are value objects, `TagRequest` equality depends on value equality, hash collections require them, and equality should remain consistent with `compareTo()`.

## Response 4
Conceded the duplication and refactored `showTagged()` to build a list of output lines and call `show(...)` once. Verification also exposed and corrected a regression where new-tag detection occurred after roster mutation.

## Response 5
Agreed there was no substantive reason for direct set access and updated both methods to use `hasTag()`, centralizing membership checks.

# What You Got Wrong (if any)

The first implementation placed tag normalization on `Participant`, which the user correctly identified as misplaced responsibility. `showTagged()` also duplicated its common message and used an unnecessary early return. During refinement, new-tag detection briefly occurred after mutation, which made the creation message disappear.

# Any Other Interesting Information (if any)

The final design treats tags as immutable normalized value objects while keeping participants immutable during edits.
