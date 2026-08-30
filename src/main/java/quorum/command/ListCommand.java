package quorum.command;

import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Displays every participant in the roster. */
public class ListCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        view.showRoster(roster.asList());
        return CommandOutcome.CONTINUE;
    }
}
