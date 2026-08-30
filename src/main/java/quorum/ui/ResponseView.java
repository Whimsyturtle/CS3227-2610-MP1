package quorum.ui;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import quorum.model.Participant;
import quorum.model.Tag;
import quorum.model.WakefulnessResult;

/** Receives responses produced while Quorum executes commands. */
public interface ResponseView {
    /** Displays the application welcome message. */
    void showWelcome();

    /** Displays the application goodbye message. */
    void showGoodbye();

    /** Displays the added participant and resulting roster size. */
    void showAdded(Participant participant, int total);

    /** Displays the deleted participant and resulting roster size. */
    void showDeleted(Participant participant, int total);

    /** Displays the participant after an edit. */
    void showEdited(Participant participant);

    /** Displays a tag added to a participant and whether the tag is new. */
    void showTagged(Participant participant, Tag tag, boolean isNewTag);

    /** Displays a tag removed from a participant. */
    void showUntagged(Participant participant, Tag tag);

    /** Displays the tags currently in use. */
    void showTags(List<Tag> tags);

    /** Displays the maximum-scoring meeting slots and their wakefulness details. */
    void showMeeting(Tag tag, LocalDate date, ZoneId userZone,
                     List<WakefulnessResult> bestResults);

    /** Displays the participants currently in the roster. */
    void showRoster(List<Participant> participants);

    /** Displays the timezones matching the given search term. */
    void showZones(String searchTerm, List<String> zones);

    /** Displays the given error message. */
    void showError(String message);
}
