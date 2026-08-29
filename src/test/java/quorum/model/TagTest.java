package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class TagTest {
    @Test
    void constructor_singleCharacterAtMinimumLength_preservesCanonicalValue()
            throws QuorumException {
        Tag tag = new Tag("x");

        assertEquals("X", tag.value());
        assertEquals("X", tag.toString());
    }

    @Test
    void constructor_allowedPunctuationAndMixedCase_normalizesToUppercase()
            throws QuorumException {
        Tag tag = new Tag("cs2103t-group_1!");

        assertEquals("CS2103T-GROUP_1!", tag.value());
    }

    @Test
    void constructor_defaultLocaleIsTurkish_stillUsesLocaleIndependentNormalization()
            throws QuorumException {
        Locale originalLocale = Locale.getDefault();
        try {
            // Turkish has locale-specific casing for "i"; tags must normalize identically
            // regardless of the system's default locale.
            Locale.setDefault(Locale.forLanguageTag("tr"));

            assertEquals("IKI", new Tag("iki").value());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void constructor_nullValue_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Tag(null));

        assertEquals("Tag cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_emptyValueAtInvalidBoundary_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Tag(""));

        assertEquals("Tag cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_whitespaceOnlyValue_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Tag(" \t\n"));

        assertEquals("Tag cannot be null or blank.", exception.getMessage());
    }

    @Test
    void constructor_embeddedUnicodeWhitespace_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Tag("study\u2003group"));

        assertEquals("Tag cannot contain whitespace.", exception.getMessage());
    }

    @Test
    void constructor_comma_throwsSpecificException() {
        QuorumException exception = assertThrows(QuorumException.class, () ->
                new Tag("friends,family"));

        assertEquals("Tag cannot contain commas.", exception.getMessage());
    }

    @Test
    void compareTo_tagsBeforeEqualAndAfter_returnsExpectedOrdering()
            throws QuorumException {
        Tag alpha = new Tag("alpha");
        Tag equivalentAlpha = new Tag("ALPHA");
        Tag beta = new Tag("beta");

        assertTrue(alpha.compareTo(beta) < 0);
        assertEquals(0, alpha.compareTo(equivalentAlpha));
        assertTrue(beta.compareTo(alpha) > 0);
    }

    @Test
    void compareTo_null_throwsSpecificNullPointerException() throws QuorumException {
        Tag tag = new Tag("work");

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                tag.compareTo(null));

        assertEquals("Tag to compare cannot be null.", exception.getMessage());
    }

    @Test
    void equality_equivalentNormalizedValues_haveEqualHashes() throws QuorumException {
        Tag lowerCase = new Tag("work");
        Tag upperCase = new Tag("WORK");

        assertEquals(lowerCase, lowerCase);
        assertEquals(lowerCase, upperCase);
        assertEquals(lowerCase.hashCode(), upperCase.hashCode());
    }

    @Test
    void equality_differentValueOrType_isNotEqual() throws QuorumException {
        Tag tag = new Tag("work");

        assertNotEquals(tag, new Tag("play"));
        assertNotEquals(tag, "WORK");
        assertNotEquals(tag, null);
    }
}
