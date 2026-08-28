package quorum.command;

import quorum.Ui;
import quorum.model.Roster;

/** Stops Quorum from accepting further commands. */
public class ByeCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        return CommandOutcome.EXIT;
    }
}
