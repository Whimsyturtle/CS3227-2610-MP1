package quorum;

import java.time.ZoneId;

/** Represents a named participant and their time zone. */
public class Participant {
    private final String name;
    private final ZoneId zone;

    /** Creates a participant with the given name and time zone. */
    public Participant(String name, ZoneId zone) {
        this.name = name;
        this.zone = zone;
    }

    public String getName() {
        return name;
    }

    public ZoneId getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return name + " (" + zone.getId() + ")";
    }
}
