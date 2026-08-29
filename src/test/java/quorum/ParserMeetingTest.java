package quorum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.command.MeetingCommand;
import quorum.command.MeetingRequest;
import quorum.model.Tag;

class ParserMeetingTest {
    private static final String MEETING_PREFIX =
            "Asia/Singapore FRIENDS /on 2026-08-30 /for ";

    private final Parser parser = new Parser();

    @Test
    void parse_meetingKeyword_returnsMeetingCommand() throws QuorumException {
        assertInstanceOf(MeetingCommand.class,
                parser.parse("meeting Asia/Singapore FRIENDS /on 2026-08-30 /for 1h"));
    }

    @Test
    void parseMeeting_validArguments_returnsNormalizedRequest() throws QuorumException {
        MeetingRequest request = parser.parseMeeting(
                "  Asia/Singapore friends /ON 2026-08-30 /FoR 1H30M  ");

        assertEquals(ZoneId.of("Asia/Singapore"), request.userZone());
        assertEquals(new Tag("FRIENDS"), request.tag());
        assertEquals(LocalDate.of(2026, 8, 30), request.date());
        assertEquals(Duration.ofMinutes(90), request.duration());
    }

    @Test
    void parseMeeting_durationAtLimit_returnsRequest() throws QuorumException {
        MeetingRequest request = parser.parseMeeting(MEETING_PREFIX + "24h");

        assertEquals(Duration.ofHours(24), request.duration());
    }

    @Test
    void parseMeeting_wrongArgumentOrder_throwsUsageError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Asia/Singapore FRIENDS /for 1h /on 2026-08-30"));

        assertTrue(exception.getMessage().contains("Invalid meeting command"));
    }

    @Test
    void parseMeeting_invalidZone_throwsZoneError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Not/AZone FRIENDS /on 2026-08-30 /for 1h"));

        assertTrue(exception.getMessage().contains("Not/AZone"));
    }

    @Test
    void parseMeeting_invalidTag_throwsTagError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Asia/Singapore FRIENDS,FAMILY /on 2026-08-30 /for 1h"));

        assertEquals("Tag cannot contain commas.", exception.getMessage());
    }

    @Test
    void parseMeeting_invalidDate_throwsFormatError() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(
                        "Asia/Singapore FRIENDS /on 2026-02-30 /for 1h"));

        assertTrue(exception.getMessage().contains("Use YYYY-MM-DD"));
    }

    @Test
    void parseMeeting_zeroMalformedOrOverLimitDuration_throwsFormatError() {
        String[] invalidDurations = {
            "0m", "0h0m", "1hour", "1m1h", "24h1m", "25h", "-1h"
        };

        for (String duration : invalidDurations) {
            QuorumException exception = assertThrows(QuorumException.class, () ->
                    parser.parseMeeting(MEETING_PREFIX + duration), duration);
            assertTrue(exception.getMessage().contains("Invalid duration"), duration);
        }
    }

    @Test
    void parseMeeting_numericDurationOverflow_throwsFormatError() {
        String duration = "999999999999999999999999999999h";

        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseMeeting(MEETING_PREFIX + duration));

        assertTrue(exception.getMessage().contains("Invalid duration"));
    }
}
