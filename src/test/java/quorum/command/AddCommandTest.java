package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

class AddCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_emptyRoster_addsParticipantShowsNewTotalAndContinues()
            throws QuorumException {
        Participant alice = new Participant("Alice", SINGAPORE);
        Roster roster = new Roster();
        AddUiSpy ui = new AddUiSpy();

        CommandOutcome outcome = new AddCommand(alice).execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, roster.size());
        assertSame(alice, roster.asList().getFirst());
        assertEquals(1, ui.callCount);
        assertSame(alice, ui.participant);
        assertEquals(1, ui.total);
    }

    @Test
    void execute_nonEmptyRoster_appendsExactParticipantAndShowsNewTotal()
            throws QuorumException {
        Participant alice = new Participant("Alice", SINGAPORE);
        Participant bob = new Participant(
                "Bob", ZoneId.of("Europe/London"), List.of(new Tag("work")));
        Roster roster = new Roster();
        roster.add(alice);
        AddUiSpy ui = new AddUiSpy();

        CommandOutcome outcome = new AddCommand(bob).execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(List.of(alice, bob), roster.asList());
        assertEquals(1, ui.callCount);
        assertSame(bob, ui.participant);
        assertEquals(2, ui.total);
    }

    private static final class AddUiSpy extends SilentUi {
        private int callCount;
        private Participant participant;
        private int total;

        @Override
        public void showAdded(Participant shownParticipant, int shownTotal) {
            callCount++;
            participant = shownParticipant;
            total = shownTotal;
        }
    }
}
