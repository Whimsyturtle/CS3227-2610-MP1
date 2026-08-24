package quorum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stores the participants currently tracked by the application. */
public class Roster {
    private final List<Participant> participants = new ArrayList<>();

    public void add(Participant participant) {
        participants.add(participant);
    }

    public int size() {
        return participants.size();
    }

    /** Returns a read-only view, so callers cannot mutate the roster behind its back. */
    public List<Participant> asList() {
        return Collections.unmodifiableList(participants);
    }
}
