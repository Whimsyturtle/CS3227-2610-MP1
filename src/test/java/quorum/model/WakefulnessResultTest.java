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
            Instant.parse("2026-08-30T10:00:00Z"), Instant.parse("2026-08-30T11:00:00Z"));
    private static final MeetingAttendee ALICE = new MeetingAttendee("Alice", ZoneId.of("UTC"));
    private static final MeetingAttendee BOB = new MeetingAttendee("Bob", ZoneId.of("UTC"));

    @Test
    void derivesTheNumberOfAwakeAttendees() {
        WakefulnessResult result = new WakefulnessResult(SLOT, 3, List.of(ALICE));

        assertEquals(SLOT, result.slot());
        assertEquals(3, result.attendeeCount());
        assertEquals(List.of(ALICE), result.notAwake());
        assertEquals(2, result.awakeCount());
    }

    @Test
    void acceptsTheAttendeeCountBoundaries() {
        WakefulnessResult everyoneAwake = new WakefulnessResult(SLOT, 1, List.of());
        WakefulnessResult nobodyAwake = new WakefulnessResult(SLOT, 1, List.of(ALICE));

        assertEquals(1, everyoneAwake.awakeCount());
        assertEquals(0, nobodyAwake.awakeCount());
    }

    @Test
    void rejectsZeroOrNegativeAttendeeCounts() {
        IllegalArgumentException zero = assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 0, List.of()));
        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, -1, List.of()));

        assertEquals("Invalid attendee count.", zero.getMessage());
        assertEquals("Invalid attendee count.", negative.getMessage());
    }

    @Test
    void rejectsMoreNotAwakeAttendeesThanTheTotalCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 1, List.of(ALICE, BOB)));

        assertEquals("Invalid attendee count.", exception.getMessage());
    }

    @Test
    void rejectsNullSlotAndNullNotAwakeList() {
        NullPointerException nullSlot = assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(null, 1, List.of()));
        NullPointerException nullList = assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(SLOT, 1, null));

        assertEquals("Time slot cannot be null.", nullSlot.getMessage());
        assertEquals("Not-awake attendees cannot be null.", nullList.getMessage());
    }

    @Test
    void copiesTheNotAwakeListAndExposesItAsImmutable() {
        List<MeetingAttendee> original = new ArrayList<>(List.of(ALICE));
        WakefulnessResult result = new WakefulnessResult(SLOT, 2, original);

        original.clear();

        assertEquals(List.of(ALICE), result.notAwake());
        assertThrows(UnsupportedOperationException.class, () ->
                result.notAwake().add(BOB));
    }

    @Test
    void rejectsNullAttendeesInTheNotAwakeList() {
        List<MeetingAttendee> attendees = new ArrayList<>();
        attendees.add(null);

        assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(SLOT, 1, attendees));
    }
}
