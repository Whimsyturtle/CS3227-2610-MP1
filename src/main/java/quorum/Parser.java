package quorum;

import java.time.DateTimeException;
import java.time.ZoneId;

/**
 * Turns raw user input into structured objects. Knows the command syntax and
 * nothing about how commands are carried out or displayed.
 */
public class Parser {
    private static final String ZONE_DELIMITER = "/tz";

    /** Splits input into a command type and its raw argument string. */
    public Command parse(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        CommandType type = CommandType.from(parts[0]);
        String arguments = parts.length > 1 ? parts[1] : "";
        return new Command(type, arguments);
    }

    /**
     * Builds a `Participant` from the argument string of an add command.
     *
     * @throws QuorumException if the name or zone is missing or unrecognised.
     */
    public Participant parseParticipant(String arguments) throws QuorumException {
        String[] parts = arguments.split(ZONE_DELIMITER, 2);
        if (parts.length < 2) {
            throw new QuorumException("I need a timezone. Try: add Alice /tz Asia/Singapore");
        }

        String name = parts[0].trim();
        if (name.isEmpty()) {
            throw new QuorumException("Missing name.");
        }

        String zoneText = parts[1].trim();
        if (zoneText.isEmpty()) {
            throw new QuorumException("Missing timezone after /tz.");
        }

        return new Participant(name, toZone(zoneText));
    }

    private ZoneId toZone(String zoneText) throws QuorumException {
        try {
            return ZoneId.of(zoneText);
        } catch (DateTimeException e) {
            throw new QuorumException(
                    "I don't recognise the zone \"" + zoneText + "\". Try one like Asia/Singapore.");
        }
    }
}
