package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

class EditCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");
    private static final ZoneId LONDON = ZoneId.of("Europe/London");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    @Test
    void execute_indexAtLowerBoundary_editsFirstParticipantAndContinues()
            throws QuorumException {
        assertSuccessfulEdit(1, TOKYO);
    }

    @Test
    void execute_indexBetweenBoundaries_acceptsUnchangedTimezoneAndContinues()
            throws QuorumException {
        assertSuccessfulEdit(2, LONDON);
    }

    @Test
    void execute_indexAtUpperBoundary_editsLastParticipantAndContinues()
            throws QuorumException {
        assertSuccessfulEdit(3, TOKYO);
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
        EditUiSpy ui = new EditUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(1, TOKYO).execute(roster, ui));

        assertEquals("There is no participant at index 1.", exception.getMessage());
        assertEquals(0, roster.size());
        assertEquals(0, ui.callCount);
    }

    private void assertSuccessfulEdit(int index, ZoneId newZone)
            throws QuorumException {
        Roster roster = threeParticipantRoster();
        List<Participant> before = List.copyOf(roster.asList());
        Participant original = before.get(index - 1);
        EditUiSpy ui = new EditUiSpy();

        CommandOutcome outcome = commandFor(index, newZone).execute(roster, ui);

        Participant edited = roster.asList().get(index - 1);
        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertNotSame(original, edited);
        assertEquals(original.getName(), edited.getName());
        assertEquals(newZone, edited.getZone());
        assertEquals(original.getTags(), edited.getTags());
        assertEquals(3, roster.size());
        for (int listIndex = 0; listIndex < before.size(); listIndex++) {
            if (listIndex != index - 1) {
                assertSame(before.get(listIndex), roster.asList().get(listIndex));
            }
        }
        assertEquals(1, ui.callCount);
        assertSame(edited, ui.participant);
    }

    private void assertInvalidIndex(int index) throws QuorumException {
        Roster roster = threeParticipantRoster();
        List<Participant> before = List.copyOf(roster.asList());
        EditUiSpy ui = new EditUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(index, TOKYO).execute(roster, ui));

        assertEquals("There is no participant at index " + index + ".",
                exception.getMessage());
        assertEquals(before, roster.asList());
        assertEquals(0, ui.callCount);
    }

    private EditCommand commandFor(int index, ZoneId zone) {
        return new EditCommand(new EditRequest(index, zone));
    }

    private Roster threeParticipantRoster() throws QuorumException {
        Roster roster = new Roster();
        roster.add(new Participant("Alice", SINGAPORE, List.of(new Tag("work"))));
        roster.add(new Participant("Bob", LONDON));
        roster.add(new Participant("Carol", NEW_YORK));
        return roster;
    }

    private static final class EditUiSpy extends SilentUi {
        private int callCount;
        private Participant participant;

        @Override
        public void showEdited(Participant shownParticipant) {
            callCount++;
            participant = shownParticipant;
        }
    }
}
