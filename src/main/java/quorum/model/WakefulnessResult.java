package quorum.model;

import java.util.List;
import java.util.Objects;

/** The result of applying wakefulness scoring to a time slot. */
public record WakefulnessResult(TimeSlot slot, int attendeeCount,
                                List<MeetingAttendee> notAwake) {
    /** Creates an immutable wakefulness result. */
    public WakefulnessResult {
        Objects.requireNonNull(slot, "Time slot cannot be null.");
        Objects.requireNonNull(notAwake, "Not-awake attendees cannot be null.");
        if (attendeeCount < 1 || notAwake.size() > attendeeCount) {
            throw new IllegalArgumentException("Invalid attendee count.");
        }
        notAwake = List.copyOf(notAwake);
    }

    /** Returns the number of attendees awake for the entire time slot. */
    public int awakeCount() {
        return attendeeCount - notAwake.size();
    }
}
