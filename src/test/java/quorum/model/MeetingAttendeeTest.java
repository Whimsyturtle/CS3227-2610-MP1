package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class MeetingAttendeeTest {
    private static final ZoneId ZONE = ZoneId.of("Asia/Singapore");

    @Test
    void constructor_nonBlankNameAndZone_preservesValues() {
        MeetingAttendee attendee = new MeetingAttendee("Alice", ZONE);

        assertEquals("Alice", attendee.name());
        assertEquals(ZONE, attendee.zone());
    }

    @Test
    void constructor_nullName_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MeetingAttendee(null, ZONE));

        assertEquals("Attendee name cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_blankName_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MeetingAttendee(" \t\n", ZONE));

        assertEquals("Attendee name cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_nullZone_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new MeetingAttendee("Alice", null));

        assertEquals("Attendee timezone cannot be null.", exception.getMessage());
    }
}
