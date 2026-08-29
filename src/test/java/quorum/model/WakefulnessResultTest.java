package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests wakefulness result scoring and defensive copying. */
public class WakefulnessResultTest {
    private static final TimeSlot SLOT = new TimeSlot(
            Instant.parse("2026-08-30T08:00:00Z"),
            Instant.parse("2026-08-30T09:00:00Z"));

    @Test
    public void calculatesAwakeCountFromAttendeeCountAndNotAwakeList() {
        MeetingAttendee alice = new MeetingAttendee("Alice", ZoneId.of("UTC"));
        MeetingAttendee bob = new MeetingAttendee("Bob", ZoneId.of("UTC"));

        WakefulnessResult result = new WakefulnessResult(SLOT, 3, List.of(alice, bob));

        assertEquals(3, result.attendeeCount());
        assertEquals(List.of(alice, bob), result.notAwake());
        assertEquals(1, result.awakeCount());
    }

    @Test
    public void copiesAndProtectsNotAwakeList() {
        List<MeetingAttendee> attendees = new ArrayList<>();
        MeetingAttendee alice = new MeetingAttendee("Alice", ZoneId.of("UTC"));
        attendees.add(alice);
        WakefulnessResult result = new WakefulnessResult(SLOT, 2, attendees);

        attendees.clear();

        assertEquals(List.of(alice), result.notAwake());
        assertThrows(UnsupportedOperationException.class,
                () -> result.notAwake().add(alice));
    }

    @Test
    public void allowsNoAwakeAttendeesWhenAllAreNotAwake() {
        MeetingAttendee alice = new MeetingAttendee("Alice", ZoneId.of("UTC"));

        WakefulnessResult result = new WakefulnessResult(SLOT, 1, List.of(alice));

        assertEquals(0, result.awakeCount());
    }

    @Test
    public void rejectsInvalidAttendeeCounts() {
        MeetingAttendee alice = new MeetingAttendee("Alice", ZoneId.of("UTC"));

        assertThrows(IllegalArgumentException.class,
                () -> new WakefulnessResult(SLOT, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new WakefulnessResult(SLOT, 1, List.of(alice, alice)));
    }

    @Test
    public void rejectsNullSlotOrNotAwakeList() {
        assertThrows(NullPointerException.class,
                () -> new WakefulnessResult(null, 1, List.of()));
        assertThrows(NullPointerException.class,
                () -> new WakefulnessResult(SLOT, 1, null));
    }
}
