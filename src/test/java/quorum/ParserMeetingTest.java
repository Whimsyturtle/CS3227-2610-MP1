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
import quorum.model.Tag;

/** Tests parsing and validation of the meeting command. */
public class ParserMeetingTest {
    private final Parser parser = new Parser();

    @Test
    public void parsesMeetingArgumentsIntoARequest() throws QuorumException {
        MeetingRequest request = parser.parseMeeting(
                "  Asia/Singapore   friends   /ON   2026-08-30   /FOR   1h30m ");

        assertEquals(ZoneId.of("Asia/Singapore"), request.userZone());
        assertEquals(new Tag("FRIENDS"), request.tag());
        assertEquals(LocalDate.of(2026, 8, 30), request.date());
        assertEquals(Duration.ofMinutes(90), request.duration());
    }

    @Test
    public void parseRoutesMeetingKeywordToMeetingCommand() throws QuorumException {
        Command command = parser.parse(
                "MEETING Asia/Singapore FRIENDS /on 2026-08-30 /for 1h");

        assertInstanceOf(MeetingCommand.class, command);
    }

    @Test
    public void acceptsMaximumDuration() throws QuorumException {
        MeetingRequest request = parser.parseMeeting(
                "UTC FRIENDS /on 2026-08-30 /for 24h");

        assertEquals(Duration.ofHours(24), request.duration());
    }

    @Test
    public void rejectsMalformedMeetingSyntax() {
        String[] invalidCommands = {
            "Asia/Singapore FRIENDS /on /for 1h",
            "Asia/Singapore FRIENDS 2026-08-30 /for 1h",
            "Asia/Singapore FRIENDS /on 2026-08-30 1h",
            "Asia/Singapore FRIENDS /on 2026-08-30 /for 1h extra",
            "Asia/Singapore FRIENDS /on 2026-08-30 /for 1"
        };

        for (String invalidCommand : invalidCommands) {
            assertThrows(QuorumException.class,
                    () -> parser.parseMeeting(invalidCommand));
        }
    }

    @Test
    public void rejectsInvalidMeetingZone() {
        QuorumException exception = assertThrows(QuorumException.class,
                () -> parser.parseMeeting(
                        "Not/AZone FRIENDS /on 2026-08-30 /for 1h"));

        assertEquals(
                "I don't recognise the zone \"Not/AZone\". Try \"zones Singapore\" "
                        + "to find a valid timezone.",
                exception.getMessage());
    }

    @Test
    public void rejectsInvalidMeetingDate() {
        QuorumException exception = assertThrows(QuorumException.class,
                () -> parser.parseMeeting(
                        "UTC FRIENDS /on 2026-02-30 /for 1h"));

        assertEquals("Invalid date \"2026-02-30\". Use YYYY-MM-DD, such as 2026-08-30.",
                exception.getMessage());
    }

    @Test
    public void rejectsZeroAndOverlongMeetingDurations() {
        String[] invalidDurations = {"0m", "0h", "25h"};

        for (String invalidDuration : invalidDurations) {
            assertThrows(QuorumException.class,
                    () -> parser.parseMeeting(
                            "UTC FRIENDS /on 2026-08-30 /for " + invalidDuration));
        }
    }
}
