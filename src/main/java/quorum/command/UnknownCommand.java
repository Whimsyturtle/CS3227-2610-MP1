package quorum.command;

import quorum.Ui;
import quorum.model.Roster;

/** Handles input whose command keyword is not supported. */
public class UnknownCommand implements Command {
    private static final String MESSAGE =
            "I don't know that one. Try: add, delete, edit, list, bye";

    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        ui.showError(MESSAGE);
        return CommandOutcome.CONTINUE;
    }
}
