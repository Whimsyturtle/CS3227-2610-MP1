package quorum.storage;

import quorum.QuorumException;
import quorum.Roster;

/** Loads and saves a roster without exposing its persistence mechanism. */
public interface RosterStorage {
    /** Loads the stored roster, or an empty roster when no stored roster exists. */
    Roster load() throws QuorumException;

    /** Saves the roster. */
    void save(Roster roster) throws QuorumException;
}
