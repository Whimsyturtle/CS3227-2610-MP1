package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

class ByeCommandTest {
    @Test
    void execute_nonEmptyRoster_returnsExitWithoutMutation() throws QuorumException {
        Participant alice = new Participant("Alice", ZoneId.of("Asia/Singapore"));
        Roster roster = new Roster();
        roster.add(alice);

        Command command = new ByeCommand();
        CommandOutcome outcome = command.execute(roster, new SilentUi() { });

        assertEquals(CommandOutcome.EXIT, outcome);
        assertEquals(1, roster.size());
        assertSame(alice, roster.asList().getFirst());
    }
}
