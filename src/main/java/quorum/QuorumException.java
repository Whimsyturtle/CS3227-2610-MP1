package quorum;

/** Signals a problem the user can fix, and carries a message safe to display. */
public class QuorumException extends Exception {
    public QuorumException(String message) {
        super(message);
    }
}
