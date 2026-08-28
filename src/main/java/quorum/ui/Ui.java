package quorum.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import quorum.model.Participant;

/**
 * Owns every read from and write to the console. No other class touches
 * System.in or System.out.
 */
public class Ui {
    private static final String LINE = "-".repeat(50);

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI connected to the process's standard input and output. */
    public Ui() {
        this(System.in, System.out);
    }

    /** Allows application tests to supply isolated input and output streams. */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /** Returns whether another command is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command from the console. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the application welcome message. */
    public void showWelcome() {
        show("QUORUM",
                "Finding the hours when everyone's awake.");
    }

    /** Displays the application goodbye message. */
    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    /** Displays the added participant and resulting roster size. */
    public void showAdded(Participant participant, int total) {
        show("Added: " + participant, "Now tracking " + total + ".");
    }

    /** Displays the deleted participant and resulting roster size. */
    public void showDeleted(Participant participant, int total) {
        show("Deleted: " + participant, "Now tracking " + total + ".");
    }

    /** Displays the participant after an edit. */
    public void showEdited(Participant participant) {
        show("Edited: " + participant);
    }

    /** Displays the participants currently in the roster. */
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

    /** Displays the given error message. */
    public void showError(String message) {
        show(message);
    }

    private void show(String... lines) {
        output.println(LINE);
        for (String line : lines) {
            output.println(" " + line);
        }
        output.println(LINE);
    }
}
