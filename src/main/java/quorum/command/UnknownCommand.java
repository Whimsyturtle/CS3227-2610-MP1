package quorum.command;

import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Handles input whose command keyword is not supported. */
public class UnknownCommand implements Command {
    private static final String MESSAGE =
            "I don't know that one. Try: add, delete, edit, tag, untag, tags, "
                    + "meeting, list, zones, bye";

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        view.showError(MESSAGE);
        return CommandOutcome.CONTINUE;
    }
}
