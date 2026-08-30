package quorum.command;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;
import quorum.ui.ResponseView;

/** Adds a tag to a participant selected by index. */
public class TagCommand implements Command {
    private final TagRequest request;

    public TagCommand(TagRequest request) {
        this.request = request;
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) throws QuorumException {
        Tag tag = request.tag();
        boolean isNewTag = !roster.hasTag(tag);
        Participant participant = roster.tag(request.index(), tag);
        view.showTagged(participant, tag, isNewTag);
        return CommandOutcome.CONTINUE;
    }
}
