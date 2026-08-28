package quorum.command;

import quorum.QuorumException;
import quorum.model.Roster;
import quorum.ui.Ui;

/** Represents an instruction that can be executed by Quorum. */
public interface Command {
    /** Executes this command and returns whether the application should continue. */
    CommandOutcome execute(Roster roster, Ui ui) throws QuorumException;
}
