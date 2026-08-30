package quorum.command;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Deletes a participant from the roster by index. */
public class DeleteCommand implements Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) throws QuorumException {
        Participant participant = roster.remove(index);
        view.showDeleted(participant, roster.size());
        return CommandOutcome.CONTINUE;
    }
}
