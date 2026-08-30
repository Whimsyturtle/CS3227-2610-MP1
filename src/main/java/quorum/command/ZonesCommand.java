package quorum.command;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import quorum.model.Roster;
import quorum.ui.ResponseView;

/** Searches the timezones available to the application. */
public class ZonesCommand implements Command {
    private final String searchTerm;

    public ZonesCommand(String searchTerm) {
        this.searchTerm = searchTerm.trim();
    }

    @Override
    public CommandOutcome execute(Roster roster, ResponseView view) {
        String normalizedSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        List<String> matches = ZoneId.getAvailableZoneIds().stream()
                .filter(zone -> zone.toLowerCase(Locale.ROOT).contains(normalizedSearchTerm))
                .sorted()
                .toList();
        view.showZones(searchTerm, matches);
        return CommandOutcome.CONTINUE;
    }
}
