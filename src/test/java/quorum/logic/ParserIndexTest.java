package quorum.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class ParserIndexTest {
    private final Parser parser = new Parser();

    @Test
    void parseIndex_atLowerBoundary_returnsOne() throws QuorumException {
        assertEquals(1, parser.parseIndex(" 1 "));
    }

    @Test
    void parseIndex_justBelowLowerBoundary_throwsSpecificException() {
        assertIndexError("0", "Index must be at least 1.");
    }

    @Test
    void parseIndex_justAboveLowerBoundary_returnsTwo() throws QuorumException {
        assertEquals(2, parser.parseIndex("2"));
    }

    @Test
    void parseIndex_justBelowUpperBoundary_returnsValue() throws QuorumException {
        assertEquals(Integer.MAX_VALUE - 1, parser.parseIndex("2147483646"));
    }

    @Test
    void parseIndex_atUpperBoundary_returnsValue() throws QuorumException {
        assertEquals(Integer.MAX_VALUE, parser.parseIndex("2147483647"));
    }

    @Test
    void parseIndex_justAboveUpperBoundary_throwsSpecificException() {
        assertIndexError("2147483648", "Index must be a whole number.");
    }

    @Test
    void parseIndex_blankInput_throwsSpecificException() {
        assertIndexError(" \t ", "Missing index.");
    }

    @Test
    void parseIndex_nonIntegralNumber_throwsSpecificException() {
        assertIndexError("1.5", "Index must be a whole number.");
    }

    private void assertIndexError(String arguments, String expectedMessage) {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                parser.parseIndex(arguments));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
