package quorum;

/** Represents a parsed instruction and the text that follows it. */
public record Command(CommandType type, String arguments) {

}
