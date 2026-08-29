package quorum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.command.EditRequest;

class ParserEditTest {
    private static final String EDIT_TIMEZONE_ERROR =
            "I need a timezone. Try: edit 1 /tz Europe/London";
    private static final String INVALID_ZONE_ERROR =
            "I don't recognise the zone \"Not/AZone\". "
                    + "Try \"zones Singapore\" to find a valid timezone.";

    private final Parser parser = new Parser();

    @Test
    void parseEdit_validIndexAndZone_returnsRequest() throws QuorumException {
        EditRequest request = parser.parseEdit("  2  /tz  Europe/London  ");

        assertEquals(2, request.index());
        assertEquals(ZoneId.of("Europe/London"), request.zone());
    }

    @Test
    void parseEdit_missingDelimiterWithOtherwiseValidInputs_throwsUsageError() {
        assertEditError("1 Europe/London", EDIT_TIMEZONE_ERROR);
    }

    @Test
    void parseEdit_missingIndexWithValidZone_throwsSpecificIndexError() {
        assertEditError(" /tz Europe/London", "Missing index.");
    }

    @Test
    void parseEdit_invalidIndexWithValidZone_throwsSpecificIndexError() {
        assertEditError("0 /tz Europe/London", "Index must be at least 1.");
    }

    @Test
    void parseEdit_missingZoneWithValidIndex_throwsSpecificZoneError() {
        assertEditError("1 /tz  ", "Missing timezone after /tz.");
    }

    @Test
    void parseEdit_invalidZoneWithValidIndex_throwsSpecificZoneError() {
        assertEditError("1 /tz Not/AZone", INVALID_ZONE_ERROR);
    }

    @Test
    void parseEdit_invalidIndexAndZone_reportsIndexErrorFirst() {
        assertEditError("zero /tz Not/AZone", "Index must be a whole number.");
    }

    private void assertEditError(String arguments, String expectedMessage) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseEdit(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
