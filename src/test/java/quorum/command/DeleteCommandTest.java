package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

class DeleteCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_indexAtLowerBoundary_deletesFirstParticipantAndContinues()
            throws QuorumException {
        assertSuccessfulDeletion(1, 0);
    }

    @Test
    void execute_indexAboveLowerBoundary_deletesMiddleParticipantAndContinues()
            throws QuorumException {
        assertSuccessfulDeletion(2, 1);
    }

    @Test
    void execute_indexAtUpperBoundary_deletesLastParticipantAndContinues()
            throws QuorumException {
        assertSuccessfulDeletion(3, 2);
    }

    @Test
    void execute_indexJustBelowLowerBoundary_throwsWithoutMutationOrUiCall()
            throws QuorumException {
        assertInvalidIndex(0);
    }

    @Test
    void execute_indexJustAboveUpperBoundary_throwsWithoutMutationOrUiCall()
            throws QuorumException {
        assertInvalidIndex(4);
    }

    @Test
    void execute_emptyRoster_throwsWithoutUiCall() {
        Roster roster = new Roster();
        DeleteUiSpy ui = new DeleteUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                new DeleteCommand(1).execute(roster, ui));

        assertEquals("There is no participant at index 1.", exception.getMessage());
        assertEquals(0, roster.size());
        assertEquals(0, ui.callCount);
    }

    private void assertSuccessfulDeletion(int index, int expectedListIndex)
            throws QuorumException {
        Roster roster = threeParticipantRoster();
        List<Participant> before = List.copyOf(roster.asList());
        Participant expected = before.get(expectedListIndex);
        DeleteUiSpy ui = new DeleteUiSpy();

        CommandOutcome outcome = new DeleteCommand(index).execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(2, roster.size());
        assertEquals(before.stream().filter(participant -> participant != expected).toList(),
                roster.asList());
        assertEquals(1, ui.callCount);
        assertSame(expected, ui.participant);
        assertEquals(2, ui.total);
    }

    private void assertInvalidIndex(int index) throws QuorumException {
        Roster roster = threeParticipantRoster();
        List<Participant> before = List.copyOf(roster.asList());
        DeleteUiSpy ui = new DeleteUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                new DeleteCommand(index).execute(roster, ui));

        assertEquals("There is no participant at index " + index + ".",
                exception.getMessage());
        assertEquals(before, roster.asList());
        assertEquals(0, ui.callCount);
    }

    private Roster threeParticipantRoster() throws QuorumException {
        Roster roster = new Roster();
        roster.add(new Participant("Alice", SINGAPORE));
        roster.add(new Participant("Bob", SINGAPORE));
        roster.add(new Participant("Carol", SINGAPORE));
        return roster;
    }

    private static final class DeleteUiSpy extends SilentUi {
        private int callCount;
        private Participant participant;
        private int total;

        @Override
        public void showDeleted(Participant shownParticipant, int shownTotal) {
            callCount++;
            participant = shownParticipant;
            total = shownTotal;
        }
    }
}
