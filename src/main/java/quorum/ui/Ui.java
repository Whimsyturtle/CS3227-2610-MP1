package quorum.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/** Owns every read from and write to the console. */
public class Ui extends TextResponseView {
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

    @Override
    protected void show(String... lines) {
        output.println(LINE);
        for (String line : lines) {
            output.println(" " + line);
        }
        output.println(LINE);
    }
}
