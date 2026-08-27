package quorum;

/** Represents a command supported by the application. */
public enum CommandType {
    ADD, LIST, BYE, UNKNOWN;

    /** Returns the command type for the given keyword, or {@code UNKNOWN} if unsupported. */
    public static CommandType from(String keyword) {
        try {
            return valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
