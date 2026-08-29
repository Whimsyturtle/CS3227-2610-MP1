package quorum.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class WakefulnessResultTest {
    private static final TimeSlot SLOT = new TimeSlot(
            Instant.parse("2026-08-30T00:00:00Z"),
            Instant.parse("2026-08-30T01:00:00Z"));
    private static final MeetingAttendee ALICE =
            new MeetingAttendee("Alice", ZoneId.of("Asia/Singapore"));

    @Test
    void awakeCount_subtractsNotAwakeAttendees() {
        WakefulnessResult allAwake = new WakefulnessResult(SLOT, 3, List.of());
        WakefulnessResult someAsleep = new WakefulnessResult(SLOT, 3, List.of(ALICE));
        WakefulnessResult noneAwake = new WakefulnessResult(SLOT, 1, List.of(ALICE));

        assertEquals(3, allAwake.awakeCount());
        assertEquals(2, someAsleep.awakeCount());
        assertEquals(0, noneAwake.awakeCount());
    }

    @Test
    void constructor_notAwakeList_isDefensivelyCopiedAndUnmodifiable() {
        List<MeetingAttendee> source = new ArrayList<>();
        source.add(ALICE);

        WakefulnessResult result = new WakefulnessResult(SLOT, 2, source);
        source.clear();

        assertEquals(List.of(ALICE), result.notAwake());
        assertThrows(UnsupportedOperationException.class, () ->
                result.notAwake().add(
                        new MeetingAttendee("Bob", ZoneId.of("Europe/London"))));
    }

    @Test
    void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(null, 1, List.of()));
        assertThrows(NullPointerException.class, () ->
                new WakefulnessResult(SLOT, 1, null));
    }

    @Test
    void constructor_invalidAttendeeCount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 0, List.of()));
        assertThrows(IllegalArgumentException.class, () ->
                new WakefulnessResult(SLOT, 1, List.of(
                        ALICE,
                        new MeetingAttendee("Bob", ZoneId.of("Europe/London")))));
    }
}
