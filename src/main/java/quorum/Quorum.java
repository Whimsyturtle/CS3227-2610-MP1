package quorum;

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
            Command command = parser.parse(ui.readCommand());
            if (command.type() == CommandType.BYE) {
                break;
            }
            execute(command);
        }
        ui.showGoodbye();
    }

    private void execute(Command command) {
        try {
            switch (command.type()) {
            case ADD -> executeAdd(command.arguments());
            case LIST -> ui.showRoster(roster.asList());
            default -> ui.showError("I don't know that one. Try: add, list, bye");
            }
        } catch (QuorumException e) {
            ui.showError(e.getMessage());
        }
    }

    private void executeAdd(String arguments) throws QuorumException {
        Participant participant = parser.parseParticipant(arguments);
        roster.add(participant);
        ui.showAdded(participant, roster.size());
    }
}
