package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

/** Tests the invariants of a meeting attendee. */
public class MeetingAttendeeTest {
    @Test
    public void storesNameAndTimezone() {
        MeetingAttendee attendee = new MeetingAttendee(
                "Alice", ZoneId.of("Asia/Singapore"));

        assertEquals("Alice", attendee.name());
        assertEquals(ZoneId.of("Asia/Singapore"), attendee.zone());
    }

    @Test
    public void rejectsNullOrBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new MeetingAttendee(null, ZoneId.of("UTC")));
        assertThrows(IllegalArgumentException.class,
                () -> new MeetingAttendee("  ", ZoneId.of("UTC")));
    }

    @Test
    public void rejectsNullTimezone() {
        assertThrows(NullPointerException.class,
                () -> new MeetingAttendee("Alice", null));
    }
}
