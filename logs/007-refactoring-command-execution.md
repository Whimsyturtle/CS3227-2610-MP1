# Refactoring Command Execution

# My Request(s)

## Request 1

Evaluate an ancestor type and its methods for a new command subpackage containing one class per supported command.

## Request 2

Evaluate whether `ByeCommand` should throw a `ByeException` instead of exposing exit state.

## Request 3

Use `CommandOutcome` and implement the command hierarchy refactor.

## Request 4

Remove redundant constructor Javadocs, make `CommandOutcome` open to future outcomes, and simplify the special `yield` used when parsing `edit`.

# Your Response(s)

## Response 1

Recommended a `Command` interface whose concrete implementations own execution, while `Parser` continues owning syntax and validation.

## Response 2

Advised against using exceptions for normal termination and recommended returning `CONTINUE` or `EXIT` from command execution.

## Response 3

Added command classes for add, delete, edit, list, bye, and unknown input; updated `Parser` and `Quorum`; and removed the old generic `Command` record and `CommandType` enum.

## Response 4

Removed the redundant Javadocs, generalized the outcome description, and changed `EditCommand` to accept the parsed `EditRequest` directly, eliminating `yield` from `Parser`.

# What You Got Wrong (if any)

Initially added low-value constructor Javadocs and unpacked `EditRequest` unnecessarily.

# Any Other Interesting Information (if any)

Using an outcome keeps expected control flow separate from `QuorumException`, which remains reserved for user-correctable errors.
