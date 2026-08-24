package quorum;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Owns every read from and write to the console. No other class touches
 * System.in or System.out.
 */
public class Ui {
    private static final String LINE = "-".repeat(50);

    private final Scanner scanner = new Scanner(System.in);

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showWelcome() {
        show("QUORUM",
                "Finding the hours when everyone's awake.");
    }

    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    public void showAdded(Participant participant, int total) {
        show("Added: " + participant, "Now tracking " + total + ".");
    }

    public void showRoster(List<Participant> participants) {
        if (participants.isEmpty()) {
            show("Nobody here yet.");
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add("Who's in the roster:");
        for (int i = 0; i < participants.size(); i++) {
            lines.add("  " + (i + 1) + ". " + participants.get(i));
        }
        show(lines.toArray(new String[0]));
    }

    public void showError(String message) {
        show(message);
    }

    private void show(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(" " + line);
        }
        System.out.println(LINE);
    }
}
