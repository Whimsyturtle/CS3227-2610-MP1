package quorum;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import quorum.command.AddCommand;
import quorum.command.ByeCommand;
import quorum.command.Command;
import quorum.command.DeleteCommand;
import quorum.command.EditCommand;
import quorum.command.EditRequest;
import quorum.command.ListCommand;
import quorum.command.MeetingCommand;
import quorum.command.MeetingRequest;
import quorum.command.TagCommand;
import quorum.command.TagRequest;
import quorum.command.TagsCommand;
import quorum.command.UnknownCommand;
import quorum.command.UntagCommand;
import quorum.command.ZonesCommand;
import quorum.model.Participant;
import quorum.model.Tag;

/**
 * Turns raw user input into structured objects. Knows the command syntax and
 * nothing about how commands are carried out or displayed.
 */
public class Parser {
    private static final String ZONE_DELIMITER = "/tz";
    private static final Pattern MEETING_PATTERN = Pattern.compile(
            "^(?<zone>\\S+)\\s+(?<tag>\\S+)\\s+/on\\s+(?<date>\\S+)"
                    + "\\s+/for\\s+(?<duration>\\S+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "^(?:(?<hours>\\d+)h)?(?:(?<minutes>\\d+)m)?$",
            Pattern.CASE_INSENSITIVE);
    private static final Duration MAX_MEETING_DURATION = Duration.ofHours(24);

    /** Parses the given input into a command with structured arguments. */
    public Command parse(String input) throws QuorumException {
        String[] parts = input.trim().split("\\s+", 2);
        String keyword = parts[0].toLowerCase(Locale.ROOT);
        String arguments = parts.length > 1 ? parts[1] : "";

        return switch (keyword) {
        case "add" -> new AddCommand(parseParticipant(arguments));
        case "delete" -> new DeleteCommand(parseIndex(arguments));
        case "edit" -> new EditCommand(parseEdit(arguments));
        case "tag" -> new TagCommand(parseTagRequest(arguments));
        case "untag" -> new UntagCommand(parseTagRequest(arguments));
        case "tags" -> new TagsCommand();
        case "meeting" -> new MeetingCommand(parseMeeting(arguments));
        case "list" -> new ListCommand();
        case "zones" -> new ZonesCommand(parseZoneSearch(arguments));
        case "bye" -> new ByeCommand();
        default -> new UnknownCommand();
        };
    }

    /**
     * Builds a participant from the arguments of an add command.
     *
     * @throws QuorumException if the name or timezone is missing or invalid
     */
    public Participant parseParticipant(String arguments) throws QuorumException {
        String[] parts = arguments.split(ZONE_DELIMITER, 2);
        if (parts.length < 2) {
            throw new QuorumException("I need a timezone. Try: add Alice /tz Asia/Singapore");
        }
        return new Participant(parts[0].trim(), parseZone(parts[1]));
    }

    /**
     * Parses the index and new timezone from the arguments of an edit command.
     *
     * @throws QuorumException if the index or timezone is missing or invalid
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
     * Parses a participant index and tag from tag or untag arguments.
     *
     * @throws QuorumException if the index or tag is missing or invalid
     */
    public TagRequest parseTagRequest(String arguments) throws QuorumException {
        String[] parts = arguments.trim().split("\\s+", 2);
        if (parts.length < 2) {
            if (parts[0].isEmpty()) {
                throw new QuorumException("Missing index.");
            }
            parseIndex(parts[0]);
            throw new QuorumException("Missing tag.");
        }

        int index = parseIndex(parts[0]);
        Tag tag = new Tag(parts[1]);
        return new TagRequest(index, tag);
    }

    /**
     * Parses the display timezone, tag, date, and duration of a meeting search.
     *
     * @throws QuorumException if the arguments do not follow the meeting syntax
     */
    public MeetingRequest parseMeeting(String arguments) throws QuorumException {
        Matcher matcher = MEETING_PATTERN.matcher(arguments.trim());
        if (!matcher.matches()) {
            throw new QuorumException(
                    "Invalid meeting command. "
                            + "Try: meeting Asia/Singapore FRIENDS /on 2026-08-30 /for 1h");
        }

        ZoneId zone = parseZone(matcher.group("zone"));
        Tag tag = new Tag(matcher.group("tag"));
        LocalDate date = parseMeetingDate(matcher.group("date"));
        Duration duration = parseMeetingDuration(matcher.group("duration"));
        return new MeetingRequest(zone, tag, date, duration);
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
     * Parses a timezone from the given arguments.
     *
     * @throws QuorumException if the timezone is missing or invalid
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

    private LocalDate parseMeetingDate(String dateText) throws QuorumException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new QuorumException(
                    "Invalid date \"" + dateText + "\". Use YYYY-MM-DD, such as 2026-08-30.");
        }
    }

    private Duration parseMeetingDuration(String durationText) throws QuorumException {
        Matcher matcher = DURATION_PATTERN.matcher(durationText);
        if (!matcher.matches()
                || matcher.group("hours") == null && matcher.group("minutes") == null) {
            throw invalidMeetingDuration(durationText);
        }

        try {
            long hours = matcher.group("hours") == null
                    ? 0 : Long.parseLong(matcher.group("hours"));
            long minutes = matcher.group("minutes") == null
                    ? 0 : Long.parseLong(matcher.group("minutes"));
            Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
            if (duration.isZero() || duration.compareTo(MAX_MEETING_DURATION) > 0) {
                throw invalidMeetingDuration(durationText);
            }
            return duration;
        } catch (ArithmeticException e) {
            throw invalidMeetingDuration(durationText);
        }
    }

    private QuorumException invalidMeetingDuration(String durationText) {
        return new QuorumException(
                "Invalid duration \"" + durationText
                        + "\". Use a duration up to 24h, such as 30m, 1h, or 1h30m.");
    }
}
