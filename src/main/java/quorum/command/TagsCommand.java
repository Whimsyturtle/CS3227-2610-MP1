package quorum.command;

import quorum.model.Roster;
import quorum.ui.Ui;

/** Displays the distinct tags currently in use. */
public class TagsCommand implements Command {
    @Override
    public CommandOutcome execute(Roster roster, Ui ui) {
        ui.showTags(roster.getTags());
        return CommandOutcome.CONTINUE;
    }
}
