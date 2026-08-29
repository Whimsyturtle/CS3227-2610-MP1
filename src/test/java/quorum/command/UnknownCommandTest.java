package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

class UnknownCommandTest {
    private static final String EXPECTED_MESSAGE =
            "I don't know that one. Try: add, delete, edit, tag, untag, tags, "
                    + "meeting, list, zones, bye";

    @Test
    void execute_nonEmptyRoster_showsCommandSuggestionsContinuesWithoutMutation()
            throws QuorumException {
        Participant alice = new Participant("Alice", ZoneId.of("Asia/Singapore"));
        Roster roster = new Roster();
        roster.add(alice);
        UnknownUiSpy ui = new UnknownUiSpy();

        CommandOutcome outcome = new UnknownCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(EXPECTED_MESSAGE, ui.message);
        assertEquals(1, roster.size());
        assertSame(alice, roster.asList().getFirst());
    }

    private static final class UnknownUiSpy extends SilentUi {
        private int callCount;
        private String message;

        @Override
        public void showError(String shownMessage) {
            callCount++;
            message = shownMessage;
        }
    }
}
