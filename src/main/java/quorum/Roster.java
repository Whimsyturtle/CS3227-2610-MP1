package quorum;

import java.util.ArrayList;
import java.util.List;

public class Roster {
    private final List<Participant> participants = new ArrayList<>();

    public void add(Participant participant) {
        participants.add(participant);
    }

    public int size() {
        return participants.size();
    }
}