package quorum.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class ParserZoneTest {
    private static final String INVALID_ZONE_ERROR =
            "I don't recognise the zone \"Not/AZone\". "
                    + "Try \"zones Singapore\" to find a valid timezone.";

    private final Parser parser = new Parser();

    @Test
    void parseZoneSearch_termLengthJustBelowMinimum_throwsSpecificException() {
        assertZoneSearchError(" \t ");
    }

    @Test
    void parseZoneSearch_termLengthAtMinimum_returnsTerm()
            throws QuorumException {
        assertEquals("A", parser.parseZoneSearch("A"));
    }

    @Test
    void parseZoneSearch_termLengthJustAboveMinimum_returnsTerm()
            throws QuorumException {
        assertEquals("As", parser.parseZoneSearch("As"));
    }

    @Test
    void parseZoneSearch_multiWordTerm_trimsOuterWhitespaceOnly()
            throws QuorumException {
        assertEquals("New  York", parser.parseZoneSearch("  New  York  "));
    }

    @Test
    void parseZone_regionIdWithOuterWhitespace_returnsZone() throws QuorumException {
        assertEquals(ZoneId.of("Asia/Singapore"),
                parser.parseZone("  Asia/Singapore  "));
    }

    @Test
    void parseZone_fixedOffset_returnsZone() throws QuorumException {
        assertEquals(ZoneId.of("+08:00"), parser.parseZone("+08:00"));
    }

    @Test
    void parseZone_blankInput_throwsSpecificException() {
        assertZoneError(" \t ", "Missing timezone after /tz.");
    }

    @Test
    void parseZone_invalidNonBlankInput_throwsSpecificException() {
        assertZoneError("Not/AZone", INVALID_ZONE_ERROR);
    }

    private void assertZoneSearchError(String arguments) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseZoneSearch(arguments));

        assertEquals("Missing timezone search term. Try: zones Singapore",
                exception.getMessage());
    }

    private void assertZoneError(String arguments, String expectedMessage) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseZone(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
