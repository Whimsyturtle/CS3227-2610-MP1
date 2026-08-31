package quorum.ui;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import quorum.model.MeetingAttendee;
import quorum.model.Participant;
import quorum.model.Tag;
import quorum.model.TimeSlot;
import quorum.model.WakefulnessResult;

/** Formats Quorum responses as text for display by a concrete user interface. */
public abstract class TextResponseView implements ResponseView {
    private static final int MAX_MEETING_SLOTS = 5;
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    @Override
    public void showWelcome() {
        show("QUORUM",
                "Finding the hours when everyone's awake.");
    }

    @Override
    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    @Override
    public void showAdded(Participant participant, int total) {
        show("Added: " + participant, "Now tracking " + total + ".");
    }

    @Override
    public void showDeleted(Participant participant, int total) {
        show("Deleted: " + participant, "Now tracking " + total + ".");
    }

    @Override
    public void showEdited(Participant participant) {
        show("Edited: " + participant);
    }

    @Override
    public void showTagged(Participant participant, Tag tag, boolean isNewTag) {
        List<String> lines = new ArrayList<>();
        lines.add("Tagged " + participant.getName() + " with " + tag + ".");
        if (isNewTag) {
            lines.add("Created new tag: " + tag + ".");
        }
        show(lines.toArray(new String[0]));
    }

    @Override
    public void showUntagged(Participant participant, Tag tag) {
        show("Removed tag " + tag + " from " + participant.getName() + ".");
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void showError(String message) {
        show(message);
    }

    /** Displays one response consisting of the given lines. */
    protected abstract void show(String... lines);

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
        return start.format(DATE_TIME_FORMAT) + "-" + end.format(DATE_TIME_FORMAT);
    }
}
