package quorum.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

class TsvRosterCodecTest {
    private static final String HEADER = "name\tzoneId\ttags";

    private final RosterCodec codec = new TsvRosterCodec();

    @Test
    void encode_emptyRoster_returnsHeaderWithTerminatingLineFeed() {
        assertEquals(HEADER + "\n", codec.encode(new Roster()));
    }

    @Test
    void encode_participantsWithZeroOneAndTwoTags_returnsCanonicalTsvInRosterOrder()
            throws QuorumException {
        Roster roster = new Roster();
        roster.add(new Participant("A", ZoneId.of("Z")));
        roster.add(new Participant(
                "B\u00e9atrice, Jr.", ZoneId.of("Europe/Paris"), List.of(new Tag("work"))));
        roster.add(new Participant(
                "Carol Tan", ZoneId.of("Asia/Singapore"),
                List.of(new Tag("zebra"), new Tag("alpha"))));

        String content = codec.encode(roster);

        assertEquals(HEADER + "\n"
                + "A\tZ\t\n"
                + "B\u00e9atrice, Jr.\tEurope/Paris\tWORK\n"
                + "Carol Tan\tAsia/Singapore\tALPHA,ZEBRA\n", content);
    }

    @Test
    void encode_nullRoster_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> codec.encode(null));
    }

    @Test
    void decode_headerOnly_returnsEmptyRoster() throws QuorumException {
        Roster roster = codec.decode(HEADER);

        assertEquals(0, roster.size());
        assertTrue(roster.asList().isEmpty());
    }

    @Test
    void decode_validRowsWithBomBlankLineAndCrLf_restoresCanonicalParticipants()
            throws QuorumException {
        String content = "\uFEFF" + HEADER + "\r\n"
                + "A\tZ\t\r\n"
                + "\r\n"
                + "B\u00e9atrice, Jr.\tEurope/Paris\twork\r\n"
                + "Carol Tan\tAsia/Singapore\tzebra,alpha";

        Roster roster = codec.decode(content);

        assertEquals(3, roster.size());
        assertParticipant(roster.asList().get(0), "A", "Z");
        assertParticipant(roster.asList().get(1),
                "B\u00e9atrice, Jr.", "Europe/Paris", "WORK");
        assertParticipant(roster.asList().get(2),
                "Carol Tan", "Asia/Singapore", "ALPHA", "ZEBRA");
    }

    @Test
    void decode_nullContent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> codec.decode(null));
    }

    @Test
    void decode_emptyContent_throwsSpecificHeaderException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(""));

        assertEquals("Expected header \"name<TAB>zoneId<TAB>tags\" on line 1.",
                exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void decode_twoLeadingByteOrderMarks_throwsSpecificHeaderException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode("\uFEFF\uFEFF" + HEADER));

        assertEquals("Expected header \"name<TAB>zoneId<TAB>tags\" on line 1.",
                exception.getMessage());
    }

    @Test
    void decode_fieldCountJustBelowRequiredCount_throwsSpecificFieldException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\tAsia/Singapore"));

        assertEquals("Expected exactly three tab-separated fields on line 2.",
                exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void decode_fieldCountJustAboveRequiredCount_throwsSpecificFieldException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\tAsia/Singapore\tWORK\textra"));

        assertEquals("Expected exactly three tab-separated fields on line 2.",
                exception.getMessage());
    }

    @Test
    void decode_emptyNameWithOtherFieldsValid_wrapsSpecificParticipantException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\n\tAsia/Singapore\tWORK"));

        assertEquals("Invalid participant on line 2: Name cannot be null or blank.",
                exception.getMessage());
        QuorumException cause = assertInstanceOf(QuorumException.class, exception.getCause());
        assertEquals("Name cannot be null or blank.", cause.getMessage());
    }

    @Test
    void decode_emptyTimezoneWithOtherFieldsValid_throwsSpecificMissingTimezoneException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\t\tWORK"));

        assertEquals("Missing timezone on line 2.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void decode_invalidTimezoneAfterBlankLine_wrapsSpecificTimezoneException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\n\nAlice\tNot/AZone\tWORK"));

        assertEquals("Invalid timezone \"Not/AZone\" on line 3.", exception.getMessage());
        assertInstanceOf(DateTimeException.class, exception.getCause());
    }

    @Test
    void decode_trailingTagDelimiter_createsBlankTagAndWrapsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\tAsia/Singapore\tWORK,"));

        assertEquals("Invalid participant on line 2: Tag cannot be null or blank.",
                exception.getMessage());
        QuorumException cause = assertInstanceOf(QuorumException.class, exception.getCause());
        assertEquals("Tag cannot be null or blank.", cause.getMessage());
    }

    @Test
    void decode_whitespaceInTagWithOtherFieldsValid_wrapsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\tAsia/Singapore\tSTUDY GROUP"));

        assertEquals("Invalid participant on line 2: Tag cannot contain whitespace.",
                exception.getMessage());
        QuorumException cause = assertInstanceOf(QuorumException.class, exception.getCause());
        assertEquals("Tag cannot contain whitespace.", cause.getMessage());
    }

    @Test
    void decode_duplicateNormalizedTagsWithOtherFieldsValid_wrapsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER + "\nAlice\tAsia/Singapore\twork,WORK"));

        assertEquals("Invalid participant on line 2: Tags cannot contain duplicates.",
                exception.getMessage());
        QuorumException cause = assertInstanceOf(QuorumException.class, exception.getCause());
        assertEquals("Tags cannot contain duplicates.", cause.getMessage());
    }

    @Test
    void decode_duplicateNormalizedNames_wrapsSpecificExceptionWithSecondLineNumber() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                codec.decode(HEADER
                        + "\nAlice Tan\tAsia/Singapore\t"
                        + "\nALICE  TAN\tEurope/London\t"));

        assertEquals("Invalid participant on line 3: Alice Tan is already in the roster.",
                exception.getMessage());
        QuorumException cause = assertInstanceOf(QuorumException.class, exception.getCause());
        assertEquals("Alice Tan is already in the roster.", cause.getMessage());
    }

    private void assertParticipant(
            Participant participant, String name, String zoneId, String... tags) {
        assertEquals(name, participant.getName());
        assertEquals(ZoneId.of(zoneId), participant.getZone());
        assertEquals(List.of(tags), participant.getTags().stream()
                .map(Tag::value)
                .toList());
    }
}
