package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

class ListCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_emptyRoster_showsEmptyRosterAndContinues() {
        Roster roster = new Roster();
        ListUiSpy ui = new ListUiSpy();

        CommandOutcome outcome = new ListCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(), ui.participants);
        assertEquals(0, roster.size());
    }

    @Test
    void execute_singleParticipant_showsExactParticipantWithoutMutation()
            throws QuorumException {
        Participant alice = new Participant("Alice", SINGAPORE);
        Roster roster = rosterOf(alice);
        ListUiSpy ui = new ListUiSpy();

        CommandOutcome outcome = new ListCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(alice), ui.participants);
        assertSame(alice, roster.asList().getFirst());
    }

    @Test
    void execute_multipleParticipants_showsRosterOrderWithoutMutation()
            throws QuorumException {
        Participant alice = new Participant("Alice", SINGAPORE);
        Participant bob = new Participant("Bob", ZoneId.of("Europe/London"));
        Roster roster = rosterOf(alice, bob);
        ListUiSpy ui = new ListUiSpy();

        CommandOutcome outcome = new ListCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(alice, bob), ui.participants);
        assertSame(alice, roster.asList().get(0));
        assertSame(bob, roster.asList().get(1));
    }

    private Roster rosterOf(Participant... participants) {
        Roster roster = new Roster();
        for (Participant participant : participants) {
            roster.add(participant);
        }
        return roster;
    }

    private static final class ListUiSpy extends SilentUi {
        private int callCount;
        private List<Participant> participants;

        @Override
        public void showRoster(List<Participant> shownParticipants) {
            callCount++;
            participants = List.copyOf(shownParticipants);
        }
    }
}
