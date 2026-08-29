package quorum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.command.Command;
import quorum.command.MeetingCommand;
import quorum.command.MeetingRequest;

class ParserMeetingTest {
    private static final String VALID_DATE = "2026-08-30";
    private static final String INVALID_COMMAND_MESSAGE =
            "Invalid meeting command. Try: meeting Asia/Singapore FRIENDS /on 2026-08-30"
                    + " /for 1h";

    private final Parser parser = new Parser();

    @Test
    void parsesMeetingRequestAndNormalizesTheTag() throws QuorumException {
        MeetingRequest request = parser.parseMeeting(
                "  UTC   friends   /ON   2028-02-29   /FOR   1h30m  ");

        assertEquals(ZoneId.of("UTC"), request.userZone());
        assertEquals("FRIENDS", request.tag().value());
        assertEquals(LocalDate.of(2028, 2, 29), request.date());
        assertEquals(Duration.ofMinutes(90), request.duration());
    }

    @Test
    void parseDispatchesTheMeetingKeywordToAMeetingCommand() throws QuorumException {
        Command command = parser.parse(
                "MEETING UTC friends /on " + VALID_DATE + " /for 1h");

        assertInstanceOf(MeetingCommand.class, command);
    }

    @Test
    void parsesEachSupportedDurationShape() throws QuorumException {
        assertEquals(Duration.ofHours(1), parseDuration("1h"));
        assertEquals(Duration.ofMinutes(30), parseDuration("30m"));
        assertEquals(Duration.ofMinutes(90), parseDuration("1h30m"));
    }

    @Test
    void acceptsDurationsJustBelowAndExactlyAtThe24HourLimit() throws QuorumException {
        assertEquals(Duration.ofHours(23).plusMinutes(59), parseDuration("23h59m"));
        assertEquals(Duration.ofHours(24), parseDuration("24h"));
    }

    @Test
    void rejectsDurationsAboveThe24HourLimit() {
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 24h1m",
                "Invalid duration \"24h1m\". Use a duration up to 24h, such as 30m,"
                        + " 1h, or 1h30m.");
    }

    @Test
    void rejectsZeroDurationAtThePositiveDurationBoundary() {
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 0m",
                invalidDurationMessage("0m"));
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 0h0m",
                invalidDurationMessage("0h0m"));
    }

    @Test
    void rejectsMalformedDurationsIndividually() {
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 1",
                invalidDurationMessage("1"));
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 1h30",
                invalidDurationMessage("1h30"));
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 30m1h",
                invalidDurationMessage("30m1h"));
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for -1m",
                invalidDurationMessage("-1m"));
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 1.5h",
                invalidDurationMessage("1.5h"));
    }

    @Test
    void reportsOverflowingDurationNumbersAsInvalidDurations() {
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 999999999999999999999999999h",
                invalidDurationMessage("999999999999999999999999999h"));
    }

    @Test
    void reportsDurationArithmeticOverflowAsAnInvalidDuration() {
        String duration = "9223372036854775807h";

        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for " + duration,
                invalidDurationMessage(duration));
    }

    @Test
    void rejectsEachMissingMeetingSyntaxPart() {
        assertMeetingException("", INVALID_COMMAND_MESSAGE);
        assertMeetingException(
                "FRIENDS /on " + VALID_DATE + " /for 1h", INVALID_COMMAND_MESSAGE);
        assertMeetingException(
                "UTC /on " + VALID_DATE + " /for 1h", INVALID_COMMAND_MESSAGE);
        assertMeetingException("UTC FRIENDS /on /for 1h", INVALID_COMMAND_MESSAGE);
        assertMeetingException(
                "UTC FRIENDS " + VALID_DATE + " /for 1h", INVALID_COMMAND_MESSAGE);
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for", INVALID_COMMAND_MESSAGE);
        assertMeetingException(
                "UTC FRIENDS /on " + VALID_DATE + " /for 1h extra",
                INVALID_COMMAND_MESSAGE);
    }

    @Test
    void reportsAnInvalidTimezoneAfterValidSyntax() {
        assertMeetingException(
                "Mars/Atlantis FRIENDS /on " + VALID_DATE + " /for 1h",
                "I don't recognise the zone \"Mars/Atlantis\". Try \"zones Singapore\""
                        + " to find a valid timezone.");
    }

    @Test
    void reportsAnInvalidTagAfterValidSyntax() {
        assertMeetingException(
                "UTC FRIENDS,TEAM /on " + VALID_DATE + " /for 1h",
                "Tag cannot contain commas.");
    }

    @Test
    void reportsAnInvalidDateAfterValidSyntax() {
        assertMeetingException(
                "UTC FRIENDS /on 2026-02-29 /for 1h",
                "Invalid date \"2026-02-29\". Use YYYY-MM-DD, such as 2026-08-30.");
    }

    private Duration parseDuration(String duration) throws QuorumException {
        return parser.parseMeeting(
                "UTC FRIENDS /on " + VALID_DATE + " /for " + duration).duration();
    }

    private void assertMeetingException(String arguments, String message) {
        QuorumException exception = assertThrows(
                QuorumException.class, () -> parser.parseMeeting(arguments));
        assertEquals(message, exception.getMessage());
    }

    private String invalidDurationMessage(String duration) {
        return "Invalid duration \"" + duration
                + "\". Use a duration up to 24h, such as 30m, 1h, or 1h30m.";
    }
}
