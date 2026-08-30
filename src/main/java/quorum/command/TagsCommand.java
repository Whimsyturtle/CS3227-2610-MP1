package quorum.command;

import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Displays the distinct tags currently in use. */
public class TagsCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        view.showTags(roster.getTags());
        return CommandOutcome.CONTINUE;
    }
}
