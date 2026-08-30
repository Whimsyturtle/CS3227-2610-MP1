package quorum.command;

import quorum.model.Participant;
import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Adds a participant to the roster. */
public class AddCommand implements Command {
    private final Participant participant;

    public AddCommand(Participant participant) {
        this.participant = participant;
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        roster.add(participant);
        view.showAdded(participant, roster.size());
        return CommandOutcome.CONTINUE;
    }
}
