package quorum;

public class Quorum {
    private final Ui ui = new Ui();

    public static void main(String[] args) {
        new Quorum().run();
    }

    private void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand().trim();
            if (input.equals("bye")) {
                break;
            }
            ui.showEcho(input);
        }
        ui.showGoodbye();
    }
}