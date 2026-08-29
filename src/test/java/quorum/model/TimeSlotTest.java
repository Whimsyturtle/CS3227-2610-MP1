package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TimeSlotTest {
    private static final Instant START = Instant.parse("2026-08-30T00:00:00Z");

    @Test
    void constructor_endJustAfterStart_preservesEndpoints() {
        Instant end = START.plusNanos(1);

        TimeSlot slot = new TimeSlot(START, end);

        assertEquals(START, slot.start());
        assertEquals(end, slot.end());
    }

    @Test
    void constructor_nullStart_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new TimeSlot(null, START.plusSeconds(1)));

        assertEquals("Time-slot start cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_nullEnd_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new TimeSlot(START, null));

        assertEquals("Time-slot end cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_endEqualToStart_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TimeSlot(START, START));

        assertEquals("Time-slot end must be after its start.", exception.getMessage());
    }

    @Test
    void constructor_endJustBeforeStart_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new TimeSlot(START, START.minusNanos(1)));

        assertEquals("Time-slot end must be after its start.", exception.getMessage());
    }
}
