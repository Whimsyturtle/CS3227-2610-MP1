package quorum;

import java.time.DateTimeException;
import java.time.ZoneId;

public class Quorum {
    private final Ui ui = new Ui();
    private final Roster roster = new Roster();

    public static void main(String[] args) {
        new Quorum().run();
    }

    private void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand().trim();
            if (input.equals("bye")) {
                break;
            } else if (input.startsWith("add ")) {
                add(input.substring("add ".length()));
            } else if (input.equals("list")) {
                ui.showRoster(roster.asList());
            } else {
                ui.showError("I don't know that one. Try: add, bye");
            }
        }
        ui.showGoodbye();
    }

    private void add(String arguments) {
        String[] parts = arguments.split("/tz", 2);
        if (parts.length < 2) {
            ui.showError("I need a timezone. Try: add Alice /tz Asia/Singapore");
            return;
        }
        String name = parts[0].trim();
        String zoneText = parts[1].trim();
        if (name.isEmpty() || zoneText.isEmpty()) {
            ui.showError("Use: add <name> /tz <zone>");
            return;
        }
        try {
            Participant participant = new Participant(name, ZoneId.of(zoneText));
            roster.add(participant);
            ui.showAdded(participant, roster.size());
        } catch (DateTimeException e) {
            ui.showError("I don't recognise the zone \"" + zoneText + "\".");
        }
    }
}