package quorum.command;

import quorum.QuorumException;
import quorum.Ui;
import quorum.model.Participant;
import quorum.model.Roster;

/** Edits the time zone of a participant selected by index. */
public class EditCommand implements Command {
    private final EditRequest request;

    public EditCommand(EditRequest request) {
        this.request = request;
    }

    @Override
    public CommandOutcome execute(Roster roster, Ui ui) throws QuorumException {
        Participant participant = roster.editZone(request.index(), request.zone());
        ui.showEdited(participant);
        return CommandOutcome.CONTINUE;
    }
}
