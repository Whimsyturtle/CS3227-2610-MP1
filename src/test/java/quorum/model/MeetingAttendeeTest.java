package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class MeetingAttendeeTest {
    @Test
    void acceptsANameWithAtLeastOneNonBlankCharacterAndPreservesIt() {
        MeetingAttendee attendee = new MeetingAttendee("  Alice  ", ZoneId.of("UTC"));

        assertEquals("  Alice  ", attendee.name());
        assertEquals(ZoneId.of("UTC"), attendee.zone());
        assertEquals(new MeetingAttendee("  Alice  ", ZoneId.of("UTC")), attendee);
    }

    @Test
    void rejectsNullAndBlankNamesWithTheSpecificMessage() {
        IllegalArgumentException nullName = assertThrows(IllegalArgumentException.class, () ->
                new MeetingAttendee(null, ZoneId.of("UTC")));
        IllegalArgumentException blankName = assertThrows(IllegalArgumentException.class, () ->
                new MeetingAttendee(" \t\n", ZoneId.of("UTC")));

        assertEquals("Attendee name cannot be null or blank.", nullName.getMessage());
        assertEquals("Attendee name cannot be null or blank.", blankName.getMessage());
    }

    @Test
    void rejectsANullTimezoneWithTheSpecificMessage() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> new MeetingAttendee("Alice", null));

        assertEquals("Attendee timezone cannot be null.", exception.getMessage());
    }
}
