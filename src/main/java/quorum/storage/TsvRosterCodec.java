package quorum.storage;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

/** Encodes and decodes rosters as tab-separated values. */
public class TsvRosterCodec implements RosterCodec {
    private static final char BYTE_ORDER_MARK = '\uFEFF';
    private static final String HEADER = "name\tzoneId\ttags";

    @Override
    public String encode(Roster roster) {
        StringBuilder content = new StringBuilder(HEADER).append('\n');
        for (int i = 0; i < roster.size(); i++) {
            Participant participant = roster.asList().get(i);
            content.append(participant.getName())
                    .append('\t')
                    .append(participant.getZone().getId())
                    .append('\t')
                    .append(participant.getTags().stream()
                            .map(Tag::value)
                            .collect(Collectors.joining(",")))
                    .append('\n');
        }
        return content.toString();
    }

    @Override
    public Roster decode(String content) throws QuorumException {
        String contentWithoutMark = removeByteOrderMark(content);
        String[] lines = contentWithoutMark.split("\\R", -1);
        if (lines.length == 0 || !HEADER.equals(lines[0])) {
            throw new QuorumException(
                    "Expected header \"name<TAB>zoneId<TAB>tags\" on line 1.");
        }

        Roster roster = new Roster();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                continue;
            }
            Participant participant = decodeParticipant(line, i + 1);
            try {
                roster.add(participant);
            } catch (QuorumException e) {
                throw new QuorumException(
                        "Invalid participant on line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return roster;
    }

    private Participant decodeParticipant(String line, int lineNumber)
            throws QuorumException {
        String[] fields = line.split("\t", -1);
        if (fields.length != 3) {
            throw new QuorumException(
                    "Expected exactly three tab-separated fields on line " + lineNumber + ".");
        }

        String name = fields[0];
        String zoneText = fields[1];
        if (zoneText.isEmpty()) {
            throw new QuorumException("Missing timezone on line " + lineNumber + ".");
        }

        try {
            Collection<Tag> tags = decodeTags(fields[2]);
            return new Participant(name, ZoneId.of(zoneText), tags);
        } catch (DateTimeException e) {
            throw new QuorumException(
                    "Invalid timezone \"" + zoneText + "\" on line " + lineNumber + ".", e);
        } catch (QuorumException e) {
            throw new QuorumException(
                    "Invalid participant on line " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private Collection<Tag> decodeTags(String field) throws QuorumException {
        List<Tag> tags = new ArrayList<>();
        if (field.isEmpty()) {
            return tags;
        }
        for (String value : field.split(",", -1)) {
            tags.add(new Tag(value));
        }
        return tags;
    }

    private String removeByteOrderMark(String content) {
        if (!content.isEmpty() && content.charAt(0) == BYTE_ORDER_MARK) {
            return content.substring(1);
        }
        return content;
    }
}
