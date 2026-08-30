package quorum.command;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Edits the timezone of a participant selected by index. */
public class EditCommand implements Command {
    private final EditRequest request;

    public EditCommand(EditRequest request) {
        this.request = request;
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) throws QuorumException {
        Participant participant = roster.editZone(request.index(), request.zone());
        view.showEdited(participant);
        return CommandOutcome.CONTINUE;
    }
}
