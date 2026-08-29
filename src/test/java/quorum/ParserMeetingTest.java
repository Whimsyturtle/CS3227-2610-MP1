package quorum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.command.MeetingCommand;
import quorum.command.MeetingRequest;
import quorum.model.Tag;

class ParserMeetingTest {
    private static final String VALID_PREFIX =
            "Asia/Singapore FRIENDS /on 2026-08-30 /for ";
    private static final String USAGE_ERROR =
            "Invalid meeting command. "
                    + "Try: meeting Asia/Singapore FRIENDS /on 2026-08-30 /for 1h";

    private final Parser parser = new Parser();

    @Test
    void parse_meetingKeyword_returnsMeetingCommand() throws QuorumException {
        assertInstanceOf(MeetingCommand.class,
                parser.parse("MeEtInG Asia/Singapore FRIENDS /on 2026-08-30 /for 1h"));
    }

    @Test
    void parseMeeting_validFieldsAndFlexibleWhitespace_returnsNormalizedRequest()
            throws QuorumException {
        MeetingRequest request = parser.parseMeeting(
                "  Asia/Singapore\t friends   /ON  2026-08-30  /FoR  1H30M  ");

        assertEquals(ZoneId.of("Asia/Singapore"), request.userZone());
        assertEquals(new Tag("FRIENDS"), request.tag());
        assertEquals(LocalDate.of(2026, 8, 30), request.date());
        assertEquals(Duration.ofMinutes(90), request.duration());
    }

    @Test
    void parseMeeting_hoursOnlyDuration_returnsRequest() throws QuorumException {
        assertEquals(Duration.ofHours(2), parseDuration("2h"));
    }

    @Test
    void parseMeeting_minutesOnlyDurationIncludingNormalization_returnsRequest()
            throws QuorumException {
        assertEquals(Duration.ofMinutes(90), parseDuration("90m"));
    }

    @Test
    void parseMeeting_durationAtMinimum_returnsRequest() throws QuorumException {
        assertEquals(Duration.ofMinutes(1), parseDuration("1m"));
    }

    @Test
    void parseMeeting_durationJustAboveMinimum_returnsRequest() throws QuorumException {
        assertEquals(Duration.ofMinutes(2), parseDuration("2m"));
    }

    @Test
    void parseMeeting_durationJustBelowMinimum_throwsSpecificDurationError() {
        assertInvalidDuration("0m");
    }

    @Test
    void parseMeeting_durationJustBelowMaximum_returnsRequest() throws QuorumException {
        assertEquals(Duration.ofHours(24).minusMinutes(1), parseDuration("23h59m"));
    }

    @Test
    void parseMeeting_durationAtMaximum_returnsRequest() throws QuorumException {
        assertEquals(Duration.ofHours(24), parseDuration("24h"));
    }

    @Test
    void parseMeeting_durationJustAboveMaximum_throwsSpecificDurationError() {
        assertInvalidDuration("24h1m");
    }

    @Test
    void parseMeeting_durationWithoutUnit_throwsSpecificDurationError() {
        assertInvalidDuration("30");
    }

    @Test
    void parseMeeting_representableNumberWhoseDurationOverflows_throwsSpecificDurationError() {
        assertInvalidDuration("9223372036854775807h");
    }

    @Test
    void parseMeeting_numberLargerThanLong_throwsSpecificDurationError() {
        assertInvalidDuration("9223372036854775808h");
    }

    @Test
    void parseMeeting_missingZone_throwsUsageError() {
        assertUsageError("FRIENDS /on 2026-08-30 /for 1h");
    }

    @Test
    void parseMeeting_missingTag_throwsUsageError() {
        assertUsageError("Asia/Singapore /on 2026-08-30 /for 1h");
    }

    @Test
    void parseMeeting_missingDate_throwsUsageError() {
        assertUsageError("Asia/Singapore FRIENDS /on /for 1h");
    }

    @Test
    void parseMeeting_missingDuration_throwsUsageError() {
        assertUsageError("Asia/Singapore FRIENDS /on 2026-08-30 /for");
    }

    @Test
    void parseMeeting_wrongArgumentOrder_throwsUsageError() {
        assertUsageError("Asia/Singapore FRIENDS /for 1h /on 2026-08-30");
    }

    @Test
    void parseMeeting_trailingArgument_throwsUsageError() {
        assertUsageError("Asia/Singapore FRIENDS /on 2026-08-30 /for 1h extra");
    }

    @Test
    void parseMeeting_invalidZone_throwsSpecificZoneError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting("Not/AZone FRIENDS /on 2026-08-30 /for 1h"));

        assertEquals("I don't recognise the zone \"Not/AZone\". "
                + "Try \"zones Singapore\" to find a valid timezone.", exception.getMessage());
    }

    @Test
    void parseMeeting_invalidTag_throwsSpecificTagError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Asia/Singapore FRIENDS,FAMILY /on 2026-08-30 /for 1h"));

        assertEquals("Tag cannot contain commas.", exception.getMessage());
    }

    @Test
    void parseMeeting_nonIsoDate_throwsSpecificDateError() {
        assertInvalidDate("30-08-2026");
    }

    @Test
    void parseMeeting_nonexistentCalendarDate_throwsSpecificDateError() {
        assertInvalidDate("2026-02-29");
    }

    private Duration parseDuration(String durationText) throws QuorumException {
        return parser.parseMeeting(VALID_PREFIX + durationText).duration();
    }

    private void assertInvalidDuration(String durationText) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(VALID_PREFIX + durationText));

        assertEquals("Invalid duration \"" + durationText
                + "\". Use a duration up to 24h, such as 30m, 1h, or 1h30m.",
                exception.getMessage());
    }

    private void assertUsageError(String arguments) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(arguments));

        assertEquals(USAGE_ERROR, exception.getMessage());
    }

    private void assertInvalidDate(String dateText) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Asia/Singapore FRIENDS /on " + dateText + " /for 1h"));

        assertEquals("Invalid date \"" + dateText
                + "\". Use YYYY-MM-DD, such as 2026-08-30.", exception.getMessage());
    }
}
