package quorum.command;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;
import quorum.ui.ResponseView;

/** Removes a tag from a participant selected by index. */
public class UntagCommand implements Command {
    private final TagRequest request;

    public UntagCommand(TagRequest request) {
        this.request = request;
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) throws QuorumException {
        Tag tag = request.tag();
        Participant participant = roster.untag(request.index(), tag);
        view.showUntagged(participant, tag);
        return CommandOutcome.CONTINUE;
    }
}
