package quorum;

import quorum.command.Command;
import quorum.command.CommandOutcome;

/** Coordinates user input, command parsing, and roster updates. */
public class Quorum {
    private final Ui ui = new Ui();
    private final Parser parser = new Parser();
    private final Roster roster = new Roster();

    public static void main(String[] args) {
        new Quorum().run();
    }

    private void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            try {
                Command command = parser.parse(ui.readCommand());
                CommandOutcome outcome = command.execute(roster, ui);
                if (outcome == CommandOutcome.EXIT) {
                    break;
                }
            } catch (QuorumException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
    }
}
