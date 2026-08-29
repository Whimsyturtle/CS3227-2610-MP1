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

class TagCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_indexAtLowerBoundary_addsRosterNewTagAndContinues()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice"), participant("Bob"),
                participant("Carol"));

        assertSuccessfulTagging(roster, 1, work, true);
    }

    @Test
    void execute_indexBetweenBoundaries_addsExistingRosterTagAndContinues()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice", work), participant("Bob"),
                participant("Carol"));

        assertSuccessfulTagging(roster, 2, work, false);
    }

    @Test
    void execute_indexAtUpperBoundary_addsExistingRosterTagAndContinues()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice", work), participant("Bob"),
                participant("Carol"));

        assertSuccessfulTagging(roster, 3, work, false);
    }

    @Test
    void execute_participantAlreadyHasTag_throwsWithoutMutationOrUiCall()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Roster roster = rosterOf(alice, participant("Bob"), participant("Carol"));
        List<Participant> before = List.copyOf(roster.asList());
        TagUiSpy ui = new TagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(1, new Tag("WORK")).execute(roster, ui));

        assertEquals("Alice already has tag WORK.", exception.getMessage());
        assertEquals(before, roster.asList());
        assertSame(alice, roster.asList().getFirst());
        assertEquals(0, ui.callCount);
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
        TagUiSpy ui = new TagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(1, work).execute(roster, ui));

        assertEquals("There is no participant at index 1.", exception.getMessage());
        assertEquals(0, roster.size());
        assertEquals(0, ui.callCount);
    }

    private void assertSuccessfulTagging(Roster roster, int index, Tag tag,
                                         boolean expectedNewTag)
            throws QuorumException {
        List<Participant> before = List.copyOf(roster.asList());
        Participant original = before.get(index - 1);
        TagUiSpy ui = new TagUiSpy();

        CommandOutcome outcome = commandFor(index, tag).execute(roster, ui);

        Participant tagged = roster.asList().get(index - 1);
        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertNotSame(original, tagged);
        assertTrue(tagged.hasTag(tag));
        assertFalse(original.hasTag(tag));
        assertEquals(original.getName(), tagged.getName());
        assertEquals(original.getZone(), tagged.getZone());
        assertEquals(3, roster.size());
        for (int listIndex = 0; listIndex < before.size(); listIndex++) {
            if (listIndex != index - 1) {
                assertSame(before.get(listIndex), roster.asList().get(listIndex));
            }
        }
        assertEquals(1, ui.callCount);
        assertSame(tagged, ui.participant);
        assertSame(tag, ui.tag);
        assertEquals(expectedNewTag, ui.isNewTag);
    }

    private void assertInvalidIndex(int index) throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice"), participant("Bob"),
                participant("Carol"));
        List<Participant> before = List.copyOf(roster.asList());
        TagUiSpy ui = new TagUiSpy();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                commandFor(index, work).execute(roster, ui));

        assertEquals("There is no participant at index " + index + ".",
                exception.getMessage());
        assertEquals(before, roster.asList());
        assertEquals(0, ui.callCount);
    }

    private TagCommand commandFor(int index, Tag tag) {
        return new TagCommand(new TagRequest(index, tag));
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

    private static final class TagUiSpy extends SilentUi {
        private int callCount;
        private Participant participant;
        private Tag tag;
        private boolean isNewTag;

        @Override
        public void showTagged(Participant shownParticipant, Tag shownTag,
                               boolean shownIsNewTag) {
            callCount++;
            participant = shownParticipant;
            tag = shownTag;
            isNewTag = shownIsNewTag;
        }
    }
}
