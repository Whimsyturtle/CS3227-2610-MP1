package quorum;

/** A parsed instruction: what the user wants, and the text that follows it. */
public record Command(CommandType type, String arguments);