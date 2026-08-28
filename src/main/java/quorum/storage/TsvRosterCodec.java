package quorum.storage;

import java.time.DateTimeException;
import java.time.ZoneId;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

/** Encodes and decodes rosters as tab-separated values. */
public class TsvRosterCodec implements RosterCodec {
    private static final char BYTE_ORDER_MARK = '\uFEFF';
    private static final String HEADER = "name\tzoneId";

    @Override
    public String encode(Roster roster) {
        StringBuilder content = new StringBuilder(HEADER).append('\n');
        for (int i = 0; i < roster.size(); i++) {
            Participant participant = roster.asList().get(i);
            content.append(participant.getName())
                    .append('\t')
                    .append(participant.getZone().getId())
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
                    "Expected header \"name<TAB>zoneId\" on line 1.");
        }

        Roster roster = new Roster();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                continue;
            }
            roster.add(decodeParticipant(line, i + 1));
        }
        return roster;
    }

    private Participant decodeParticipant(String line, int lineNumber)
            throws QuorumException {
        String[] fields = line.split("\t", -1);
        if (fields.length != 2) {
            throw new QuorumException(
                    "Expected exactly two tab-separated fields on line " + lineNumber + ".");
        }

        String name = fields[0];
        String zoneText = fields[1];
        if (zoneText.isEmpty()) {
            throw new QuorumException("Missing timezone on line " + lineNumber + ".");
        }

        try {
            return new Participant(name, ZoneId.of(zoneText));
        } catch (DateTimeException e) {
            throw new QuorumException(
                    "Invalid timezone \"" + zoneText + "\" on line " + lineNumber + ".", e);
        } catch (QuorumException e) {
            throw new QuorumException(
                    "Invalid participant on line " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private String removeByteOrderMark(String content) {
        if (!content.isEmpty() && content.charAt(0) == BYTE_ORDER_MARK) {
            return content.substring(1);
        }
        return content;
    }
}
