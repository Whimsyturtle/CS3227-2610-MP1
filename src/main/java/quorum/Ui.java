package quorum;

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

    public void showEcho(String input) {
        show(input);
    }

    private void show(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(" " + line);
        }
        System.out.println(LINE);
    }
}