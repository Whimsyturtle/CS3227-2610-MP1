package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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

/** Tests execution of the meeting command against a roster and UI. */
public class MeetingCommandTest {
    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Singapore");
    private static final LocalDate MEETING_DATE = LocalDate.of(2026, 8, 30);

    @Test
    public void executesForTaggedParticipantsAndDisplaysBestResults() throws Exception {
        Tag friends = new Tag("friends");
        Participant alice = new Participant("Alice", ZoneId.of("UTC"), Set.of(friends));
        Participant ignored = new Participant("Ignored", ZoneId.of("UTC"));
        Roster roster = new Roster();
        roster.add(alice);
        roster.add(ignored);
        MeetingRequest request = new MeetingRequest(
                USER_ZONE, friends, MEETING_DATE, Duration.ofHours(6));
        RecordingUi ui = new RecordingUi();

        CommandOutcome outcome = new MeetingCommand(request).execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(friends, ui.tag);
        assertEquals(MEETING_DATE, ui.date);
        assertEquals(USER_ZONE, ui.userZone);
        assertFalse(ui.results.isEmpty());
        assertEquals(2, ui.results.getFirst().attendeeCount());
        assertEquals(2, ui.results.getFirst().awakeCount());
        assertEquals(
                MEETING_DATE.atTime(LocalTime.of(16, 0)).atZone(USER_ZONE).toInstant(),
                ui.results.getFirst().slot().start());
    }

    @Test
    public void rejectsMeetingWhenNoParticipantHasTheRequestedTag() throws Exception {
        Roster roster = new Roster();
        roster.add(new Participant("Alice", ZoneId.of("UTC")));
        MeetingRequest request = new MeetingRequest(
                USER_ZONE, new Tag("friends"), MEETING_DATE, Duration.ofHours(1));
        RecordingUi ui = new RecordingUi();

        QuorumException exception = assertThrows(QuorumException.class,
                () -> new MeetingCommand(request).execute(roster, ui));

        assertEquals("No participants have tag FRIENDS.", exception.getMessage());
        assertFalse(ui.wasShown);
    }

    private static final class RecordingUi extends Ui {
        private LocalDate date;
        private List<WakefulnessResult> results = List.of();
        private Tag tag;
        private ZoneId userZone;
        private boolean wasShown;

        private RecordingUi() {
            super(new ByteArrayInputStream(new byte[0]),
                    new PrintStream(OutputStream.nullOutputStream()));
        }

        @Override
        public void showMeeting(Tag meetingTag, LocalDate meetingDate,
                                ZoneId meetingUserZone,
                                List<WakefulnessResult> meetingResults) {
            tag = meetingTag;
            date = meetingDate;
            userZone = meetingUserZone;
            results = meetingResults;
            wasShown = true;
        }
    }
}
