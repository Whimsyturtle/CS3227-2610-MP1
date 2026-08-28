package quorum.model;

import java.time.ZoneId;

import quorum.QuorumException;

/** Represents a named participant and their timezone. */
public class Participant {
    private final String name;
    private final ZoneId zone;

    /** Creates a participant with the given name and timezone. */
    public Participant(String name, ZoneId zone) throws QuorumException {
        validateName(name);
        this.name = name;
        if (zone == null) {
            throw new QuorumException("Timezone cannot be null.");
        }
        this.zone = zone;
    }

    public String getName() {
        return name;
    }

    public ZoneId getZone() {
        return zone;
    }

    /** Returns a copy of this participant with the given timezone. */
    public Participant withZone(ZoneId newZone) throws QuorumException {
        return new Participant(name, newZone);
    }

    @Override
    public String toString() {
        return name + " (" + zone.getId() + ")";
    }

    private static void validateName(String name) throws QuorumException {
        if (name == null || name.isBlank()) {
            throw new QuorumException("Name cannot be null or blank.");
        }
        if (name.indexOf('\t') >= 0 || name.indexOf('\r') >= 0 || name.indexOf('\n') >= 0) {
            throw new QuorumException("Name cannot contain tabs or line breaks.");
        }
    }
}
