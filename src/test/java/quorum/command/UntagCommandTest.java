package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

class UntagCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_indexAtLowerBoundary_removesSharedTagAndContinues()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice", work), participant("Bob", work),
                participant("Carol"));

        assertSuccessfulUntagging(roster, 1, work);
        assertTrue(roster.hasTag(work));
    }

    @Test
    void execute_indexBetweenBoundaries_removesOnlyRequestedTagAndContinues()
            throws QuorumException {
        Tag family = new Tag("family");
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice"), participant("Bob", family, work),
                participant("Carol"));

        assertSuccessfulUntagging(roster, 2, work);

        assertTrue(roster.asList().get(1).hasTag(family));
        assertFalse(roster.hasTag(work));
    }

    @Test
    void execute_indexAtUpperBoundary_removesLastUseOfTagAndContinues()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice"), participant("Bob"),
                participant("Carol", work));

        assertSuccessfulUntagging(roster, 3, work);
        assertFalse(roster.hasTag(work));
    }

    @Test
    void execute_participantLacksTagUsedElsewhere_throwsWithoutMutationOrUiCall()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice");
        Roster roster = rosterOf(alice, participant("Bob", work), participant("Carol"));

        assertMissingTag(roster, alice, work);
    }

    @Test
    void execute_tagUnusedByRoster_throwsWithoutMutationOrUiCall()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice");
        Roster roster = rosterOf(alice, participant("Bob"), participant("Carol"));

        assertMissingTag(roster, alice, work);
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
    void execute_emptyRoster_throwsWithoutUiCall() throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = new Roster();
        UntagUiSpy ui = new UntagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(1, work).execute(roster, ui));

        assertEquals("There is no participant at index 1.", exception.getMessage());
        assertEquals(0, roster.size());
        assertEquals(0, ui.callCount);
    }

    private void assertSuccessfulUntagging(Roster roster, int index, Tag tag)
            throws QuorumException {
        List<Participant> before = List.copyOf(roster.asList());
        Participant original = before.get(index - 1);
        UntagUiSpy ui = new UntagUiSpy();

        CommandOutcome outcome = commandFor(index, tag).execute(roster, ui);

        Participant untagged = roster.asList().get(index - 1);
        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertNotSame(original, untagged);
        assertFalse(untagged.hasTag(tag));
        assertTrue(original.hasTag(tag));
        assertEquals(original.getName(), untagged.getName());
        assertEquals(original.getZone(), untagged.getZone());
        assertEquals(3, roster.size());
        for (int listIndex = 0; listIndex < before.size(); listIndex++) {
            if (listIndex != index - 1) {
                assertSame(before.get(listIndex), roster.asList().get(listIndex));
            }
        }
        assertEquals(1, ui.callCount);
        assertSame(untagged, ui.participant);
        assertSame(tag, ui.tag);
    }

    private void assertMissingTag(Roster roster, Participant participant, Tag tag) {
        List<Participant> before = List.copyOf(roster.asList());
        UntagUiSpy ui = new UntagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(1, tag).execute(roster, ui));

        assertEquals(participant.getName() + " does not have tag " + tag + ".",
                exception.getMessage());
        assertEquals(before, roster.asList());
        assertSame(participant, roster.asList().getFirst());
        assertEquals(0, ui.callCount);
    }

    private void assertInvalidIndex(int index) throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice", work), participant("Bob", work),
                participant("Carol", work));
        List<Participant> before = List.copyOf(roster.asList());
        UntagUiSpy ui = new UntagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(index, work).execute(roster, ui));

        assertEquals("There is no participant at index " + index + ".",
                exception.getMessage());
        assertEquals(before, roster.asList());
        assertEquals(0, ui.callCount);
    }

    private UntagCommand commandFor(int index, Tag tag) {
        return new UntagCommand(new TagRequest(index, tag));
    }

    private Participant participant(String name, Tag... tags) throws QuorumException {
        return new Participant(name, SINGAPORE, List.of(tags));
    }

    private Roster rosterOf(Participant... participants) {
        Roster roster = new Roster();
        for (Participant participant : participants) {
            roster.add(participant);
        }
        return roster;
    }

    private static final class UntagUiSpy extends SilentUi {
        private int callCount;
        private Participant participant;
        private Tag tag;

        @Override
        public void showUntagged(Participant shownParticipant, Tag shownTag) {
            callCount++;
            participant = shownParticipant;
            tag = shownTag;
        }
    }
}
