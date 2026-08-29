package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class WakefulnessResultTest {
    private static final TimeSlot SLOT = new TimeSlot(
            Instant.parse("2026-08-30T00:00:00Z"),
            Instant.parse("2026-08-30T01:00:00Z"));
    private static final MeetingAttendee ALICE = attendee("Alice");
    private static final MeetingAttendee BOB = attendee("Bob");
    private static final MeetingAttendee CAROL = attendee("Carol");

    @Test
    void constructor_attendeeCountAtMinimumWithNoneNotAwake_hasOneAwake() {
        WakefulnessResult result = new WakefulnessResult(SLOT, 1, List.of());

        assertEquals(SLOT, result.slot());
        assertEquals(1, result.attendeeCount());
        assertEquals(List.of(), result.notAwake());
        assertEquals(1, result.awakeCount());
    }

    @Test
    void constructor_attendeeCountJustAboveMinimum_derivesAwakeCount() {
        WakefulnessResult result = new WakefulnessResult(SLOT, 2, List.of(ALICE));

        assertEquals(1, result.awakeCount());
    }

    @Test
    void constructor_notAwakeCountEqualToAttendeeCount_hasNoneAwake() {
        WakefulnessResult result = new WakefulnessResult(SLOT, 2, List.of(ALICE, BOB));

        assertEquals(0, result.awakeCount());
    }

    @Test
    void constructor_attendeeCountJustBelowMinimum_throwsSpecificException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 0, List.of()));

        assertEquals("Invalid attendee count.", exception.getMessage());
    }

    @Test
    void constructor_notAwakeCountJustAboveAttendeeCount_throwsSpecificException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 2, List.of(ALICE, BOB, CAROL)));

        assertEquals("Invalid attendee count.", exception.getMessage());
    }

    @Test
    void constructor_nullSlot_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(null, 1, List.of()));

        assertEquals("Time slot cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_nullNotAwakeList_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(SLOT, 1, null));

        assertEquals("Not-awake attendees cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_nullNotAwakeElement_throwsNullPointerException() {
        List<MeetingAttendee> attendeesWithNull = new ArrayList<>();
        attendeesWithNull.add(null);

        assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(SLOT, 1, attendeesWithNull));
    }

    @Test
    void constructor_mutableNotAwakeList_defensivelyCopiesUnmodifiableList() {
        List<MeetingAttendee> source = new ArrayList<>();
        source.add(ALICE);

        WakefulnessResult result = new WakefulnessResult(SLOT, 2, source);
        source.clear();

        assertEquals(List.of(ALICE), result.notAwake());
        assertThrows(UnsupportedOperationException.class, () -> result.notAwake().add(BOB));
    }

    private static MeetingAttendee attendee(String name) {
        return new MeetingAttendee(name, ZoneId.of("UTC"));
    }
}
