package quorum.command;

import quorum.Roster;
import quorum.Ui;

/** Displays every participant in the roster. */
public class ListCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        ui.showRoster(roster.asList());
        return CommandOutcome.CONTINUE;
    }
}
