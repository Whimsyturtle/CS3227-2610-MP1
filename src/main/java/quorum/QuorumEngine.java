package quorum;

import java.nio.file.Path;
import java.util.Objects;

import quorum.command.Command;
import quorum.command.CommandOutcome;
import quorum.model.Roster;
import quorum.storage.FileRosterStorage;
import quorum.storage.RosterStorage;
import quorum.storage.TsvRosterCodec;
import quorum.ui.ResponseView;

/** Loads application state and executes one submitted command at a time. */
public class QuorumEngine {
    private static final Path DEFAULT_ROSTER_FILE = Path.of("data", "roster.tsv");

    private final Parser parser;
    private final RosterStorage storage;
    private final Roster roster;

    /** Creates an engine using the default parser and local roster file. */
    public QuorumEngine() throws QuorumException {
        this(new Parser(), createDefaultStorage());
    }

    QuorumEngine(Parser parser, RosterStorage storage) throws QuorumException {
        this.parser = Objects.requireNonNull(parser);
        this.storage = Objects.requireNonNull(storage);
        roster = storage.load();
    }

    /**
     * Parses and executes one command, then persists the resulting roster state.
     *
     * @return whether the frontend should continue accepting commands
     * @throws QuorumException if the command cannot be parsed or executed, or the resulting
     *                         roster state cannot be persisted
     */
    public CommandOutcome execute(String input, ResponseView view) throws QuorumException {
        Command command = parser.parse(input);
        CommandOutcome outcome = command.execute(roster, view);
        storage.save(roster);
        return outcome;
    }

    private static RosterStorage createDefaultStorage() {
        return new FileRosterStorage(DEFAULT_ROSTER_FILE, new TsvRosterCodec());
    }
}
