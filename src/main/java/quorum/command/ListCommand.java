package quorum.command;

import quorum.Ui;
import quorum.model.Roster;

/** Displays every participant in the roster. */
public class ListCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        ui.showRoster(roster.asList());
        return CommandOutcome.CONTINUE;
    }
}
