package quorum.model;

import java.time.ZoneId;
import java.util.Objects;

/**
 * A person included in meeting-time scoring. Represents either a roster
 * participant or the anonymous user who supplied the meeting timezone.
 */
public record MeetingAttendee(String name, ZoneId zone) {
    /** Creates an attendee with a display name and timezone. */
    public MeetingAttendee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attendee name cannot be null or blank.");
        }
        Objects.requireNonNull(zone, "Attendee timezone cannot be null.");
    }
}
