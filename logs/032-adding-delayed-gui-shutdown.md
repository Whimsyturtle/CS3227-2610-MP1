# Adding Delayed GUI Shutdown

# My Request(s)

## Request 1
Asked for GUI mode to close its window automatically about three seconds after the user enters `bye`.

# Your Response(s)

## Response 1
Updated `ChatApplication` to show the goodbye message, disable input, and use a JavaFX `PauseTransition` to close the window after three seconds. Console behavior was left unchanged, and the full test and Checkstyle suite passed.

# What You Got Wrong (if any)

N/A

# Any Other Interesting Information (if any)

N/A
