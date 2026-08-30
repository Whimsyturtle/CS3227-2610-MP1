package quorum.logic;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.command.AddCommand;
import quorum.command.ByeCommand;
import quorum.command.DeleteCommand;
import quorum.command.EditCommand;
import quorum.command.ListCommand;
import quorum.command.MeetingCommand;
import quorum.command.TagCommand;
import quorum.command.TagsCommand;
import quorum.command.UnknownCommand;
import quorum.command.UntagCommand;
import quorum.command.ZonesCommand;

class ParserRoutingTest {
    private final Parser parser = new Parser();

    @Test
    void parse_addKeywordWithFlexibleWhitespace_returnsAddCommand()
            throws QuorumException {
        assertInstanceOf(AddCommand.class,
                parser.parse("  add   Alice Tan /tz Asia/Singapore  "));
    }

    @Test
    void parse_deleteKeyword_returnsDeleteCommand() throws QuorumException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
    }

    @Test
    void parse_editKeyword_returnsEditCommand() throws QuorumException {
        assertInstanceOf(EditCommand.class,
                parser.parse("edit 1 /tz Europe/London"));
    }

    @Test
    void parse_tagKeyword_returnsTagCommand() throws QuorumException {
        assertInstanceOf(TagCommand.class, parser.parse("tag 1 friends"));
    }

    @Test
    void parse_untagKeyword_returnsUntagCommand() throws QuorumException {
        assertInstanceOf(UntagCommand.class, parser.parse("untag 1 friends"));
    }

    @Test
    void parse_tagsKeywordWithSurplusArguments_stillReturnsTagsCommand()
            throws QuorumException {
        assertInstanceOf(TagsCommand.class, parser.parse("tags ignored"));
    }

    @Test
    void parse_meetingKeyword_returnsMeetingCommand() throws QuorumException {
        assertInstanceOf(MeetingCommand.class,
                parser.parse("MeEtInG Asia/Singapore FRIENDS /on 2026-08-30 /for 1h"));
    }

    @Test
    void parse_listKeywordUnderTurkishLocale_returnsListCommand()
            throws QuorumException {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertInstanceOf(ListCommand.class, parser.parse("LIST"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void parse_zonesKeyword_returnsZonesCommand() throws QuorumException {
        assertInstanceOf(ZonesCommand.class, parser.parse("zones Singapore"));
    }

    @Test
    void parse_byeKeyword_returnsByeCommand() throws QuorumException {
        assertInstanceOf(ByeCommand.class, parser.parse("bye"));
    }

    @Test
    void parse_unknownKeyword_returnsUnknownCommand() throws QuorumException {
        assertInstanceOf(UnknownCommand.class, parser.parse("remind Alice"));
    }

    @Test
    void parse_whitespaceOnlyInput_returnsUnknownCommand() throws QuorumException {
        assertInstanceOf(UnknownCommand.class, parser.parse(" \t "));
    }
}
