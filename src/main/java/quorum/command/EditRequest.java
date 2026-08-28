package quorum.command;

import java.time.ZoneId;

/** Represents the changes requested by an edit command. */
public record EditRequest(int index, ZoneId zone) {

}
