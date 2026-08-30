package quorum.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;

class ParserParticipantTest {
    private static final String ADD_TIMEZONE_ERROR =
            "I need a timezone. Try: add Alice /tz Asia/Singapore";
    private static final String INVALID_ZONE_ERROR =
            "I don't recognise the zone \"Not/AZone\". "
                    + "Try \"zones Singapore\" to find a valid timezone.";

    private final Parser parser = new Parser();

    @Test
    void parseParticipant_nameLengthAtMinimum_returnsParticipant()
            throws QuorumException {
        Participant participant = parser.parseParticipant("A /tz Z");

        assertEquals("A", participant.getName());
        assertEquals(ZoneId.of("Z"), participant.getZone());
    }

    @Test
    void parseParticipant_nameLengthJustAboveMinimumAndUppercaseDelimiter_returnsParticipant()
            throws QuorumException {
        Participant participant = parser.parseParticipant(
                "  Al  /TZ  Europe/London  ");

        assertEquals("Al", participant.getName());
        assertEquals(ZoneId.of("Europe/London"), participant.getZone());
    }

    @Test
    void parseParticipant_multiWordName_normalizesRepeatedSpaces()
            throws QuorumException {
        Participant participant = parser.parseParticipant(
                "Alice  Tan /tz Asia/Singapore");

        assertEquals("Alice Tan", participant.getName());
        assertEquals(ZoneId.of("Asia/Singapore"), participant.getZone());
    }

    @Test
    void parseParticipant_nameLengthJustBelowMinimum_throwsSpecificNameError() {
        assertParticipantError(" /tz Asia/Singapore",
                "Name cannot be null or blank.");
    }

    @Test
    void parseParticipant_lineBreakInNameWithValidZone_throwsSpecificNameError() {
        assertParticipantError("Alice\nTan /tz Asia/Singapore",
                "Name cannot contain tabs or line breaks.");
    }

    @Test
    void parseParticipant_incompleteTimezoneDelimiter_throwsUsageError() {
        assertParticipantError("Alice /t Asia/Singapore", ADD_TIMEZONE_ERROR);
    }

    @Test
    void parseParticipant_missingWhitespaceAfterDelimiter_throwsUsageError() {
        assertParticipantError("Alice /tzZ", ADD_TIMEZONE_ERROR);
    }

    @Test
    void parseParticipant_missingDelimiterWithOtherwiseValidName_throwsUsageError() {
        assertParticipantError("Alice Asia/Singapore", ADD_TIMEZONE_ERROR);
    }

    @Test
    void parseParticipant_missingZoneWithValidName_throwsSpecificZoneError() {
        assertParticipantError("Alice /tz  ", "Missing timezone after /tz.");
    }

    @Test
    void parseParticipant_invalidZoneWithValidName_throwsSpecificZoneError() {
        assertParticipantError("Alice /tz Not/AZone", INVALID_ZONE_ERROR);
    }

    private void assertParticipantError(String arguments, String expectedMessage) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseParticipant(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
