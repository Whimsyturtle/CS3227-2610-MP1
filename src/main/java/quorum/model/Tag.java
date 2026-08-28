package quorum.model;

import java.util.Locale;
import java.util.Objects;

import quorum.QuorumException;

/** A normalized tag used to group participants. */
public final class Tag implements Comparable<Tag> {
    private final String value;

    /** Creates a tag by validating and normalizing the given value. */
    public Tag(String value) throws QuorumException {
        if (value == null || value.isBlank()) {
            throw new QuorumException("Tag cannot be null or blank.");
        }
        if (value.codePoints().anyMatch(Character::isWhitespace)) {
            throw new QuorumException("Tag cannot contain whitespace.");
        }
        if (value.indexOf(',') >= 0) {
            throw new QuorumException("Tag cannot contain commas.");
        }
        this.value = value.toUpperCase(Locale.ROOT);
    }

    public String value() {
        return value;
    }

    @Override
    public int compareTo(Tag other) {
        Objects.requireNonNull(other, "Tag to compare cannot be null.");
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Tag tag)) {
            return false;
        }
        return value.equals(tag.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
