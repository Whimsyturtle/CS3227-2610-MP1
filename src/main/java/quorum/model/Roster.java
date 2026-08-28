package quorum.model;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import quorum.QuorumException;

/** Stores the participants tracked by the application. */
public class Roster {
    private final List<Participant> participants = new ArrayList<>();

    /** Adds the given participant to the roster. */
    public void add(Participant participant) {
        participants.add(Objects.requireNonNull(participant, "Participant cannot be null."));
    }

    /**
     * Removes and returns the participant at the given one-based index.
     *
     * @throws QuorumException if the index does not correspond to a participant
     */
    public Participant remove(int index) throws QuorumException {
        return participants.remove(toListIndex(index));
    }

    /**
     * Updates and returns the participant at the given one-based index.
     *
     * @throws QuorumException if the index does not correspond to a participant
     */
    public Participant editZone(int index, ZoneId zone) throws QuorumException {
        int listIndex = toListIndex(index);
        Participant current = participants.get(listIndex);
        Participant edited = current.withZone(zone);
        participants.set(listIndex, edited);
        return edited;
    }

    /** Adds a tag to the participant at the given one-based index. */
    public Participant tag(int index, Tag tag) throws QuorumException {
        int listIndex = toListIndex(index);
        Participant tagged = participants.get(listIndex).withTag(tag);
        participants.set(listIndex, tagged);
        return tagged;
    }

    /** Removes a tag from the participant at the given one-based index. */
    public Participant untag(int index, Tag tag) throws QuorumException {
        int listIndex = toListIndex(index);
        Participant untagged = participants.get(listIndex).withoutTag(tag);
        participants.set(listIndex, untagged);
        return untagged;
    }

    /** Returns whether at least one participant currently has the given tag. */
    public boolean hasTag(Tag tag) {
        return participants.stream().anyMatch(participant -> participant.hasTag(tag));
    }

    /** Returns the distinct tags currently in use, ordered alphabetically. */
    public List<Tag> getTags() {
        return participants.stream()
                .flatMap(participant -> participant.getTags().stream())
                .distinct()
                .sorted()
                .toList();
    }

    /** Returns the number of participants in the roster. */
    public int size() {
        return participants.size();
    }

    /** Returns an unmodifiable view of the roster. */
    public List<Participant> asList() {
        return Collections.unmodifiableList(participants);
    }

    private int toListIndex(int index) throws QuorumException {
        if (index < 1 || index > participants.size()) {
            throw new QuorumException("There is no participant at index " + index + ".");
        }
        return index - 1;
    }
}
