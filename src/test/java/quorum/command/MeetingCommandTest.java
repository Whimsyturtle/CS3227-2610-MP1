package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;
import quorum.model.WakefulnessResult;
import quorum.ui.Ui;

class MeetingCommandTest {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final LocalDate MEETING_DATE = LocalDate.of(2026, 8, 30);

    @Test
    void selectsMatchingParticipantsAndDisplaysBestResults() throws QuorumException {
        Roster roster = new Roster();
        roster.add(participant("Alice", "UTC", "friends"));
        roster.add(participant("Bob", "UTC", "work"));
        RecordingUi ui = new RecordingUi();
        MeetingRequest request = new MeetingRequest(
                UTC, new Tag("FRIENDS"), MEETING_DATE, Duration.ofHours(1));

        CommandOutcome outcome = new MeetingCommand(request).execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.showMeetingCalls);
        assertEquals(request.tag(), ui.tag);
        assertEquals(request.date(), ui.date);
        assertEquals(request.userZone(), ui.userZone);
        assertEquals(27, ui.results.size());
        for (WakefulnessResult result : ui.results) {
            assertEquals(2, result.attendeeCount());
            assertEquals(2, result.awakeCount());
            assertEquals(List.of(), result.notAwake());
        }
    }

    @Test
    void rejectsARequestWhenNoRosterParticipantHasTheRequestedTag() throws QuorumException {
        Roster roster = new Roster();
        roster.add(participant("Alice", "UTC", "work"));
        RecordingUi ui = new RecordingUi();
        MeetingRequest request = new MeetingRequest(
                UTC, new Tag("FRIENDS"), MEETING_DATE, Duration.ofHours(1));

        QuorumException exception = assertThrows(
                QuorumException.class, () -> new MeetingCommand(request).execute(roster, ui));

        assertEquals("No participants have tag FRIENDS.", exception.getMessage());
        assertEquals(0, ui.showMeetingCalls);
    }

    private Participant participant(String name, String zone, String tag)
            throws QuorumException {
        return new Participant(name, ZoneId.of(zone), Set.of(new Tag(tag)));
    }

    private static final class RecordingUi extends Ui {
        private int showMeetingCalls;
        private Tag tag;
        private LocalDate date;
        private ZoneId userZone;
        private List<WakefulnessResult> results = List.of();

        private RecordingUi() {
            super(new ByteArrayInputStream(new byte[0]),
                    new PrintStream(OutputStream.nullOutputStream()));
        }

        @Override
        public void showMeeting(Tag meetingTag, LocalDate meetingDate,
                                ZoneId meetingUserZone,
                                List<WakefulnessResult> meetingResults) {
            showMeetingCalls++;
            tag = meetingTag;
            date = meetingDate;
            userZone = meetingUserZone;
            results = List.copyOf(meetingResults);
        }
    }
}
