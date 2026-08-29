package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
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
    private static final ZoneId USER_ZONE = ZoneId.of("UTC");
    private static final LocalDate MEETING_DATE = LocalDate.of(2026, 8, 30);
    private static final Duration MEETING_DURATION = Duration.ofHours(1);

    @Test
    void execute_mixedRoster_showsOnlyTaggedParticipantsResultsAndContinues()
            throws QuorumException {
        Tag friends = new Tag("friends");
        Tag work = new Tag("work");
        Roster roster = new Roster();
        roster.add(new Participant("Alice", USER_ZONE, Set.of(friends)));
        roster.add(new Participant("Not invited", ZoneId.of("Pacific/Honolulu"),
                Set.of(work)));
        roster.add(new Participant("Carol", USER_ZONE, Set.of(friends, work)));
        RecordingUi ui = new RecordingUi();
        MeetingCommand command = commandFor(friends);

        CommandOutcome outcome = command.execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.meetingCount);
        assertEquals(friends, ui.tag);
        assertEquals(MEETING_DATE, ui.date);
        assertEquals(USER_ZONE, ui.userZone);
        assertEquals(27, ui.results.size());
        assertTrue(ui.results.stream().allMatch(result -> result.attendeeCount() == 3));
        assertTrue(ui.results.stream().allMatch(result -> result.awakeCount() == 3));
        assertEquals(MEETING_DATE.atTime(8, 0).atZone(USER_ZONE).toInstant(),
                ui.results.getFirst().slot().start());
        assertEquals(MEETING_DATE.atTime(21, 0).atZone(USER_ZONE).toInstant(),
                ui.results.getLast().slot().start());
    }

    @Test
    void execute_noParticipantWithTag_throwsSpecificErrorWithoutShowingMeeting()
            throws QuorumException {
        Tag friends = new Tag("friends");
        Roster roster = new Roster();
        roster.add(new Participant("Alice", USER_ZONE, Set.of(new Tag("work"))));
        RecordingUi ui = new RecordingUi();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(friends).execute(roster, ui));

        assertEquals("No participants have tag FRIENDS.", exception.getMessage());
        assertEquals(0, ui.meetingCount);
        assertNull(ui.results);
    }

    private MeetingCommand commandFor(Tag tag) {
        return new MeetingCommand(
                new MeetingRequest(USER_ZONE, tag, MEETING_DATE, MEETING_DURATION));
    }

    private static final class RecordingUi extends Ui {
        private int meetingCount;
        private Tag tag;
        private LocalDate date;
        private ZoneId userZone;
        private List<WakefulnessResult> results;

        private RecordingUi() {
            super(InputStream.nullInputStream(),
                    new PrintStream(OutputStream.nullOutputStream()));
        }

        @Override
        public void showMeeting(Tag shownTag, LocalDate shownDate, ZoneId shownUserZone,
                                List<WakefulnessResult> shownResults) {
            meetingCount++;
            tag = shownTag;
            date = shownDate;
            userZone = shownUserZone;
            results = List.copyOf(shownResults);
        }
    }
}
