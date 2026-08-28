package quorum;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Locale;

import quorum.command.AddCommand;
import quorum.command.ByeCommand;
import quorum.command.Command;
import quorum.command.DeleteCommand;
import quorum.command.EditCommand;
import quorum.command.EditRequest;
import quorum.command.ListCommand;
import quorum.command.UnknownCommand;
import quorum.command.ZonesCommand;
import quorum.model.Participant;

/**
 * Turns raw user input into structured objects. Knows the command syntax and
 * nothing about how commands are carried out or displayed.
 */
public class Parser {
    private static final String ZONE_DELIMITER = "/tz";

    /** Parses the given input into a command with structured arguments. */
    public Command parse(String input) throws QuorumException {
        String[] parts = input.trim().split("\\s+", 2);
        String keyword = parts[0].toLowerCase(Locale.ROOT);
        String arguments = parts.length > 1 ? parts[1] : "";

        return switch (keyword) {
        case "add" -> new AddCommand(parseParticipant(arguments));
        case "delete" -> new DeleteCommand(parseIndex(arguments));
        case "edit" -> new EditCommand(parseEdit(arguments));
        case "list" -> new ListCommand();
        case "zones" -> new ZonesCommand(parseZoneSearch(arguments));
        case "bye" -> new ByeCommand();
        default -> new UnknownCommand();
        };
    }

    /**
     * Builds a participant from the arguments of an add command.
     *
     * @throws QuorumException if the name or time zone is missing or invalid
     */
    public Participant parseParticipant(String arguments) throws QuorumException {
        String[] parts = arguments.split(ZONE_DELIMITER, 2);
        if (parts.length < 2) {
            throw new QuorumException("I need a timezone. Try: add Alice /tz Asia/Singapore");
        }
        return new Participant(parts[0].trim(), parseZone(parts[1]));
    }

    /**
     * Parses the index and new time zone from the arguments of an edit command.
     *
     * @throws QuorumException if the index or time zone is missing or invalid
     */
    public EditRequest parseEdit(String arguments) throws QuorumException {
        String[] parts = arguments.split(ZONE_DELIMITER, 2);
        if (parts.length < 2) {
            throw new QuorumException("I need a timezone. Try: edit 1 /tz Europe/London");
        }

        return new EditRequest(parseIndex(parts[0]), parseZone(parts[1]));
    }

    /**
     * Parses a one-based index from the given arguments.
     *
     * @throws QuorumException if the index is missing or is not a positive integer
     */
    public int parseIndex(String arguments) throws QuorumException {
        String indexText = arguments.trim();
        if (indexText.isEmpty()) {
            throw new QuorumException("Missing index.");
        }

        try {
            int index = Integer.parseInt(indexText);
            if (index < 1) {
                throw new QuorumException("Index must be at least 1.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new QuorumException("Index must be a whole number.");
        }
    }

    /**
     * Parses the search term from the arguments of a zones command.
     *
     * @throws QuorumException if the search term is missing
     */
    public String parseZoneSearch(String arguments) throws QuorumException {
        String searchTerm = arguments.trim();
        if (searchTerm.isEmpty()) {
            throw new QuorumException("Missing timezone search term. Try: zones Singapore");
        }
        return searchTerm;
    }

    /**
     * Parses a time zone from the given arguments.
     *
     * @throws QuorumException if the time zone is missing or invalid
     */
    public ZoneId parseZone(String arguments) throws QuorumException {
        String zoneText = arguments.trim();
        if (zoneText.isEmpty()) {
            throw new QuorumException("Missing timezone after /tz.");
        }

        try {
            return ZoneId.of(zoneText);
        } catch (DateTimeException e) {
            throw new QuorumException(
                    "I don't recognise the zone \"" + zoneText
                            + "\". Try \"zones Singapore\" to find a valid timezone.");
        }
    }
}
