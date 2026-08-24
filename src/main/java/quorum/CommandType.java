package quorum;

/** Commands supported by the application. */
public enum CommandType {
    ADD, LIST, BYE, UNKNOWN;

    public static CommandType from(String keyword) {
        try {
            return valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
