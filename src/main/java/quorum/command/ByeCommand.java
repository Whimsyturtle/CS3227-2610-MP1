package quorum.command;

import quorum.Roster;
import quorum.Ui;

/** Stops Quorum from accepting further commands. */
public class ByeCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        return CommandOutcome.EXIT;
    }
}
