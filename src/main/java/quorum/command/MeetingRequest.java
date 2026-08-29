package quorum.command;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import quorum.model.Tag;

/** Holds the inputs needed to search for meeting times. */
public record MeetingRequest(ZoneId userZone, Tag tag, LocalDate date, Duration duration) {

}
