# Creating and Refining Quorum User Guide

# My Request(s)

## Request 1
Proposed a User Guide with a Quick Start, a short command demo, a table of contents, per-command syntax and validation guidance with useful callouts and error paths, and a glossary. Asked whether this structure worked and what other sections might help.

## Request 2
Asked for `docs/UserGuide.md` to be written concisely, with repository terminology and careful verification after each section.

## Request 3
Objected that the introduction and recommendation section incorrectly defined Quorum around its current wakefulness behavior. Requested the README slogan verbatim and future-proof wording.

## Request 4
Asked to remove the entire wakefulness-scoring explanation section and its table-of-contents link.

## Request 5
Questioned whether saying Quorum "requires Java 25" incorrectly implied that newer Java versions would not work.

## Request 6
Flagged Quick Start issues involving read permission, terminal wording, the **Send** button, and unnecessary GUI-specific `bye` behavior.

## Request 7
Requested improvements to the Five-Minute Tour: assume an empty roster, discover every timezone used, say "meeting times," improve awkward ranking language, and add an excluded participant.

## Request 8
Asked for the tour introduction to explain the `meeting` command generally and for a note explaining how the user's timezone is represented.

## Request 9
Flagged "the first timezone" as vague and requested a direct reference to the example `meeting` command.

## Request 10
Noted inconsistent use of "timezone" and "timezone ID," preferring "timezone" throughout.

## Request 11
Strongly objected that the command summary again treated wakefulness ranking as the general purpose of `meeting`, despite planned future modes.

## Request 12
Suggested removing examples for commands that take no arguments.

## Request 13
Clarified that tags are normalized to uppercase, not merely displayed in uppercase.

## Request 14
Requested more natural wording to explain that `bye` does not remove roster data.

## Request 15
Asked to remove the separate testing section because the Five-Minute Tour already serves as a practical test.

## Request 16
Asked to shorten troubleshooting by removing two rows and to improve the timezone-recovery instruction.

# Your Response(s)

## Response 1
Agreed with the proposed structure and recommended adding a short introduction, command summary, an explanation of the current meeting behavior, data storage, manual testing, troubleshooting, and known limitations. Also suggested using a consistent template for each command and keeping the Quick Start separate from the guided tour.

## Response 2
Created and verified `docs/UserGuide.md` against the implementation, packaged CLI, and all 240 automated tests.

## Response 3
Conceded the overgeneralization, copied the README slogan exactly, and scoped wakefulness language to current behavior.

## Response 4
Removed the requested section and its table-of-contents entry.

## Response 5
Agreed that "requires" was too strong and changed the guide to say Quorum is built for Java 25, with Java 25 or newer accepted.

## Response 6
Changed the folder requirement to read/write access, improved terminal phrasing, changed "select" to "click," and removed the GUI-specific `bye` detail.

## Response 7
Reworked the tour around an empty roster, added timezone searches, added untagged Carol in Honolulu, and clarified that Carol's timezone does not affect the tagged meeting search.

## Response 8
Generalized the tour introduction and added a note explaining that `Asia/Singapore` represents the user and controls displayed times.

## Response 9
Reworded the note to refer directly to `Asia/Singapore` in the example command.

## Response 10
Standardized the entire guide on "timezone" and updated the glossary and troubleshooting text.

## Response 11
Apologized for repeating the assumption and changed the summary purpose to "Find meeting times," keeping wakefulness explicitly current only in detailed behavior.

## Response 12
Removed redundant examples for `list`, `tags`, and `bye`.

## Response 13
Changed the note to say tags are case-insensitive and normalized to uppercase.

## Response 14
Changed the description to say the roster remains available the next time Quorum starts.

## Response 15
Removed the testing section and its table-of-contents entry.

## Response 16
Removed the unsupported-version and missing-tag troubleshooting rows, then clarified how to reuse a timezone returned by `zones`.

# What You Got Wrong (if any)

I repeatedly generalized the current wakefulness ranking into the overall purpose of Quorum and `meeting`, despite the planned addition of other modes. I also introduced several avoidable documentation issues: overly strong Java wording, inconsistent timezone terminology, vague or awkward phrasing, redundant sections and examples, and an inaccurate description of tag normalization. The user pushed back on these points, and I conceded and corrected each one.

# Any Other Interesting Information (if any)

The guide was checked against the parser, model, UI, storage implementation, packaged JAR, and CLI workflow. All 240 automated tests passed. The discussion emphasized keeping documentation concise, precise, consistent with repository terminology, and flexible enough for planned features.
