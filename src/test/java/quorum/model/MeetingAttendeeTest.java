package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class MeetingAttendeeTest {
    @Test
    void constructor_validValues_preservesNameAndZone() {
        ZoneId zone = ZoneId.of("Asia/Singapore");

        MeetingAttendee attendee = new MeetingAttendee("Alice", zone);

        assertEquals("Alice", attendee.name());
        assertEquals(zone, attendee.zone());
    }

    @Test
    void constructor_nullOrBlankName_throwsIllegalArgumentException() {
        ZoneId zone = ZoneId.of("UTC");

        assertThrows(IllegalArgumentException.class, () -> new MeetingAttendee(null, zone));
        assertThrows(IllegalArgumentException.class, () -> new MeetingAttendee("", zone));
        assertThrows(IllegalArgumentException.class, () -> new MeetingAttendee(" \t\n", zone));
    }

    @Test
    void constructor_nullZone_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MeetingAttendee("Alice", null));
    }
}
