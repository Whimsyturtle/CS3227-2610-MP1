package quorum.ui;

import java.util.Objects;
import java.util.function.Consumer;

/** Sends text responses to the JavaFX conversation display. */
public class ChatUi extends TextResponseView {
    private final Consumer<String> messageDisplay;

    /** Creates a chat UI that passes each formatted response to the given display. */
    public ChatUi(Consumer<String> messageDisplay) {
        this.messageDisplay = Objects.requireNonNull(messageDisplay);
    }

    @Override
    protected void show(String... lines) {
        messageDisplay.accept(String.join("\n", lines));
    }
}
