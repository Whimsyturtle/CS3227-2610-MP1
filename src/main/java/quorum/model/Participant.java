package quorum.model;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import quorum.QuorumException;

/** Represents a named participant and their timezone. */
public class Participant {
    private final String name;
    private final ZoneId zone;
    private final Set<Tag> tags;

    /** Creates a participant with the given name and timezone. */
    public Participant(String name, ZoneId zone) throws QuorumException {
        this(name, zone, Set.of());
    }

    /** Creates a participant with the given name, timezone, and tags. */
    public Participant(String name, ZoneId zone, Collection<Tag> tags)
            throws QuorumException {
        validateName(name);
        this.name = name;
        if (zone == null) {
            throw new QuorumException("Timezone cannot be null.");
        }
        this.zone = zone;

        if (tags == null) {
            throw new QuorumException("Tags cannot be null.");
        }
        TreeSet<Tag> copiedTags = new TreeSet<>();
        for (Tag tag : tags) {
            if (tag == null) {
                throw new QuorumException("Tags cannot contain null.");
            }
            if (!copiedTags.add(tag)) {
                throw new QuorumException("Tags cannot contain duplicates.");
            }
        }
        this.tags = Collections.unmodifiableSet(copiedTags);
    }

    public String getName() {
        return name;
    }

    public ZoneId getZone() {
        return zone;
    }

    /** Returns this participant's alphabetically ordered tags. */
    public Set<Tag> getTags() {
        return tags;
    }

    /** Returns whether this participant has the given tag. */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    /** Returns a copy of this participant with the given timezone. */
    public Participant withZone(ZoneId newZone) throws QuorumException {
        return new Participant(name, newZone, tags);
    }

    /** Returns a copy of this participant with the given tag added. */
    public Participant withTag(Tag tag) throws QuorumException {
        if (hasTag(tag)) {
            throw new QuorumException(name + " already has tag " + tag + ".");
        }

        Set<Tag> updatedTags = new TreeSet<>(tags);
        updatedTags.add(tag);
        return new Participant(name, zone, updatedTags);
    }

    /** Returns a copy of this participant with the given tag removed. */
    public Participant withoutTag(Tag tag) throws QuorumException {
        if (!hasTag(tag)) {
            throw new QuorumException(name + " does not have tag " + tag + ".");
        }

        Set<Tag> updatedTags = new TreeSet<>(tags);
        updatedTags.remove(tag);
        return new Participant(name, zone, updatedTags);
    }

    @Override
    public String toString() {
        return name + " (" + zone.getId() + ")";
    }

    private static void validateName(String name) throws QuorumException {
        if (name == null || name.isBlank()) {
            throw new QuorumException("Name cannot be null or blank.");
        }
        if (name.indexOf('\t') >= 0 || name.indexOf('\r') >= 0 || name.indexOf('\n') >= 0) {
            throw new QuorumException("Name cannot contain tabs or line breaks.");
        }
    }

}
