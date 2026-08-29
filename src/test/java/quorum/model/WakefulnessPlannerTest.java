package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class WakefulnessPlannerTest {
    private static final LocalDate DATE = LocalDate.of(2026, 8, 30);

    @Test
    void findBestResults_sharedAwakeHours_returnsAllChronologicalTies()
            throws QuorumException {
        ZoneId userZone = ZoneId.of("Asia/Singapore");
        Participant participant = new Participant("New Yorker", ZoneId.of("America/New_York"));

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                userZone, List.of(participant), DATE, Duration.ofHours(1));

        List<LocalTime> starts = results.stream()
                .map(result -> result.slot().start().atZone(userZone).toLocalTime())
                .toList();
        assertEquals(List.of(
                LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0),
                LocalTime.of(20, 0), LocalTime.of(20, 30), LocalTime.of(21, 0)),
                starts);
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 2));
        assertTrue(results.stream().allMatch(result -> result.notAwake().isEmpty()));
    }

    @Test
    void findBestResults_noUniversalTime_returnsOnlyMaximumScore()
            throws QuorumException {
        ZoneId userZone = ZoneId.of("UTC");
        List<Participant> participants = List.of(
                new Participant("East", ZoneId.of("Asia/Shanghai")),
                new Participant("West", ZoneId.of("America/Los_Angeles")));

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                userZone, participants, DATE, Duration.ofHours(1));

        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(result -> result.attendeeCount() == 3));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 2));
        assertTrue(results.stream().allMatch(result -> result.notAwake().size() == 1));
    }

    @Test
    void findBestResults_checksEntireDurationAtAwakeBoundary() {
        ZoneId zone = ZoneId.of("UTC");

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                zone, List.of(), DATE, Duration.ofHours(2));

        assertEquals(25, results.size());
        assertEquals(LocalTime.of(8, 0), localStart(results.getFirst(), zone));
        assertEquals(LocalTime.of(20, 0), localStart(results.getLast(), zone));
        assertTrue(results.stream().allMatch(result -> result.awakeCount() == 1));
    }

    @Test
    void findBestResults_dstSpringDay_usesActualInstantsInLocalDay() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDate springForwardDate = LocalDate.of(2026, 3, 8);

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                newYork, List.of(), springForwardDate, Duration.ofHours(24));

        assertEquals(46, results.size());
        assertEquals(LocalTime.MIDNIGHT, localStart(results.getFirst(), newYork));
        assertEquals(LocalTime.of(23, 30), localStart(results.getLast(), newYork));
        assertTrue(results.stream()
                .map(result -> localStart(result, newYork))
                .noneMatch(time -> time.getHour() == 2));
    }

    @Test
    void findBestResults_invalidArguments_throwHelpfulExceptions() {
        ZoneId zone = ZoneId.of("UTC");

        assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(null, List.of(), DATE,
                        Duration.ofHours(1)));
        assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(zone, null, DATE,
                        Duration.ofHours(1)));
        assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(zone, List.of(), null,
                        Duration.ofHours(1)));
        assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(zone, List.of(), DATE, null));
        assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(zone, List.of(), DATE,
                        Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(zone, List.of(), DATE,
                        Duration.ofMinutes(-1)));
    }

    private LocalTime localStart(WakefulnessResult result, ZoneId zone) {
        return result.slot().start().atZone(zone).toLocalTime();
    }
}
