package quorum;

import java.nio.file.Path;

import quorum.command.Command;
import quorum.command.CommandOutcome;
import quorum.model.Roster;
import quorum.storage.FileRosterStorage;
import quorum.storage.RosterStorage;
import quorum.storage.TsvRosterCodec;

/** Coordinates user input, command parsing, and roster updates. */
public class Quorum {
    private static final Path DEFAULT_ROSTER_FILE = Path.of("data", "roster.tsv");

    private final Ui ui;
    private final Parser parser;
    private final RosterStorage storage;

    /** Creates Quorum with its console UI and default local roster file. */
    public Quorum() {
        this(new Ui(), new Parser(),
                new FileRosterStorage(DEFAULT_ROSTER_FILE, new TsvRosterCodec()));
    }

    Quorum(Ui ui, Parser parser, RosterStorage storage) {
        this.ui = ui;
        this.parser = parser;
        this.storage = storage;
    }

    public static void main(String[] args) {
        new Quorum().run();
    }

    void run() {
        try {
            runCommandLoop(storage.load());
        } catch (QuorumException e) {
            ui.showError(e.getMessage());
        }
    }

    private void runCommandLoop(Roster roster) throws QuorumException {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            CommandOutcome outcome;
            try {
                Command command = parser.parse(ui.readCommand());
                outcome = command.execute(roster, ui);
            } catch (QuorumException e) {
                ui.showError(e.getMessage());
                continue;
            }
            storage.save(roster);
            if (outcome == CommandOutcome.EXIT) {
                break;
            }
        }
        ui.showGoodbye();
    }
}
