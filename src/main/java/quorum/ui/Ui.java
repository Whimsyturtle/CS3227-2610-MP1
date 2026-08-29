package quorum.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import quorum.model.MeetingAttendee;
import quorum.model.Participant;
import quorum.model.Tag;
import quorum.model.TimeSlot;
import quorum.model.WakefulnessResult;

/**
 * Owns every read from and write to the console. No other class touches
 * System.in or System.out.
 */
public class Ui {
    private static final String LINE = "-".repeat(50);
    private static final int MAX_MEETING_SLOTS = 5;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI connected to the process's standard input and output. */
    public Ui() {
        this(System.in, System.out);
    }

    /** Allows application tests to supply isolated input and output streams. */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command from the console. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the application welcome message. */
    public void showWelcome() {
        show("QUORUM",
                "Finding the hours when everyone's awake.");
    }

    /** Displays the application goodbye message. */
    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    /** Displays the added participant and resulting roster size. */
    public void showAdded(Participant participant, int total) {
        show("Added: " + participant, "Now tracking " + total + ".");
    }

    /** Displays the deleted participant and resulting roster size. */
    public void showDeleted(Participant participant, int total) {
        show("Deleted: " + participant, "Now tracking " + total + ".");
    }

    /** Displays the participant after an edit. */
    public void showEdited(Participant participant) {
        show("Edited: " + participant);
    }

    /** Displays a tag added to a participant and whether the tag is new. */
    public void showTagged(Participant participant, Tag tag, boolean isNewTag) {
        List<String> lines = new ArrayList<>();
        lines.add("Tagged " + participant.getName() + " with " + tag + ".");
        if (isNewTag) {
            lines.add("Created new tag: " + tag + ".");
        }
        show(lines.toArray(new String[0]));
    }

    /** Displays a tag removed from a participant. */
    public void showUntagged(Participant participant, Tag tag) {
        show("Removed tag " + tag + " from " + participant.getName() + ".");
    }

    /** Displays the tags currently in use. */
    public void showTags(List<Tag> tags) {
        if (tags.isEmpty()) {
            show("No tags in use yet.");
            return;
        }

        List<String> lines = new ArrayList<>();
        lines.add("Tags in use:");
        for (Tag tag : tags) {
            lines.add("  " + tag);
        }
        show(lines.toArray(new String[0]));
    }

    /** Displays the maximum-scoring meeting slots and their wakefulness details. */
    public void showMeeting(Tag tag, LocalDate date, ZoneId userZone,
                            List<WakefulnessResult> bestResults) {
        WakefulnessResult first = bestResults.getFirst();
        int awakeCount = first.awakeCount();
        int attendeeCount = first.attendeeCount();

        List<String> lines = new ArrayList<>();
        lines.add("Best meeting times for tag " + tag + " on " + date + ":");
        if (awakeCount < attendeeCount) {
            lines.add("No candidate slot keeps everyone awake.");
            lines.add("The best score is " + awakeCount + "/" + attendeeCount + " awake:");
        } else {
            lines.add("Everyone is awake for the full duration of these slots ("
                    + attendeeCount + "/" + attendeeCount + "):");
        }

        int displayedCount = Math.min(MAX_MEETING_SLOTS, bestResults.size());
        for (int i = 0; i < displayedCount; i++) {
            WakefulnessResult result = bestResults.get(i);
            lines.add("  " + (i + 1) + ". " + formatSlot(result.slot(), userZone)
                    + " - " + result.awakeCount() + "/"
                    + result.attendeeCount() + " awake");
            if (!result.notAwake().isEmpty()) {
                lines.add("     Not awake: " + result.notAwake().stream()
                        .map(attendee -> formatAttendee(attendee, result.slot()))
                        .collect(Collectors.joining(", ")));
            }
        }

        int hiddenCount = bestResults.size() - displayedCount;
        if (hiddenCount > 0) {
            String noun = hiddenCount == 1 ? "slot" : "slots";
            lines.add(hiddenCount + " more equally optimal " + noun + ".");
        }
        show(lines.toArray(new String[0]));
    }

    /** Displays the participants currently in the roster. */
    public void showRoster(List<Participant> participants) {
        if (participants.isEmpty()) {
            show("Nobody here yet.");
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add("Who's in the roster:");
        for (int i = 0; i < participants.size(); i++) {
            lines.add("  " + (i + 1) + ". " + participants.get(i));
        }
        show(lines.toArray(new String[0]));
    }

    /** Displays the timezones matching the given search term. */
    public void showZones(String searchTerm, List<String> zones) {
        if (zones.isEmpty()) {
            show("No timezones found for \"" + searchTerm + "\".",
                    "Try searching for a city or region, such as: zones Asia");
            return;
        }

        List<String> lines = new ArrayList<>();
        lines.add("Timezones matching \"" + searchTerm + "\":");
        for (String zone : zones) {
            lines.add("  " + zone);
        }
        show(lines.toArray(new String[0]));
    }

    /** Displays the given error message. */
    public void showError(String message) {
        show(message);
    }

    private void show(String... lines) {
        output.println(LINE);
        for (String line : lines) {
            output.println(" " + line);
        }
        output.println(LINE);
    }

    private String formatSlot(TimeSlot slot, ZoneId zone) {
        return formatInterval(slot, zone) + " " + zone.getId();
    }

    private String formatAttendee(MeetingAttendee attendee, TimeSlot slot) {
        return attendee.name() + " (" + formatInterval(slot, attendee.zone())
                + " " + attendee.zone().getId() + ")";
    }

    private String formatInterval(TimeSlot slot, ZoneId zone) {
        ZonedDateTime start = slot.start().atZone(zone);
        ZonedDateTime end = slot.end().atZone(zone);
        if (start.toLocalDate().equals(end.toLocalDate())) {
            return start.format(TIME_FORMAT) + "-" + end.format(TIME_FORMAT);
        }
        return start.format(DATE_TIME_FORMAT) + "-" + end.format(DATE_TIME_FORMAT);
    }
}
