package quorum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stores the participants tracked by the application. */
public class Roster {
    private final List<Participant> participants = new ArrayList<>();

    /** Adds the given participant to the roster. */
    public void add(Participant participant) {
        participants.add(participant);
    }

    /**
     * Removes and returns the participant at the given one-based index.
     *
     * @throws QuorumException if the index does not correspond to a participant
     */
    public Participant remove(int index) throws QuorumException {
        if (index < 1 || index > participants.size()) {
            throw new QuorumException("There is no participant at index " + index + ".");
        }
        return participants.remove(index - 1);
    }

    /** Returns the number of participants in the roster. */
    public int size() {
        return participants.size();
    }

    /** Returns an unmodifiable view of the roster. */
    public List<Participant> asList() {
        return Collections.unmodifiableList(participants);
    }
}
