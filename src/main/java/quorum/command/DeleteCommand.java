package quorum.command;

import quorum.Participant;
import quorum.QuorumException;
import quorum.Roster;
import quorum.Ui;

/** Deletes a participant from the roster by index. */
public class DeleteCommand implements Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandOutcome execute(Roster roster, Ui ui) throws QuorumException {
        Participant participant = roster.remove(index);
        ui.showDeleted(participant, roster.size());
        return CommandOutcome.CONTINUE;
    }
}
