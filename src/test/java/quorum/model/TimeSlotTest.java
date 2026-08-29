package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/** Tests the invariants of a candidate meeting time slot. */
public class TimeSlotTest {
    private static final Instant START = Instant.parse("2026-08-30T08:00:00Z");
    private static final Instant END = Instant.parse("2026-08-30T09:00:00Z");

    @Test
    public void storesStartAndEnd() {
        TimeSlot slot = new TimeSlot(START, END);

        assertEquals(START, slot.start());
        assertEquals(END, slot.end());
    }

    @Test
    public void rejectsNullEndpoints() {
        assertThrows(NullPointerException.class, () -> new TimeSlot(null, END));
        assertThrows(NullPointerException.class, () -> new TimeSlot(START, null));
    }

    @Test
    public void rejectsEqualOrReversedEndpoints() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(START, START));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot(END, START));
    }
}
