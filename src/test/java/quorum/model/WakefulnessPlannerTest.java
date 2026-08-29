package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;

class WakefulnessPlannerTest {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final LocalDate MEETING_DATE = LocalDate.of(2026, 8, 30);

    @Test
    void countsTheUserAndReturnsEveryOneHourSlotWithinTheAwakeWindow() {
        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(), MEETING_DATE, Duration.ofHours(1));

        assertEquals(27, results.size());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(8, 0)),
                results.getFirst().slot().start());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(21, 0)),
                results.getLast().slot().start());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(22, 0)),
                results.getLast().slot().end());
        for (int i = 0; i < results.size(); i++) {
            WakefulnessResult result = results.get(i);
            Instant expectedStart = candidateStart(MEETING_DATE, UTC, LocalTime.of(8, 0))
                    .plus(Duration.ofMinutes(30L * i));
            assertEquals(expectedStart, result.slot().start());
            assertEquals(expectedStart.plus(Duration.ofHours(1)), result.slot().end());
            assertEquals(1, result.attendeeCount());
            assertEquals(1, result.awakeCount());
            assertTrue(result.notAwake().isEmpty());
        }
    }

    @Test
    void scoresFullDurationAndReportsThePersonOutsideTheAwakeWindow() throws QuorumException {
        Participant singapore = participant("Singapore", "Asia/Singapore");
        Participant honolulu = participant("Honolulu", "Pacific/Honolulu");

        List<WakefulnessResult> results = WakefulnessPlanner.findBestResults(
                UTC, List.of(singapore, honolulu), MEETING_DATE, Duration.ofHours(1));

        assertEquals(33, results.size());
        assertEquals(2, results.getFirst().awakeCount());
        assertEquals(2, results.getLast().awakeCount());
        for (WakefulnessResult result : results) {
            assertEquals(3, result.attendeeCount());
            assertEquals(2, result.awakeCount());
            assertEquals(1, result.notAwake().size());
        }

        WakefulnessResult atSingaporeEnd = resultAt(results, LocalTime.of(13, 0));
        assertEquals(List.of("Honolulu"), names(atSingaporeEnd.notAwake()));
        assertTrue(hasStart(results, LocalTime.of(13, 0)));
        assertFalse(hasStart(results, LocalTime.of(13, 30)));

        WakefulnessResult atHonoluluStart = resultAt(results, LocalTime.of(18, 0));
        assertEquals(List.of("Singapore"), names(atHonoluluStart.notAwake()));
        assertFalse(hasStart(results, LocalTime.of(17, 30)));

        WakefulnessResult whileUserSleeps = resultAt(results, LocalTime.of(0, 0));
        assertEquals(List.of("You"), names(whileUserSleeps.notAwake()));
    }

    @Test
    void appliesTheFullDurationAtAndAroundTheFourteenHourAwakeWindow() {
        List<WakefulnessResult> justBelow = WakefulnessPlanner.findBestResults(
                UTC, List.of(), MEETING_DATE, Duration.ofHours(13).plusMinutes(59));
        List<WakefulnessResult> exactlyAt = WakefulnessPlanner.findBestResults(
                UTC, List.of(), MEETING_DATE, Duration.ofHours(14));
        List<WakefulnessResult> justAbove = WakefulnessPlanner.findBestResults(
                UTC, List.of(), MEETING_DATE, Duration.ofHours(14).plusMinutes(1));

        assertEquals(1, justBelow.size());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(8, 0)),
                justBelow.getFirst().slot().start());
        assertEquals(1, justBelow.getFirst().awakeCount());

        assertEquals(1, exactlyAt.size());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(8, 0)),
                exactlyAt.getFirst().slot().start());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(22, 0)),
                exactlyAt.getFirst().slot().end());
        assertEquals(1, exactlyAt.getFirst().awakeCount());

        assertEquals(48, justAbove.size());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.MIDNIGHT),
                justAbove.getFirst().slot().start());
        assertEquals(candidateStart(MEETING_DATE, UTC, LocalTime.of(23, 30)),
                justAbove.getLast().slot().start());
        for (WakefulnessResult result : justAbove) {
            assertEquals(0, result.awakeCount());
            assertEquals(List.of("You"), names(result.notAwake()));
        }
    }

    @Test
    void generatesCandidateStartsForTheActualLengthOfDstDays() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDate springDate = LocalDate.of(2026, 3, 8);
        LocalDate autumnDate = LocalDate.of(2026, 11, 1);

        List<WakefulnessResult> springResults = WakefulnessPlanner.findBestResults(
                newYork, List.of(), springDate, Duration.ofHours(14).plusMinutes(1));
        List<WakefulnessResult> autumnResults = WakefulnessPlanner.findBestResults(
                newYork, List.of(), autumnDate, Duration.ofHours(14).plusMinutes(1));

        assertDayCandidateRange(springResults, springDate, newYork, 46);
        assertDayCandidateRange(autumnResults, autumnDate, newYork, 50);
    }

    @Test
    void rejectsNullPlannerInputsIndividually() {
        NullPointerException nullZone = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        null, List.of(), MEETING_DATE, Duration.ofHours(1)));
        NullPointerException nullParticipants = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, null, MEETING_DATE, Duration.ofHours(1)));
        NullPointerException nullDate = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), null, Duration.ofHours(1)));
        NullPointerException nullDuration = assertThrows(NullPointerException.class, () ->
                WakefulnessPlanner.findBestResults(UTC, List.of(), MEETING_DATE, null));

        assertEquals("User timezone cannot be null.", nullZone.getMessage());
        assertEquals("Participants cannot be null.", nullParticipants.getMessage());
        assertEquals("Meeting date cannot be null.", nullDate.getMessage());
        assertEquals("Meeting duration cannot be null.", nullDuration.getMessage());
    }

    @Test
    void rejectsZeroAndNegativePlannerDurations() {
        IllegalArgumentException zero = assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), MEETING_DATE, Duration.ZERO));
        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class, () ->
                WakefulnessPlanner.findBestResults(
                        UTC, List.of(), MEETING_DATE, Duration.ofNanos(-1)));

        assertEquals("Meeting duration must be positive.", zero.getMessage());
        assertEquals("Meeting duration must be positive.", negative.getMessage());
    }

    private Participant participant(String name, String zone) throws QuorumException {
        return new Participant(name, ZoneId.of(zone), Set.of());
    }

    private Instant candidateStart(LocalDate date, ZoneId zone, LocalTime time) {
        return date.atTime(time).atZone(zone).toInstant();
    }

    private WakefulnessResult resultAt(List<WakefulnessResult> results, LocalTime time) {
        Instant expectedStart = candidateStart(MEETING_DATE, UTC, time);
        return results.stream()
                .filter(result -> result.slot().start().equals(expectedStart))
                .findFirst()
                .orElseThrow();
    }

    private boolean hasStart(List<WakefulnessResult> results, LocalTime time) {
        Instant expectedStart = candidateStart(MEETING_DATE, UTC, time);
        return results.stream().anyMatch(
                result -> result.slot().start().equals(expectedStart));
    }

    private List<String> names(List<MeetingAttendee> attendees) {
        return attendees.stream().map(MeetingAttendee::name).toList();
    }

    private void assertDayCandidateRange(List<WakefulnessResult> results,
                                         LocalDate date, ZoneId zone, int expectedCount) {
        assertEquals(expectedCount, results.size());
        assertEquals(date.atStartOfDay(zone).toInstant(), results.getFirst().slot().start());
        assertEquals(date.atTime(23, 30).atZone(zone).toInstant(),
                results.getLast().slot().start());
        for (WakefulnessResult result : results) {
            assertEquals(0, result.awakeCount());
            assertEquals(List.of("You"), names(result.notAwake()));
        }
    }
}
