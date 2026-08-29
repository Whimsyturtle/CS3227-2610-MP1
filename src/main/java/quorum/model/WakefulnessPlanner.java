package quorum.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Ranks meeting starts by how many attendees are awake for the full interval. */
public final class WakefulnessPlanner {
    public static final LocalTime AWAKE_FROM = LocalTime.of(8, 0);
    public static final LocalTime AWAKE_UNTIL = LocalTime.of(22, 0);
    public static final Duration START_INTERVAL = Duration.ofMinutes(30);

    private WakefulnessPlanner() {

    }

    /**
     * Returns every maximum-scoring slot that starts on the requested date in
     * the user's timezone. The user is counted as an anonymous attendee.
     */
    public static List<WakefulnessResult> findBestResults(
            ZoneId userZone, List<Participant> participants,
            LocalDate date, Duration duration) {
        Objects.requireNonNull(userZone, "User timezone cannot be null.");
        Objects.requireNonNull(participants, "Participants cannot be null.");
        Objects.requireNonNull(date, "Meeting date cannot be null.");
        Objects.requireNonNull(duration, "Meeting duration cannot be null.");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("Meeting duration must be positive.");
        }

        List<MeetingAttendee> attendees = new ArrayList<>();
        attendees.add(new MeetingAttendee("You", userZone));
        for (Participant participant : participants) {
            attendees.add(new MeetingAttendee(participant.getName(), participant.getZone()));
        }

        Instant dayStart = date.atStartOfDay(userZone).toInstant();
        Instant nextDayStart = date.plusDays(1).atStartOfDay(userZone).toInstant();
        List<WakefulnessResult> bestResults = new ArrayList<>();
        int bestScore = -1;

        for (Instant start = dayStart; start.isBefore(nextDayStart);
                start = start.plus(START_INTERVAL)) {
            WakefulnessResult result = evaluate(start, duration, attendees);
            if (result.awakeCount() > bestScore) {
                bestScore = result.awakeCount();
                bestResults.clear();
            }
            if (result.awakeCount() == bestScore) {
                bestResults.add(result);
            }
        }
        return List.copyOf(bestResults);
    }

    private static WakefulnessResult evaluate(Instant start, Duration duration,
                                              List<MeetingAttendee> attendees) {
        Instant end = start.plus(duration);
        List<MeetingAttendee> notAwake = attendees.stream()
                .filter(attendee -> !isAwakeFor(start, end, attendee.zone()))
                .toList();
        return new WakefulnessResult(
                new TimeSlot(start, end), attendees.size(), notAwake);
    }

    private static boolean isAwakeFor(Instant start, Instant end, ZoneId zone) {
        ZonedDateTime localStart = start.atZone(zone);
        LocalDate localDate = localStart.toLocalDate();
        Instant awakeStart = localDate.atTime(AWAKE_FROM).atZone(zone).toInstant();
        Instant awakeEnd = localDate.atTime(AWAKE_UNTIL).atZone(zone).toInstant();
        return !start.isBefore(awakeStart) && !end.isAfter(awakeEnd);
    }
}
