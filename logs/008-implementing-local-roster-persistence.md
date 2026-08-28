# Implementing Local Roster Persistence

# My Request(s)

## Request 1

Asked for several ways to persist the roster locally, considering file location, schema, manual editing, missing or corrupted files, and future testability. Accepted a simple save-after-each-command strategy for now.

## Request 2

Leant toward CSV while proposing `data/roster.json` relative to the working directory. Argued that SQL was excessive and JSON might require another dependency, and asked whether `Participant.toString()` could provide serialization.

## Request 3

Conceded that commas in names make naive CSV troublesome and switched to TSV, accepting rejection of names containing tabs. Pushed back on dismissing `toString()` and proposed a second string representation analogous to Python's `str()` and `repr()`.

## Request 4

Conceded that persistence through `toString()` would create undesirable coupling. Proposed separate codec and storage classes so encoding/decoding and file I/O had distinct responsibilities.

## Request 5

Accepted the proposed design and asked for it to be implemented.

## Request 6

Raised three implementation nits: split the growing `Quorum.run()` method, explain why `Ui` was modified, and replace the many storage exception classes with one exception similar to `QuorumException`.

## Request 7

Reconsidered the API and pushed for general `Storage` and `Codec` interfaces, anticipating that another data model might need persistence later.

## Request 8

Objected to vague or invented Javadoc terminology: "roster document," "complete roster," and "Quorum tab-separated format." Requested direct descriptions without implying special concepts or a proprietary TSV format.

## Request 9

Asked what `BYTE_ORDER_MARK` meant and why it had been added.

## Request 10

Remained skeptical about the unfamiliar BOM explanation and requested an authoritative source.

## Request 11

Pushed back on validating participants again inside `encode()`, reasoning that bad commands and manually corrupted files were already checked. Suggested an assertion instead of runtime validation.

## Request 12

Conceded that participant and roster invariants belonged at the model level.

## Request 13

Objected that model validation now duplicated `Parser.parseParticipant()`. Requested one owner for each check rather than parallel validation logic.

## Request 14

Pushed back on translating or juggling exception types and asked why model validation could not throw `QuorumException` directly.

## Request 15

Challenged the temporary-file and replacement logic in `FileRosterStorage.save()` as overkill. Asked whether Java already provided a method instead of recreating the mechanism.

## Request 16

Explicitly remained unconvinced and asked for stronger verification of the claim that Java lacked a one-call atomic text-write method.

## Request 17

Accepted the tradeoff and requested the simpler direct-write implementation, with a TODO for improving crash safety later.

# Your Response(s)

## Response 1

Compared local persistence approaches and discussed how format, path policy, corruption behavior, and dependency injection would affect usability and future tests.

## Response 2

Flagged the CSV/`.json` mismatch, delimiter escaping, and the instability of using display-oriented `toString()` as a storage contract. Also highlighted implications of a working-directory-relative path.

## Response 3

Agreed that TSV plus rejecting tabs and line breaks was simple. Explained that Java has no separate Python-style `repr()` protocol and recommended a clearly named codec operation instead of another object string representation.

## Response 4

Endorsed the codec/storage split: the codec transforms rosters to and from text, while storage owns paths and I/O. This also permits isolated tests and future storage implementations.

## Response 5

Implemented TSV persistence at `data/roster.tsv`, startup loading, saving after commands, malformed/missing-file handling, and documentation.

## Response 6

Split roster loading from the command loop, explained that `Ui` stream injection supported isolated application tests, and consolidated persistence failures under `QuorumException`.

## Response 7

Recommended retaining roster-specific interfaces until a real second persisted model established useful shared semantics; generic names alone would not guarantee a reusable abstraction.

## Response 8

Revised the Javadocs to say simply "roster" and "tab-separated values," removing "document," "complete," and "Quorum format."

## Response 9

Explained that the byte-order mark check tolerates an optional leading Unicode `U+FEFF`, which some text editors may add, and that Quorum does not write one itself.

## Response 10

Checked authoritative Unicode documentation and provided a source for the BOM behavior instead of relying on an unsupported explanation.

## Response 11

Agreed that codec-side participant validation was misplaced. Explained that assertions may be disabled and should not enforce required data invariants, then proposed enforcing validity when model objects are created.

## Response 12

Moved participant invariants into `Participant`, including nonblank names, valid TSV characters, and a non-null timezone.

## Response 13

Removed the parser's duplicate name-validity check. The parser now owns command syntax and conversion, while `Participant` owns participant invariants.

## Response 14

Changed model validation to throw `QuorumException` directly, avoiding conversion through another validation exception while retaining contextual line information during file decoding.

## Response 15

Explained that safe replacement normally composes a temporary write with `Files.move(..., ATOMIC_MOVE)` and that `Files.writeString()` alone can truncate a destination before a failed write.

## Response 16

Verified the claim against the official Java 25 `Files` API and OpenJDK 25 implementation: no one-call atomic text writer exists, and `writeString()` delegates to an ordinary write.

## Response 17

Simplified `save()` to create the parent directory and call `Files.writeString()` directly. Removed the temporary-file helpers and added a TODO documenting the partial-write risk.

# What You Got Wrong (if any)

I initially overengineered the implementation with extra exception types and atomic-replacement machinery, modified `Ui` without first explaining why, used unclear Javadoc terminology, introduced BOM handling without context, and temporarily duplicated validation. I also incorrectly collapsed the first conversation log into six broad requests instead of recording all 17 turns.

# Any Other Interesting Information (if any)

N/A
