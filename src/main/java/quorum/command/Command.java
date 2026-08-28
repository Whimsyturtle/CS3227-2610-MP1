package quorum.command;

import quorum.QuorumException;
import quorum.Ui;
import quorum.model.Roster;

/** Represents an instruction that can be executed by Quorum. */
public interface Command {
    /** Executes this command and returns whether the application should continue. */
    CommandOutcome execute(Roster roster, Ui ui) throws QuorumException;
}
