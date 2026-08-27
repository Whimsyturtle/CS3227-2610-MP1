package quorum.command;

import quorum.Participant;
import quorum.Roster;
import quorum.Ui;

/** Adds a participant to the roster. */
public class AddCommand implements Command {
    private final Participant participant;

    public AddCommand(Participant participant) {
        this.participant = participant;
    }

    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        roster.add(participant);
        ui.showAdded(participant, roster.size());
        return CommandOutcome.CONTINUE;
    }
}
