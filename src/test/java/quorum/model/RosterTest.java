package quorum.model;

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

class RosterTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    @Test
    void newRoster_hasZeroSizeAndNoParticipantsOrTags() throws QuorumException {
        Roster roster = new Roster();

        assertEquals(0, roster.size());
        assertTrue(roster.asList().isEmpty());
        assertTrue(roster.getTags().isEmpty());
        assertTrue(roster.getParticipantsWithTag(new Tag("work")).isEmpty());
        assertFalse(roster.hasTag(new Tag("work")));
    }

    @Test
    void add_validParticipants_appendsInEncounterOrder() throws QuorumException {
        Roster roster = new Roster();
        Participant alice = participant("Alice");
        Participant bob = participant("Bob");

        roster.add(alice);
        roster.add(bob);

        assertEquals(2, roster.size());
        assertEquals(List.of(alice, bob), roster.asList());
    }

    @Test
    void add_nullParticipant_throwsSpecificExceptionWithoutChangingRoster() {
        Roster roster = new Roster();

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                roster.add(null));

        assertEquals("Participant cannot be null.", exception.getMessage());
        assertEquals(0, roster.size());
    }

    @Test
    void asList_returnedListIsUnmodifiableLiveView() throws QuorumException {
        Roster roster = new Roster();
        Participant alice = participant("Alice");
        Participant bob = participant("Bob");
        roster.add(alice);
        List<Participant> view = roster.asList();

        assertThrows(UnsupportedOperationException.class, () -> view.add(bob));

        roster.add(bob);
        assertEquals(List.of(alice, bob), view);
    }

    @Test
    void remove_indexJustBelowLowerBoundary_throwsSpecificExceptionWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.remove(0));

        assertEquals("There is no participant at index 0.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void remove_indexAtLowerBoundary_removesAndReturnsFirstParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant alice = roster.asList().get(0);
        Participant bob = roster.asList().get(1);

        Participant removed = roster.remove(1);

        assertSame(alice, removed);
        assertEquals(List.of(bob), roster.asList());
        assertEquals(1, roster.size());
    }

    @Test
    void remove_indexAtUpperBoundary_removesAndReturnsLastParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant alice = roster.asList().get(0);
        Participant bob = roster.asList().get(1);

        Participant removed = roster.remove(2);

        assertSame(bob, removed);
        assertEquals(List.of(alice), roster.asList());
    }

    @Test
    void remove_indexJustAboveUpperBoundary_throwsSpecificExceptionWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.remove(3));

        assertEquals("There is no participant at index 3.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void remove_firstIndexFromEmptyRoster_throwsSpecificException() {
        Roster roster = new Roster();

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.remove(1));

        assertEquals("There is no participant at index 1.", exception.getMessage());
    }

    @Test
    void editZone_indexAtLowerBoundary_replacesAndReturnsFirstParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant originalAlice = roster.asList().get(0);
        Participant bob = roster.asList().get(1);

        Participant editedAlice = roster.editZone(1, TOKYO);

        assertNotSame(originalAlice, editedAlice);
        assertSame(editedAlice, roster.asList().get(0));
        assertSame(bob, roster.asList().get(1));
        assertEquals(TOKYO, editedAlice.getZone());
        assertEquals(SINGAPORE, originalAlice.getZone());
        assertEquals(2, roster.size());
    }

    @Test
    void editZone_indexAtUpperBoundary_replacesAndReturnsLastParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant alice = roster.asList().get(0);
        Participant originalBob = roster.asList().get(1);

        Participant editedBob = roster.editZone(2, TOKYO);

        assertNotSame(originalBob, editedBob);
        assertSame(alice, roster.asList().get(0));
        assertSame(editedBob, roster.asList().get(1));
        assertEquals(TOKYO, editedBob.getZone());
        assertEquals(SINGAPORE, originalBob.getZone());
        assertEquals(2, roster.size());
    }

    @Test
    void editZone_indexJustBelowLowerBoundary_throwsWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.editZone(0, TOKYO));

        assertEquals("There is no participant at index 0.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void editZone_indexJustAboveUpperBoundary_throwsWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.editZone(3, TOKYO));

        assertEquals("There is no participant at index 3.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void editZone_nullZoneWithValidIndex_throwsWithoutReplacingParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant original = roster.asList().get(0);

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.editZone(1, null));

        assertEquals("Timezone cannot be null.", exception.getMessage());
        assertSame(original, roster.asList().get(0));
    }

    @Test
    void tag_absentTagAtLowerBoundary_replacesOnlyFirstParticipant()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        Participant originalAlice = roster.asList().get(0);
        Participant bob = roster.asList().get(1);
        Tag work = new Tag("work");

        Participant tagged = roster.tag(1, work);

        assertNotSame(originalAlice, tagged);
        assertSame(tagged, roster.asList().get(0));
        assertSame(bob, roster.asList().get(1));
        assertTrue(tagged.hasTag(work));
        assertFalse(originalAlice.hasTag(work));
    }

    @Test
    void tag_tagUsedElsewhereAtUpperBoundary_addsTagToLastParticipant()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Participant bob = participant("Bob");
        Roster roster = rosterOf(alice, bob);

        Participant tagged = roster.tag(2, work);

        assertEquals(List.of(work), List.copyOf(tagged.getTags()));
        assertSame(tagged, roster.asList().get(1));
        assertEquals(List.of(alice, tagged), roster.getParticipantsWithTag(work));
    }

    @Test
    void tag_indexJustBelowLowerBoundary_throwsWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());
        Tag work = new Tag("work");

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.tag(0, work));

        assertEquals("There is no participant at index 0.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void tag_indexJustAboveUpperBoundary_throwsWithoutMutation()
            throws QuorumException {
        Roster roster = rosterWithTwoParticipants();
        List<Participant> before = List.copyOf(roster.asList());
        Tag work = new Tag("work");

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.tag(3, work));

        assertEquals("There is no participant at index 3.", exception.getMessage());
        assertEquals(before, roster.asList());
    }

    @Test
    void tag_existingParticipantTag_throwsSpecificExceptionWithoutMutation()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Roster roster = rosterOf(alice);

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.tag(1, new Tag("WORK")));

        assertEquals("Alice already has tag WORK.", exception.getMessage());
        assertSame(alice, roster.asList().get(0));
    }

    @Test
    void untag_presentTagAtLowerBoundary_replacesAndReturnsFirstParticipant()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant originalAlice = participant("Alice", work);
        Participant bob = participant("Bob", work);
        Roster roster = rosterOf(originalAlice, bob);

        Participant untaggedAlice = roster.untag(1, work);

        assertNotSame(originalAlice, untaggedAlice);
        assertSame(untaggedAlice, roster.asList().get(0));
        assertSame(bob, roster.asList().get(1));
        assertFalse(untaggedAlice.hasTag(work));
        assertTrue(originalAlice.hasTag(work));
        assertTrue(roster.hasTag(work));
    }

    @Test
    void untag_presentTagAtUpperBoundary_replacesAndReturnsLastParticipant()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Participant originalBob = participant("Bob", work);
        Roster roster = rosterOf(alice, originalBob);

        Participant untaggedBob = roster.untag(2, work);

        assertNotSame(originalBob, untaggedBob);
        assertSame(alice, roster.asList().get(0));
        assertSame(untaggedBob, roster.asList().get(1));
        assertFalse(untaggedBob.hasTag(work));
        assertTrue(originalBob.hasTag(work));
        assertTrue(roster.hasTag(work));
    }

    @Test
    void untag_indexJustBelowLowerBoundary_throwsWithoutMutation()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Roster roster = rosterOf(alice, participant("Bob"));

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.untag(0, work));

        assertEquals("There is no participant at index 0.", exception.getMessage());
        assertSame(alice, roster.asList().get(0));
    }

    @Test
    void untag_indexJustAboveUpperBoundary_throwsWithoutMutation()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Roster roster = rosterOf(alice, participant("Bob"));

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.untag(3, work));

        assertEquals("There is no participant at index 3.", exception.getMessage());
        assertSame(alice, roster.asList().get(0));
    }

    @Test
    void untag_absentParticipantTag_throwsSpecificExceptionWithoutMutation()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice");
        Roster roster = rosterOf(alice);

        QuorumException exception = assertThrows(QuorumException.class, () ->
                roster.untag(1, work));

        assertEquals("Alice does not have tag WORK.", exception.getMessage());
        assertSame(alice, roster.asList().get(0));
    }

    @Test
    void hasTag_sharedTagTracksWhetherAtLeastOneParticipantStillUsesIt()
            throws QuorumException {
        Tag work = new Tag("work");
        Roster roster = rosterOf(participant("Alice", work), participant("Bob", work));

        assertTrue(roster.hasTag(work));
        roster.untag(1, work);
        assertTrue(roster.hasTag(work));
        roster.untag(2, work);
        assertFalse(roster.hasTag(work));
    }

    @Test
    void getTags_multipleParticipants_returnsDistinctAlphabeticalUnmodifiableList()
            throws QuorumException {
        Tag alpha = new Tag("alpha");
        Tag beta = new Tag("beta");
        Tag zebra = new Tag("zebra");
        Roster roster = rosterOf(
                participant("Alice", zebra, alpha),
                participant("Bob", beta, alpha));

        List<Tag> tags = roster.getTags();

        assertEquals(List.of(alpha, beta, zebra), tags);
        assertThrows(UnsupportedOperationException.class, () -> tags.add(new Tag("work")));
    }

    @Test
    void getParticipantsWithTag_mixedMembership_returnsRosterOrderAndUnmodifiableList()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Participant bob = participant("Bob");
        Participant carol = participant("Carol", work);
        Roster roster = rosterOf(alice, bob, carol);

        List<Participant> participants = roster.getParticipantsWithTag(work);

        assertEquals(List.of(alice, carol), participants);
        assertThrows(UnsupportedOperationException.class, participants::clear);
    }

    @Test
    void getParticipantsWithTag_noMatches_returnsEmptyList() throws QuorumException {
        Roster roster = rosterOf(participant("Alice", new Tag("family")));

        assertTrue(roster.getParticipantsWithTag(new Tag("work")).isEmpty());
    }

    private Participant participant(String name, Tag... tags) throws QuorumException {
        return new Participant(name, SINGAPORE, List.of(tags));
    }

    private Roster rosterWithTwoParticipants() throws QuorumException {
        return rosterOf(participant("Alice"), participant("Bob"));
    }

    private Roster rosterOf(Participant... participants) {
        Roster roster = new Roster();
        for (Participant participant : participants) {
            roster.add(participant);
        }
        return roster;
    }
}
