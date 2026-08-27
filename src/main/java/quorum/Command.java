package quorum;

/** Represents a parsed instruction comprising a command type and its raw arguments. */
public record Command(CommandType type, String arguments) {

}
