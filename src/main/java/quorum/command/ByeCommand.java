package quorum.command;

import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Stops Quorum from accepting further commands. */
public class ByeCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        return CommandOutcome.EXIT;
    }
}
