package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

/** Tests meeting-slot generation and wakefulness scoring. */
public class WakefulnessPlannerTest {
    private static final ZoneId USER_ZONE = ZoneId.of("Asia/Singapore");
    private static final LocalDate MEETING_DATE = LocalDate.of(2026, 8, 30);

    @Test
    public void includesEveryMaximumScoringStartForTheUser() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                USER_ZONE, List.of(), MEETING_DATE, Duration.ofHours(1));

        assertEquals(27, results.size());
        assertEquals(LocalTime.of(8, 0), localStart(results.getFirst()));
        assertEquals(LocalTime.of(21, 0), localStart(results.getLast()));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 1));
        assertTrue(results.stream().allMatch(result -> result.notAwake().isEmpty()));
    }

    @Test
    public void countsParticipantsWhoAreAwakeForTheWholeInterval() throws QuorumException {
        Participant alice = new Participant("Alice", ZoneId.of("UTC"));

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                USER_ZONE, List.of(alice), MEETING_DATE, Duration.ofHours(6));

        assertEquals(1, results.size());
        WakefulnessResult result = results.getFirst();
        assertEquals(2, result.attendeeCount());
        assertEquals(2, result.awakeCount());
        assertTrue(result.notAwake().isEmpty());
        assertEquals(LocalTime.of(16, 0), localStart(result));
        assertEquals(LocalTime.of(22, 0),
                result.slot().end().atZone(USER_ZONE).toLocalTime());
    }

    @Test
    public void reportsAttendeeWhoIsNotAwakeForTheFullInterval() throws QuorumException {
        Participant honolulu = new Participant("Honolulu", ZoneId.of("Pacific/Honolulu"));
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                USER_ZONE, List.of(honolulu), MEETING_DATE, Duration.ofHours(9));

        WakefulnessResult result = results.stream()
                .filter(candidate -> localStart(candidate).equals(LocalTime.of(8, 0)))
                .findFirst()
                .orElseThrow();

        assertEquals(2, result.attendeeCount());
        assertEquals(1, result.awakeCount());
        assertEquals(
                List.of(new MeetingAttendee("Honolulu", ZoneId.of("Pacific/Honolulu"))),
                result.notAwake());
    }

    @Test
    public void returnsAnImmutableResultList() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                USER_ZONE, List.of(), MEETING_DATE, Duration.ofHours(1));

        assertThrows(UnsupportedOperationException.class,
                () -> results.add(results.getFirst()));
    }

    @Test
    public void rejectsNullInputs() {
        assertThrows(NullPointerException.class,
                () -> WakefulnessPlanner.findBestResults(
                        null, List.of(), MEETING_DATE, Duration.ofHours(1)));
        assertThrows(NullPointerException.class,
                () -> WakefulnessPlanner.findBestResults(
                        USER_ZONE, null, MEETING_DATE, Duration.ofHours(1)));
        assertThrows(NullPointerException.class,
                () -> WakefulnessPlanner.findBestResults(
                        USER_ZONE, List.of(), null, Duration.ofHours(1)));
        assertThrows(NullPointerException.class,
                () -> WakefulnessPlanner.findBestResults(
                        USER_ZONE, List.of(), MEETING_DATE, null));
    }

    @Test
    public void rejectsNonPositiveDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> WakefulnessPlanner.findBestResults(
                        USER_ZONE, List.of(), MEETING_DATE, Duration.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> WakefulnessPlanner.findBestResults(
                        USER_ZONE, List.of(), MEETING_DATE, Duration.ofMinutes(-1)));
    }

    private static LocalTime localStart(WakefulnessResult result) {
        return result.slot().start().atZone(USER_ZONE).toLocalTime();
    }
}
