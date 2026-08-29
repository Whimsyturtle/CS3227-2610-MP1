# Implementing Tags Command

# My Request(s)
## Request 1
Asked to read Log 12 and implement only the agreed `tags` command, following the final design and the existing `tag` and `untag` implementation. Invited clarification if the design and current code conflicted.

# Your Response(s)
## Response 1
Implemented `tags` as a discovery command that lists distinct tags currently in use in alphabetical order and shows a clear empty-state message. While integrating it, noticed that the previous `tag` and `untag` implementation had not updated `UnknownCommand`, leaving both commands out of its suggestions; corrected the list to include `tag`, `untag`, and `tags`. Also added parser routing, roster tag aggregation, UI output, and focused tests. Kept meeting scheduling and other unagreed functionality out of scope, then verified that both tests and Checkstyle passed.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

N/A
