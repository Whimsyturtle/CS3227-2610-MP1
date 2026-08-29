package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TimeSlotTest {
    private static final Instant START = Instant.parse("2026-08-30T00:00:00Z");
    private static final Instant END = Instant.parse("2026-08-30T01:00:00Z");

    @Test
    void constructor_endAfterStart_preservesEndpoints() {
        TimeSlot slot = new TimeSlot(START, END);

        assertEquals(START, slot.start());
        assertEquals(END, slot.end());
    }

    @Test
    void constructor_nullEndpoint_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TimeSlot(null, END));
        assertThrows(NullPointerException.class, () -> new TimeSlot(START, null));
    }

    @Test
    void constructor_equalOrEarlierEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(START, START));
        assertThrows(IllegalArgumentException.class, () ->
                new TimeSlot(START, START.minusSeconds(1)));
    }
}
