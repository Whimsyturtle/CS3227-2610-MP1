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

    /** Returns the number of participants in the roster. */
    public int size() {
        return participants.size();
    }

    /** Returns an unmodifiable view of the roster. */
    public List<Participant> asList() {
        return Collections.unmodifiableList(participants);
    }
}
