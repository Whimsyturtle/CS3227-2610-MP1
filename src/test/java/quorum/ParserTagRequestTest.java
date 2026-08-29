package quorum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import quorum.command.TagRequest;
import quorum.model.Tag;

class ParserTagRequestTest {
    private final Parser parser = new Parser();

    @Test
    void parseTagRequest_tagLengthAtMinimum_returnsNormalizedRequest()
            throws QuorumException {
        TagRequest request = parser.parseTagRequest("1 x");

        assertEquals(1, request.index());
        assertEquals(new Tag("X"), request.tag());
    }

    @Test
    void parseTagRequest_tagLengthJustAboveMinimumAndFlexibleWhitespace_returnsRequest()
            throws QuorumException {
        TagRequest request = parser.parseTagRequest("  2\tqA  ");

        assertEquals(2, request.index());
        assertEquals(new Tag("QA"), request.tag());
    }

    @Test
    void parseTagRequest_missingIndex_throwsSpecificException() {
        assertTagRequestError(" \t ", "Missing index.");
    }

    @Test
    void parseTagRequest_validIndexButMissingTag_throwsSpecificException() {
        assertTagRequestError("1", "Missing tag.");
    }

    @Test
    void parseTagRequest_invalidIndexWithValidTag_throwsSpecificIndexError() {
        assertTagRequestError("0 friends", "Index must be at least 1.");
    }

    @Test
    void parseTagRequest_whitespaceWithinTag_throwsSpecificTagError() {
        assertTagRequestError("1 close friends", "Tag cannot contain whitespace.");
    }

    @Test
    void parseTagRequest_commaWithinTag_throwsSpecificTagError() {
        assertTagRequestError("1 friends,family", "Tag cannot contain commas.");
    }

    @Test
    void parseTagRequest_invalidIndexAndTag_reportsIndexErrorFirst() {
        assertTagRequestError("zero friends,family", "Index must be a whole number.");
    }

    private void assertTagRequestError(String arguments, String expectedMessage) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseTagRequest(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
