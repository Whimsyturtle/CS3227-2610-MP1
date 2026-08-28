package quorum.storage;

import quorum.QuorumException;
import quorum.model.Roster;

/** Encodes and decodes a roster. */
public interface RosterCodec {
    /** Encodes the roster into its persistent text representation. */
    String encode(Roster roster);

    /** Decodes a roster from its persistent text representation. */
    Roster decode(String content) throws QuorumException;
}
