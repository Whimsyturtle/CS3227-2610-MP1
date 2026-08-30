package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class ParticipantTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    @Test
    void constructor_oneCharacterNameAndNoTags_preservesValues() throws QuorumException {
        Participant participant = new Participant("A", SINGAPORE);

        assertEquals("A", participant.getName());
        assertEquals(SINGAPORE, participant.getZone());
        assertTrue(participant.getTags().isEmpty());
        assertEquals("A (Asia/Singapore)", participant.toString());
    }

    @Test
    void constructor_nameWithSurroundingAndRepeatedSpaces_storesNormalizedName()
            throws QuorumException {
        Participant participant = new Participant("  Alice  Tan  ", SINGAPORE);

        assertEquals("Alice Tan", participant.getName());
    }

    @Test
    void constructor_validNameZoneAndTags_ordersTagsAlphabetically()
            throws QuorumException {
        Tag work = new Tag("work");
        Tag family = new Tag("family");

        Participant participant = new Participant(
                "Alice Tan", SINGAPORE, List.of(work, family));

        assertEquals(List.of(family, work), new ArrayList<>(participant.getTags()));
        assertTrue(participant.hasTag(new Tag("WORK")));
        assertFalse(participant.hasTag(new Tag("friends")));
    }

    @Test
    void constructor_nullName_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant(null, SINGAPORE));

        assertEquals("Name cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_emptyNameAtInvalidBoundary_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("", SINGAPORE));

        assertEquals("Name cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_whitespaceOnlyName_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant(" \t\n", SINGAPORE));

        assertEquals("Name cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_tabOrAsciiLineBreakInName_throwsSpecificException() {
        List<String> invalidNames = List.of("Alice\tTan", "Alice\rTan", "Alice\nTan");

        for (String invalidName : invalidNames) {
            QuorumException exception = assertThrows(QuorumException.class, () ->
                    new Participant(invalidName, SINGAPORE));
            assertEquals("Name cannot contain tabs or line breaks.", exception.getMessage());
        }
    }

    @Test
    void constructor_unicodeLineSeparatorInName_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("Alice\u2028Tan", SINGAPORE));

        assertEquals("Name cannot contain tabs or line breaks.", exception.getMessage());
    }

    @Test
    void constructor_nullZoneWithValidName_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("Alice", null));

        assertEquals("Timezone cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_nullTagsWithOtherInputsValid_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("Alice", SINGAPORE, null));

        assertEquals("Tags cannot be null.", exception.getMessage());
    }

    @Test
    void constructor_nullTagElementWithOtherInputsValid_throwsSpecificException() {
        List<Tag> tags = new ArrayList<>();
        tags.add(null);

        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("Alice", SINGAPORE, tags));

        assertEquals("Tags cannot contain null.", exception.getMessage());
    }

    @Test
    void constructor_duplicateNormalizedTags_throwsSpecificException()
            throws QuorumException {
        List<Tag> tags = List.of(new Tag("work"), new Tag("WORK"));

        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Participant("Alice", SINGAPORE, tags));

        assertEquals("Tags cannot contain duplicates.", exception.getMessage());
    }

    @Test
    void constructor_mutableTags_defensivelyCopiesUnmodifiableSet()
            throws QuorumException {
        Tag work = new Tag("work");
        Tag family = new Tag("family");
        List<Tag> source = new ArrayList<>();
        source.add(work);

        Participant participant = new Participant("Alice", SINGAPORE, source);
        source.add(family);

        assertEquals(List.of(work), new ArrayList<>(participant.getTags()));
        assertThrows(UnsupportedOperationException.class, () ->
                participant.getTags().add(family));
    }

    @Test
    void withZone_validZone_returnsCopyChangingOnlyZone() throws QuorumException {
        Tag work = new Tag("work");
        Participant original = new Participant("Alice", SINGAPORE, List.of(work));

        Participant edited = original.withZone(TOKYO);

        assertNotSame(original, edited);
        assertEquals("Alice", edited.getName());
        assertEquals(TOKYO, edited.getZone());
        assertEquals(List.of(work), new ArrayList<>(edited.getTags()));
        assertEquals(SINGAPORE, original.getZone());
    }

    @Test
    void withZone_nullZone_throwsSpecificExceptionWithoutChangingParticipant()
            throws QuorumException {
        Participant original = new Participant("Alice", SINGAPORE);

        QuorumException exception = assertThrows(QuorumException.class, () ->
                original.withZone(null));

        assertEquals("Timezone cannot be null.", exception.getMessage());
        assertEquals(SINGAPORE, original.getZone());
    }

    @Test
    void withTag_absentTag_returnsCopyWithTagAndLeavesOriginalUnchanged()
            throws QuorumException {
        Tag family = new Tag("family");
        Tag work = new Tag("work");
        Participant original = new Participant("Alice", SINGAPORE, List.of(work));

        Participant tagged = original.withTag(family);

        assertNotSame(original, tagged);
        assertEquals("Alice", tagged.getName());
        assertEquals(SINGAPORE, tagged.getZone());
        assertEquals(List.of(family, work), new ArrayList<>(tagged.getTags()));
        assertEquals(List.of(work), new ArrayList<>(original.getTags()));
    }

    @Test
    void withTag_equivalentExistingTag_throwsSpecificExceptionWithoutChangingParticipant()
            throws QuorumException {
        Tag work = new Tag("work");
        Participant original = new Participant("Alice", SINGAPORE, List.of(work));

        QuorumException exception = assertThrows(QuorumException.class, () ->
                original.withTag(new Tag("WORK")));

        assertEquals("Alice already has tag WORK.", exception.getMessage());
        assertEquals(List.of(work), new ArrayList<>(original.getTags()));
    }

    @Test
    void withoutTag_presentTag_returnsCopyWithoutTagAndLeavesOriginalUnchanged()
            throws QuorumException {
        Tag family = new Tag("family");
        Tag work = new Tag("work");
        Participant original = new Participant(
                "Alice", SINGAPORE, List.of(family, work));

        Participant untagged = original.withoutTag(work);

        assertNotSame(original, untagged);
        assertEquals(List.of(family), new ArrayList<>(untagged.getTags()));
        assertEquals(List.of(family, work), new ArrayList<>(original.getTags()));
    }

    @Test
    void withoutTag_absentTag_throwsSpecificExceptionWithoutChangingParticipant()
            throws QuorumException {
        Tag family = new Tag("family");
        Participant original = new Participant("Alice", SINGAPORE, List.of(family));

        QuorumException exception = assertThrows(QuorumException.class, () ->
                original.withoutTag(new Tag("work")));

        assertEquals("Alice does not have tag WORK.", exception.getMessage());
        assertEquals(List.of(family), new ArrayList<>(original.getTags()));
    }
}
