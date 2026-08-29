package quorum.command;

import java.util.List;

import quorum.QuorumException;
import quorum.model.Participant;
import quorum.model.Roster;
import quorum.model.WakefulnessPlanner;
import quorum.model.WakefulnessResult;
import quorum.ui.Ui;

/** Finds the highest-scoring meeting slots for the user and participants with a given tag. */
public class MeetingCommand implements Command {
    private final MeetingRequest request;

    /** Creates a command for the given meeting search. */
    public MeetingCommand(MeetingRequest request) {
        this.request = request;
    }

    @Override
    public CommandOutcome execute(Roster roster, Ui ui) throws QuorumException {
        List<Participant> participants = roster.getParticipantsWithTag(request.tag());
        if (participants.isEmpty()) {
            throw new QuorumException("No participants have tag " + request.tag() + ".");
        }

        List<WakefulnessResult> bestResults = WakefulnessPlanner.findBestResults(
                request.userZone(), participants, request.date(), request.duration());
        ui.showMeeting(request.tag(), request.date(), request.userZone(), bestResults);
        return CommandOutcome.CONTINUE;
    }
}
