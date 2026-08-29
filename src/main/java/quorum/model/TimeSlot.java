package quorum.model;

import java.time.Instant;
import java.util.Objects;

/** A candidate time interval for a meeting. */
public record TimeSlot(Instant start, Instant end) {
    /** Creates a time slot whose end is after its start. */
    public TimeSlot {
        Objects.requireNonNull(start, "Time-slot start cannot be null.");
        Objects.requireNonNull(end, "Time-slot end cannot be null.");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Time-slot end must be after its start.");
        }
    }
}
