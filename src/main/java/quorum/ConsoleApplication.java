package quorum;

import quorum.command.CommandOutcome;
import quorum.ui.ConsoleUi;

/** Runs Quorum's console-based command loop. */
public class ConsoleApplication {
    private final ConsoleUi ui;

    /** Creates a console application connected to standard input and output. */
    public ConsoleApplication() {
        ui = new ConsoleUi();
    }

    void run() {
        try {
            runCommandLoop(new QuorumEngine());
        } catch (QuorumException e) {
            ui.showError(e.getMessage());
        }
    }

    private void runCommandLoop(QuorumEngine engine) {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            CommandOutcome outcome;
            try {
                outcome = engine.execute(ui.readCommand(), ui);
            } catch (QuorumException e) {
                ui.showError(e.getMessage());
                continue;
            }
            if (outcome == CommandOutcome.EXIT) {
                break;
            }
        }
        ui.showGoodbye();
    }
}
