package quorum.ui;

import java.net.URL;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quorum.QuorumEngine;
import quorum.QuorumException;
import quorum.command.CommandOutcome;

/** Provides Quorum's JavaFX chat interface. */
public class ChatApplication extends Application {
    private static final double WINDOW_WIDTH = 720;
    private static final double WINDOW_HEIGHT = 540;
    private static final double MINIMUM_WINDOW_WIDTH = 520;
    private static final double MINIMUM_WINDOW_HEIGHT = 400;
    private static final double MAXIMUM_BUBBLE_WIDTH = 520;

    private final VBox messages = new VBox(12);
    private final TextField commandInput = new TextField();
    private final Button sendButton = new Button("Send");

    private ChatUi ui;
    private QuorumEngine engine;

    @Override
    public void start(Stage stage) {
        ui = new ChatUi(this::appendBotMessage);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app");
        root.setTop(createHeader());
        root.setCenter(createConversation());
        root.setBottom(createComposer());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        URL stylesheet = ChatApplication.class.getResource("chat.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Quorum");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.show();

        ui.showWelcome();
        try {
            engine = new QuorumEngine();
            commandInput.requestFocus();
        } catch (QuorumException e) {
            ui.showError(e.getMessage());
            disableComposer();
        }
    }

    private VBox createHeader() {
        Label title = new Label("QUORUM");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Finding the hours when everyone's awake.");
        subtitle.getStyleClass().add("subtitle");

        VBox header = new VBox(3, title, subtitle);
        header.getStyleClass().add("header");
        return header;
    }

    private ScrollPane createConversation() {
        messages.getStyleClass().add("messages");
        messages.setPadding(new Insets(20));

        ScrollPane conversation = new ScrollPane(messages);
        conversation.getStyleClass().add("conversation");
        conversation.setFitToWidth(true);
        conversation.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messages.heightProperty().addListener((observable, oldHeight, newHeight) ->
                conversation.setVvalue(1.0));
        return conversation;
    }

    private HBox createComposer() {
        commandInput.setPromptText("Type a command...");
        commandInput.setOnAction(event -> submitCommand());
        HBox.setHgrow(commandInput, Priority.ALWAYS);

        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());

        HBox composer = new HBox(10, commandInput, sendButton);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");
        return composer;
    }

    private void submitCommand() {
        String input = commandInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        appendMessage(input, true);
        commandInput.clear();

        try {
            CommandOutcome outcome = engine.execute(input, ui);
            if (outcome == CommandOutcome.EXIT) {
                ui.showGoodbye();
                disableComposer();
            }
        } catch (QuorumException e) {
            ui.showError(e.getMessage());
        }
    }

    private void appendBotMessage(String message) {
        appendMessage(message, false);
    }

    private void appendMessage(String message, boolean isUser) {
        Label bubble = new Label(message);
        bubble.setWrapText(true);
        bubble.setMaxWidth(MAXIMUM_BUBBLE_WIDTH);
        bubble.getStyleClass().add(isUser ? "user-bubble" : "bot-bubble");

        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messages.getChildren().add(row);
    }

    private void disableComposer() {
        commandInput.setDisable(true);
        sendButton.setDisable(true);
    }
}
