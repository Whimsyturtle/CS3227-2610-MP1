package quorum.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;

class ZonesCommandTest {
    @Test
    void execute_searchWithNoMatches_showsEmptyResultAndContinues() {
        Roster roster = new Roster();
        ZonesUiSpy ui = new ZonesUiSpy();

        CommandOutcome outcome =
                new ZonesCommand("No_Such_Zone_7f3d2a").execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals("No_Such_Zone_7f3d2a", ui.searchTerm);
        assertEquals(List.of(), ui.zones);
        assertEquals(0, roster.size());
    }

    @Test
    void execute_trimmedMixedCaseSubstringWithOneMatch_showsCanonicalZone()
            throws QuorumException {
        Participant alice = new Participant("Alice", ZoneId.of("Asia/Singapore"));
        Roster roster = rosterOf(alice);
        ZonesUiSpy ui = new ZonesUiSpy();

        CommandOutcome outcome =
                new ZonesCommand("  kIrItImAtI  ").execute(roster, ui);

        assertEquals(CommandOutcome.CONTINUE, outcome);
        assertEquals(1, ui.callCount);
        assertEquals("kIrItImAtI", ui.searchTerm);
        assertEquals(List.of("Pacific/Kiritimati"), ui.zones);
        assertEquals(List.of(alice), roster.asList());
    }

    @Test
    void execute_multipleMatchesUnderTurkishLocale_showsAllMatchesSorted()
            throws QuorumException {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Participant alice = new Participant("Alice", ZoneId.of("Asia/Singapore"));
            Roster roster = rosterOf(alice);
            ZonesUiSpy ui = new ZonesUiSpy();

            CommandOutcome outcome =
                    new ZonesCommand("  aMeRiCa/InDiAnA/  ").execute(roster, ui);

            assertEquals(CommandOutcome.CONTINUE, outcome);
            assertEquals(1, ui.callCount);
            assertEquals("aMeRiCa/InDiAnA/", ui.searchTerm);
            assertEquals(List.of(
                    "America/Indiana/Indianapolis",
                    "America/Indiana/Knox",
                    "America/Indiana/Marengo",
                    "America/Indiana/Petersburg",
                    "America/Indiana/Tell_City",
                    "America/Indiana/Vevay",
                    "America/Indiana/Vincennes",
                    "America/Indiana/Winamac"), ui.zones);
            assertEquals(List.of(alice), roster.asList());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    private Roster rosterOf(Participant... participants) {
        Roster roster = new Roster();
        for (Participant participant : participants) {
            roster.add(participant);
        }
        return roster;
    }

    private static final class ZonesUiSpy extends SilentUi {
        private int callCount;
        private String searchTerm;
        private List<String> zones;

        @Override
        public void showZones(String shownSearchTerm, List<String> shownZones) {
            callCount++;
            searchTerm = shownSearchTerm;
            zones = List.copyOf(shownZones);
        }
    }
}
