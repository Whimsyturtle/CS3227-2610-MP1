package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TimeSlotTest {
    private static final Instant START = Instant.parse("2026-08-30T10:00:00Z");

    @Test
    void acceptsTheSmallestPositiveIntervalAndPreservesBothInstants() {
        Instant end = START.plusNanos(1);
        TimeSlot slot = new TimeSlot(START, end);

        assertEquals(START, slot.start());
        assertEquals(end, slot.end());
        assertEquals(new TimeSlot(START, end), slot);
    }

    @Test
    void rejectsAnEndAtOrBeforeTheStart() {
        IllegalArgumentException sameInstant = assertThrows(
                IllegalArgumentException.class, () -> new TimeSlot(START, START));
        IllegalArgumentException beforeStart = assertThrows(IllegalArgumentException.class, () ->
                new TimeSlot(START, START.minusNanos(1)));

        assertEquals("Time-slot end must be after its start.", sameInstant.getMessage());
        assertEquals("Time-slot end must be after its start.", beforeStart.getMessage());
    }

    @Test
    void rejectsANullStart() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> new TimeSlot(null, START));

        assertEquals("Time-slot start cannot be null.", exception.getMessage());
    }

    @Test
    void rejectsANullEnd() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> new TimeSlot(START, null));

        assertEquals("Time-slot end cannot be null.", exception.getMessage());
    }
}
