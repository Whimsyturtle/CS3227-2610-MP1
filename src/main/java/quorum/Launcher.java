package quorum;

import java.util.Arrays;

import javafx.application.Application;
import quorum.ui.ChatApplication;

/** Selects the graphical interface by default or the console with {@code --cli}. */
public final class Launcher {
    private static final String CONSOLE_OPTION = "--cli";

    private Launcher() {

    }

    /** Launches Quorum using the interface requested by the command-line arguments. */
    public static void main(String[] args) {
        if (Arrays.asList(args).contains(CONSOLE_OPTION)) {
            new ConsoleApplication().run();
            return;
        }
        Application.launch(ChatApplication.class, args);
    }
}
