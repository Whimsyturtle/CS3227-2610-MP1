package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.Tag;

class TagsCommandTest {
    private static final ZoneId SINGAPORE = ZoneId.of("Asia/Singapore");

    @Test
    void execute_emptyRoster_showsNoTagsAndContinues() {
        Roster roster = new Roster();
        TagsUiSpy ui = new TagsUiSpy();

        CommandOutcome outcome = new TagsCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(), ui.tags);
        assertEquals(0, roster.size());
    }

    @Test
    void execute_exactlyOneTag_showsTagWithoutMutation() throws QuorumException {
        Tag work = new Tag("work");
        Participant alice = participant("Alice", work);
        Roster roster = rosterOf(alice);
        TagsUiSpy ui = new TagsUiSpy();

        CommandOutcome outcome = new TagsCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(work), ui.tags);
        assertSame(alice, roster.asList().getFirst());
    }

    @Test
    void execute_repeatedAndUnorderedTags_showsDistinctTagsAlphabetically()
            throws QuorumException {
        Tag alpha = new Tag("alpha");
        Tag beta = new Tag("beta");
        Tag zebra = new Tag("zebra");
        Participant alice = participant("Alice", zebra, alpha);
        Participant bob = participant("Bob", beta, alpha);
        Roster roster = rosterOf(alice, bob);
        TagsUiSpy ui = new TagsUiSpy();

        CommandOutcome outcome = new TagsCommand().execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals(List.of(alpha, beta, zebra), ui.tags);
        assertEquals(List.of(alice, bob), roster.asList());
    }

    private Participant participant(String name, Tag... tags) throws QuorumException {
        return new Participant(name, SINGAPORE, List.of(tags));
    }

    private Roster rosterOf(Participant... participants) {
        Roster roster = new Roster();
        for (Participant participant : participants) {
            roster.add(participant);
        }
        return roster;
    }

    private static final class TagsUiSpy extends SilentUi {
        private int callCount;
        private List<Tag> tags;

        @Override
        public void showTags(List<Tag> shownTags) {
            callCount++;
            tags = List.copyOf(shownTags);
        }
    }
}
