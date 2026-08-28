# Designing Tags and Meeting Time Ranking

# My Request(s)
## Request 1
Proposed `tag <INDEX> <STRING_TAG>` and an inverse `untag` command. Each participant could have zero or more non-duplicate tags, representing reusable groups such as university courses or clubs. Asked whether tags were the right abstraction or whether a better grouping model existed. Also proposed `meeting <MY_TIMEZONE> <TAG>` to find the best meeting time for the user and all participants carrying that tag, and asked how "best" should be measured and whether multiple algorithms or subcommands might eventually be useful.

## Request 2
Chose lightweight tags over first-class groups for the prototype. Accepted adding a `tags` discovery command and special feedback when a tag is created for the first time. Rejected backward-compatible TSV migration because the format could simply be replaced at this stage, and questioned imposing stricter tag rules than the participant-name rules. Rejected `/host <INDEX>` because requiring the user to create their own roster entry felt unnatural. Added `/on` and `/for` as necessary meeting parameters and favored one simple initial algorithm: assume shared awake hours and maximize how many people are awake for a candidate slot.

## Request 3
Refined tag validation by rejecting commas as well as whitespace, allowing all tags to fit in one comma-separated TSV field. Preferred normalizing user input to uppercase so spelling case could not create confusing duplicate tags. Suggested avoiding a convenience-based tie-breaker by listing the top five or ten meeting slots instead, while asking whether that output would become clunky. Confirmed that Quorum should explicitly report when no candidate slot keeps everyone awake.

# Your Response(s)
## Response 1
Agreed that lightweight many-to-many tags fit the current participant model and advised delaying first-class groups until groups require metadata such as default duration, quorum, or recurring-meeting settings. Recommended exact normalized tag matching, `tag`, `untag`, and `tags`, plus a special message for newly created tags to expose typos. Initially proposed restricted slug-like tags, backward-compatible TSV migration, and an immutable tag set on each participant. For meetings, pointed out that timezone data estimates wakefulness rather than calendar availability, that a date and duration are required for DST-correct results, and that the user's timezone needed clearer attendee-versus-display semantics. Compared strict overlap, total inconvenience, minimax fairness, quorum, and rotation, initially recommending all-awake filtering followed by minimax fairness.

## Response 2
Accepted the reduced scope and agreed that tags could allow any nonempty, whitespace-free token, while still recommending case normalization to avoid accidental duplicates. Suggested a fresh variable-width TSV representation because commas were still valid at this point. Withdrew `/host` and defined `MY_TIMEZONE` as both an anonymous additional attendee and the result-display timezone, so the user would not need a roster entry. Proposed `meeting <MY_TIMEZONE> <TAG> /on <DATE> /for <DURATION>`, shared local awake hours such as `08:00-22:00`, and date-based candidates evaluated through timezone-aware instants. Defined the sole score as the number of people whose entire meeting interval falls within awake hours, recommended reporting all equally optimal choices rather than inventing a convenience preference, and distinguished no matching participants from no slot where everyone is awake.

## Response 3
Agreed to normalize tags immediately with `toUpperCase(Locale.ROOT)` and reject empty tags, whitespace, and commas. This enabled a three-column TSV format whose final field contains alphabetically sorted, comma-separated tags. Recommended displaying at most five slots because ten would be verbose, and clarified that limiting output does not mathematically remove ties: equally optimal candidates should be ordered chronologically in the user's timezone and accompanied by an "N more equally optimal slots" message. Recommended showing only maximum-scoring slots, not filling the list with worse alternatives, and including each slot's awake count plus the names and local times of anyone not awake. Finalized a small MVP using fixed awake hours, 30-minute candidate increments, full-duration wakefulness checks, explicit universal-attendance failure output, and no fairness tie-breaker.

# What You Got Wrong (if any)

The initial response over-scoped the prototype by suggesting restrictive tag syntax, backward-compatible TSV migration, a `/host` participant, and a more complex fairness metric. The user pushed back on those choices, and the later responses conceded and simplified the design. The initial wording also risked equating being awake with being able to attend; this was later corrected.

# Any Other Interesting Information (if any)

The emerging MVP uses `tag`, `untag`, and `tags`, plus `meeting <MY_TIMEZONE> <TAG> /on <DATE> /for <DURATION>`. Tags are uppercase, unique per participant, reusable across participants, and cannot contain whitespace or commas. Meeting candidates use shared awake hours, count the user plus tagged participants, maximize the number awake for the full duration, and display up to five equally optimal slots with an explicit warning when universal attendance is impossible.
