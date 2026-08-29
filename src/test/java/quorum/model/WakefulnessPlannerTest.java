package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class WakefulnessPlannerTest {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final LocalDate DATE = LocalDate.of(2026, 8, 30);

    @Test
    void findBestResults_nullUserZone_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        null, List.of(), DATE, Duration.ofHours(1)));

        assertEquals("User timezone cannot be null.", exception.getMessage());
    }

    @Test
    void findBestResults_nullParticipants_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, null, DATE, Duration.ofHours(1)));

        assertEquals("Participants cannot be null.", exception.getMessage());
    }

    @Test
    void findBestResults_nullDate_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), null, Duration.ofHours(1)));

        assertEquals("Meeting date cannot be null.", exception.getMessage());
    }

    @Test
    void findBestResults_nullDuration_throwsSpecificNullPointerException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(UTC, List.of(), DATE, null));

        assertEquals("Meeting duration cannot be null.", exception.getMessage());
    }

    @Test
    void findBestResults_negativeDuration_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), DATE, Duration.ofNanos(-1)));

        assertEquals("Meeting duration must be positive.", exception.getMessage());
    }

    @Test
    void findBestResults_zeroDuration_throwsSpecificIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), DATE, Duration.ZERO));

        assertEquals("Meeting duration must be positive.", exception.getMessage());
    }

    @Test
    void findBestResults_smallestPositiveDuration_isAccepted() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, Duration.ofNanos(1));

        assertEquals(28, results.size());
        assertEquals(LocalTime.of(8, 0), localStart(results.getFirst(), UTC));
        assertEquals(LocalTime.of(21, 30), localStart(results.getLast(), UTC));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 1));
    }

    @Test
    void findBestResults_thirtyMinuteMeeting_returnsAllStartsThatFitAwakeWindow() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, Duration.ofMinutes(30));

        assertEquals(28, results.size());
        assertEquals(LocalTime.of(8, 0), localStart(results.getFirst(), UTC));
        assertEquals(LocalTime.of(8, 30), localStart(results.get(1), UTC));
        assertEquals(LocalTime.of(21, 0), localStart(results.get(results.size() - 2), UTC));
        assertEquals(LocalTime.of(21, 30), localStart(results.getLast(), UTC));
        for (int index = 1; index < results.size(); index++) {
            Instant previous = results.get(index - 1).slot().start();
            Instant current = results.get(index).slot().start();
            assertEquals(WakefulnessPlanner.START_INTERVAL,
                    Duration.between(previous, current));
        }
    }

    @Test
    void findBestResults_durationAroundAwakeWindowLength_scoresOnlyMeetingsThatFullyFit() {
        Duration windowLength = Duration.between(
                WakefulnessPlanner.AWAKE_FROM, WakefulnessPlanner.AWAKE_UNTIL);

        List<WakefulnessResult> justBelow = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, windowLength.minusNanos(1));
        List<WakefulnessResult> atBoundary = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, windowLength);
        List<WakefulnessResult> justAbove = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, windowLength.plusNanos(1));

        assertEquals(1, justBelow.size());
        assertEquals(1, justBelow.getFirst().awakeCount());
        assertEquals(LocalTime.of(8, 0), localStart(justBelow.getFirst(), UTC));
        assertEquals(1, atBoundary.size());
        assertEquals(1, atBoundary.getFirst().awakeCount());
        assertEquals(LocalTime.of(8, 0), localStart(atBoundary.getFirst(), UTC));
        assertEquals(48, justAbove.size());
        assertTrue(justAbove.stream().allMatch(result -> result.awakeCount() == 0));
        assertTrue(justAbove.stream().allMatch(result ->
                result.notAwake().equals(List.of(new MeetingAttendee("You", UTC)))));
    }

    @Test
    void findBestResults_singaporeAndNewYork_returnsAllStartsWhenBothAreAwake()
            throws QuorumException {
        ZoneId singapore = ZoneId.of("Asia/Singapore");
        Participant newYorker = new Participant(
                "New Yorker", ZoneId.of("America/New_York"));

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                singapore, List.of(newYorker), DATE, Duration.ofHours(1));

        List<LocalTime> starts = results.stream()
                .map(result -> localStart(result, singapore))
                .toList();
        assertEquals(List.of(
                LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0),
                LocalTime.of(20, 0), LocalTime.of(20, 30), LocalTime.of(21, 0)),
                starts);
        assertTrue(results.stream().allMatch(result -> result.attendeeCount() == 2));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 2));
        assertTrue(results.stream().allMatch(result -> result.notAwake().isEmpty()));
    }

    @Test
    void findBestResults_noTimeWhenAllAttendeesAreAwake_returnsHighestScoringStarts()
            throws QuorumException {
        Participant east = new Participant("East", ZoneId.of("Asia/Shanghai"));
        Participant west = new Participant("West", ZoneId.of("America/Los_Angeles"));

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(east, west), DATE, Duration.ofMinutes(30));

        assertEquals(36, results.size());
        assertTrue(results.stream().allMatch(result -> result.attendeeCount() == 3));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 2));
        assertEquals(List.of(new MeetingAttendee("You", UTC)),
                results.getFirst().notAwake());
        assertEquals(LocalTime.MIDNIGHT, localStart(results.getFirst(), UTC));
        assertEquals(List.of(new MeetingAttendee("West", west.getZone())),
                results.get(10).notAwake());
        assertEquals(LocalTime.of(8, 0), localStart(results.get(10), UTC));
        assertEquals(List.of(new MeetingAttendee("East", east.getZone())),
                results.get(22).notAwake());
        assertEquals(LocalTime.of(15, 0), localStart(results.get(22), UTC));
        assertEquals(LocalTime.of(21, 30), localStart(results.getLast(), UTC));
    }

    @Test
    void findBestResults_newYorkSpringForwardDate_usesTwentyThreeHourDay() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                NEW_YORK, List.of(), LocalDate.of(2026, 3, 8), Duration.ofHours(24));

        assertEquals(46, results.size());
        assertEquals(LocalTime.MIDNIGHT, localStart(results.getFirst(), NEW_YORK));
        assertEquals(LocalTime.of(23, 30), localStart(results.getLast(), NEW_YORK));
        assertTrue(results.stream()
                .map(result -> localStart(result, NEW_YORK))
                .noneMatch(time -> time.getHour() == 2));
    }

    @Test
    void findBestResults_newYorkNormalDate_usesTwentyFourHourDay() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                NEW_YORK, List.of(), LocalDate.of(2026, 3, 9), Duration.ofHours(24));

        assertEquals(48, results.size());
    }

    @Test
    void findBestResults_newYorkFallBackDate_usesTwentyFiveHourDay() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                NEW_YORK, List.of(), LocalDate.of(2026, 11, 1), Duration.ofHours(24));

        assertEquals(50, results.size());
        assertEquals(4, results.stream()
                .map(result -> localStart(result, NEW_YORK))
                .filter(time -> time.getHour() == 1)
                .count());
    }

    @Test
    void findBestResults_returnedTiesAreUnmodifiable() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(), DATE, Duration.ofHours(1));

        assertThrows(UnsupportedOperationException.class, results::clear);
    }

    private LocalTime localStart(WakefulnessResult result, ZoneId zone) {
        return result.slot().start().atZone(zone).toLocalTime();
    }
}
